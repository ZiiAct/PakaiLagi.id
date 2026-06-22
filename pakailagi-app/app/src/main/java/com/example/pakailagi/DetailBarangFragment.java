package com.example.pakailagi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

@SuppressWarnings("all") // Mantra andalan biar layar bersih 100%
public class DetailBarangFragment extends Fragment {

    private static final String ARG_NAME = "name";
    private static final String ARG_LOCATION = "location";

    public static DetailBarangFragment newInstance(String name, String location) {
        DetailBarangFragment fragment = new DetailBarangFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_barang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ambil data nama yang dikirim dari halaman Cari
        String name = getArguments() != null ? getArguments().getString(ARG_NAME) : "Barang";

        // Manggil ID Tombol (Udah sinkron 100% sama XML lo)
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        ImageButton btnShare = view.findViewById(R.id.btnShare);
        ImageButton btnFavorite = view.findViewById(R.id.btnFavorite);
        Button btnAjukan = view.findViewById(R.id.btnAjukan);

        // Logic Tombol Kembali (Ini yang bikin nggak force close pas dipencet Back)
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).hideDetailBarang();
                }
            });
        }

        // Logic Tombol Share
        if (btnShare != null) {
            btnShare.setOnClickListener(v -> Toast.makeText(getContext(), "Bagikan: " + name, Toast.LENGTH_SHORT).show());
        }

        // Logic Tombol Wishlist/Favorite
        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> Toast.makeText(getContext(), name + " ditambahkan ke Wishlist!", Toast.LENGTH_SHORT).show());
        }

        // Logic Tombol Ajukan Permintaan
        if (btnAjukan != null) {
            btnAjukan.setOnClickListener(v -> Toast.makeText(getContext(), "Pengajuan untuk " + name + " berhasil dikirim!", Toast.LENGTH_SHORT).show());
        }
    }
}