package com.example.pakailagi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

@SuppressWarnings("all")
public class ProfilFragment extends Fragment {

    public ProfilFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View btnBack = view.findViewById(R.id.btnBackProfil);
        if (btnBack != null) btnBack.setOnClickListener(v -> simulateNav(R.id.nav_home_layout));

        View btnSettings = view.findViewById(R.id.btnSettings);
        if (btnSettings != null) btnSettings.setOnClickListener(v -> Toast.makeText(getContext(), "Menu Pengaturan Dibuka", Toast.LENGTH_SHORT).show());

        try {
            ViewGroup root = (ViewGroup) view;
            android.widget.ScrollView scrollView = (android.widget.ScrollView) root.getChildAt(0);
            LinearLayout mainLayout = (LinearLayout) scrollView.getChildAt(0);

            // IKON PENSIL (UBAH FOTO PROFIL)
            LinearLayout profileInfo = (LinearLayout) mainLayout.getChildAt(1);
            android.widget.RelativeLayout avatarContainer = (android.widget.RelativeLayout) profileInfo.getChildAt(0);
            View btnPencil = avatarContainer.getChildAt(1);
            ImageView ivProfile = (ImageView) ((CardView) avatarContainer.getChildAt(0)).getChildAt(0);

            btnPencil.setOnClickListener(v -> {
                ivProfile.setImageResource(android.R.drawable.ic_menu_myplaces);
                ivProfile.setColorFilter(Color.parseColor("#1A7B42"));
                Toast.makeText(getContext(), "Foto Profil Diperbarui!", Toast.LENGTH_SHORT).show();
            });

            // KOTAK AKUN & AKTIVITAS
            CardView cardAkun = (CardView) mainLayout.getChildAt(4);
            LinearLayout layoutAkun = (LinearLayout) cardAkun.getChildAt(0);
            layoutAkun.getChildAt(0).setOnClickListener(v -> Toast.makeText(getContext(), "Buka Edit Profil", Toast.LENGTH_SHORT).show());
            layoutAkun.getChildAt(2).setOnClickListener(v -> simulateNav(R.id.nav_grant_layout));

            // KOTAK DUKUNGAN & LOGOUT
            CardView cardDukungan = (CardView) mainLayout.getChildAt(6);
            LinearLayout layoutDukungan = (LinearLayout) cardDukungan.getChildAt(0);
            layoutDukungan.getChildAt(0).setOnClickListener(v -> Toast.makeText(getContext(), "Membuka Pusat Bantuan...", Toast.LENGTH_SHORT).show());

            // LOGOUT SAKTI (ANTI BLACKOUT)
            layoutDukungan.getChildAt(2).setOnClickListener(v -> {
                try {
                    Toast.makeText(getContext(), "Logout Berhasil!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    if (getActivity() != null) getActivity().finish();
                } catch (Exception ex) {
                    // Kalau LoginActivity belum didaftarin di Manifest, cegah layar hitam!
                    Toast.makeText(getContext(), "SYSTEM INFO: Gagal ke halaman Login (Cek AndroidManifest.xml). Kembali ke Beranda.", Toast.LENGTH_LONG).show();
                    simulateNav(R.id.nav_home_layout);
                }
            });

        } catch (Exception e) {}
    }

    private void simulateNav(int navId) {
        if (getActivity() != null) {
            View nav = getActivity().findViewById(navId);
            if (nav != null) nav.performClick();
        }
    }
}