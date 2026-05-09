package ru.sibsutis.bot.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatsDto {
    private Double avgHeartRate;
    private Double avgRespiratoryRate;
    private Double avgTemperature;
    private Integer anomalyCount;
    private Integer totalReadings;
}