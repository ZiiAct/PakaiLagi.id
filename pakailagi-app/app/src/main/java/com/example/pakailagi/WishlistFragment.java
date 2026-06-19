package com.example.pakailagi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pakailagi.adapter.RecommendationAdapter;
import com.example.pakailagi.adapter.WishlistAdapter;
import com.example.pakailagi.model.RecommendationItem;
import com.example.pakailagi.model.WishlistItem;
import java.util.ArrayList;
import java.util.List;

public class WishlistFragment extends Fragment implements WishlistAdapter.OnItemActionListener {

    private WishlistAdapter wishlistAdapter;
    private List<WishlistItem> wishlistItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wishlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View wlContent = view.findViewById(R.id.wishlistContent);
        final int wlLeft   = wlContent.getPaddingLeft();
        final int wlTop    = wlContent.getPaddingTop();
        final int wlRight  = wlContent.getPaddingRight();
        final int wlBottom = wlContent.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int sbH = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            wlContent.setPadding(wlLeft, wlTop + sbH, wlRight, wlBottom);
            return insets;
        });

        setupWishlistRecyclerView(view);
        setupRecommendationsRecyclerView(view);
    }

    private void setupWishlistRecyclerView(View view) {
        wishlistItems = getDummyWishlistItems();
        wishlistAdapter = new WishlistAdapter(wishlistItems, this);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerWishlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(wishlistAdapter);
    }

    private void setupRecommendationsRecyclerView(View view) {
        List<RecommendationItem> recommendations = getDummyRecommendations();
        RecommendationAdapter adapter = new RecommendationAdapter(recommendations);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerRecommendations);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private List<WishlistItem> getDummyWishlistItems() {
        List<WishlistItem> items = new ArrayList<>();
        items.add(new WishlistItem("Macroeconomics Edition", "400m dari sini", "LIKE NEW"));
        items.add(new WishlistItem("Sony WH-1000XM4", "1,2km dari sini", "GOOD"));
        items.add(new WishlistItem("Lampu Belajar IKEA", "850m dari sini", "LIKE NEW"));
        return items;
    }

    private List<RecommendationItem> getDummyRecommendations() {
        List<RecommendationItem> items = new ArrayList<>();
        items.add(new RecommendationItem("Tumbir Stainless"));
        items.add(new RecommendationItem("Kursi Kerja"));
        items.add(new RecommendationItem("Meja Belajar"));
        return items;
    }

    @Override
    public void onDelete(int position) {
        wishlistAdapter.removeItem(position);
    }

    @Override
    public void onViewDetail(WishlistItem item) {
        Toast.makeText(getContext(), "Detail: " + item.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onApply(WishlistItem item) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).navigateTo(R.id.nav_riwayat);
        }
    }
}
