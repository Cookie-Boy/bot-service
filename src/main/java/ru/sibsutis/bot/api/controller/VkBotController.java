package ru.sibsutis.bot.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.sibsutis.bot.core.service.VkBotService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VkBotController {

    private final VkBotService vkBotService;

    @PostMapping("/notify/{vkUserId}")
    public ResponseEntity<?> notifyUser(@PathVariable String vkUserId, @RequestBody String text) {
        boolean result = vkBotService.sendMessage(Long.parseLong(vkUserId), text);
        return result ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().build();
    }
}
