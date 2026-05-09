package ru.sibsutis.bot.core.command.appointments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.api.client.ExternalGateway;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookCommand implements BotCommand {

    private final MessageSender sender;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public String getCommandName() {
        return "/book";
    }

    @Override
    public void execute(VkMessage message) {
        sender.send(message.getUserId(),
                "Для записи к врачу заполните форму по этой ссылке: " + frontendUrl + "/appointments/new");
    }
}
