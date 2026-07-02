package com.example.pakailagi;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pakailagi.adapter.WishlistAdapter;
import com.example.pakailagi.model.ItemModel;
import com.example.pakailagi.model.WishlistItem;
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
public class WishlistFragment extends Fragment {

    private final List<WishlistItem> wishlistItems = new ArrayList<>();
    private WishlistAdapter wishlistAdapter;
    private ValueEventListener wishlistListener;
    private DatabaseReference wishlistRef;
    private RecyclerView rvWishlist;

    public WishlistFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. TOMBOL FAB (Tanda Plus di bawah)
        View fabAdd = view.findViewById(R.id.fabAdd);
        if (fabAdd != null)
            fabAdd.setOnClickListener(
                    v -> Toast.makeText(getContext(), "Tambah barang ke Wishlist...", Toast.LENGTH_SHORT).show());

        // 2. CHIP KATEGORI (Bisa ganti warna)
        try {
            ViewGroup root = (ViewGroup) view;
            ViewGroup scrollView = (ViewGroup) root.getChildAt(0);
            ViewGroup mainLayout = (ViewGroup) scrollView.getChildAt(0);
            ViewGroup hsvChips = (ViewGroup) mainLayout.getChildAt(2);
            ViewGroup chipsContainer = (ViewGroup) hsvChips.getChildAt(0);

            for (int i = 0; i < chipsContainer.getChildCount(); i++) {
                View chipView = chipsContainer.getChildAt(i);
                if (chipView instanceof CardView) {
                    CardView chip = (CardView) chipView;
                    chip.setOnClickListener(v -> {
                        for (int j = 0; j < chipsContainer.getChildCount(); j++) {
                            if (chipsContainer.getChildAt(j) instanceof CardView) {
                                CardView c = (CardView) chipsContainer.getChildAt(j);
                                c.setCardBackgroundColor(Color.parseColor("#E8F0FE"));
                                if (c.getChildAt(0) instanceof TextView)
                                    ((TextView) c.getChildAt(0)).setTextColor(Color.parseColor("#495057"));
                            }
                        }
                        chip.setCardBackgroundColor(Color.parseColor("#1A7B42"));
                        if (chip.getChildAt(0) instanceof TextView)
                            ((TextView) chip.getChildAt(0)).setTextColor(Color.WHITE);
                        Toast.makeText(getContext(), "Kategori Diubah", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        } catch (Exception e) {
        }

        // 3. PASANG ADAPTER KE RECYCLERVIEW
        rvWishlist = view.findViewById(R.id.rvWishlistItems);
        if (rvWishlist != null) {
            rvWishlist.setLayoutManager(new LinearLayoutManager(getContext()));
            wishlistAdapter = new WishlistAdapter(wishlistItems, new WishlistAdapter.OnItemActionListener() {
                @Override
                public void onDelete(int position) {
                    if (position < 0 || position >= wishlistItems.size())
                        return;
                    WishlistItem removed = wishlistItems.get(position);
                    // Remove from Firebase
                    removeFromFirebaseWishlist(removed.getItemId());
                    // Remove locally
                    wishlistItems.remove(position);
                    wishlistAdapter.notifyItemRemoved(position);
                }

                @Override
                public void onViewDetail(WishlistItem item) {
                    if (getActivity() instanceof MainActivity) {
                        // Build ItemModel from WishlistItem data
                        ItemModel itemModel = new ItemModel();
                        itemModel.setId(item.getItemId());
                        itemModel.setItemName(item.getName());
                        itemModel.setLocation(item.getLocation());
                        itemModel.setCondition(item.getCondition());
                        ((MainActivity) getActivity()).showDetailBarang(itemModel);
                    }
                }

                @Override
                public void onApply(WishlistItem item) {
                    Toast.makeText(getContext(), "Permintaan diajukan untuk " + item.getName(), Toast.LENGTH_SHORT)
                            .show();
                }
            });
            rvWishlist.setAdapter(wishlistAdapter);
        }

        // 4. LOAD DATA WISHLIST DARI FIREBASE
        loadWishlistFromFirebase();
    }

    /**
     * Reads user_wishlists/{uid} to get item IDs, then fetches each item
     * from hibahReq/{itemId} and adds it to the wishlist list.
     */
    private void loadWishlistFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null)
            return;

        String uid = currentUser.getUid();
        wishlistRef = FirebaseDatabase.getInstance()
                .getReference("user_wishlists")
                .child(uid);

        wishlistListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || getContext() == null)
                    return;

                wishlistItems.clear();
                long totalItems = snapshot.getChildrenCount();

                if (totalItems == 0) {
                    if (wishlistAdapter != null)
                        wishlistAdapter.notifyDataSetChanged();
                    return;
                }

                // For each itemId in the wishlist, fetch the item details
                final long[] fetchedCount = { 0 };
                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    String itemId = itemSnap.getKey();
                    if (itemId == null) {
                        fetchedCount[0]++;
                        continue;
                    }

                    FirebaseDatabase.getInstance()
                            .getReference("hibahReq")
                            .child(itemId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot ds) {
                                    fetchedCount[0]++;
                                    if (ds.exists()) {
                                        String name = ds.child("itemName").getValue(String.class);
                                        String location = ds.child("location").getValue(String.class);
                                        String cond = ds.child("condition").getValue(String.class);

                                        if (name == null)
                                            name = "Barang Tanpa Nama";
                                        if (location == null)
                                            location = "Lokasi tidak tersedia";
                                        if (cond == null)
                                            cond = "Bekas Layak";

                                        wishlistItems.add(new WishlistItem(itemId, name, location, cond));
                                    }
                                    if (fetchedCount[0] == totalItems && isAdded()) {
                                        if (wishlistAdapter != null)
                                            wishlistAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    fetchedCount[0]++;
                                    if (fetchedCount[0] == totalItems && isAdded()) {
                                        if (wishlistAdapter != null)
                                            wishlistAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Gagal memuat wishlist.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        wishlistRef.addValueEventListener(wishlistListener);
    }

    /** Removes an item from user_wishlists/{uid}/{itemId} in Firebase. */
    private void removeFromFirebaseWishlist(String itemId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null || itemId == null || itemId.isEmpty())
            return;

        FirebaseDatabase.getInstance()
                .getReference("user_wishlists")
                .child(currentUser.getUid())
                .child(itemId)
                .removeValue();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (wishlistRef != null && wishlistListener != null) {
            wishlistRef.removeEventListener(wishlistListener);
        }
    }
}