package ru.sibsutis.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDto {
    private String petId;
    private String generatedAt;
    private String period;
    private String summary;
    private List<String> recommendations;
    private HealthStatsDto stats;
}