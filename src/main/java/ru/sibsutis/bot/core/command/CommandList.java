package ru.sibsutis.bot.core.command;

import jakarta.annotation.PostConstruct;
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
    private final List<BotCommand> botCommands;

    @Autowired
    public CommandList(List<BotCommand> botCommands) {
        this.botCommands = botCommands;
    }

    @PostConstruct
    public void init() {
        botCommands.forEach(cmd -> commands.put(cmd.getCommandName(), cmd));
    }

    public BotCommand get(String commandName) {
        return commands.get(commandName);
    }
}
