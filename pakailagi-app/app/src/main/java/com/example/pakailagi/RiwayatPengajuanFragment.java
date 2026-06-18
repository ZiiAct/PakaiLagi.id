package com.example.pakailagi;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pakailagi.adapter.PengajuanAdapter;
import com.example.pakailagi.model.PengajuanItem;
import java.util.ArrayList;
import java.util.List;

public class RiwayatPengajuanFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_riwayat_pengajuan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View rToolbar = view.findViewById(R.id.riwayatToolbar);
        final int rtLeft  = rToolbar.getPaddingLeft();
        final int rtRight = rToolbar.getPaddingRight();
        final int rtBase  = (int)(56 * getResources().getDisplayMetrics().density);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int sbH = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            rToolbar.getLayoutParams().height = rtBase + sbH;
            rToolbar.requestLayout();
            rToolbar.setPadding(rtLeft, sbH, rtRight, 0);
            return insets;
        });

        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateTo(R.id.nav_wishlist);
            }
        });

        RecyclerView recycler = view.findViewById(R.id.recyclerPengajuan);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(new PengajuanAdapter(getDummyItems()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().getWindow().setStatusBarColor(
                    requireContext().getColor(R.color.toolbar_dark));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().getWindow().setStatusBarColor(
                    requireContext().getColor(R.color.primary_green));
        }
    }

    private List<PengajuanItem> getDummyItems() {
        List<PengajuanItem> items = new ArrayList<>();
        items.add(new PengajuanItem(
                "Buku Kalkulus Purcel", "200m", "12 Okt 2023",
                PengajuanItem.Status.PENDING, null));
        items.add(new PengajuanItem(
                "Headphone Sony WH-1000XM4", "1,2km", "10 Okt 2023",
                PengajuanItem.Status.READY, "Kosan Hijau, Jl. Ganesha No. 10"));
        return items;
    }
}
