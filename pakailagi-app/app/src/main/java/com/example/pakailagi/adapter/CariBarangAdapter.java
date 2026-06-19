package com.example.pakailagi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pakailagi.R;
import com.example.pakailagi.model.CariBarangItem;
import java.util.List;

public class CariBarangAdapter extends RecyclerView.Adapter<CariBarangAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(CariBarangItem item);
    }

    private final List<CariBarangItem> items;
    private final OnItemClickListener listener;

    public CariBarangAdapter(List<CariBarangItem> items, OnItemClickListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cari_barang, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CariBarangItem item = items.get(position);
        holder.tvName.setText(item.getName());
        holder.tvLocation.setText(item.getLocation());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvLocation;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName     = itemView.findViewById(R.id.tvCariItemName);
            tvLocation = itemView.findViewById(R.id.tvCariItemLocation);
        }
    }
}
