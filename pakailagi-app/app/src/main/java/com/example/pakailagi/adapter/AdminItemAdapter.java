package com.example.pakailagi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pakailagi.R;
import com.example.pakailagi.model.ItemModel;

import java.util.List;

public class AdminItemAdapter extends RecyclerView.Adapter<AdminItemAdapter.AdminViewHolder> {

    private Context context;
    private List<ItemModel> itemList;
    private OnItemClickListener listener;

    // 1. Interface khusus agar baris barang bisa diklik dan merespon ke Activity utama
    public interface OnItemClickListener {
        void onItemClick(ItemModel item);
    }

    // 2. Constructor: Wadah penampung saat Activity mengirim data ke Adapter ini
    public AdminItemAdapter(Context context, List<ItemModel> itemList, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    // 3. Menghubungkan Adapter dengan file desain cetakan (XML) buatan Anda
    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_admin, parent, false);
        return new AdminViewHolder(view);
    }

    // 4. Proses "Menjahit" / Memasukkan data teks ke dalam cetakan UI
    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        ItemModel item = itemList.get(position);

        // Set teks sesuai data dari database
        holder.tvItemName.setText(item.getItemName());
        holder.tvDonorName.setText("Oleh: " + item.getDonorName());
        holder.tvStatus.setText(item.getStatus());

        // Logika saat baris ini diklik oleh Admin
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    // 5. Menghitung ada berapa banyak barang yang pending
    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    // 6. Kelas "Kaitan" untuk menyambungkan ID dari XML ke Java
    public class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvDonorName, tvStatus;
        ImageView ivItemImage;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            // Mencari komponen berdasarkan ID di item_pending_admin.xml
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvDonorName = itemView.findViewById(R.id.tvDonorName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
        }
    }
}