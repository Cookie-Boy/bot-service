package ru.sibsutis.bot.core.service;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetLongPollHistoryResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibsutis.bot.configuration.VkBotConfig;
import ru.sibsutis.bot.core.command.CommandDispatcher;
import ru.sibsutis.bot.core.exception.GlobalExceptionHandler;
import ru.sibsutis.bot.core.model.VkMessage;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class VkBotService {

    private final VkApiClient vk;
    private final GroupActor actor;
    private final Long groupId;
    private final String apiVersion;
    private final CommandDispatcher dispatcher;
    private final GlobalExceptionHandler exceptionHandler;

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Autowired
    public VkBotService(VkBotConfig config,
                        CommandDispatcher dispatcher,
                        GlobalExceptionHandler exceptionHandler) {
        this.vk = new VkApiClient(HttpTransportClient.getInstance());
        this.actor = new GroupActor(config.getGroupId(), config.getToken());
        this.groupId = config.getGroupId();
        this.apiVersion = config.getApiVersion();
        this.dispatcher = dispatcher;
        this.exceptionHandler = exceptionHandler;
    }

    @PostConstruct
    public void start() {
        registerSettings();
        executor.submit(this::pollLoop);
        log.info("Long poll started");
    }

    @PreDestroy
    public void stop() {
        running.set(false);
        executor.shutdownNow();
        log.info("Long poll stopped");
    }

    private void registerSettings() {
        try {
            vk.groups().setLongPollSettings(actor)
                    .groupId(groupId)
                    .enabled(true)
                    .messageNew(true)
                    .messageEvent(true)
                    .apiVersion(apiVersion)
                    .execute();
        } catch (ApiException | ClientException e) {
            throw new RuntimeException("Cannot register long poll settings", e);
        }
    }

    private void pollLoop() {
        String key;
        Integer ts;
        Integer pts;
        try {
            var serverResponse = vk.messages().getLongPollServer(actor)
                    .needPts(true)
                    .execute();
            key = serverResponse.getKey();
            ts = serverResponse.getTs();
            pts = serverResponse.getPts();
        } catch (Exception e) {
            exceptionHandler.handle(e, "Getting initial long poll server");
            return;
        }

        while (running.get()) {
            try {
                var response = vk.messages().getLongPollHistory(actor)
                        .captchaKey(key)
                        .ts(ts)
                        .pts(pts)
                        .execute();

                if (response.getMessages() != null) {
                    List<Message> messages = response.getMessages().getItems();
                    if (messages != null) {
                        for (Message msg : messages) {
                            if (!msg.isOut()) {
                                processMessage(msg);
                            }
                        }
                    }
                }

                pts = response.getNewPts();

                if (response.getMessages() == null || response.getMessages().getItems().isEmpty()) {
                    sleepSafe(300);
                }

            } catch (Exception e) {
                exceptionHandler.handle(e, "Long poll cycle");
                sleepSafe(1000);
                try {
                    var newServer = vk.messages().getLongPollServer(actor)
                            .needPts(true)
                            .execute();
                    key = newServer.getKey();
                    ts = newServer.getTs();
                    pts = newServer.getPts();
                } catch (Exception ex) {
                    exceptionHandler.handle(ex, "Reconnect to long poll");
                    return;
                }
            }
        }
    }

    private void processMessage(Message message) {
        dispatcher.dispatch(new VkMessage(message.getFromId(), message.getText(), message.getPayload()));
    }

    private void sleepSafe(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}