package ru.sibsutis.bot.core.command.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.command.CommandList;
import ru.sibsutis.bot.core.command.CommandStack;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackCommand implements BotCommand {

    private final CommandStack stack;
    private final CommandList commands;

    @Override
    public String getCommandName() {
        return "/back";
    }

    @Override
    public void execute(VkMessage message) {
        stack.pop();
        BotCommand prevCommand = commands.get(stack.peek());
        prevCommand.execute(message);
    }
}