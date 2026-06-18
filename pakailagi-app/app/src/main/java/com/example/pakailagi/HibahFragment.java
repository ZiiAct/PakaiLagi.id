package com.example.pakailagi;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class HibahFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hibah, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle status bar overlap (edge-to-edge window)
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            view.findViewById(R.id.hibahHeader).setPadding(
                    dpToPx(16), statusBarHeight + dpToPx(14), dpToPx(12), dpToPx(14));
            return insets;
        });

        // Populate Kategori dropdown
        String[] categories = {"Elektronik", "Perabotan", "Buku & Alat Tulis",
                               "Olahraga", "Pakaian", "Peralatan Dapur", "Lainnya"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, categories);
        AutoCompleteTextView dropdown = view.findViewById(R.id.dropdownKategori);
        dropdown.setAdapter(adapter);
        dropdown.setOnClickListener(v -> dropdown.showDropDown());

        // Submit button
        view.findViewById(R.id.btnHibahkanSekarang).setOnClickListener(v ->
                Toast.makeText(getContext(), "Barang berhasil dihibahkan!", Toast.LENGTH_SHORT).show());
    }

    private int dpToPx(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = requireActivity().getWindow();
        window.setStatusBarColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Window window = requireActivity().getWindow();
        window.setStatusBarColor(requireContext().getColor(R.color.primary_green));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(0);
        }
    }
}
