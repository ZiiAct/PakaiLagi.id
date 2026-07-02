package com.example.pakailagi;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

@SuppressWarnings("all")
public class RiwayatPengajuanFragment extends Fragment {

    public RiwayatPengajuanFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_riwayat_pengajuan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View btnBack = view.findViewById(R.id.btnBackRiwayat);
        if (btnBack != null)
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null)
                    getActivity().findViewById(R.id.nav_home_layout).performClick();
            });

        try {
            ViewGroup root = (ViewGroup) view;
            LinearLayout mainLayout = (LinearLayout) ((android.widget.ScrollView) root.getChildAt(0)).getChildAt(0);

            // SESUAI REQUEST: TOMBOL SEARCH BIKIN DIALOG INPUT
            android.widget.RelativeLayout headerLayout = (android.widget.RelativeLayout) mainLayout.getChildAt(0);
            ImageView btnSearch = (ImageView) headerLayout.getChildAt(2);
            btnSearch.setOnClickListener(v -> {
                EditText input = new EditText(getContext());
                input.setHint("Ketik nama barang...");
                input.setPadding(40, 40, 40, 40);
                new AlertDialog.Builder(getContext())
                        .setTitle("Cari Riwayat")
                        .setView(input)
                        .setPositiveButton("Cari", (dialog, which) -> Toast
                                .makeText(getContext(), "Mencari: " + input.getText(), Toast.LENGTH_SHORT).show())
                        .show();
            });

            // Tab Logic
            LinearLayout tabsRow = (LinearLayout) mainLayout.getChildAt(1);
            LinearLayout tabSedangDiajukan = (LinearLayout) tabsRow.getChildAt(0);
            LinearLayout tabSelesai = (LinearLayout) tabsRow.getChildAt(1);
            TextView tvSedangDiajukan = (TextView) tabSedangDiajukan.getChildAt(0);
            TextView tvSelesai = (TextView) tabSelesai.getChildAt(0);

            CardView card1 = (CardView) mainLayout.getChildAt(3);
            CardView card2 = (CardView) mainLayout.getChildAt(4);

            LinearLayout layoutCard1 = (LinearLayout) card1.getChildAt(0);
            LinearLayout textLayoutCard1 = (LinearLayout) ((LinearLayout) layoutCard1.getChildAt(0)).getChildAt(1);
            TextView tvTitleCard1 = (TextView) ((android.widget.RelativeLayout) textLayoutCard1.getChildAt(0))
                    .getChildAt(0);
            TextView tvStatusCard1 = (TextView) ((LinearLayout) textLayoutCard1.getChildAt(2)).getChildAt(1);
            LinearLayout buttonsCard1 = (LinearLayout) layoutCard1.getChildAt(1);

            buttonsCard1.getChildAt(0).setOnClickListener(
                    v -> Toast.makeText(getContext(), "Pengajuan Dibatalkan!", Toast.LENGTH_SHORT).show());
            buttonsCard1.getChildAt(1).setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).showDetailBarang("Buku Kalkulus Purcel", "200m dari Anda");
            });

            CardView btnKabari = (CardView) ((LinearLayout) card2.getChildAt(0)).getChildAt(2);
            btnKabari.setOnClickListener(v -> Toast
                    .makeText(getContext(), "Pesan dikirim! Pemilik berhasil dikabari.", Toast.LENGTH_LONG).show());

            tabSedangDiajukan.setOnClickListener(v -> {
                tvSedangDiajukan.setTextColor(Color.parseColor("#212529"));
                tvSedangDiajukan.setTypeface(null, Typeface.BOLD);
                tvSelesai.setTextColor(Color.parseColor("#6C757D"));
                tvSelesai.setTypeface(null, Typeface.NORMAL);

                card2.setVisibility(View.VISIBLE);
                tvTitleCard1.setText("Buku Kalkulus Purcel");
                tvStatusCard1.setText("Menunggu Persetujuan");
                tvStatusCard1.setTextColor(Color.parseColor("#F57C00"));
                buttonsCard1.setVisibility(View.VISIBLE);
            });

            tabSelesai.setOnClickListener(v -> {
                tvSelesai.setTextColor(Color.parseColor("#212529"));
                tvSelesai.setTypeface(null, Typeface.BOLD);
                tvSedangDiajukan.setTextColor(Color.parseColor("#6C757D"));
                tvSedangDiajukan.setTypeface(null, Typeface.NORMAL);

                card2.setVisibility(View.GONE);
                tvTitleCard1.setText("Meja Belajar Lipat (Selesai)");
                tvStatusCard1.setText("Telah Diterima");
                tvStatusCard1.setTextColor(Color.parseColor("#1A7B42"));
                buttonsCard1.setVisibility(View.GONE);
            });

        } catch (Exception e) {
        }
    }
}