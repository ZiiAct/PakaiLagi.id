package com.example.pakailagi;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class CariBarangFragment extends Fragment {

    public CariBarangFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cari_barang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Tombol Back
        ImageButton btnBack = view.findViewById(R.id.btnCariBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) getActivity().findViewById(R.id.nav_home_layout).performClick();
            });
        }

        // 2. Pasang Adapter & Fitur Search Beneran!
        RecyclerView rv = view.findViewById(R.id.recyclerCariBarang);
        DummyCariAdapter adapter = new DummyCariAdapter();
        if (rv != null) rv.setAdapter(adapter);

        EditText etSearch = view.findViewById(R.id.etCariSearch);
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.filter(s.toString()); // Filter data pas ngetik!
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    // ==========================================
    // ADAPTER DUMMY DENGAN FITUR FILTER SEARCH
    // ==========================================
    class DummyCariAdapter extends RecyclerView.Adapter<DummyCariAdapter.VH> {
        List<String> masterTitles = new ArrayList<>(Arrays.asList("Kipas Angin Cosmos", "Buku Pemrograman Java", "Meja Belajar Lipat", "Sepatu Lari Nike"));
        List<String> masterLocs = new ArrayList<>(Arrays.asList("Jakarta Selatan", "Bandung", "Bekasi", "Depok"));

        List<String> titles = new ArrayList<>(masterTitles);
        List<String> locs = new ArrayList<>(masterLocs);

        public void filter(String query) {
            titles.clear(); locs.clear();
            if (query.isEmpty()) {
                titles.addAll(masterTitles); locs.addAll(masterLocs);
            } else {
                for (int i = 0; i < masterTitles.size(); i++) {
                    if (masterTitles.get(i).toLowerCase().contains(query.toLowerCase())) {
                        titles.add(masterTitles.get(i));
                        locs.add(masterLocs.get(i));
                    }
                }
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cari_barang, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            h.tvTitle.setText(titles.get(pos));
            h.tvLoc.setText(locs.get(pos));
            h.itemView.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).showDetailBarang(titles.get(pos), locs.get(pos) + " • 2 km dari Anda");
                }
            });
        }

        @Override
        public int getItemCount() { return titles.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvLoc;
            VH(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tvItemTitle);
                tvLoc = v.findViewById(R.id.tvItemLocation);
            }
        }
    }
}