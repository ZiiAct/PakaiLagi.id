package com.example.pakailagi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pakailagi.ItemInspectionActivity;
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
    private DatabaseReference itemsRef;
    private DatabaseReference receiveReqRef;
    private TextView tvTotalItems;

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
        itemsRef = FirebaseDatabase.getInstance().getReference("items");
        receiveReqRef = FirebaseDatabase.getInstance().getReference("receiveReq");
        rvInventory = view.findViewById(R.id.rvInventory);
        rvInventory.setLayoutManager(new LinearLayoutManager(requireContext()));
        tvTotalItems = view.findViewById(R.id.tvTotalItems);

        inventoryList = new ArrayList<>();

        // Load inventory items (real-time)
        fetchInventoryItems();
    }

    private void fetchInventoryItems() {
        // Real-time listener: fetch all items, filter for pending + approved
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                inventoryList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    ItemModel item = data.getValue(ItemModel.class);
                    if (item != null) {
                        item.setId(data.getKey());
                        String status = item.getStatus();
                        // Only include pending and approved items
                        if ("pending".equals(status) || "approved".equals(status)) {
                            inventoryList.add(item);
                        }
                    }
                }

                // Update total items count dynamically
                tvTotalItems.setText(String.valueOf(inventoryList.size()));

                // Setup adapter with click listener
                adapter = new AdminItemAdapter(requireContext(), inventoryList, item -> {
                    // Determine inspection mode based on item status
                    determineInspectionMode(item);
                });

                rvInventory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Gagal memuat inventory: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Determine the correct inspection mode for an item:
     * - pending item → Mode 1 (Item Approval)
     * - approved item with pending receiveReq → Mode 2 (Request Approval)
     * - approved item with "process" receiveReq → Mode 3 (Process & Completion)
     * - approved item with no receiveReq → Mode 1 fallback (just view)
     */
    private void determineInspectionMode(ItemModel item) {
        if ("pending".equals(item.getStatus())) {
            // Mode 1: Item Approval
            navigateToInspection(item, 1, null);
        } else if ("approved".equals(item.getStatus())) {
            // Check if there's a receiveReq for this item
            receiveReqRef.orderByChild("itemId").equalTo(item.getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!isAdded()) return;

                            String foundRequestId = null;
                            String requestStatus = null;

                            for (DataSnapshot reqData : snapshot.getChildren()) {
                                String status = reqData.child("status").getValue(String.class);
                                // Find the most relevant active request (pending or process)
                                if ("pending".equals(status)) {
                                    foundRequestId = reqData.getKey();
                                    requestStatus = "pending";
                                    break; // pending request takes priority
                                } else if ("process".equals(status)) {
                                    foundRequestId = reqData.getKey();
                                    requestStatus = "process";
                                    // Don't break — keep looking for pending
                                }
                            }

                            if (foundRequestId != null && "pending".equals(requestStatus)) {
                                // Mode 2: Request Approval
                                navigateToInspection(item, 2, foundRequestId);
                            } else if (foundRequestId != null && "process".equals(requestStatus)) {
                                // Mode 3: Process & Completion
                                navigateToInspection(item, 3, foundRequestId);
                            } else {
                                // No active request — just view as Mode 1
                                navigateToInspection(item, 1, null);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            if (isAdded()) {
                                Toast.makeText(requireContext(), "Gagal cek request: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Navigate to ItemInspectionActivity with the appropriate inspection mode.
     */
    private void navigateToInspection(ItemModel item, int mode, String requestId) {
        Intent intent = new Intent(requireContext(), ItemInspectionActivity.class);
        intent.putExtra("itemId", item.getId());
        intent.putExtra("itemName", item.getItemName() != null ? item.getItemName() : "");
        intent.putExtra("donorName", item.getDonorName() != null ? item.getDonorName() : "Unknown");
        intent.putExtra("status", item.getStatus() != null ? item.getStatus() : "");
        intent.putExtra("description", item.getDescription() != null ? item.getDescription() : "");
        intent.putExtra("condition", item.getCondition() != null ? item.getCondition() : "");
        intent.putExtra("imageUrl", item.getImageUrl() != null ? item.getImageUrl() : "");
        intent.putExtra("location", item.getLocation() != null ? item.getLocation() : "");
        intent.putExtra("category", item.getCategory() != null ? item.getCategory() : "");
        intent.putExtra("inspectionMode", mode);
        if (requestId != null) {
            intent.putExtra("requestId", requestId);
        }
        startActivity(intent);
    }
}
