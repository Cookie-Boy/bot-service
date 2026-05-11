package ru.sibsutis.bot.core.command.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.api.client.ExternalGateway;
import ru.sibsutis.bot.api.dto.HealthStatsDto;
import ru.sibsutis.bot.api.dto.PetDto;
import ru.sibsutis.bot.api.dto.RecommendationDto;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthAnalyzePeriodExecutor {

    private final MessageSender sender;
    private final ExternalGateway externalGateway;

    public void execute(VkMessage message, String period) {
        List<PetDto> pets = externalGateway.getPetsByVkUserId(message.getUserId());

        if (pets.isEmpty()) {
            sender.send(message.getUserId(), "🐾 У вас пока нет зарегистрированных питомцев.");
            return;
        }

        StringBuilder sb = new StringBuilder("📋 Анализ здоровья\n");
        sb.append(String.format("Период: %s\n\n", getPeriodDescription(period)));

        boolean hasAnyData = false;

        for (int i = 0; i < pets.size(); i++) {
            PetDto pet = pets.get(i);
            RecommendationDto recommendation = externalGateway.analyze(pet.getId(), period);

            if (recommendation != null) {
                sb.append(formatRecommendation(pet, recommendation));
                hasAnyData = true;
            } else {
                sb.append(formatNoData(pet));
            }

            // Разделитель между питомцами
            if (i < pets.size() - 1) {
                sb.append("\n");
            }
        }

        if (!hasAnyData) {
            sb.append("Нет данных для анализа за выбранный период.\n");
            sb.append("Попробуйте позже или запросите анализ через /health.");
        }

        sender.send(message.getUserId(), sb.toString());
    }

    private String formatRecommendation(PetDto pet, RecommendationDto rec) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("🐶 %s", pet.getName()));
        if (pet.getBreed() != null && !pet.getBreed().isEmpty()) {
            sb.append(String.format(" (%s)", pet.getBreed()));
        }
        sb.append("\n");

        if (rec.getGeneratedAt() != null && !rec.getGeneratedAt().isEmpty()) {
            sb.append(String.format("📅 Сформировано: %s\n", rec.getGeneratedAt()));
        }

        if (rec.getSummary() != null && !rec.getSummary().isEmpty()) {
            sb.append("\n📝 Сводка:\n");
            sb.append(String.format("%s\n", rec.getSummary()));
        }

        if (rec.getStats() != null) {
            sb.append("\n📊 Статистика:\n");
            HealthStatsDto stats = rec.getStats();

            if (stats.getAvgHeartRate() != null) {
                sb.append(String.format("   💓 Средний пульс: %.1f уд/мин\n", stats.getAvgHeartRate()));
            }
            if (stats.getAvgRespiratoryRate() != null) {
                sb.append(String.format("   🫁 Среднее дыхание: %.1f дых/мин\n", stats.getAvgRespiratoryRate()));
            }
            if (stats.getAvgTemperature() != null) {
                sb.append(String.format("   🌡️ Средняя температура: %.1f°C\n", stats.getAvgTemperature()));
            }
            if (stats.getAnomalyCount() != null) {
                String anomalyEmoji = stats.getAnomalyCount() > 0 ? "⚠️" : "✅";
                sb.append(String.format("   %s Аномалий: %d\n", anomalyEmoji, stats.getAnomalyCount()));
            }
            if (stats.getTotalReadings() != null) {
                sb.append(String.format("   📈 Всего измерений: %d\n", stats.getTotalReadings()));
            }
        }

        // Рекомендации
        if (rec.getRecommendations() != null && !rec.getRecommendations().isEmpty()) {
            sb.append("\n💡 Рекомендации:\n");
            for (int i = 0; i < rec.getRecommendations().size(); i++) {
                sb.append(String.format("   %d. %s\n", i + 1, rec.getRecommendations().get(i)));
            }
        }

        return sb.toString();
    }

    private String formatNoData(PetDto pet) {
        return String.format("🐶 %s\n└─ Нет данных для анализа\n", pet.getName());
    }

    private String getPeriodDescription(String period) {
        return switch (period != null ? period.toLowerCase() : "") {
            case "day" -> "за день";
            case "week" -> "за неделю";
            case "month" -> "за месяц";
            default -> period != null ? period : "не указан";
        };
    }
}

