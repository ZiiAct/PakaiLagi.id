package com.example.pakailagi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etUsername, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLoginHere;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginHere = findViewById(R.id.tvLoginHere);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> performRegistration());
        tvLoginHere.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String pass = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        // Validasi: semua field wajib diisi
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(username)
                || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Tolong isi semua data!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "Password minimal 6 karakter!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cek apakah username sudah dipakai
        // Catatan: Jika Firebase Database Rules belum punya index untuk "username",
        // onCancelled akan dipanggil. Dalam kasus itu, kita tetap lanjut buat akun.
        mDatabase.child("users").orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(RegisterActivity.this,
                                    "Username sudah digunakan, coba yang lain!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Username aman, lanjut daftar ke Firebase Auth
                            createFirebaseUser(fullName, username, email, pass);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Database rules belum punya index untuk username,
                        // tetap lanjut buat akun (skip cek duplikat username)
                        Log.w("RegisterActivity", "Username check cancelled: " + error.getMessage() +
                                ". Melanjutkan registrasi tanpa cek duplikat username.");
                        createFirebaseUser(fullName, username, email, pass);
                    }
                });
    }

    private void createFirebaseUser(String fullName, String username, String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Simpan data tambahan ke Realtime Database
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("fullName", fullName);
                            userData.put("username", username);
                            userData.put("email", email);
                            userData.put("uid", user.getUid());
                            userData.put("role", "user");

                            mDatabase.child("users").child(user.getUid()).setValue(userData)
                                    .addOnCompleteListener(dbTask -> {
                                        // Logout dulu agar user login manual
                                        mAuth.signOut();

                                        Toast.makeText(RegisterActivity.this,
                                                "Registrasi berhasil! Silakan login.", Toast.LENGTH_LONG).show();

                                        // Balik ke halaman login
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        finish();
                                    });
                        }
                    } else {
                        String errorMsg = "Registrasi gagal.";
                        if (task.getException() != null) {
                            String msg = task.getException().getMessage();
                            if (msg != null && msg.contains("email address is already in use")) {
                                errorMsg = "Email sudah terdaftar, gunakan email lain.";
                            } else if (msg != null && msg.contains("badly formatted")) {
                                errorMsg = "Format email tidak valid.";
                            }
                        }
                        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}