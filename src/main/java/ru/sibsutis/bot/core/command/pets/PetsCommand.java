package ru.sibsutis.bot.core.command.pets;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.api.client.ExternalGateway;
import ru.sibsutis.bot.api.dto.PetDto;
import ru.sibsutis.bot.core.annotation.NonStackable;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;

import java.util.List;

@Component
@NonStackable
@RequiredArgsConstructor
public class PetsCommand implements BotCommand {

    private final MessageSender sender;
    private final ExternalGateway externalGateway;

    @Override
    public String getCommandName() {
        return "/pets";
    }

    @Override
    public void execute(VkMessage message) {
        List<PetDto> pets = externalGateway.getPetsByVkUserId(message.getUserId());

        if (pets.isEmpty()) {
            sender.send(message.getUserId(), "🐾 У вас пока нет зарегистрированных питомцев.");
            return;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < pets.size(); i++) {
            PetDto pet = pets.get(i);
            formatPetInfo(sb, pet, i + 1);
            if (i < pets.size() - 1) {
                sb.append("\n━━━━━━━━━━━━━━━━━━━━━\n\n");
            }
        }

        sender.send(message.getUserId(), sb.toString());
    }

    private void formatPetInfo(StringBuilder sb, PetDto pet, int index) {
        // Эмодзи в зависимости от вида
        String speciesEmoji = getSpeciesEmoji(pet.getSpecies());
        sb.append(String.format("%d. %s %s\n", index, speciesEmoji, pet.getName()));

        if (pet.getBreed() != null && !pet.getBreed().isEmpty()) {
            sb.append(String.format("   🧬 Порода: %s\n", pet.getBreed()));
        }

        if (pet.getSpecies() != null && !pet.getSpecies().isEmpty()) {
            sb.append(String.format("   📌 Вид: %s\n", pet.getSpecies()));
        }

        if (pet.getAge() != null) {
            sb.append(String.format("   🎂 Возраст: %d %s\n", pet.getAge(), getAgeSuffix(pet.getAge())));
        }

        if (pet.getChipNumber() != null && !pet.getChipNumber().isEmpty()) {
            sb.append(String.format("   📟 Чип: %s\n", pet.getChipNumber()));
        }

        // Информация об ошейнике
        if (pet.getCollar() != null) {
            boolean isActive = Boolean.TRUE.equals(pet.getCollar().getActive());
            String collarStatus = isActive ? "🟢 Активен" : "🔴 Неактивен";
            sb.append("   📿 Ошейник: ").append(collarStatus).append("\n");

            if (isActive && pet.getCollar().getHomeInfo() != null) {
                var homeInfo = pet.getCollar().getHomeInfo();
                if (homeInfo.getRadius() != null && homeInfo.getRadius() > 0) {
                    sb.append(String.format("      • Радиус безопасности: %d м\n", homeInfo.getRadius()));
                }
                if (Boolean.TRUE.equals(homeInfo.getAlerting())) {
                    sb.append("      • Уведомления при выходе за радиус: Вкл\n");
                }
            }
        }

        // Медицинская информация (кратко)
        if (pet.getMedicalRecord() != null) {
            var medical = pet.getMedicalRecord();

            int vacCount = medical.getVaccinations() != null ? medical.getVaccinations().size() : 0;
            if (vacCount > 0) {
                sb.append(String.format("   💉 Вакцинаций: %d\n", vacCount));
            }

            boolean hasAllergies = medical.getAllergies() != null && !medical.getAllergies().isEmpty();
            boolean hasChronic = medical.getChronicDiseases() != null && !medical.getChronicDiseases().isEmpty();

            if (hasAllergies || hasChronic) {
                sb.append("   ⚠️ Особенности: ");
                if (hasAllergies) sb.append("аллергии ");
                if (hasChronic) sb.append("хронические болезни ");
                sb.append("\n");
            }
        }
    }

    private String getSpeciesEmoji(String species) {
        if (species == null) return "🐾";
        String lower = species.toLowerCase();
        if (lower.contains("собак") || lower.contains("dog")) return "🐶";
        if (lower.contains("кош") || lower.contains("cat")) return "🐱";
        return "🐾";
    }

    private String getAgeSuffix(int age) {
        int lastDigit = age % 10;
        int lastTwoDigits = age % 100;
        if (lastTwoDigits >= 11 && lastTwoDigits <= 14) return "лет";
        if (lastDigit == 1) return "год";
        if (lastDigit >= 2 && lastDigit <= 4) return "года";
        return "лет";
    }
}
