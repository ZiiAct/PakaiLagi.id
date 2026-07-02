package com.example.pakailagi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pakailagi.R;
import com.example.pakailagi.adapter.AdminItemAdapter;
import com.example.pakailagi.model.ItemModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminInventoryFragment extends Fragment {

    private RecyclerView rvInventory;
    private AdminItemAdapter adapter;
    private List<ItemModel> inventoryList;
    private DatabaseReference databaseReference;

    public AdminInventoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize Firebase & RecyclerView
        databaseReference = FirebaseDatabase.getInstance().getReference("items");
        rvInventory = view.findViewById(R.id.rvInventory);
        rvInventory.setLayoutManager(new LinearLayoutManager(requireContext()));

        inventoryList = new ArrayList<>();

        // Load inventory items
        fetchInventoryItems();
    }

    private void fetchInventoryItems() {
        // Query: Ambil semua items yang available
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inventoryList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    ItemModel item = data.getValue(ItemModel.class);
                    if (item != null) {
                        item.setId(data.getKey());
                        inventoryList.add(item);
                    }
                }

                // Setup adapter
                adapter = new AdminItemAdapter(requireContext(), inventoryList, item -> {
                    // Handle item click
                });

                rvInventory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Gagal memuat inventory: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
