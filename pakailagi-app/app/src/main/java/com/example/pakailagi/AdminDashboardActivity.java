package com.example.pakailagi;

import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView rvPendingItems;
    private AdminItemAdapter adapter;
    private List<ItemModel> pendingItemList;

    // Ganti "items" dengan nama node tabel barang kamu di Firebase jika berbeda
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // 1. Inisialisasi Firebase & RecyclerView
        databaseReference = FirebaseDatabase.getInstance().getReference("items");
        rvPendingItems = findViewById(R.id.rvPendingItems);
        rvPendingItems.setLayoutManager(new LinearLayoutManager(this));

        pendingItemList = new ArrayList<>();

        // 2. Tarik Data dari Firebase
        fetchPendingItems();

        setupInteractions();
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
                                // Pastikan ID barang diambil dari Key Firebase agar bisa di-update nanti
                                item.setId(data.getKey());
                                pendingItemList.add(item);
                            }
                        }

                        // 3. Masukkan data ke Adapter buatan tim Frontend
                        adapter = new AdminItemAdapter(AdminDashboardActivity.this, pendingItemList, new AdminItemAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(ItemModel item) {
                                // Jika Admin mengklik baris barang, munculkan dialog persetujuan
                                showApprovalDialog(item);
                            }
                        });

                        rvPendingItems.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminDashboardActivity.this, "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showApprovalDialog(ItemModel item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        // Update field 'status' di Firebase
        databaseReference.child(itemId).child("status").setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AdminDashboardActivity.this, "Barang berhasil di-" + newStatus, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminDashboardActivity.this, "Gagal update status!", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupInteractions() {
        // 1. Jika tombol diklik, arahkan ke Activity Admin yang sesuai

        // Ganti InventoryActivity.class -> AdminInventoryActivity.class
        findViewById(R.id.nav_inventory).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminInventoryActivity.class));
        });

        // Ganti ShopActivity.class -> AdminShopActivity.class
        findViewById(R.id.nav_shop).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminShopActivity.class));
        });

        // Ganti ProfileActivity.class -> AdminProfileActivity.class
        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminProfileActivity.class));
        });

        // Untuk Home, jika memang belum ada AdminHomeActivity,
        // sesuaikan dengan nama class yang Anda miliki.
    }
}