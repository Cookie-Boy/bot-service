package ru.sibsutis.bot.api.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.sibsutis.bot.api.dto.DoctorDto;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ManagementServiceClient {

    private final RestClient restClient;
    private final TokenProvider tokenProvider;

    public Optional<DoctorDto> getDoctorById(UUID doctorId) {
        try {
            String token = tokenProvider.getFreshToken();
            log.info("Fresh token: {}", token);
            return restClient.get()
                    .uri("api/management/doctors/{doctorId}", doctorId)
                    .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RuntimeException e) {
            log.error("Error during sending a request to management-service: {}", String.valueOf(e));
            return Optional.empty();
        }
    }
}
