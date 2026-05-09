package ru.sibsutis.bot.core.service;

import com.vk.api.sdk.objects.messages.Keyboard;

public interface MessageSender {
    boolean send(Long userId, String text);
    boolean send(Long userId, Keyboard keyboard);
    boolean send(Long userId, String text, Keyboard keyboard);
}