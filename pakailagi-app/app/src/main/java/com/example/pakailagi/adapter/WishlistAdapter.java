package com.example.pakailagi.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pakailagi.R;
import com.example.pakailagi.model.WishlistItem;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    public interface OnItemActionListener {
        void onDelete(int position);
        void onViewDetail(WishlistItem item);
        void onApply(WishlistItem item);
    }

    private final List<WishlistItem> items;
    private final OnItemActionListener listener;

    public WishlistAdapter(List<WishlistItem> items, OnItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wishlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WishlistItem item = items.get(position);
        holder.tvName.setText(item.getName());
        holder.tvLocation.setText(item.getLocation());
        holder.tvCondition.setText(item.getCondition());

        if ("LIKE NEW".equals(item.getCondition())) {
            holder.tvCondition.setBackgroundColor(Color.parseColor("#1A7B42"));
        } else {
            holder.tvCondition.setBackgroundColor(Color.parseColor("#495057"));
        }

        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID) listener.onDelete(pos);
        });
        holder.btnDetail.setOnClickListener(v -> listener.onViewDetail(item));
        holder.btnApply.setOnClickListener(v -> listener.onApply(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView tvCondition, tvName, tvLocation;
        ImageButton btnDelete;
        CardView btnDetail, btnApply;

        ViewHolder(View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgWishlistItem);
            tvCondition = itemView.findViewById(R.id.tvConditionBadge);
            tvName = itemView.findViewById(R.id.tvWishlistItemName);
            tvLocation = itemView.findViewById(R.id.tvWishlistLocation);
            btnDelete = itemView.findViewById(R.id.btnDeleteWishlist);
            btnDetail = itemView.findViewById(R.id.btnLihatDetail);
            btnApply = itemView.findViewById(R.id.btnAjukan);
        }
    }
}
