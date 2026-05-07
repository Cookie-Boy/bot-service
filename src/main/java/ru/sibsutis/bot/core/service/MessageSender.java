package ru.sibsutis.bot.core.service;

public interface MessageSender {
    boolean send(Long userId, String text);
}