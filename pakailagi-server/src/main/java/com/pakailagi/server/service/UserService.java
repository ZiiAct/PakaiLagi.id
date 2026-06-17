package com.pakailagi.server.service;

import com.pakailagi.server.entity.User;
import com.pakailagi.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User registerUser(User user) {
        // Menetapkan *role* dan status bawaan secara otomatis saat *user* baru mendaftar
        user.setRole("USER");
        user.setAccountStatus("ACTIVE");
        return userRepository.save(user);
    }
}