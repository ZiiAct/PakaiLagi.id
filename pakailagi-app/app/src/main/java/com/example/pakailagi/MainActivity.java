package com.example.pakailagi;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private Fragment activeFragment;
    private HomeFragment homeFragment;
    private WishlistFragment wishlistFragment;
    private RiwayatPengajuanFragment riwayatFragment;
    private ProfilFragment profilFragment;
    private CariBarangFragment cariBarangFragment;
    private HibahFragment hibahFragment;
    private DetailBarangFragment detailBarangFragment;
    private Fragment previousDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupFragments();
        setupBottomNavigation();
    }

    private void setupFragments() {
        homeFragment       = new HomeFragment();
        wishlistFragment   = new WishlistFragment();
        riwayatFragment    = new RiwayatPengajuanFragment();
        profilFragment     = new ProfilFragment();
        cariBarangFragment = new CariBarangFragment();
        hibahFragment      = new HibahFragment();
        activeFragment     = homeFragment;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, profilFragment,       "profil").hide(profilFragment)
                .add(R.id.fragment_container, riwayatFragment,      "riwayat").hide(riwayatFragment)
                .add(R.id.fragment_container, hibahFragment,        "hibah").hide(hibahFragment)
                .add(R.id.fragment_container, wishlistFragment,     "wishlist").hide(wishlistFragment)
                .add(R.id.fragment_container, cariBarangFragment,   "cari").hide(cariBarangFragment)
                .add(R.id.fragment_container, homeFragment,         "home")
                .commit();
    }

    private void setupBottomNavigation() {
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                switchFragment(homeFragment);
                return true;
            } else if (id == R.id.nav_wishlist) {
                switchFragment(wishlistFragment);
                return true;
            } else if (id == R.id.nav_riwayat) {
                switchFragment(riwayatFragment);
                return true;
            } else if (id == R.id.nav_profil) {
                switchFragment(profilFragment);
                return true;
            } else if (id == R.id.nav_grant) {
                switchFragment(hibahFragment);
                return true;
            }
            return false;
        });
    }

    public void navigateTo(int navItemId) {
        bottomNav.setSelectedItemId(navItemId);
    }

    public void showDetailBarang(String name, String location) {
        if (detailBarangFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(detailBarangFragment).commitNow();
        }
        previousDetailFragment = activeFragment;
        detailBarangFragment = DetailBarangFragment.newInstance(name, location);
        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment)
                .add(R.id.fragment_container, detailBarangFragment, "detail")
                .commit();
        activeFragment = detailBarangFragment;
    }

    public void hideDetailBarang() {
        if (detailBarangFragment != null && previousDetailFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(detailBarangFragment)
                    .show(previousDetailFragment)
                    .commit();
            activeFragment = previousDetailFragment;
            getWindow().setStatusBarColor(getColor(R.color.cari_header));
            detailBarangFragment = null;
            previousDetailFragment = null;
        }
    }

    public void showCariBarang() {
        if (activeFragment != cariBarangFragment) {
            getSupportFragmentManager().beginTransaction()
                    .hide(activeFragment)
                    .show(cariBarangFragment)
                    .commit();
            activeFragment = cariBarangFragment;
        }
    }

    private void switchFragment(Fragment target) {
        if (activeFragment != target) {
            getSupportFragmentManager().beginTransaction()
                    .hide(activeFragment)
                    .show(target)
                    .commit();
            activeFragment = target;
        }
    }
}
