package ru.sibsutis.bot.api.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.sibsutis.bot.api.dto.LatestPetResultDto;
import ru.sibsutis.bot.api.dto.RecommendationDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthServiceClient {

    private final RestClient restClient;
    private final TokenProvider tokenProvider;

    public LatestPetResultDto getLatestVitals(String petId) {
        try {
            String token = tokenProvider.getFreshToken();
            return restClient.get()
                    .uri("api/health/vitals/{petId}/latest", petId)
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RuntimeException e) {
            log.error("Failed to send a request to health-service (was trying to fetch latest vitals): {}", String.valueOf(e));
            return null;
        }
    }

    public RecommendationDto analyze(String petId, String period) {
        try {
            String token = tokenProvider.getFreshToken();
            return restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("api/health/analyze/{petId}")
                            .queryParam("period", period)
                            .build(petId))
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RuntimeException e) {
            log.error("Failed to send a request to health-service (was trying to fetch recommendation): {}", String.valueOf(e));
            return null;
        }
    }
}
