package ru.sibsutis.bot.core.keyboard;

import com.vk.api.sdk.objects.messages.*;
import java.util.ArrayList;
import java.util.List;

public class KeyboardProvider {

    private static final String COMMAND_FIELD_NAME = "cmd";
    private static final String BACK_COMMAND = "/back";

    public static Keyboard createMainKeyboard() {
        List<List<KeyboardButton>> lines = new ArrayList<>();

        List<KeyboardButton> line1 = new ArrayList<>();
        line1.add(createButton("❤️ Здоровье", "/health_menu", KeyboardButtonColor.NEGATIVE));
        line1.add(createButton("📅 Записи", "/schedule", KeyboardButtonColor.PRIMARY));
        lines.add(line1);

        List<KeyboardButton> line2 = new ArrayList<>();
        line2.add(createButton("🐾 Мои питомцы", "/pets", KeyboardButtonColor.POSITIVE));
        lines.add(line2);

        return new Keyboard().setButtons(lines);
    }

    // ======================== ЗДОРОВЬЕ ========================

    public static Keyboard createHealthMenu() {
        List<List<KeyboardButton>> lines = new ArrayList<>();

        List<KeyboardButton> line1 = new ArrayList<>();
        line1.add(createButton("📊 Показатели", "/health"));
        line1.add(createButton("🔬 Анализ", "/health/analyze"));
        lines.add(line1);

        List<KeyboardButton> line2 = new ArrayList<>();
        line2.add(createButton("🔙 Назад", BACK_COMMAND));
        lines.add(line2);

        return new Keyboard().setButtons(lines);
    }

    public static Keyboard createHealthAnalyzeMenu() {
        List<List<KeyboardButton>> lines = new ArrayList<>();

        List<KeyboardButton> line1 = new ArrayList<>();
        line1.add(createButton("☀️ День", "/health/analyze/day"));
        line1.add(createButton("📆 Неделя", "/health/analyze/week"));
        line1.add(createButton("🌙 Месяц", "/health/analyze/month"));
        lines.add(line1);

        List<KeyboardButton> line2 = new ArrayList<>();
        line2.add(createButton("🔙 Назад", BACK_COMMAND));
        lines.add(line2);

        return new Keyboard().setButtons(lines);
    }

    private static KeyboardButton createButton(String label, String command) {
        return new KeyboardButton()
                .setAction(new KeyboardButtonActionText()
                        .setType(KeyboardButtonActionTextType.TEXT)
                        .setLabel(label)
                        .setPayload(jsonPayload(COMMAND_FIELD_NAME, command)));
    }

    private static KeyboardButton createButton(String label, String command, KeyboardButtonColor color) {
        return new KeyboardButton()
                .setAction(new KeyboardButtonActionText()
                        .setType(KeyboardButtonActionTextType.TEXT)
                        .setLabel(label)
                        .setPayload(jsonPayload(COMMAND_FIELD_NAME, command)))
                .setColor(color);
    }

    private static String jsonPayload(String key, String value) {
        String escaped = value.replace("\\", "\\\\").replace("\"", "\\\"");
        return "{\"" + key + "\":\"" + escaped + "\"}";
    }
}