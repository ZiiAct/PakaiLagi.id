package com.example.pakailagi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pakailagi.R;
import com.example.pakailagi.model.CariBarangItem;
import java.util.List;

@SuppressWarnings("all") // Mantra anti-bawel Android Studio
public class CariBarangAdapter extends RecyclerView.Adapter<CariBarangAdapter.ViewHolder> {

    private final List<CariBarangItem> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CariBarangItem item);
    }

    public CariBarangAdapter(List<CariBarangItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nyambungin ke layout item_cari_barang.xml lo
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cari_barang, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CariBarangItem item = items.get(position);

        holder.tvItemName.setText(item.getItemName());
        holder.tvItemLocation.setText(item.getLocation());
        holder.tvItemCondition.setText(item.getItemCondition());

        if (item.getImageResId() != 0) {
            holder.ivItemImage.setImageResource(item.getImageResId());
        }

        // Logic buat diklik dan pindah halaman
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemLocation, tvItemCondition;
        ImageView ivItemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // ID INI UDAH SINKRON 100% SAMA item_cari_barang.xml LO
            tvItemName      = itemView.findViewById(R.id.tvItemTitle);
            tvItemLocation  = itemView.findViewById(R.id.tvItemLocation);
            tvItemCondition = itemView.findViewById(R.id.tvItemStatus);
            ivItemImage     = itemView.findViewById(R.id.ivItemImage);
        }
    }
}