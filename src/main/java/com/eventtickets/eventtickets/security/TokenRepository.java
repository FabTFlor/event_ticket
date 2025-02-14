package com.eventtickets.eventtickets.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t FROM Token t WHERE t.user.id = :id AND (t.isExpired = FALSE OR t.isRevoked = FALSE)")
    List<Token> findAllValidTokenByUser(Long id);
    

  Optional<Token> findByToken(String token);
}