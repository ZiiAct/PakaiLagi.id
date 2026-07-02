package com.example.pakailagi;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pakailagi.adapter.CariBarangAdapter;
import com.example.pakailagi.model.CariBarangItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class CariBarangFragment extends Fragment {

    // Master list holds all approved items fetched from Firebase
    private final List<CariBarangItem> masterList = new ArrayList<>();
    // Display list is filtered based on search query
    private final List<CariBarangItem> displayList = new ArrayList<>();

    private CariBarangAdapter adapter;
    private ValueEventListener itemsListener;
    private DatabaseReference itemsRef;

    public CariBarangFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cari_barang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Tombol Back
        ImageButton btnBack = view.findViewById(R.id.btnCariBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null)
                    getActivity().findViewById(R.id.nav_home_layout).performClick();
            });
        }

        // 2. Setup RecyclerView dengan CariBarangAdapter
        RecyclerView rv = view.findViewById(R.id.recyclerCariBarang);
        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new CariBarangAdapter(displayList, item -> {
                // Klik item → buka Detail dengan data real dari Firebase
                if (getActivity() instanceof MainActivity) {
                    com.example.pakailagi.model.ItemModel itemModel = new com.example.pakailagi.model.ItemModel();
                    itemModel.setId(item.getIdItem());
                    itemModel.setItemName(item.getItemName());
                    itemModel.setLocation(item.getLocation());
                    itemModel.setCondition(item.getItemCondition());
                    itemModel.setImageUrl(item.getImageUrl());
                    ((MainActivity) getActivity()).showDetailBarang(itemModel);
                }
            });
            rv.setAdapter(adapter);
        }

        // 3. SearchBar: filter displayList dari masterList
        EditText etSearch = view.findViewById(R.id.etCariSearch);
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterItems(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        // 4. Load data dari Firebase
        loadApprovedItems();
    }

    /**
     * Fetches all items from hibahReq where status == "approved",
     * populates masterList, then syncs displayList.
     */
    private void loadApprovedItems() {
        itemsRef = FirebaseDatabase.getInstance().getReference("hibahReq");
        itemsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded() || getContext() == null)
                    return;

                masterList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String status = ds.child("status").getValue(String.class);
                    if ("approved".equals(status)) {
                        String id = ds.getKey();
                        String name = ds.child("itemName").getValue(String.class);
                        String location = ds.child("location").getValue(String.class);
                        String cond = ds.child("condition").getValue(String.class);
                        String imgUrl = ds.child("imageUrl").getValue(String.class);

                        if (name == null)
                            name = "Barang Tanpa Nama";
                        if (location == null)
                            location = "Lokasi tidak tersedia";
                        if (cond == null)
                            cond = "Bekas Layak";

                        masterList.add(new CariBarangItem(id, name, location, cond, imgUrl));
                    }
                }

                // Sync displayList (no active filter on fresh load)
                EditText etSearch = getView() != null ? getView().findViewById(R.id.etCariSearch) : null;
                String currentQuery = (etSearch != null) ? etSearch.getText().toString() : "";
                filterItems(currentQuery);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Gagal memuat data barang.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        itemsRef.addValueEventListener(itemsListener);
    }

    /**
     * Filters masterList by query (case-insensitive match on name),
     * updates displayList, and notifies adapter.
     */
    private void filterItems(String query) {
        displayList.clear();
        if (query == null || query.isEmpty()) {
            displayList.addAll(masterList);
        } else {
            String lowerQuery = query.toLowerCase();
            for (CariBarangItem item : masterList) {
                if (item.getItemName().toLowerCase().contains(lowerQuery)
                        || (item.getLocation() != null && item.getLocation().toLowerCase().contains(lowerQuery))) {
                    displayList.add(item);
                }
            }
        }
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Detach Firebase listener to prevent leaks
        if (itemsRef != null && itemsListener != null) {
            itemsRef.removeEventListener(itemsListener);
        }
    }
}