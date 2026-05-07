package ru.sibsutis.bot.core.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

@Component
@Slf4j
public class StartCommand implements BotCommand {

    private final MessageSender sender;

    public StartCommand(MessageSender sender) {
        this.sender = sender;
    }

    @Override
    public String getCommandName() {
        return "/start";
    }

    @Override
    public void execute(VkMessage message) {
        log.info("New user registered (stub): {}", message.getUserId());
        sender.send(message.getUserId(), """
                Привет! Я бот, который поможет тебе забронировать прием у врача.
                Доступные команды:
                /schedule - показать все записи
                /book - создать новую запись""");
    }
}