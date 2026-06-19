package com.example.pakailagi;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class DetailBarangFragment extends Fragment {

    private static final String ARG_NAME     = "name";
    private static final String ARG_LOCATION = "location";

    public static DetailBarangFragment newInstance(String name, String location) {
        DetailBarangFragment f = new DetailBarangFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_LOCATION, location);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_barang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Push content below the status bar (edge-to-edge window)
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            v.setPadding(0, statusBarHeight, 0, 0);
            return insets;
        });

        Bundle args = getArguments();
        String name     = args != null ? args.getString(ARG_NAME, "")     : "";
        String location = args != null ? args.getString(ARG_LOCATION, "") : "";

        ((TextView) view.findViewById(R.id.tvDetailName)).setText(name);
        ((TextView) view.findViewById(R.id.tvDetailLocation)).setText(location + " • 2 km dari lokasi Anda");
        ((TextView) view.findViewById(R.id.tvDetailCondition)).setText("Kondisi: Bekas Layak");
        ((TextView) view.findViewById(R.id.tvDetailDescription)).setText(
                "Barang ini masih sangat kokoh dan nyaman digunakan. Cocok untuk keperluan sehari-hari. " +
                "Terdapat sedikit tanda pemakaian di bagian sudut, namun tidak mengurangi fungsi utama. " +
                "Barang siap untuk diambil dan dapat dilihat terlebih dahulu."
        );

        addCategoryChip(view, "Perabot");
        addCategoryChip(view, "Peralatan Rumah");

        view.findViewById(R.id.btnDetailBack).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).hideDetailBarang();
            }
        });

        view.findViewById(R.id.btnDetailShare).setOnClickListener(v ->
                Toast.makeText(getContext(), "Bagikan tautan barang", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.btnDetailWishlist).setOnClickListener(v ->
                Toast.makeText(getContext(), "Ditambahkan ke wishlist", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.btnDetailAjukan).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateTo(R.id.nav_riwayat);
            }
        });
    }

    private void addCategoryChip(View root, String label) {
        LinearLayout container = root.findViewById(R.id.layoutCategories);
        TextView chip = new TextView(requireContext());
        chip.setText(label);
        chip.setTextSize(12);
        chip.setTextColor(requireContext().getColor(R.color.text_dark));
        chip.setBackgroundResource(R.drawable.bg_category_chip);
        int px8  = dpToPx(8);
        int px16 = dpToPx(16);
        chip.setPadding(px16, px8, px16, px8);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMarginEnd(dpToPx(8));
        chip.setLayoutParams(lp);
        container.addView(chip);
    }

    private int dpToPx(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(0);
        }
    }
}
