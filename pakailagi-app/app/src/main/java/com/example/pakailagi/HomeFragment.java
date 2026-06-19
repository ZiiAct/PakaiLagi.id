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

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View homeContent = view.findViewById(R.id.homeContent);
        final int hcLeft   = homeContent.getPaddingLeft();
        final int hcTop    = homeContent.getPaddingTop();
        final int hcRight  = homeContent.getPaddingRight();
        final int hcBottom = homeContent.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int sbH = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            homeContent.setPadding(hcLeft, hcTop + sbH, hcRight, hcBottom);
            return insets;
        });

        view.findViewById(R.id.btnDonateNow).setOnClickListener(v ->
            Toast.makeText(getContext(), "Fitur Donasi segera hadir!", Toast.LENGTH_SHORT).show()
        );

        view.findViewById(R.id.categorySearch).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showCariBarang();
            }
        });
    }
}
