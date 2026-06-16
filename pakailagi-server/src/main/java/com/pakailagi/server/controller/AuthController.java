package com.pakailagi.server.controller;

import com.pakailagi.server.entity.User;
import com.pakailagi.server.repository.UserRepository;
import com.pakailagi.server.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody User loginRequest) throws Exception {
        try {
            // Cek apakah username dan password cocok dengan di database
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (Exception ex) {
            throw new Exception("Username atau Password salah!");
        }

        // Jika cocok, cari user tersebut untuk mengambil rolenya
        User user = userRepository.findByUsername(loginRequest.getUsername()).get();
        
        // Buatkan tiket masuk (Token JWT)
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // Kembalikan token ke pengguna dalam format JSON
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
}