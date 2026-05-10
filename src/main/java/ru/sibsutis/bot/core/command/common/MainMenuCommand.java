package ru.sibsutis.bot.core.command.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.annotation.NonStackable;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.keyboard.KeyboardProvider;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

@Slf4j
@Component
@NonStackable
@RequiredArgsConstructor
public class MainMenuCommand implements BotCommand {

    private final MessageSender sender;

    @Override
    public String getCommandName() {
        return "/main_menu";
    }

    @Override
    public void execute(VkMessage message) {
        sender.send(message.getUserId(),
                "Выберите опцию",
                KeyboardProvider.createMainKeyboard());
    }
}
