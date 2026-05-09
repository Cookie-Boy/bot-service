package ru.sibsutis.bot.core.command;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.exception.GlobalExceptionHandler;
import ru.sibsutis.bot.core.keyboard.KeyboardProvider;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;
import ru.sibsutis.bot.core.service.VkAuthService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class CommandDispatcher {

    private final MessageSender sender;
    private final VkAuthService vkAuthService;
    private final CommandList commandList;
    private final GlobalExceptionHandler exceptionHandler;

    public void dispatch(VkMessage message) {
        if (!vkAuthService.isLinked(message.getUserId())) {
            sender.send(message.getUserId(),
                    "⛔ Ваш аккаунт ещё не привязан к личному кабинету. Используйте /start для привязки.");
            return;
        }

        String commandName = "/start".equals(message.getText()) ? "/start" : retrieveCommand(message.getPayload());

        BotCommand cmd = commandList.get(commandName);
        if (cmd != null) {
            try {
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
