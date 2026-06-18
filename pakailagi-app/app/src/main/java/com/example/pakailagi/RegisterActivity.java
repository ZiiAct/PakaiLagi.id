package com.example.pakailagi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    // Deklarasi 4 komponen input
    private EditText etFullName, etUsername, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLoginHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        // Nyingkronin ID dari XML
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginHere = findViewById(R.id.tvLoginHere);
    }

    private void setupListeners() {
        // Aksi pas tombol Register diklik
        btnRegister.setOnClickListener(v -> performRegistration());

        // Aksi pas teks "Login Here" diklik (tutup halaman ini biar balik ke login)
        tvLoginHere.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        // Ambil semua teks yang diketik
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String pass = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // Validasi: Pastikan nggak ada kotak yang kosong
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Tolong isi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kalau data komplit, tampilkan pesan sukses (sebagai dummy backend)
        Toast.makeText(this, "Registrasi sukses! Halo " + username, Toast.LENGTH_SHORT).show();

        // Opsional: Langsung tutup halaman register setelah sukses supaya otomatis balik ke halaman Login
        finish();
    }
}