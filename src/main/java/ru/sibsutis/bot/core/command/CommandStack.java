package ru.sibsutis.bot.core.command;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.command.common.BackCommand;
import ru.sibsutis.bot.core.command.common.StartCommand;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandStack {

    private final Deque<BotCommand> commands = new ConcurrentLinkedDeque<>();

    @Autowired
    public void setMainMenuCommand(@Qualifier("mainMenuCommand") BotCommand mainMenu) {
        commands.push(mainMenu);
        log.info("mainMenuCommand added to stack");
    }

    public void push(BotCommand command) {
        if (command.isStackable()) {
            commands.push(command);
        }
    }

    public BotCommand pop() {
        if (commands.size() <= 1) {
            return commands.peek();
        }
        return commands.pop();
    }

    public BotCommand peek() {
        return commands.peek();
    }
}