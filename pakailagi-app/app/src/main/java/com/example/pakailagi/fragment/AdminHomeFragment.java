package com.example.pakailagi.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class AdminHomeFragment extends Fragment {

    private RecyclerView rvPendingItems;
    private AdminItemAdapter adapter;
    private List<ItemModel> pendingItemList;
    private DatabaseReference databaseReference;

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

        pendingItemList = new ArrayList<>();

        // Load pending items data
        fetchPendingItems();
    }

    private void fetchPendingItems() {
        // Query: Hanya ambil data yang 'status'-nya 'pending'
        databaseReference.orderByChild("status").equalTo("pending")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
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
                                showApprovalDialog(item);
                            }
                        });

                        rvPendingItems.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showApprovalDialog(ItemModel item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Tinjau Persetujuan");
        builder.setMessage("Apa yang ingin Anda lakukan pada barang: " + item.getItemName() + " ?");

        builder.setPositiveButton("Setuju", (dialog, which) -> {
            updateItemStatus(item.getId(), "available");
        });

        builder.setNegativeButton("Tolak", (dialog, which) -> {
            updateItemStatus(item.getId(), "rejected");
        });

        builder.setNeutralButton("Batal", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void updateItemStatus(String itemId, String newStatus) {
        databaseReference.child(itemId).child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Barang berhasil di-" + newStatus, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Gagal update status!", Toast.LENGTH_SHORT).show();
                });
    }
}
