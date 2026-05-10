package ru.sibsutis.bot.core.command.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.annotation.NonStackable;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.model.VkMessage;

@Slf4j
@Component
@NonStackable
@RequiredArgsConstructor
public class HealthAnalyzeWeekCommand implements BotCommand {

    private final HealthAnalyzePeriodExecutor healthExecutor;

    @Override
    public String getCommandName() {
        return "/health/analyze/week";
    }

    @Override
    public void execute(VkMessage message) {
        healthExecutor.execute(message, "week");
    }
}
