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

@SuppressWarnings("all")
public class WishlistFragment extends Fragment {

    public WishlistFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. TOMBOL FAB (Tanda Plus di bawah)
        View fabAdd = view.findViewById(R.id.fabAdd);
        if (fabAdd != null) fabAdd.setOnClickListener(v -> Toast.makeText(getContext(), "Tambah barang ke Wishlist...", Toast.LENGTH_SHORT).show());

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
                        // Reset semua warna jadi abu-abu
                        for (int j = 0; j < chipsContainer.getChildCount(); j++) {
                            if (chipsContainer.getChildAt(j) instanceof CardView) {
                                CardView c = (CardView) chipsContainer.getChildAt(j);
                                c.setCardBackgroundColor(Color.parseColor("#E8F0FE"));
                                if (c.getChildAt(0) instanceof TextView) ((TextView) c.getChildAt(0)).setTextColor(Color.parseColor("#495057"));
                            }
                        }
                        // Ubah yang diklik jadi Hijau Tua
                        chip.setCardBackgroundColor(Color.parseColor("#1A7B42"));
                        if (chip.getChildAt(0) instanceof TextView) ((TextView) chip.getChildAt(0)).setTextColor(Color.WHITE);
                        Toast.makeText(getContext(), "Kategori Diubah", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        } catch (Exception e) {}

        // 3. PASANG DATA KE LIST BARANG
        RecyclerView rvWishlist = view.findViewById(R.id.rvWishlistItems);
        if (rvWishlist != null) {
            rvWishlist.setLayoutManager(new LinearLayoutManager(getContext()));
            rvWishlist.setAdapter(new DummyWishlistAdapter());
        }

        RecyclerView rvSuggestions = view.findViewById(R.id.rvSuggestions);
        if (rvSuggestions != null) {
            rvSuggestions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvSuggestions.setAdapter(new DummyRecomAdapter());
        }
    }

    // ADAPTER WISHLIST (ANTI-CRASH)
    class DummyWishlistAdapter extends RecyclerView.Adapter<DummyWishlistAdapter.VH> {
        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            try {
                return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist, parent, false));
            } catch (Exception e) {
                // JIKA XML item_wishlist ERROR, TAMPILIN TEKS INI BIAR GAK BLACKOUT!
                TextView errorText = new TextView(parent.getContext());
                errorText.setText("⚠️ XML item_wishlist.xml Error / Tidak Support");
                errorText.setPadding(30, 30, 30, 30);
                return new VH(errorText);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.itemView.setOnClickListener(v -> {
                if(getActivity() instanceof MainActivity) ((MainActivity)getActivity()).showDetailBarang("Barang Wishlist", "Jakarta");
            });
            View btnAjukan = holder.itemView.findViewById(R.id.btnAjukan);
            if (btnAjukan != null) btnAjukan.setOnClickListener(v -> Toast.makeText(getContext(), "Permintaan Diajukan!", Toast.LENGTH_SHORT).show());
        }
        @Override public int getItemCount() { return 3; }
        class VH extends RecyclerView.ViewHolder { VH(View v) { super(v); } }
    }

    // ADAPTER REKOMENDASI (ANTI-CRASH)
    class DummyRecomAdapter extends RecyclerView.Adapter<DummyRecomAdapter.VH> {
        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            try {
                return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommendation, parent, false));
            } catch (Exception e) {
                TextView errorText = new TextView(parent.getContext());
                errorText.setText("⚠️ Error Item");
                return new VH(errorText);
            }
        }
        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.itemView.setOnClickListener(v -> Toast.makeText(getContext(), "Lihat Rekomendasi", Toast.LENGTH_SHORT).show());
        }
        @Override public int getItemCount() { return 3; }
        class VH extends RecyclerView.ViewHolder { VH(View v) { super(v); } }
    }
}