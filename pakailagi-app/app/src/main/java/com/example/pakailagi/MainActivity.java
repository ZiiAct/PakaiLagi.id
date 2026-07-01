package com.example.pakailagi;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.pakailagi.model.WishlistItem;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class MainActivity extends AppCompatActivity {

    private final List<WishlistItem> wishlistItems = new ArrayList<>();
    private LinearLayout navHome, navSearch, navGrant, navWishlist, navAccount;
    private ImageView ivHome, ivSearch, ivGrant, ivWishlist, ivAccount;
    private TextView tvHome, tvSearch, tvGrant, tvWishlist, tvAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Coba muat UI Utama
            setContentView(R.layout.activity_main);

            navHome = findViewById(R.id.nav_home_layout);
            navSearch = findViewById(R.id.nav_search_layout);
            navGrant = findViewById(R.id.nav_grant_layout);
            navWishlist = findViewById(R.id.nav_wishlist_layout);
            navAccount = findViewById(R.id.nav_account_layout);

            ivHome = findViewById(R.id.iv_nav_home);
            ivSearch = findViewById(R.id.iv_nav_search);
            ivGrant = findViewById(R.id.iv_nav_grant);
            ivWishlist = findViewById(R.id.iv_nav_wishlist);
            ivAccount = findViewById(R.id.iv_nav_account);

            tvHome = findViewById(R.id.tv_nav_home);
            tvSearch = findViewById(R.id.tv_nav_search);
            tvGrant = findViewById(R.id.tv_nav_grant);
            tvWishlist = findViewById(R.id.tv_nav_wishlist);
            tvAccount = findViewById(R.id.tv_nav_account);

            if (navHome != null) navHome.setOnClickListener(v -> { switchFragment(new HomeFragment()); updateNavUI(0); });
            if (navSearch != null) navSearch.setOnClickListener(v -> { switchFragment(new CariBarangFragment()); updateNavUI(1); });
            if (navGrant != null) navGrant.setOnClickListener(v -> { switchFragment(new HibahFragment()); updateNavUI(2); });
            if (navWishlist != null) navWishlist.setOnClickListener(v -> { switchFragment(new WishlistFragment()); updateNavUI(3); });
            if (navAccount != null) navAccount.setOnClickListener(v -> { switchFragment(new ProfilFragment()); updateNavUI(4); });

            if (savedInstanceState == null) {
                switchFragment(new HomeFragment());
                updateNavUI(0);
            }

        } catch (Throwable e) {
            // JIKA GAGAL, MUNCULIN ERROR MERAH DI LAYAR!
            tampilkanErrorDiLayar("ERROR DI MAIN ACTIVITY:\n\n" + Log.getStackTraceString(e));
        }
    }

    public void switchFragment(Fragment fragment) {
        try {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        } catch (Throwable e) {
            tampilkanErrorDiLayar("ERROR BUKA FRAGMENT:\n\n" + Log.getStackTraceString(e));
        }
    }

    public void showDetailBarang(String name, String location) {
        showDetailBarang(name, location, "Bekas Layak");
    }

    public void showDetailBarang(String name, String location, String condition) {
        try {
            View bottomNav = findViewById(R.id.custom_bottom_nav);
            if (bottomNav != null) bottomNav.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, DetailBarangFragment.newInstance(name, location, condition))
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {}
    }

    public boolean addItemToWishlist(String name, String location, String condition) {
        for (WishlistItem item : wishlistItems) {
            if (item.getName().equals(name) && item.getLocation().equals(location)) {
                return false;
            }
        }
        wishlistItems.add(new WishlistItem(name, location, condition));
        return true;
    }

    public boolean isItemInWishlist(String name, String location) {
        for (WishlistItem item : wishlistItems) {
            if (item.getName().equals(name) && item.getLocation().equals(location)) {
                return true;
            }
        }
        return false;
    }

    public List<WishlistItem> getWishlistItems() {
        return wishlistItems;
    }

    public void removeWishlistItem(int position) {
        if (position >= 0 && position < wishlistItems.size()) {
            wishlistItems.remove(position);
        }
    }

    public void hideDetailBarang() {
        try {
            View bottomNav = findViewById(R.id.custom_bottom_nav);
            if (bottomNav != null) bottomNav.setVisibility(View.VISIBLE);
            getSupportFragmentManager().popBackStack();
        } catch (Exception e) {}
    }

    public void updateNavUI(int activeIndex) {
        try {
            int colorGray = Color.parseColor("#6C757D");
            int colorActiveText = Color.parseColor("#1A7B42");

            if (navHome != null) navHome.setBackground(null);
            if (navSearch != null) navSearch.setBackground(null);
            if (navGrant != null) navGrant.setBackground(null);
            if (navWishlist != null) navWishlist.setBackground(null);
            if (navAccount != null) navAccount.setBackground(null);

            if (ivHome != null) ivHome.setColorFilter(colorGray);
            if (ivSearch != null) ivSearch.setColorFilter(colorGray);
            if (ivGrant != null) ivGrant.setColorFilter(colorGray);
            if (ivWishlist != null) ivWishlist.setColorFilter(colorGray);
            if (ivAccount != null) ivAccount.setColorFilter(colorGray);

            if (tvHome != null) tvHome.setTextColor(colorGray);
            if (tvSearch != null) tvSearch.setTextColor(colorGray);
            if (tvGrant != null) tvGrant.setTextColor(colorGray);
            if (tvAccount != null) tvAccount.setTextColor(colorGray);

            if (tvWishlist != null) {
                tvWishlist.setText("Wishlist");
                tvWishlist.setTextColor(colorGray);
            }

            LinearLayout activeLayout = null;
            ImageView activeIcon = null;
            TextView activeText = null;

            switch (activeIndex) {
                case 0: activeLayout = navHome; activeIcon = ivHome; activeText = tvHome; break;
                case 1: activeLayout = navSearch; activeIcon = ivSearch; activeText = tvSearch; break;
                case 2: activeLayout = navGrant; activeIcon = ivGrant; activeText = tvGrant; break;
                case 3: activeLayout = navWishlist; activeIcon = ivWishlist; activeText = tvWishlist; break;
                case 4: activeLayout = navAccount; activeIcon = ivAccount; activeText = tvAccount; break;
            }

            if (activeLayout != null && activeIcon != null && activeText != null) {
                activeLayout.setBackgroundResource(R.drawable.bg_nav_active);
                activeIcon.setColorFilter(colorActiveText);
                activeText.setTextColor(colorActiveText);
            }
        } catch (Throwable e) {
            // Biarin aja kalau gagal ganti warna
        }
    }

    // FUNGSI BUAT NYETAK ERROR KE LAYAR
    private void tampilkanErrorDiLayar(String pesanError) {
        ScrollView sv = new ScrollView(this);
        TextView tv = new TextView(this);
        tv.setText(pesanError);
        tv.setTextColor(Color.RED);
        tv.setPadding(40, 40, 40, 40);
        sv.addView(tv);
        setContentView(sv);
    }
}