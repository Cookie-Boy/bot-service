package ru.sibsutis.bot.api.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.sibsutis.bot.api.dto.OwnerDto;
import ru.sibsutis.bot.api.dto.PetDto;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileServiceClient {

    private final RestClient restClient;
    private final TokenProvider tokenProvider;

    public Optional<OwnerDto> getOwnerByVkUserId(Long vkUserId) {
        try {
            String token = tokenProvider.getFreshToken();
            log.info("Fresh token: {}", token);
            return restClient.get()
                    .uri("api/profile/owners/by-vk-id/{vkUserId}", vkUserId)
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RuntimeException e) {
            log.error("Error during sending a request to profile-service: {}", String.valueOf(e));
            return Optional.empty();
        }
    }

    public Long getVkUserIdByPetId(String petId) {
        try {
            String token = tokenProvider.getFreshToken();
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/profile/owner/vk-user-id")
                            .queryParam("petId", petId)
                            .build())
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RuntimeException e) {
            log.error("Failed to send a request to profile-service (trying to fetch Vk User Id): {}", String.valueOf(e));
            return null;
        }
    }

    public List<PetDto> getPetsByVkUserId(Long vkUserId) {
        try {
            String token = tokenProvider.getFreshToken();
            return restClient.get()
                    .uri("api/profile/pets/vk-user-id/{vkUserId}", vkUserId)
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RuntimeException e) {
            log.error("Failed to send a request to profile-service (trying to fetch pets by vkUserId): {}", String.valueOf(e));
            return null;
        }
    }
}

