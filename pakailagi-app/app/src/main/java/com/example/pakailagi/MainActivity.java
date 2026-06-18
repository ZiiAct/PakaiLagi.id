package com.example.pakailagi;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Button btnDonateNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        btnDonateNow = findViewById(R.id.btnDonateNow);
    }

    private void setupListeners() {
        // Statement lambda udah diganti jadi expression lambda (tanpa kurung kurawal)
        btnDonateNow.setOnClickListener(v -> Toast.makeText(MainActivity.this, "Fitur Donasi segera hadir!", Toast.LENGTH_SHORT).show());

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_search) {
                Toast.makeText(this, "Ke Halaman Cari", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_grant) {
                Toast.makeText(this, "Ke Halaman Hibah", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_wishlist) {
                Toast.makeText(this, "Ke Halaman Wishlist", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_account) {
                Toast.makeText(this, "Ke Halaman Akun", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }
}