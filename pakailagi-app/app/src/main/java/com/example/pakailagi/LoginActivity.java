package com.example.pakailagi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

@SuppressWarnings("all")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_login);

            Button btnLogin = findViewById(R.id.btnLogin);
            if (btnLogin != null) {
                btnLogin.setOnClickListener(v -> {
                    Toast.makeText(this, "Login Sukses!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                });
            }

            TextView tvRegisterNow = findViewById(R.id.tvRegisterNow);
            if (tvRegisterNow != null) {
                tvRegisterNow.setOnClickListener(v -> Toast.makeText(this, "Menu Daftar Belum Tersedia", Toast.LENGTH_SHORT).show());
            }

        } catch (Throwable e) {
            // JIKA XML LOGIN BERMASALAH, CETAK MERAH DI LAYAR!
            ScrollView sv = new ScrollView(this);
            TextView tv = new TextView(this);
            tv.setText("ERROR DI LOGIN ACTIVITY:\n\n" + Log.getStackTraceString(e));
            tv.setTextColor(Color.RED);
            tv.setPadding(40, 40, 40, 40);
            sv.addView(tv);
            setContentView(sv);
        }
    }
}