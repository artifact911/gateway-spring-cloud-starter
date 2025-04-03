package com.artifact.repository;

import com.artifact.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    // Найти по username (благодаря @Indexed)
    List<RefreshToken> findByUsername(String username);

    // Удалить по токену
    void deleteById(String token);

    // Проверка существования
    boolean existsById(String token);
}
