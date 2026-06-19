package com.example.pakailagi;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declare UI elements
    private Button btnDonateNow;
    private LinearLayout navHome, navSearch, navGrant, navWishlist, navAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupListeners();

        // Atur Beranda supaya otomatis aktif saat pertama kali dibuka
        handleNavClick(navHome);
    }

    private void initializeViews() {
        // Bind IDs from activity_main.xml to Java
        btnDonateNow = findViewById(R.id.btnDonateNow);
        navHome = findViewById(R.id.nav_home_layout);
        navSearch = findViewById(R.id.nav_search_layout);
        navGrant = findViewById(R.id.nav_grant_layout);
        navWishlist = findViewById(R.id.nav_wishlist_layout);
        navAccount = findViewById(R.id.nav_account_layout);
    }

    private void setupListeners() {
        // Donate button action
        btnDonateNow.setOnClickListener(v ->
                Toast.makeText(this, "Donation feature coming soon!", Toast.LENGTH_SHORT).show()
        );

        // Set click listeners for Bottom Navigation items
        navHome.setOnClickListener(v -> handleNavClick(navHome));
        navSearch.setOnClickListener(v -> handleNavClick(navSearch));
        navGrant.setOnClickListener(v -> handleNavClick(navGrant));
        navWishlist.setOnClickListener(v -> handleNavClick(navWishlist));
        navAccount.setOnClickListener(v -> handleNavClick(navAccount));
    }

    private void handleNavClick(LinearLayout selectedLayout) {
        // Array semua menu navbar untuk di-reset warnanya
        final LinearLayout[] allNavs = {navHome, navSearch, navGrant, navWishlist, navAccount};

        for (LinearLayout nav : allNavs) {
            ImageView iv = (ImageView) nav.getChildAt(0);
            TextView tv = (TextView) nav.getChildAt(1);

            // Hilangkan background hijau (jadi transparan)
            nav.setBackgroundResource(0);

            // Reset ikon dan teks kembali ke warna abu-abu standar
            iv.setColorFilter(Color.parseColor("#6C757D"));
            tv.setTextColor(Color.parseColor("#6C757D"));
            tv.setTypeface(null, Typeface.NORMAL);
        }

        // Terapkan warna hijau ke menu yang sedang di-klik
        ImageView selectedIv = (ImageView) selectedLayout.getChildAt(0);
        TextView selectedTv = (TextView) selectedLayout.getChildAt(1);

        // Pasang file bg_nav_active (pill shape hijau) ke background tombol
        selectedLayout.setBackgroundResource(R.drawable.bg_nav_active);

        // Ubah warna ikon dan teks jadi gelap (hitam) biar kontras dengan background hijau
        selectedIv.setColorFilter(Color.parseColor("#212529"));
        selectedTv.setTextColor(Color.parseColor("#212529"));
        selectedTv.setTypeface(null, Typeface.BOLD);
    }
}