package ru.sibsutis.bot.core.command.appointments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.api.client.ExternalGateway;
import ru.sibsutis.bot.api.dto.AppointmentResponseDto;
import ru.sibsutis.bot.api.dto.DoctorDto;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.keyboard.KeyboardProvider;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CancelCommand implements BotCommand {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final MessageSender sender;
    private final ExternalGateway externalGateway;

    @Override
    public String getCommandName() {
        return "/cancel";
    }

    @Override
    public void execute(VkMessage message) {
        List<AppointmentResponseDto> appointments = externalGateway.getAppointments(message.getUserId());
        if (appointments.isEmpty()) {
            sender.send(message.getUserId(), "У вас нет запланированных приёмов.");
            return;
        }

        String schedule = appointments.stream()
                .map(this::formatAppointment)
                .collect(Collectors.joining("\n\n"));
        sender.send(message.getUserId(),
                "Ваши записи:\n\n" + schedule,
                KeyboardProvider.createScheduleMenu());
    }

    private String formatAppointment(AppointmentResponseDto a) {
        return String.format("""
                        📅 %s в %s
                        👨 Врач: %s
                        🔖 Статус: %s""",
                a.startTime().format(DATE_FORMATTER),
                a.startTime().format(TIME_FORMATTER),
                getDoctorFullName(a.doctorId()),
                getStatus(a.status()));
    }

    private String getDoctorFullName(UUID doctorId) {
        Optional<DoctorDto> optDoctor = externalGateway.getDoctorById(doctorId);
        if (optDoctor.isEmpty()) {
            return "Не найден";
        }
        DoctorDto doctor = optDoctor.get();
        return doctor.middleName() + doctor.firstName() + doctor.lastName();
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