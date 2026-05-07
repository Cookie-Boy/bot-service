package ru.sibsutis.bot.api.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.api.dto.AppointmentResponseDto;
import ru.sibsutis.bot.api.dto.OwnerDto;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExternalGateway {

    private final ProfileServiceClient profileServiceClient;

    public List<AppointmentResponseDto> getTgUserAppointments(String tgUserName) {
        return null;
    }

    public Optional<OwnerDto> getOwnerByVkUserId(Long vkUserId) {
        return profileServiceClient.getOwnerByVkUserId(vkUserId);
    }

    public Long getOwnerVkUserId(String petId) {
        return profileServiceClient.getOwnerVkUserId(petId);
    }
}
