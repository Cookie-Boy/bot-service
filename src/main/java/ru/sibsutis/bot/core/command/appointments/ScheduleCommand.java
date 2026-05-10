package ru.sibsutis.bot.core.command.appointments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.api.client.ExternalGateway;
import ru.sibsutis.bot.api.dto.AppointmentResponseDto;
import ru.sibsutis.bot.api.dto.DoctorDto;
import ru.sibsutis.bot.core.annotation.NonStackable;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@NonStackable
@RequiredArgsConstructor
public class ScheduleCommand implements BotCommand {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final MessageSender sender;
    private final ExternalGateway externalGateway;

    @Override
    public String getCommandName() {
        return "/schedule";
    }

    @Override
    public void execute(VkMessage message) {
        List<AppointmentResponseDto> appointments = externalGateway.getAppointments(message.getUserId());
        List<AppointmentResponseDto> upcoming = appointments.stream()
                .filter(a -> a.startTime().isAfter(LocalDateTime.now()))
                .filter(a -> a.status().equals("CONFIRMED") || a.status().equals("PENDING"))
                .toList();

        if (upcoming.isEmpty()) {
            sender.send(message.getUserId(), "У вас нет предстоящих приёмов.");
            return;
        }

        String schedule = upcoming.stream()
                .map(this::formatAppointment)
                .collect(Collectors.joining("\n\n"));
        sender.send(message.getUserId(), "📅 Ваши ближайшие записи:\n\n" + schedule);
    }

    private String formatAppointment(AppointmentResponseDto a) {
        return String.format("""
                        📅 %s в %s
                        👨‍⚕️ Врач: %s
                        🔖 Статус: %s""",
                a.startTime().format(DATE_FORMATTER),
                a.startTime().format(TIME_FORMATTER),
                getDoctorFullName(a.doctorId()),
                getStatus(a.status()));
    }

    private String getDoctorFullName(UUID doctorId) {
        Optional<DoctorDto> optDoctor = externalGateway.getDoctorById(doctorId);
        if (optDoctor.isEmpty()) {
            return "Не назначен";
        }
        DoctorDto doctor = optDoctor.get();
        return String.format("%s %s %s",
                        doctor.lastName() != null ? doctor.lastName() : "",
                        doctor.firstName() != null ? doctor.firstName() : "",
                        doctor.middleName() != null ? doctor.middleName() : "")
                .trim();
    }

    private String getStatus(String status) {
        return switch (status) {
            case "CONFIRMED" -> "✅ Подтверждено";
            case "PENDING" -> "⏳ Ожидает подтверждения";
            case "CANCELLED" -> "❌ Отменено";
            default -> "❓ Неизвестно";
        };
    }
}