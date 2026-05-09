package ru.sibsutis.bot.core.command;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
@Component
public class CommandStack {

    private static final String MAIN_MENU_COMMAND = "/main_menu";
    private final Deque<String> commands = new ConcurrentLinkedDeque<>();

    @PostConstruct
    private void init() {
        commands.push(MAIN_MENU_COMMAND);
        log.info("CommandStack initialized with /main_menu");
    }

    public void push(String command) {
        if (MAIN_MENU_COMMAND.equals(command)) {
            log.warn("Attempt to push MAIN_MENU_COMMAND ignored");
            return;
        }

        commands.push(command);
        log.debug("Command pushed: {}. Stack size: {}", command, commands.size());
    }

    public void pop() {
        String topCommand = commands.peek();

        if (topCommand == null) {
            log.error("Stack is empty, restoring main menu");
            commands.push(MAIN_MENU_COMMAND);
            return;
        }

        if (MAIN_MENU_COMMAND.equals(topCommand)) {
            log.debug("Cannot pop main menu command");
            return;
        }

        String removed = commands.pop();
        log.debug("Command popped: {}. Stack size: {}", removed, commands.size());

    }

    public String peek() {
        String top = commands.peek();
        return top != null ? top : MAIN_MENU_COMMAND;
    }

    public int size() {
        return commands.size();
    }

    public void clearAllExceptMainMenu() {
        commands.clear();
        commands.push(MAIN_MENU_COMMAND);
        log.info("Stack cleared, only main menu remains");
    }

    public List<String> getAllCommands() {
        return new ArrayList<>(commands);
    }
}
