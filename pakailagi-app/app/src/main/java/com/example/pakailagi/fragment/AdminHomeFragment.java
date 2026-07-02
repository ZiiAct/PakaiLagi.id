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
import java.util.Calendar;
import java.util.List;

public class AdminHomeFragment extends Fragment {

    private RecyclerView rvPendingItems;
    private AdminItemAdapter adapter;
    private List<ItemModel> pendingItemList;
    private DatabaseReference databaseReference;

    // Dashboard stat TextViews
    private TextView tvPendingCount;
    private TextView tvAccCount;
    private TextView tvRejCount;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase & RecyclerView
        databaseReference = FirebaseDatabase.getInstance().getReference("items");
        rvPendingItems = view.findViewById(R.id.rvPendingItems);
        rvPendingItems.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Dashboard stat views
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        tvAccCount = view.findViewById(R.id.tvAccCount);
        tvRejCount = view.findViewById(R.id.tvRejCount);

        pendingItemList = new ArrayList<>();

        // Load pending items data & dashboard stats
        fetchPendingItems();
        fetchDashboardStats();
    }

    private void fetchPendingItems() {
        // Query: Hanya ambil data yang 'status'-nya 'pending'
        databaseReference.orderByChild("status").equalTo("pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isAdded()) return;
                        pendingItemList.clear();

                        for (DataSnapshot data : snapshot.getChildren()) {
                            ItemModel item = data.getValue(ItemModel.class);
                            if (item != null) {
                                item.setId(data.getKey());
                                pendingItemList.add(item);
                            }
                        }

                        // Masukkan data ke Adapter
                        adapter = new AdminItemAdapter(requireContext(), pendingItemList, new AdminItemAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(ItemModel item) {
                                // Navigate to ItemInspectionActivity in Mode 1 (Item Approval)
                                navigateToInspection(item, 1, null);
                            }
                        });

                        rvPendingItems.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Fetches real-time dashboard statistics:
     * - Pending count: items with status "pending"
     * - Accepted today: statusLog entries with latestatus "approved" and today's timestamp
     * - Rejected today: statusLog entries with latestatus "rejected" and today's timestamp
     */
    private void fetchDashboardStats() {
        // 1. Pending count — real-time listener on items node
        databaseReference.orderByChild("status").equalTo("pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isAdded()) return;
                        long pendingCount = snapshot.getChildrenCount();
                        tvPendingCount.setText(String.valueOf(pendingCount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Silent fail for stats
                    }
                });

        // 2. Accepted & Rejected today — query statusLog for today's entries
        // Get today's midnight timestamp
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long todayStart = cal.getTimeInMillis();

        DatabaseReference statusLogRef = FirebaseDatabase.getInstance().getReference("statusLog");
        statusLogRef.orderByChild("timestamp").startAt(todayStart)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isAdded()) return;
                        int acceptedToday = 0;
                        int rejectedToday = 0;

                        for (DataSnapshot logEntry : snapshot.getChildren()) {
                            String status = logEntry.child("latestatus").getValue(String.class);
                            if ("approved".equals(status)) {
                                acceptedToday++;
                            } else if ("rejected".equals(status)) {
                                rejectedToday++;
                            }
                        }

                        tvAccCount.setText(String.valueOf(acceptedToday));
                        tvRejCount.setText(String.valueOf(rejectedToday));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Silent fail for stats
                    }
                });
    }

    /**
     * Navigate to ItemInspectionActivity with the appropriate inspection mode.
     *
     * @param item           The item to inspect
     * @param mode           1 = Item Approval, 2 = Request Approval, 3 = Process & Completion
     * @param requestId      The receiveReq key (null for mode 1)
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
