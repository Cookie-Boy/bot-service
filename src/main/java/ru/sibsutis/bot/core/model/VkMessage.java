package ru.sibsutis.bot.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VkMessage {
    private Long userId;
    private String text;
}
