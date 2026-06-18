package com.example.pakailagi;

import android.os.Build;
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

public class ProfilFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View pToolbar = view.findViewById(R.id.profilToolbar);
        final int ptLeft  = pToolbar.getPaddingLeft();
        final int ptRight = pToolbar.getPaddingRight();
        final int ptBase  = (int)(56 * getResources().getDisplayMetrics().density);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int sbH = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            pToolbar.getLayoutParams().height = ptBase + sbH;
            pToolbar.requestLayout();
            pToolbar.setPadding(ptLeft, sbH, ptRight, 0);
            return insets;
        });

        view.findViewById(R.id.btnProfilBack).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateTo(R.id.nav_home);
            }
        });

        view.findViewById(R.id.rowEditProfile).setOnClickListener(v ->
                Toast.makeText(getContext(), "Edit Profile", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.rowMyDonations).setOnClickListener(v ->
                Toast.makeText(getContext(), "My Donations", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.rowHelp).setOnClickListener(v ->
                Toast.makeText(getContext(), "Help", Toast.LENGTH_SHORT).show());

        view.findViewById(R.id.rowLogout).setOnClickListener(v ->
                Toast.makeText(getContext(), "Logout", Toast.LENGTH_SHORT).show());
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
}
