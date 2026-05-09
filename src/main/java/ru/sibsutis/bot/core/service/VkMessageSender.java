package ru.sibsutis.bot.core.service;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Keyboard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sibsutis.bot.configuration.VkBotConfig;

import java.util.Random;

@Service
@Slf4j
public class VkMessageSender implements MessageSender {

    private final VkApiClient vk;
    private final GroupActor actor;
    private final Random random = new Random();

    public VkMessageSender(VkBotConfig config) {
        this.vk = new VkApiClient(HttpTransportClient.getInstance());
        this.actor = new GroupActor(config.getGroupId(), config.getToken());
    }

    @Override
    public boolean send(Long userId, String text) {
        try {
            vk.messages().sendDeprecated(actor)
                    .userId(userId)
                    .message(text)
                    .randomId(random.nextInt())
                    .execute();
            return true;
        } catch (ApiException | ClientException e) {
            log.error("Failed to send message to user {}", userId, e);
            return false;
        }
    }

    @Override
    public boolean send(Long userId, String text, Keyboard keyboard) {
        try {
            vk.messages().sendDeprecated(actor)
                    .userId(userId)
                    .message(text)
                    .randomId(random.nextInt())
                    .keyboard(keyboard)
                    .execute();
            return true;
        } catch (ApiException | ClientException e) {
            log.error("Failed to send message (with keyboard) to user {}", userId, e);
            return false;
        }
    }
}
