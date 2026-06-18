package com.example.pakailagi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText; // Ini udah diganti jadi EditText biasa
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // Ini udah diganti jadi EditText biasa
    private EditText etEmailOrPhone;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegisterNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        // Nyingkronin ID dari XML ke Java
        etEmailOrPhone = findViewById(R.id.etEmailOrPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterNow = findViewById(R.id.tvRegisterNow);
    }

    private void setupListeners() {
        // Tombol Login diklik
        btnLogin.setOnClickListener(v -> handleLogin());

        // Tombol Register diklik -> pindah ke halaman Register
        if (tvRegisterNow != null) {
            tvRegisterNow.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }
    }

    private void handleLogin() {
        // Ambil teks yang diketik user
        String input = etEmailOrPhone.getText() != null ? etEmailOrPhone.getText().toString().trim() : "";
        String pass = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // Validasi kalau kosong
        if (input.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Tolong isi semua kolom", Toast.LENGTH_SHORT).show();
        } else {
            // --- DUMMY LOGIN LOGIC ---
            // Cek apakah kredensialnya admin & 123
            if (input.equals("admin") && pass.equals("123")) {
                Toast.makeText(this, "Login Sukses!", Toast.LENGTH_SHORT).show();

                // Meluncur ke MainMenu / Homepage
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                // Tutup halaman login ini (biar user kalau pencet tombol 'back' gak balik ke form login)
                finish();
            } else {
                // Kalau salah email/password
                Toast.makeText(this, "Gagal! Coba pakai admin & 123", Toast.LENGTH_LONG).show();
            }
        }
    }
}