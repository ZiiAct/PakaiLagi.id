package com.example.pakailagi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

@SuppressWarnings("all")
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText etEmailOrPhone;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegisterNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_login);

            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            etEmailOrPhone = findViewById(R.id.etEmailOrPhone);
            etPassword = findViewById(R.id.etPassword);
            btnLogin = findViewById(R.id.btnLogin);
            tvRegisterNow = findViewById(R.id.tvRegisterNow);

            // Jika user sudah login sebelumnya, langsung ke MainActivity
            if (mAuth.getCurrentUser() != null) {
                goToMain();
                return;
            }

            if (btnLogin != null) {
                btnLogin.setOnClickListener(v -> performLogin());
            }

            if (tvRegisterNow != null) {
                tvRegisterNow.setOnClickListener(v -> {
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                });
            }

        } catch (Throwable e) {
            ScrollView sv = new ScrollView(this);
            TextView tv = new TextView(this);
            tv.setText("ERROR DI LOGIN ACTIVITY:\n\n" + Log.getStackTraceString(e));
            tv.setTextColor(Color.RED);
            tv.setPadding(40, 40, 40, 40);
            sv.addView(tv);
            setContentView(sv);
        }
    }

    private void performLogin() {
        String input = etEmailOrPhone.getText() != null ? etEmailOrPhone.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (TextUtils.isEmpty(input) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Harap isi username/email dan password!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Jika input berformat email, langsung login dengan email
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            loginWithEmail(input, password);
        } else {
            // Input adalah username, cari email-nya di Realtime Database dulu
            loginWithUsername(input, password);
        }
    }

    private void loginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login Sukses!", Toast.LENGTH_SHORT).show();
                        goToMain();
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthInvalidUserException
                                || e instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, "Password atau Username salah", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Password atau Username salah", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void loginWithUsername(String username, String password) {
        // Cari email berdasarkan username di node "users"
        Query query = mDatabase.child("users").orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Ambil email dari data user yang ditemukan
                    String email = null;
                    for (DataSnapshot userSnap : snapshot.getChildren()) {
                        email = userSnap.child("email").getValue(String.class);
                        break;
                    }
                    if (email != null) {
                        loginWithEmail(email, password);
                    } else {
                        Toast.makeText(LoginActivity.this, "Password atau Username salah", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Password atau Username salah", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Kemungkinan Firebase Database Rules belum punya index untuk "username"
                // Tampilkan pesan yang lebih informatif
                android.util.Log.e("LoginActivity", "Query username dibatalkan: " + error.getMessage()
                        + " (Kode: " + error.getCode() + ")");
                Toast.makeText(LoginActivity.this,
                        "Login gagal: Konfigurasi database belum siap. Coba gunakan email untuk login.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToMain() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}