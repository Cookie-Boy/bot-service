package ru.sibsutis.bot.api.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.sibsutis.bot.api.dto.PetDto;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthServiceClient {

    private final RestClient restClient;
    private final TokenProvider tokenProvider;

    public List<PetDto> getLatestVitals(Long petId) {
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
}
