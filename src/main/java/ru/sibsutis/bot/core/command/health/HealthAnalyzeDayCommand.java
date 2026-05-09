package ru.sibsutis.bot.core.command.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.keyboard.KeyboardProvider;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthAnalyzeDayCommand implements BotCommand {

    private final HealthAnalyzePeriodExecutor healthExecutor;

    @Override
    public String getCommandName() {
        return "/health/analyze/day";
    }

    @Override
    public void execute(VkMessage message) {
        healthExecutor.execute(message, "day");
    }
}
