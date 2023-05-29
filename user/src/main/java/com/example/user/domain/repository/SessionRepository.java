package com.example.user.domain.repository;

import com.example.user.domain.entity.Session;
import com.example.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    boolean existsBySessionToken(String refreshToken);

    Session findBySessionToken(String refreshToken);

    boolean existsByUserId(Integer id);

    Session findByUserId(Integer id);

}
