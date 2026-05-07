package ru.sibsutis.bot.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import ru.sibsutis.bot.api.client.ExternalGateway;
import ru.sibsutis.bot.api.dto.OwnerDto;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class VkAuthService {

    private final StringRedisTemplate redisTemplate;
    private final ExternalGateway externalGateway;

    @Value("${vk.bot.link-vk-url}")
    private String linkVkUrl;

    public boolean isLinked(Long vkUserId) {
        String cacheKey = "vk:" + vkUserId;
        String cachedOwnerId = redisTemplate.opsForValue().get(cacheKey);
        if (cachedOwnerId != null) {
            return true;
        }

        OwnerDto owner;
        try {
            owner = externalGateway.getOwnerByVkUserId(vkUserId);
        } catch (HttpClientErrorException e) {
            return false;
        }

        redisTemplate.opsForValue().set(cacheKey, owner.getId(), 10, TimeUnit.MINUTES);
        return true;
    }

    public String generateLinkToken(Long vkUserId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("link-token:" + token, vkUserId.toString(), 5, TimeUnit.MINUTES);
        return linkVkUrl + "?token=" + token;
    }

    public Optional<Long> validateToken(String token) {
        String vkIdStr = redisTemplate.opsForValue().get("link-token:" + token);
        if (vkIdStr != null) {
            redisTemplate.delete("link-token:" + token); // одноразовое использование
            return Optional.of(Long.valueOf(vkIdStr));
        }
        return Optional.empty();
    }

    public void evictCache(Long vkId) {
        redisTemplate.delete("vk:" + vkId);
    }
}