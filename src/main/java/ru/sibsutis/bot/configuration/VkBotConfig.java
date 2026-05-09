package ru.sibsutis.bot.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "vk.bot")
public class VkBotConfig {
    private String token;
    private Long groupId;
    private String apiVersion;

    @Bean
    public Map<String, String> userStorage() {
        return new ConcurrentHashMap<>();
    }
}