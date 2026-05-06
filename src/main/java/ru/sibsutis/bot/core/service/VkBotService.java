package ru.sibsutis.bot.core.service;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibsutis.bot.api.client.ExternalGateway;
import ru.sibsutis.bot.api.dto.AppointmentResponseDto;
import ru.sibsutis.bot.configuration.VkBotConfig;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VkBotService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final Map<String, String> userStorage = new ConcurrentHashMap<>();

    private final VkApiClient vk;
    private final GroupActor actor;
    private final VkBotConfig config;
    private final ExternalGateway externalGateway;

    private final Random random = new Random();

    private final Map<String, BiConsumer<Long, String>> commandHandlers = new HashMap<>();

    private final ExecutorService longPollExecutor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean running = new AtomicBoolean(true);

    @Autowired
    public VkBotService(VkBotConfig config, ExternalGateway externalGateway) {
        this.config = config;
        this.externalGateway = externalGateway;
        this.vk = new VkApiClient(HttpTransportClient.getInstance());
        this.actor = new GroupActor(config.getGroupId(), config.getToken());
    }

    @PostConstruct
    public void init() {

        log.info("Initializing VK Bot with group ID: {}", config.getGroupId());
        commandHandlers.put("/start", this::handleStartCommand);
        commandHandlers.put("/schedule", this::handleScheduleCommand);
        commandHandlers.put("/book", this::handleBookCommand);

        registerLongPollEvents();
        longPollExecutor.submit(this::longPollLoop);
        log.info("VK Bot started successfully");
    }

    @PreDestroy
    public void destroy() {
        log.info("Shutting down VK Bot...");
        running.set(false);
        longPollExecutor.shutdownNow();
    }

    private void registerLongPollEvents() {
        try {
            vk.groups().setLongPollSettings(actor)
                    .groupId(config.getGroupId())
                    .enabled(true)
                    .messageNew(true)
                    .apiVersion(String.valueOf(config.getApiVersion()))
                    .execute();
            log.info("Long Poll events registered for group {}", config.getGroupId());
        } catch (ApiException | ClientException e) {
            log.error("Failed to register Long Poll events", e);
        }
    }

    private void longPollLoop() {
        while (running.get()) {
            try {
                Integer ts = vk.messages().getLongPollServer(actor).execute().getTs();
                MessagesGetLongPollHistoryQuery history = vk.messages()
                        .getLongPollHistory(actor)
                        .ts(ts);
                List<Message> messages = history.execute().getMessages().getItems();

                if (!messages.isEmpty()) {
                    messages.forEach(this::handleMessageEvent);
                }

            } catch (ApiException | ClientException e) {
                log.error("Failed to get Long Poll server params", e);
                return;
            }
        }
    }

    private void handleMessageEvent(Message message) {
        Long userId = message.getFromId();
        String text = message.getText();

        if (text != null && !text.trim().isEmpty()) {
            String userIdStr = String.valueOf(userId);
            userStorage.putIfAbsent(userIdStr, String.valueOf(userId));
            processCommand(userId, text);
        }
    }

    private void processCommand(Long userId, String text) {
        if (!text.startsWith("/")) {
            sendMessage(userId, "Пожалуйста, используйте команды: /start, /schedule, /book");
            return;
        }

        BiConsumer<Long, String> handler = commandHandlers.get(text.trim());
        if (handler != null) {
            handler.accept(userId, text);
        } else {
            sendMessage(userId, "Неизвестная команда. Доступные: /start, /schedule, /book");
        }
    }

    public boolean sendMessage(Long userId, String text) {
        try {
            vk.messages().sendDeprecated(actor)
                    .userId(userId)
                    .message(text)
                    .randomId(random.nextInt())
                    .execute();
            return true;
        } catch (ApiException | ClientException e) {
            log.error("Failed to send message to user with ID: {}", userId, e);
            return false;
        }
    }

    private void handleStartCommand(Long userId, String text) {
        log.info("New user registered (stub): {}", userId);

        sendMessage(userId, """
                Привет! Я бот, который поможет тебе забронировать прием у врача.
                Доступные команды:
                /schedule - показать все записи
                /book - создать новую запись""");
    }

    private void handleScheduleCommand(Long userId, String text) {
        String username = userStorage.get(userId);

        if (username == null) {
            sendMessage(userId, "Сначала зарегистрируйтесь с помощью /start");
            return;
        }

        List<AppointmentResponseDto> appointments = externalGateway.getTgUserAppointments("@" + username);

        if (appointments == null) {
            sendMessage(userId, "Из-за технических неполадок сервис 'appointments' недоступен, ожидайте...");
            return;
        }

        if (appointments.isEmpty()) {
            sendMessage(userId, "У вас нет запланированных приёмов.");
            return;
        }

        String schedule = appointments.stream()
                .map(this::formatAppointment)
                .collect(Collectors.joining("\n\n"));
        sendMessage(userId, "Ваши записи:\n\n" + schedule);
    }

    private void handleBookCommand(Long userId, String text) {
        sendMessage(userId, "Функция бронирования временно недоступна. Пожалуйста, используйте другие каналы записи.");
    }

    private String formatAppointment(AppointmentResponseDto appointment) {
        return String.format("""
                📅 %s в %s
                👨⚕️ Врач: %s
                🏥 Клиника: %s
                🔖 Статус: %s""",
                appointment.startTime().format(DATE_FORMATTER),
                appointment.startTime().format(TIME_FORMATTER),
                appointment.doctorFullName(),
                appointment.clinicName(),
                getStatusEmoji(appointment.status()));
    }

    private String getStatusEmoji(String status) {
        return switch (status) {
            case "CONFIRMED" -> "✅ Подтверждено";
            case "PENDING" -> "⏳ Ожидает подтверждения";
            case "CANCELLED" -> "❌ Отменено";
            default -> "";
        };
    }
}