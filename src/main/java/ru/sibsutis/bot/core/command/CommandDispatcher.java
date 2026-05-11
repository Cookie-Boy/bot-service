package ru.sibsutis.bot.core.command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.command.common.StartCommand;
import ru.sibsutis.bot.core.exception.GlobalExceptionHandler;
import ru.sibsutis.bot.core.keyboard.KeyboardProvider;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;
import ru.sibsutis.bot.core.service.VkAuthService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CommandDispatcher {

    private final MessageSender sender;
    private final VkAuthService vkAuthService;
    private final CommandStack stack;
    private final Map<String, BotCommand> commands = new ConcurrentHashMap<>();
    private final GlobalExceptionHandler exceptionHandler;

    @Autowired
    public CommandDispatcher(MessageSender sender,
                             VkAuthService vkAuthService,
                             CommandStack stack,
                             GlobalExceptionHandler exceptionHandler,
                             List<BotCommand> botCommands) {
        this.sender = sender;
        this.vkAuthService = vkAuthService;
        this.stack = stack;
        this.exceptionHandler = exceptionHandler;
        botCommands.forEach(cmd -> commands.put(cmd.getCommandName(), cmd));
    }

    public void dispatch(VkMessage message) {
        String commandName = "/start".equals(message.getText()) ? "/start" : retrieveCommand(message.getPayload());
        BotCommand cmd = commands.get(commandName);

        if (!vkAuthService.isLinked(message.getUserId()) && !(cmd instanceof StartCommand)) {
            sender.send(message.getUserId(),
                    "⛔ Ваш аккаунт ещё не привязан к личному кабинету. Используйте /start для привязки.");
            return;
        }

        if (cmd != null) {
            try {
                stack.push(cmd);
                cmd.execute(message);
            } catch (Exception e) {
                exceptionHandler.handle(e, "Command '" + commandName + "' from " + message.getUserId());
                sender.send(message.getUserId(), "Произошла ошибка при выполнении команды.");
            }
        } else {
            sender.send(message.getUserId(),
                    "Неизвестная команда.",
                    KeyboardProvider.createMainKeyboard());
        }
    }

    private String retrieveCommand(String payload) {
        JsonObject object = JsonParser.parseString(payload).getAsJsonObject();
        return object.get("cmd").getAsString();
    }
}
