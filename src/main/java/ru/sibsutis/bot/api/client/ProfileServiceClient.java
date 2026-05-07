package ru.sibsutis.bot.api.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.sibsutis.bot.api.dto.OwnerDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileServiceClient {

    private final RestClient restClient;
    private final TokenProvider tokenProvider;

    public OwnerDto getOwnerByVkUserId(Long vkUserId) {
        try {
            String token = tokenProvider.getFreshToken();
            log.info("Fresh token: {}", token);
            return restClient.get()
                    .uri("api/profile/owners/by-vk-id/{vkUserId}", vkUserId)
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RuntimeException e) {
            log.error("Failed to send a request to profile-service: {}", String.valueOf(e));
            return null;
        }
    }
}

