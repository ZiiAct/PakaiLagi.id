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

import com.example.pakailagi.model.ItemModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class HomeFragment extends Fragment {

    // Listener reference kept so we can remove it in onDestroyView to prevent leaks
    private ValueEventListener itemsListener;
    private DatabaseReference itemsRef;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tampilkan nama user dari Firebase
        loadUserGreeting(view);

        View btnDonateNow = view.findViewById(R.id.btnDonateNow);
        if (btnDonateNow != null)
            btnDonateNow.setOnClickListener(v -> simulateBottomNavClick(R.id.nav_grant_layout));

        View categorySearch = view.findViewById(R.id.categorySearch);
        if (categorySearch != null)
            categorySearch.setOnClickListener(v -> simulateBottomNavClick(R.id.nav_search_layout));

        View cardNotification = view.findViewById(R.id.cardNotification);
        if (cardNotification != null)
            cardNotification.setOnClickListener(
                    v -> Toast.makeText(getContext(), "Belum ada notifikasi baru", Toast.LENGTH_SHORT).show());

        LinearLayout homeContent = view.findViewById(R.id.homeContent);
        if (homeContent != null) {
            try {
                // A. Menu Kategori Bulat di Beranda
                LinearLayout menuRow = (LinearLayout) homeContent.getChildAt(2);
                menuRow.getChildAt(1).setOnClickListener(v -> simulateBottomNavClick(R.id.nav_grant_layout)); // Hibah
                menuRow.getChildAt(2).setOnClickListener(v -> simulateBottomNavClick(R.id.nav_wishlist_layout)); // Wishlist

                // TOMBOL RIWAYAT (TENGAH) BUKA FRAGMENT RIWAYAT
                menuRow.getChildAt(3).setOnClickListener(v -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).switchFragment(new RiwayatPengajuanFragment());
                        ((MainActivity) getActivity()).updateNavUI(-1);
                    }
                });

                // B. Tulisan "Lihat Semua"
                LinearLayout headerBarang = (LinearLayout) homeContent.getChildAt(3);
                View txtLihatSemua = headerBarang.getChildAt(1);
                txtLihatSemua.setOnClickListener(v -> simulateBottomNavClick(R.id.nav_search_layout));

                // C. Kartu Barang Terbaru — dinamis dari Firebase
                android.widget.HorizontalScrollView hsv = (android.widget.HorizontalScrollView) homeContent
                        .getChildAt(4);
                LinearLayout cardContainer = (LinearLayout) hsv.getChildAt(0);

                // Load items from Firebase
                loadApprovedItems(cardContainer);

            } catch (Exception e) {
            }
        }
    }

    /**
     * Fetches hibahReq items where status == "approved" and dynamically
     * inflates card views into the horizontal scroll container.
     */
    private void loadApprovedItems(LinearLayout cardContainer) {
        if (cardContainer == null || getContext() == null)
            return;

        itemsRef = FirebaseDatabase.getInstance().getReference("hibahReq");
        itemsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null || !isAdded())
                    return;

                // Collect approved items
                List<ItemModel> approvedItems = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String status = ds.child("status").getValue(String.class);
                    if ("approved".equals(status)) {
                        ItemModel item = ds.getValue(ItemModel.class);
                        if (item != null) {
                            item.setId(ds.getKey());
                            approvedItems.add(item);
                        }
                    }
                }

                // Clear existing (static dummy) cards
                cardContainer.removeAllViews();

                if (approvedItems.isEmpty()) {
                    // Show empty state text
                    TextView emptyTv = new TextView(getContext());
                    emptyTv.setText("Belum ada barang tersedia saat ini.");
                    emptyTv.setTextColor(0xFF6C757D);
                    emptyTv.setTextSize(12f);
                    emptyTv.setPadding(8, 8, 8, 8);
                    cardContainer.addView(emptyTv);
                    return;
                }

                // Inflate a card for each approved item
                LayoutInflater inflater = LayoutInflater.from(getContext());
                for (ItemModel item : approvedItems) {
                    View card = inflater.inflate(R.layout.item_home_card, cardContainer, false);

                    TextView tvName = card.findViewById(R.id.tvHomeCardName);
                    TextView tvLocation = card.findViewById(R.id.tvHomeCardLocation);
                    TextView tvStatus = card.findViewById(R.id.tvHomeCardStatus);

                    if (tvName != null)
                        tvName.setText(item.getItemName());
                    if (tvLocation != null)
                        tvLocation.setText(item.getLocation() != null ? item.getLocation() : "Lokasi tidak tersedia");
                    if (tvStatus != null)
                        tvStatus.setText("Tersedia");

                    card.setOnClickListener(v -> {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).showDetailBarang(item);
                        }
                    });

                    // Add margin between cards
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) card.getLayoutParams();
                    if (params == null)
                        params = new LinearLayout.LayoutParams(
                                (int) (160 * getResources().getDisplayMetrics().density),
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMarginEnd((int) (16 * getResources().getDisplayMetrics().density));
                    card.setLayoutParams(params);

                    cardContainer.addView(card);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Gagal memuat barang: " + error.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            }
        };

        itemsRef.addValueEventListener(itemsListener);
    }

    private void loadUserGreeting(View view) {
        TextView tvGreetingName = view.findViewById(R.id.tvGreetingName);
        if (tvGreetingName == null)
            return;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        String authName = currentUser.getDisplayName();
        if (authName != null && !authName.isEmpty()) {
            tvGreetingName.setText(authName);
        } else if (currentUser.getEmail() != null) {
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
                    String username = snapshot.child("username").getValue(String.class);
                    if (username != null && !username.isEmpty()) {
                        tvGreetingName.setText(username);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Fallback sudah di-set di atas
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Detach Firebase listener to prevent memory leaks
        if (itemsRef != null && itemsListener != null) {
            itemsRef.removeEventListener(itemsListener);
        }
    }

    private void simulateBottomNavClick(int navId) {
        if (getActivity() != null) {
            View navButton = getActivity().findViewById(navId);
            if (navButton != null)
                navButton.performClick();
        }
    }
}