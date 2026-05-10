package ru.sibsutis.bot.api.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.api.dto.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExternalGateway {

    private final AppointmentServiceClient appointmentServiceClient;
    private final ManagementServiceClient managementServiceClient;
    private final ProfileServiceClient profileServiceClient;
    private final HealthServiceClient healthServiceClient;

    public List<AppointmentResponseDto> getAppointments(Long vkUserId) {
        Optional<OwnerDto> optOwner = getOwnerByVkUserId(vkUserId);
        if (optOwner.isEmpty()) {
            return Collections.emptyList();
        }
        return appointmentServiceClient.getAppointments(optOwner.get().getId());
    }

    public Optional<OwnerDto> getOwnerByVkUserId(Long vkUserId) {
        return profileServiceClient.getOwnerByVkUserId(vkUserId);
    }

    public Long getVkUserIdByPetId(String petId) {
        return profileServiceClient.getVkUserIdByPetId(petId);
    }

    public List<PetDto> getPetsByVkUserId(Long vkUserId) {
        return profileServiceClient.getPetsByVkUserId(vkUserId);
    }

    public Optional<DoctorDto> getDoctorById(UUID doctorId) {
        return managementServiceClient.getDoctorById(doctorId);
    }

    public LatestPetResultDto getLatestVitals(String petId) {
        return healthServiceClient.getLatestVitals(petId);
    }

    public RecommendationDto analyze(String petId, String period) {
        return healthServiceClient.analyze(petId, period);
    }
}
