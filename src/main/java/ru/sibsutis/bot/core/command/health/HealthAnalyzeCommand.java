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
public class HealthAnalyzeCommand implements BotCommand {

    private final MessageSender sender;

    @Override
    public String getCommandName() {
        return "/health/analyze";
    }

    @Override
    public void execute(VkMessage message) {
        sender.send(message.getUserId(),
                "Выберите опцию",
                KeyboardProvider.createHealthAnalyzeMenu());
    }
}
