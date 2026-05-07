package ru.sibsutis.bot.core.command;

import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.exception.GlobalExceptionHandler;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CommandDispatcher {

    private final Map<String, BotCommand> commands = new ConcurrentHashMap<>();
    private final MessageSender sender;
    private final GlobalExceptionHandler exceptionHandler;

    public CommandDispatcher(MessageSender sender,
                             GlobalExceptionHandler exceptionHandler,
                             List<BotCommand> botCommands) {
        this.sender = sender;
        this.exceptionHandler = exceptionHandler;
        botCommands.forEach(cmd -> commands.put(cmd.getCommandName(), cmd));
    }

    public void dispatch(VkMessage message) {
        String text = message.getText();
        if (text == null || !text.startsWith("/")) {
            sender.send(message.getUserId(),
                    "Пожалуйста, используйте команды: /start, /schedule, /book");
            return;
        }

        BotCommand cmd = commands.get(text.trim());
        if (cmd != null) {
            try {
                cmd.execute(message);
            } catch (Exception e) {
                exceptionHandler.handle(e, "Command " + text + " from " + message.getUserId());
                sender.send(message.getUserId(), "Произошла ошибка при выполнении команды.");
            }
        } else {
            sender.send(message.getUserId(),
                    "Неизвестная команда. Доступные: /start, /schedule, /book");
        }
    }
}
