package com.artifact.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;

// Сущность RefreshToken - аналог @Entity
@RedisHash(value = "RefreshToken", timeToLive = 2592000) // TTL 30 дней в секундах
@Data
public class RefreshToken {

    @Id
    private String token;

    @Indexed // Для поиска по полю
    private String username;

    private Instant expiryDate;
}
