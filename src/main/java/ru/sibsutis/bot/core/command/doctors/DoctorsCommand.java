package ru.sibsutis.bot.core.command.doctors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.annotation.NonStackable;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

@Slf4j
@Component
@NonStackable
@RequiredArgsConstructor
public class DoctorsCommand implements BotCommand {

    private final MessageSender sender;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public String getCommandName() {
        return "/doctors";
    }

    @Override
    public void execute(VkMessage message) {
        sender.send(message.getUserId(),
                "Для просмотра информации по всем врачам перейдите по этой ссылке: " + frontendUrl + "/doctors");
    }
}
