package com.example.pakailagi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * RegisterActivity: Handles the user registration flow.
 * Ensure the IDs in activity_register.xml match the ones used in initializeViews().
 */
public class RegisterActivity extends AppCompatActivity {

    // Declare UI components
    private EditText etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLoginHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure this layout exists and has the IDs defined below
        setContentView(R.layout.activity_register);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        // Mapping UI components to XML IDs
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginHere = findViewById(R.id.tvLoginHere);
    }

    private void setupListeners() {
        // Register button action
        btnRegister.setOnClickListener(v -> performRegistration());

        // Redirect back to Login
        tvLoginHere.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Logic for backend API will be implemented here
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
    }
}