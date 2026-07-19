package com.realestate.backend.repositories;

import com.realestate.backend.entities.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetPasswordTokenRepository
        extends JpaRepository<ResetPasswordToken, Long> {

    Optional<ResetPasswordToken> findByToken(String token);

    boolean existsByUserIdAndUsedFalse(Long userId);

}