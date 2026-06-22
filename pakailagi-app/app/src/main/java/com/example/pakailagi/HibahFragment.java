package com.example.pakailagi;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

@SuppressWarnings("all")
public class HibahFragment extends Fragment {

    public HibahFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hibah, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ViewGroup mainLayout = (ViewGroup) ((android.widget.ScrollView) view).getChildAt(0);

            // 1. Ikon Info (Dialog)
            View iconInfo = ((ViewGroup) mainLayout.getChildAt(0)).getChildAt(1);
            iconInfo.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                    .setTitle("Info Hibah")
                    .setMessage("Pastikan barang dalam kondisi layak pakai dan sesuai deskripsi.")
                    .setPositiveButton("Paham", null).show());

            // 2. Simulasi Upload Foto (Gambarnya berubah!)
            ViewGroup fotoGrid = (ViewGroup) mainLayout.getChildAt(5);
            View boxUtama = fotoGrid.getChildAt(0);
            ImageView ivImage = (ImageView) ((CardView) fotoGrid.getChildAt(1)).getChildAt(0);

            boxUtama.setOnClickListener(v -> {
                ivImage.setImageResource(android.R.drawable.ic_menu_gallery);
                ivImage.setBackgroundColor(Color.parseColor("#E8F5E9"));
                ivImage.setColorFilter(Color.parseColor("#1A7B42"));
                Toast.makeText(getContext(), "Foto berhasil diunggah!", Toast.LENGTH_SHORT).show();
            });

            // 3. Dropdown Pilih Kategori Beneran
            View dropdownKategori = mainLayout.getChildAt(11);
            TextView tvKategori = (TextView) ((android.widget.RelativeLayout) dropdownKategori).getChildAt(0);
            dropdownKategori.setOnClickListener(v -> {
                String[] categories = {"Elektronik", "Perabotan", "Buku", "Olahraga", "Lainnya"};
                new AlertDialog.Builder(getContext())
                        .setTitle("Pilih Kategori")
                        .setItems(categories, (dialog, which) -> {
                            tvKategori.setText(categories[which]);
                            tvKategori.setTextColor(Color.parseColor("#212529"));
                        }).show();
            });

            // 4. Instruksi Pengambilan (Chips berubah warna saat diklik)
            ViewGroup chipsRow = (ViewGroup) mainLayout.getChildAt(17);
            TextView chipPickup = (TextView) chipsRow.getChildAt(0);
            TextView chipCOD = (TextView) chipsRow.getChildAt(1);
            TextView chipKurir = (TextView) mainLayout.getChildAt(18);

            View.OnClickListener chipListener = v -> {
                // Reset Semua ke Abu-abu
                chipPickup.setBackgroundResource(R.drawable.bg_chip_outline_grey);
                chipPickup.setTextColor(Color.parseColor("#6C757D"));
                chipCOD.setBackgroundResource(R.drawable.bg_chip_outline_grey);
                chipCOD.setTextColor(Color.parseColor("#6C757D"));
                chipKurir.setBackgroundResource(R.drawable.bg_chip_outline_grey);
                chipKurir.setTextColor(Color.parseColor("#6C757D"));

                // Ubah yang diklik jadi Hijau
                TextView clicked = (TextView) v;
                clicked.setBackgroundResource(R.drawable.bg_chip_green_solid);
                clicked.setTextColor(Color.WHITE);
            };

            chipPickup.setOnClickListener(chipListener);
            chipCOD.setOnClickListener(chipListener);
            chipKurir.setOnClickListener(chipListener);

            // 5. Tombol Submit (Muter balik ke Home)
            int childCount = mainLayout.getChildCount();
            CardView btnSubmit = (CardView) mainLayout.getChildAt(childCount - 2);
            btnSubmit.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Barang berhasil dihibahkan!", Toast.LENGTH_LONG).show();
                if (getActivity() != null) getActivity().findViewById(R.id.nav_home_layout).performClick();
            });

        } catch (Exception e) {}
    }
}