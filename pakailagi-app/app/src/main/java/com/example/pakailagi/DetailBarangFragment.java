package com.example.pakailagi;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class DetailBarangFragment extends Fragment {

    // Bundle keys — legacy name/location/condition plus new itemId
    private static final String ARG_ITEM_ID = "itemId";
    private static final String ARG_NAME = "name";
    private static final String ARG_LOCATION = "location";
    private static final String ARG_CONDITION = "condition";
    private static final String ARG_DONOR_ID = "donorId";

    // ------------------------------------------------------------------
    // Factory methods
    // ------------------------------------------------------------------

    /** New preferred factory: accepts full ItemModel. */
    public static DetailBarangFragment newInstance(ItemModel item) {
        DetailBarangFragment fragment = new DetailBarangFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, item.getId() != null ? item.getId() : "");
        args.putString(ARG_NAME, item.getItemName() != null ? item.getItemName() : "Barang");
        args.putString(ARG_LOCATION, item.getLocation() != null ? item.getLocation() : "Lokasi tidak tersedia");
        args.putString(ARG_CONDITION, item.getCondition() != null ? item.getCondition() : "Bekas Layak");
        args.putString(ARG_DONOR_ID, item.getDonorId() != null ? item.getDonorId() : "");
        fragment.setArguments(args);
        return fragment;
    }

    /** Legacy factory (name/location/condition only) — kept for backward compat. */
    public static DetailBarangFragment newInstance(String name, String location, String condition) {
        DetailBarangFragment fragment = new DetailBarangFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM_ID, "");
        args.putString(ARG_NAME, name);
        args.putString(ARG_LOCATION, location);
        args.putString(ARG_CONDITION, condition);
        args.putString(ARG_DONOR_ID, "");
        fragment.setArguments(args);
        return fragment;
    }

    // ------------------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_barang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Read Bundle args
        Bundle args = getArguments();
        String itemId = args != null ? args.getString(ARG_ITEM_ID, "") : "";
        String name = args != null ? args.getString(ARG_NAME, "Barang") : "Barang";
        String location = args != null ? args.getString(ARG_LOCATION, "Lokasi tidak tersedia")
                : "Lokasi tidak tersedia";
        String condition = args != null ? args.getString(ARG_CONDITION, "Bekas Layak") : "Bekas Layak";
        String donorId = args != null ? args.getString(ARG_DONOR_ID, "") : "";

        // Bind text views
        TextView tvDetailName = view.findViewById(R.id.tvDetailName);
        TextView tvDetailLocation = view.findViewById(R.id.tvDetailLocation);
        TextView tvConditionBadge = view.findViewById(R.id.tvConditionBadge);

        if (tvDetailName != null)
            tvDetailName.setText(name);
        if (tvDetailLocation != null)
            tvDetailLocation.setText(location);
        if (tvConditionBadge != null)
            tvConditionBadge.setText(condition);

        // Fetch extra details from Firebase if itemId is available
        if (!itemId.isEmpty()) {
            FirebaseDatabase.getInstance().getReference("hibahReq").child(itemId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!isAdded() || snapshot == null)
                                return;
                            String desc = snapshot.child("description").getValue(String.class);
                            // Description TextView is static in XML — could be updated here if needed
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }

        // Bind action buttons
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        ImageButton btnShare = view.findViewById(R.id.btnShare);
        ImageButton btnFavorite = view.findViewById(R.id.btnFavorite);
        Button btnAjukan = view.findViewById(R.id.btnAjukan);

        // Back button
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).hideDetailBarang();
                }
            });
        }

        // Share button
        if (btnShare != null) {
            btnShare.setOnClickListener(
                    v -> Toast.makeText(getContext(), "Bagikan: " + name, Toast.LENGTH_SHORT).show());
        }

        // ----- WISHLIST / FAVORITE (Step 2) -----
        if (btnFavorite != null) {
            if (!itemId.isEmpty()) {
                // Check initial wishlist state from Firebase
                checkWishlistState(itemId, btnFavorite);
                btnFavorite.setOnClickListener(v -> toggleWishlist(itemId, name, btnFavorite));
            } else {
                // Legacy path: use in-memory list
                boolean isFav = getActivity() instanceof MainActivity
                        && ((MainActivity) getActivity()).isItemInWishlist(name, location);
                updateFavoriteButtonState(btnFavorite, isFav);
                btnFavorite.setOnClickListener(v -> {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).addItemToWishlist(name, location, condition);
                    }
                    updateFavoriteButtonState(btnFavorite, true);
                    Toast.makeText(getContext(), name + " ditambahkan ke Wishlist!", Toast.LENGTH_SHORT).show();
                });
            }
        }

        // ----- AJUKAN PERMINTAAN (Step 4) -----
        if (btnAjukan != null) {
            final String finalItemId = itemId;
            final String finalName = name;
            final String finalDonorId = donorId;

            btnAjukan.setOnClickListener(v -> showAjukanDialog(finalItemId, finalName, finalDonorId));
        }
    }

    // ------------------------------------------------------------------
    // Wishlist helpers
    // ------------------------------------------------------------------

    private void checkWishlistState(String itemId, ImageButton btnFavorite) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            return;

        FirebaseDatabase.getInstance()
                .getReference("user_wishlists")
                .child(user.getUid())
                .child(itemId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (isAdded())
                            updateFavoriteButtonState(btnFavorite, snapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void toggleWishlist(String itemId, String itemName, ImageButton btnFavorite) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Silahkan login terlebih dahulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference wishRef = FirebaseDatabase.getInstance()
                .getReference("user_wishlists")
                .child(user.getUid())
                .child(itemId);

        wishRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded())
                    return;
                if (snapshot.exists()) {
                    // Already wishlisted → remove
                    wishRef.removeValue();
                    updateFavoriteButtonState(btnFavorite, false);
                    Toast.makeText(getContext(), itemName + " dihapus dari Wishlist.", Toast.LENGTH_SHORT).show();
                } else {
                    // Not wishlisted → add (value = true as presence marker)
                    wishRef.setValue(true);
                    updateFavoriteButtonState(btnFavorite, true);
                    Toast.makeText(getContext(), itemName + " ditambahkan ke Wishlist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateFavoriteButtonState(ImageButton button, boolean active) {
        int color = active ? Color.parseColor("#1A7B42") : Color.parseColor("#212529");
        button.setColorFilter(color);
    }

    // ------------------------------------------------------------------
    // Ajukan Permintaan (Step 4 + 5)
    // ------------------------------------------------------------------

    /**
     * Shows the confirmation AlertDialog.
     * "Ya" → push to receiveReq and log to statusLog.
     * "Tidak" → dismiss.
     */
    private void showAjukanDialog(String itemId, String itemName, String donorId) {
        if (getContext() == null)
            return;

        new AlertDialog.Builder(getContext())
                .setTitle("Konfirmasi Permintaan")
                .setMessage("Apa kamu yakin untuk mengajukan barang ini?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    dialog.dismiss();
                    submitRequest(itemId, itemName, donorId);
                })
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Pushes a new document to /receiveReq/{pushId} and triggers
     * an initial statusLog entry via StatusLogHelper.
     */
    private void submitRequest(String itemId, String itemName, String donorId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Silahkan login terlebih dahulu.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference receiveReqRef = FirebaseDatabase.getInstance()
                .getReference("receiveReq")
                .push();

        String requestId = receiveReqRef.getKey();
        String uid = user.getUid();

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("itemId", itemId.isEmpty() ? "unknown" : itemId);
        requestData.put("itemName", itemName);
        requestData.put("requesterId", uid);
        requestData.put("requesterName", user.getDisplayName() != null ? user.getDisplayName() : user.getEmail());
        requestData.put("donorId", donorId.isEmpty() ? "unknown" : donorId);
        requestData.put("status", "process");
        requestData.put("timestamp", ServerValue.TIMESTAMP);

        receiveReqRef.setValue(requestData)
                .addOnSuccessListener(aVoid -> {
                    if (!isAdded() || getContext() == null)
                        return;
                    Toast.makeText(getContext(),
                            "Permintaan untuk \"" + itemName + "\" berhasil dikirim!",
                            Toast.LENGTH_SHORT).show();

                    // Step 5: push initial statusLog entry
                    if (requestId != null) {
                        StatusLogHelper.logInitialRequest(
                                itemId.isEmpty() ? "unknown" : itemId,
                                requestId,
                                uid);
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded() || getContext() == null)
                        return;
                    Toast.makeText(getContext(),
                            "Gagal mengirim permintaan: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}