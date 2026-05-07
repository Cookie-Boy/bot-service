package ru.sibsutis.bot.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlobalExceptionHandler {

    public void handle(Throwable e, String context) {
        log.error("Error in {}: {}", context, e.getMessage(), e);
    }
}