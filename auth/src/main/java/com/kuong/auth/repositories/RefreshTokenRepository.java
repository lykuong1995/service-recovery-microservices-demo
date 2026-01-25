package com.kuong.auth.repositories;

import com.kuong.auth.entities.RefreshToken;
import com.kuong.auth.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
