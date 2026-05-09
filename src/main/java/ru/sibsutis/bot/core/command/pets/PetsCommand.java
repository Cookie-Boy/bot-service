package ru.sibsutis.bot.core.command.pets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.api.client.ExternalGateway;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

@Component
@RequiredArgsConstructor
public class PetsCommand implements BotCommand {

    private final MessageSender sender;
    private final ExternalGateway externalGateway;


    @Override
    public String getCommandName() {
        return "/pets";
    }

    @Override
    public void execute(VkMessage message) {

    }
}
