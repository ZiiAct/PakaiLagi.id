package com.example.pakailagi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pakailagi.R;
import com.example.pakailagi.model.PengajuanItem;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class PengajuanAdapter extends RecyclerView.Adapter<PengajuanAdapter.ViewHolder> {

    private final List<PengajuanItem> items;

    public PengajuanAdapter(List<PengajuanItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pengajuan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        PengajuanItem item = items.get(position);

        h.tvName.setText(item.getName());
        h.tvDistance.setText(item.getDistance());
        h.tvDate.setText("Diajukan pada " + item.getDate());
        h.tvAddress.setText(item.getPickupLocation());

        if (item.getStatus() == PengajuanItem.Status.PENDING) {
            h.imgStatus.setImageResource(R.drawable.ic_warning);
            h.tvStatus.setText(R.string.status_menunggu);
            h.tvStatus.setTextColor(h.itemView.getContext().getColor(R.color.status_orange));
            h.layoutPendingButtons.setVisibility(View.VISIBLE);
            h.layoutReadyContent.setVisibility(View.GONE);
        } else {
            h.imgStatus.setImageResource(R.drawable.ic_check_circle);
            h.tvStatus.setText(R.string.status_siap);
            h.tvStatus.setTextColor(h.itemView.getContext().getColor(R.color.primary_green));
            h.layoutPendingButtons.setVisibility(View.GONE);
            h.layoutReadyContent.setVisibility(View.VISIBLE);
        }

        h.btnBatalkan.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Batalkan: " + item.getName(), Toast.LENGTH_SHORT).show());
        h.btnDetailItem.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Detail: " + item.getName(), Toast.LENGTH_SHORT).show());
        h.btnKabari.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Sudah diambil: " + item.getName(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDistance, tvDate, tvStatus, tvAddress;
        ImageView imgStatus;
        LinearLayout layoutPendingButtons, layoutReadyContent;
        MaterialButton btnBatalkan, btnDetailItem, btnKabari;

        ViewHolder(View v) {
            super(v);
            tvName              = v.findViewById(R.id.tvPengajuanName);
            tvDistance          = v.findViewById(R.id.tvPengajuanDistance);
            tvDate              = v.findViewById(R.id.tvPengajuanDate);
            tvStatus            = v.findViewById(R.id.tvPengajuanStatus);
            tvAddress           = v.findViewById(R.id.tvPickupAddress);
            imgStatus           = v.findViewById(R.id.imgPengajuanStatus);
            layoutPendingButtons = v.findViewById(R.id.layoutPendingButtons);
            layoutReadyContent  = v.findViewById(R.id.layoutReadyContent);
            btnBatalkan         = v.findViewById(R.id.btnBatalkan);
            btnDetailItem       = v.findViewById(R.id.btnDetailItem);
            btnKabari           = v.findViewById(R.id.btnKabari);
        }
    }
}
