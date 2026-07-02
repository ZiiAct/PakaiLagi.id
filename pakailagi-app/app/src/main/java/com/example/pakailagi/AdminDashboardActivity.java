package com.example.pakailagi;

import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pakailagi.R;
import com.example.pakailagi.fragment.AdminHomeFragment;
import com.example.pakailagi.fragment.AdminInventoryFragment;
import com.example.pakailagi.fragment.AdminShopFragment;
import com.example.pakailagi.fragment.AdminProfileFragment;

public class AdminDashboardActivity extends AppCompatActivity {

    private LinearLayout navHome, navInventory, navShop, navProfile;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize navbar items
        navHome = findViewById(R.id.nav_home);
        navInventory = findViewById(R.id.nav_inventory);
        navShop = findViewById(R.id.nav_shop);
        navProfile = findViewById(R.id.nav_profile);

        fragmentManager = getSupportFragmentManager();

        // Load Home Fragment by default
        if (savedInstanceState == null) {
            loadFragment(new AdminHomeFragment(), "home");
            updateNavbarState(0);
        }

        setupNavbarListeners();
    }

    private void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment, tag);
        transaction.commit();
    }

    private void setupNavbarListeners() {
        navHome.setOnClickListener(v -> {
            loadFragment(new AdminHomeFragment(), "home");
            updateNavbarState(0);
        });

        navInventory.setOnClickListener(v -> {
            loadFragment(new AdminInventoryFragment(), "inventory");
            updateNavbarState(1);
        });

        // Shop nav item is hidden (visibility="gone" in layout); listener kept for when it's re-enabled
        navShop.setOnClickListener(v -> {
            loadFragment(new AdminShopFragment(), "shop");
            updateNavbarState(2);
        });

        navProfile.setOnClickListener(v -> {
            loadFragment(new AdminProfileFragment(), "profile");
            updateNavbarState(3);
        });
    }

    private void updateNavbarState(int activeIndex) {
        // Reset all navbar items to inactive state
        resetNavbarItems();

        // Update the active item
        switch (activeIndex) {
            case 0:
                setNavbarItemActive(navHome);
                break;
            case 1:
                setNavbarItemActive(navInventory);
                break;
            case 2:
                setNavbarItemActive(navShop);
                break;
            case 3:
                setNavbarItemActive(navProfile);
                break;
        }
    }

    private void resetNavbarItems() {
        setNavbarItemInactive(navHome);
        setNavbarItemInactive(navInventory);
        setNavbarItemInactive(navShop);
        setNavbarItemInactive(navProfile);
    }

    private void setNavbarItemActive(LinearLayout navItem) {
        // Find the inner LinearLayout (the one with background)
        LinearLayout innerLayout = (LinearLayout) navItem.getChildAt(0);
        if (innerLayout != null) {
            innerLayout.setBackgroundResource(R.drawable.bg_admin_nav_active);
            // Update text and icon colors to white
            for (int i = 0; i < innerLayout.getChildCount(); i++) {
                if (innerLayout.getChildAt(i) instanceof android.widget.ImageView) {
                    android.widget.ImageView iv = (android.widget.ImageView) innerLayout.getChildAt(i);
                    iv.setColorFilter(getResources().getColor(android.R.color.white));
                } else if (innerLayout.getChildAt(i) instanceof android.widget.TextView) {
                    android.widget.TextView tv = (android.widget.TextView) innerLayout.getChildAt(i);
                    tv.setTextColor(getResources().getColor(android.R.color.white));
                }
            }
        } else {
            // Direct styling if structure is different
            navItem.setBackgroundResource(R.drawable.bg_admin_nav_active);
        }
    }

    private void setNavbarItemInactive(LinearLayout navItem) {
        // Find the inner LinearLayout
        LinearLayout innerLayout = (LinearLayout) navItem.getChildAt(0);
        if (innerLayout != null) {
            innerLayout.setBackground(null);
            // Update text and icon colors to gray
            for (int i = 0; i < innerLayout.getChildCount(); i++) {
                if (innerLayout.getChildAt(i) instanceof android.widget.ImageView) {
                    android.widget.ImageView iv = (android.widget.ImageView) innerLayout.getChildAt(i);
                    iv.setColorFilter(getResources().getColor(R.color.admin_nav_inactive));
                } else if (innerLayout.getChildAt(i) instanceof android.widget.TextView) {
                    android.widget.TextView tv = (android.widget.TextView) innerLayout.getChildAt(i);
                    tv.setTextColor(getResources().getColor(R.color.admin_nav_inactive));
                }
            }
        } else {
            navItem.setBackground(null);
        }
    }
}
