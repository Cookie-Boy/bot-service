package ru.sibsutis.bot.core.command.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.command.CommandStack;
import ru.sibsutis.bot.core.model.VkMessage;

@Component
@RequiredArgsConstructor
public class BackCommand implements BotCommand {

    private final CommandStack stack;

    @Override
    public String getCommandName() {
        return "/back";
    }

    @Override
    public void execute(VkMessage message) {
        stack.pop();
        BotCommand prevCommand = stack.peek();
        prevCommand.execute(message);
    }
}