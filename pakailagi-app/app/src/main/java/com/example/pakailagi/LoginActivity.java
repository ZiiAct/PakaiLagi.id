package com.example.pakailagi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * LoginActivity: Handles user authentication UI and logic.
 */
public class LoginActivity extends AppCompatActivity {

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
        etEmailOrPhone = findViewById(R.id.etEmailOrPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterNow = findViewById(R.id.tvRegisterNow);
    }

    private void setupListeners() {
        // Handle Login button click
        btnLogin.setOnClickListener(v -> handleLogin());

        // Handle Register click: Navigate to RegisterActivity
        tvRegisterNow.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String input = etEmailOrPhone.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (input.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Placeholder for Auth Logic
            Toast.makeText(this, "Logging in as: " + input, Toast.LENGTH_SHORT).show();
        }
    }
}