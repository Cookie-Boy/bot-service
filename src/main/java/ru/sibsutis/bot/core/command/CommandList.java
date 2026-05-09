package ru.sibsutis.bot.core.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CommandList {

    private final Map<String, BotCommand> commands = new ConcurrentHashMap<>();

    @Autowired
    public CommandList(List<BotCommand> botCommands) {
        botCommands.forEach(cmd -> commands.put(cmd.getCommandName(), cmd));
    }

    public BotCommand get(String commandName) {
        return commands.get(commandName);
    }
}
