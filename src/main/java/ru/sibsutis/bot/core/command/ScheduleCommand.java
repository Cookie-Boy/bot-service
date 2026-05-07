package ru.sibsutis.bot.core.command;

import org.springframework.stereotype.Component;
import ru.sibsutis.bot.api.client.ExternalGateway;
import ru.sibsutis.bot.api.dto.AppointmentResponseDto;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ScheduleCommand implements BotCommand {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final MessageSender sender;
    private final ExternalGateway externalGateway;
    private final Map<String, String> userStorage;  // shared storage

    public ScheduleCommand(MessageSender sender,
                           ExternalGateway externalGateway,
                           Map<String, String> userStorage) {
        this.sender = sender;
        this.externalGateway = externalGateway;
        this.userStorage = userStorage;
    }

    @Override
    public String getCommandName() {
        return "/schedule";
    }

    @Override
    public void execute(VkMessage message) {
        String username = userStorage.get(message.getUserId().toString());
        if (username == null) {
            sender.send(message.getUserId(), "Сначала зарегистрируйтесь с помощью /start");
            return;
        }

        List<AppointmentResponseDto> appointments = externalGateway.getTgUserAppointments("@" + username);
        if (appointments == null) {
            sender.send(message.getUserId(), "Из-за технических неполадок сервис 'appointments' недоступен, ожидайте...");
            return;
        }
        if (appointments.isEmpty()) {
            sender.send(message.getUserId(), "У вас нет запланированных приёмов.");
            return;
        }

        String schedule = appointments.stream()
                .map(this::formatAppointment)
                .collect(Collectors.joining("\n\n"));
        sender.send(message.getUserId(), "Ваши записи:\n\n" + schedule);
    }

    private String formatAppointment(AppointmentResponseDto a) {
        return String.format("""
                        📅 %s в %s
                        👨⚕️ Врач: %s
                        🏥 Клиника: %s
                        🔖 Статус: %s""",
                a.startTime().format(DATE_FORMATTER),
                a.startTime().format(TIME_FORMATTER),
                a.doctorFullName(),
                a.clinicName(),
                getStatus(a.status()));
    }

    private String getStatus(String status) {
        return switch (status) {
            case "CONFIRMED" -> "✅ Подтверждено";
            case "PENDING" -> "⏳ Ожидает подтверждения";
            case "CANCELLED" -> "❌ Отменено";
            default -> "";
        };
    }
}