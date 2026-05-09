package ru.sibsutis.bot.core.keyboard;

import com.vk.api.sdk.objects.messages.*;

import java.util.ArrayList;
import java.util.List;

public class KeyboardProvider {

    private static final String BACK_COMMAND = "/back";
    private static final String MAIN_MENU_COMMAND = "/main_menu";

    public static Keyboard createMainKeyboard() {
        List<List<KeyboardButton>> lines = new ArrayList<>();

        List<KeyboardButton> line1 = new ArrayList<>();
        line1.add(createButton("Здоровье", "/health_menu"));
        line1.add(createButton("Питомцы", "/pets_menu"));
        lines.add(line1);

        List<KeyboardButton> line2 = new ArrayList<>();
        line2.add(createButton("Записи", "/schedule_menu"));
        line2.add(createButton("Врачи", "/doctors"));
        lines.add(line2);

        return new Keyboard().setButtons(lines);
    }

    // ======================== ЗДОРОВЬЕ ========================

    public static Keyboard createHealthMenu() {
        List<List<KeyboardButton>> lines = new ArrayList<>();

        List<KeyboardButton> line1 = new ArrayList<>();
        line1.add(createButton("Показатели", "/health"));
        line1.add(createButton("Анализ", "/health_analyze_menu"));
        lines.add(line1);

        List<KeyboardButton> line2 = new ArrayList<>();
        line2.add(createButton("Назад", BACK_COMMAND));
        lines.add(line2);

        return new Keyboard().setButtons(lines);
    }

    public static Keyboard createHealthAnalyzeMenu() {
        List<List<KeyboardButton>> lines = new ArrayList<>();

        List<KeyboardButton> line1 = new ArrayList<>();
        line1.add(createButton("День", "/health/analyze/day"));
        line1.add(createButton("Неделя", "/health/analyze/week"));
        line1.add(createButton("Месяц", "/health/analyze/month"));
        lines.add(line1);

        List<KeyboardButton> line2 = new ArrayList<>();
        line2.add(createButton("Назад", BACK_COMMAND));
        lines.add(line2);

        return new Keyboard().setButtons(lines);
    }

    // ======================== ЗАПИСИ ========================

    public static Keyboard createScheduleMenu() {
        List<List<KeyboardButton>> lines = new ArrayList<>();

        List<KeyboardButton> line1 = new ArrayList<>();
        line1.add(createButton("Ближайшие", "/schedule"));
        line1.add(createButton("Записаться", "/book"));
        lines.add(line1);

        List<KeyboardButton> line2 = new ArrayList<>();
        line2.add(createButton("Отменить", "/cancel"));
        lines.add(line2);

        List<KeyboardButton> line3 = new ArrayList<>();
        line3.add(createButton("Назад", BACK_COMMAND));
        lines.add(line3);

        return new Keyboard().setButtons(lines);
    }

    public static Keyboard createCancelAppointmentMenu(List<String> appointments) {
        List<List<KeyboardButton>> lines = new ArrayList<>();

        for (String appointment : appointments) {
            String payload = "/cancel " + appointment;
            List<KeyboardButton> line = new ArrayList<>();
            line.add(createButton(appointment, payload));
            lines.add(line);
        }

        List<KeyboardButton> backLine = new ArrayList<>();
        backLine.add(createButton("Назад", BACK_COMMAND));
        lines.add(backLine);

        return new Keyboard().setButtons(lines);
    }

    // ======================== ПИТОМЦЫ ========================

    public static Keyboard createPetsMenu() {
        List<List<KeyboardButton>> lines = new ArrayList<>();

        List<KeyboardButton> line1 = new ArrayList<>();
        line1.add(createButton("Список", "/pets"));
        line1.add(createButton("Добавить", "/pets/add"));
        lines.add(line1);

        List<KeyboardButton> line2 = new ArrayList<>();
        line2.add(createButton("Удалить", "/pets/delete_menu"));
        lines.add(line2);

        List<KeyboardButton> line3 = new ArrayList<>();
        line3.add(createButton("Назад", BACK_COMMAND));
        lines.add(line3);

        return new Keyboard().setButtons(lines);
    }

    public static Keyboard createDeletePetMenu(List<String> pets) {
        List<List<KeyboardButton>> lines = new ArrayList<>();

        for (String pet : pets) {
            String payload = "/pets/delete " + pet;
            List<KeyboardButton> line = new ArrayList<>();
            line.add(createButton(pet, payload));
            lines.add(line);
        }

        List<KeyboardButton> backLine = new ArrayList<>();
        backLine.add(createButton("Назад", BACK_COMMAND));
        lines.add(backLine);

        return new Keyboard().setButtons(lines);
    }

    // ======================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ========================

    private static KeyboardButton createButton(String label, String command) {
        return new KeyboardButton()
                .setAction(new KeyboardButtonActionText()
                        .setType(KeyboardButtonActionTextType.TEXT)
                        .setLabel(label)
                        .setPayload(jsonPayload("cmd", command)));
    }

    public static Keyboard createBackToMainMenu() {
        List<List<KeyboardButton>> lines = new ArrayList<>();
        List<KeyboardButton> line = new ArrayList<>();
        line.add(createButton("Главное меню", MAIN_MENU_COMMAND));
        lines.add(line);
        return new Keyboard().setButtons(lines);
    }

    private static String jsonPayload(String key, String value) {
        String escaped = value.replace("\\", "\\\\").replace("\"", "\\\"");
        return "{\"" + key + "\":\"" + escaped + "\"}";
    }
}