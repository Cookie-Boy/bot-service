package ru.sibsutis.bot.core.command;

import ru.sibsutis.bot.core.model.VkMessage;

public interface BotCommand {
    String getCommandName();
    void execute(VkMessage message);

    default boolean isStackable() {
        return true;
    }
}