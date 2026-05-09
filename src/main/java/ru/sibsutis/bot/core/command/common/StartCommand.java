package ru.sibsutis.bot.core.command.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sibsutis.bot.core.command.BotCommand;
import ru.sibsutis.bot.core.keyboard.KeyboardProvider;
import ru.sibsutis.bot.core.model.VkMessage;
import ru.sibsutis.bot.core.service.MessageSender;
import ru.sibsutis.bot.core.service.VkAuthService;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartCommand implements BotCommand {

    private final MessageSender sender;
    private final VkAuthService vkAuthService;

    @Override
    public String getCommandName() {
        return "/start";
    }

    @Override
    public boolean isStackable() {
        return false;
    }

    @Override
    public void execute(VkMessage message) {
        Long vkId = message.getUserId();
        log.info("Start command from vkId={}", vkId);

        if (vkAuthService.isLinked(vkId)) {
            sender.send(vkId, """
                    ✅ Ваш аккаунт уже привязан к платформе.
                    Доступные команды:
                    /schedule – показать все записи
                    /book – создать новую запись""",
                    KeyboardProvider.createMainKeyboard());
        } else {
            String link = vkAuthService.generateLinkToken(vkId);
            sender.send(vkId, """
                    Привет! Для продолжения нужно связать ваш VK аккаунт с личным кабинетом.
                    Перейдите по ссылке и авторизуйтесь на сайте:
                    %s

                    Ссылка действительна 5 минут.""".formatted(link));
        }
    }
}