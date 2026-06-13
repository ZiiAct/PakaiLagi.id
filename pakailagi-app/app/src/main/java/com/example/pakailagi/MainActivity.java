package com.example.pakailagi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pakailagi.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup views
        binding.appNameText.setText("PakaiLagi");
        binding.appSloganText.setText("Solusi Re-Use Barang & Keberlanjutan Lingkungan");

        // Action button click listener
        binding.actionButton.setOnClickListener(v -> {
            binding.statusText.setText("Firebase & App System Ready!");
        });
    }
}
