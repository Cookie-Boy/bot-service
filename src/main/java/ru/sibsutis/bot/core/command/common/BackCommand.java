package ru.sibsutis.bot.core.command.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.command.CommandStack;
import ru.sibsutis.bot.core.model.VkMessage;

@Slf4j
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
        BotCommand poppedCommand = stack.pop();
        log.info("Command '{}' popped", poppedCommand.getCommandName());
        BotCommand prevCommand = stack.peek();
        log.info("Peeked command: '{}'", prevCommand.getCommandName());
        prevCommand.execute(message);
    }
}