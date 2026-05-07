package ru.sibsutis.bot.core.command;

import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

@Component
public class BookCommand implements BotCommand {

    private final MessageSender sender;

    public BookCommand(MessageSender sender) {
        this.sender = sender;
    }

    @Override
    public String getCommandName() {
        return "/book";
    }

    @Override
    public void execute(VkMessage message) {
        sender.send(message.getUserId(),
                "Функция бронирования временно недоступна. Пожалуйста, используйте другие каналы записи.");
    }
}
