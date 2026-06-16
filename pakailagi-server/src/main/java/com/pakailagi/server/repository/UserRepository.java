package com.pakailagi.server.repository;

import com.pakailagi.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Fungsi baru untuk mencari user berdasarkan username saat login
    Optional<User> findByUsername(String username);
}