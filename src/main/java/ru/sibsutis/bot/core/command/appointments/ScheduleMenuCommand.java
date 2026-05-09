package ru.sibsutis.bot.core.command.appointments;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.api.client.ExternalGateway;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.keyboard.KeyboardProvider;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

@Component
@RequiredArgsConstructor
public class ScheduleMenuCommand implements BotCommand {

    private final MessageSender sender;

    @Override
    public String getCommandName() {
        return "/pets";
    }

    @Override
    public void execute(VkMessage message) {
        sender.send(message.getUserId(),
                "Выберите опцию",
                KeyboardProvider.createScheduleMenu());
    }
}