package ru.sibsutis.bot.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "vk.bot")
public class VkBotConfig {
    private String token;
    private Long groupId;
    private Double apiVersion;
}