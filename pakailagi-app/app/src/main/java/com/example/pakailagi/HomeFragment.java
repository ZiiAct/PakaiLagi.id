package com.example.pakailagi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressWarnings("all")
public class HomeFragment extends Fragment {

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tampilkan nama user dari Firebase
        loadUserGreeting(view);

        View btnDonateNow = view.findViewById(R.id.btnDonateNow);
        if (btnDonateNow != null) btnDonateNow.setOnClickListener(v -> simulateBottomNavClick(R.id.nav_grant_layout));

        View categorySearch = view.findViewById(R.id.categorySearch);
        if (categorySearch != null) categorySearch.setOnClickListener(v -> simulateBottomNavClick(R.id.nav_search_layout));

        View cardNotification = view.findViewById(R.id.cardNotification);
        if (cardNotification != null) cardNotification.setOnClickListener(v -> Toast.makeText(getContext(), "Belum ada notifikasi baru", Toast.LENGTH_SHORT).show());

        LinearLayout homeContent = view.findViewById(R.id.homeContent);
        if (homeContent != null) {
            try {
                // A. Menu Kategori Bulat di Beranda
                LinearLayout menuRow = (LinearLayout) homeContent.getChildAt(2);
                menuRow.getChildAt(1).setOnClickListener(v -> simulateBottomNavClick(R.id.nav_grant_layout)); // Hibah
                menuRow.getChildAt(2).setOnClickListener(v -> simulateBottomNavClick(R.id.nav_wishlist_layout)); // Wishlist

                // SESUAI REQUEST: TOMBOL RIWAYAT (TENGAH) BUKA FRAGMENT RIWAYAT
                menuRow.getChildAt(3).setOnClickListener(v -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).switchFragment(new RiwayatPengajuanFragment());
                        ((MainActivity) getActivity()).updateNavUI(-1); // Matiin highlight hijau di Bottom Nav
                    }
                });

                // B. Tulisan "Lihat Semua"
                LinearLayout headerBarang = (LinearLayout) homeContent.getChildAt(3);
                View txtLihatSemua = headerBarang.getChildAt(1);
                txtLihatSemua.setOnClickListener(v -> Toast.makeText(getContext(), "Menampilkan semua barang...", Toast.LENGTH_SHORT).show());

                // C. Kartu Barang Terbaru
                android.widget.HorizontalScrollView hsv = (android.widget.HorizontalScrollView) homeContent.getChildAt(4);
                LinearLayout cardContainer = (LinearLayout) hsv.getChildAt(0);

                View cardKursi = cardContainer.getChildAt(0);
                cardKursi.setOnClickListener(v -> openDetail("Kursi Kantor Ergonomis", "Bekasi • 5 km dari Anda"));

                View cardKotak = cardContainer.getChildAt(1);
                cardKotak.setOnClickListener(v -> openDetail("Kotak Penyimpanan Plastik", "Jakarta Selatan • 2 km dari Anda"));

            } catch (Exception e) {}
        }
    }

    private void loadUserGreeting(View view) {
        TextView tvGreetingName = view.findViewById(R.id.tvGreetingName);
        if (tvGreetingName == null) return;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        // Set nama sementara dari Firebase Auth (jika ada displayName)
        String authName = currentUser.getDisplayName();
        if (authName != null && !authName.isEmpty()) {
            tvGreetingName.setText(authName);
        } else if (currentUser.getEmail() != null) {
            // Fallback: ambil bagian sebelum @ dari email
            String email = currentUser.getEmail();
            String nameFromEmail = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
            tvGreetingName.setText(nameFromEmail);
        }

        // Ambil nama dari Realtime Database (lebih akurat)
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(currentUser.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    if (fullName != null && !fullName.isEmpty()) {
                        tvGreetingName.setText(fullName);
                        return;
                    }
                    // Fallback ke username jika fullName kosong
                    String username = snapshot.child("username").getValue(String.class);
                    if (username != null && !username.isEmpty()) {
                        tvGreetingName.setText(username);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Biarkan nama yang sudah di-set dari fallback di atas
            }
        });
    }

    private void simulateBottomNavClick(int navId) {
        if (getActivity() != null) {
            View navButton = getActivity().findViewById(navId);
            if (navButton != null) navButton.performClick();
        }
    }

    private void openDetail(String name, String location) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showDetailBarang(name, location);
        }
    }
}