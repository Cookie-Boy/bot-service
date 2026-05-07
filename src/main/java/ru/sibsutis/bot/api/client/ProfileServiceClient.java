package ru.sibsutis.bot.api.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileServiceClient {

    private final RestClient restClient;
    private final TokenProvider tokenProvider;

    public String getOwnerVkUserId(String petId) {
        try {
            String token = tokenProvider.getFreshToken();
            log.info("Fresh token: {}", token);
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .path("api/profile/owners")
                        .queryParam("petId", petId)
                        .build())
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RuntimeException e) {
            log.error("Failed to send a request to appointment-service: {}", String.valueOf(e));
            return null;
        }
    }
}

