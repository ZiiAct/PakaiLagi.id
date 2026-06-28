package com.example.pakailagi.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pakailagi.R;
import com.example.pakailagi.model.PengajuanItem;
import java.util.List;

@SuppressWarnings("all") // Mantra anti-bawel dari Android Studio
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

        // LOGIC PENGECEKAN STATUS (MENUNGGU vs SIAP DIAMBIL)
        if (item.getStatus() == PengajuanItem.Status.PENDING) {
            h.imgStatus.setImageResource(R.drawable.timeorange); // Pake ikon jam orange
            h.tvStatus.setText("Menunggu Persetujuan");
            h.tvStatus.setTextColor(Color.parseColor("#F57C00")); // Warna orange aman

            h.layoutPendingButtons.setVisibility(View.VISIBLE);
            h.layoutReadyContent.setVisibility(View.GONE);
        } else {
            h.imgStatus.setImageResource(R.drawable.ic_check_circle); // Pake ikon centang hijau
            h.tvStatus.setText("Siap Diambil");
            h.tvStatus.setTextColor(Color.parseColor("#1A7B42")); // Warna hijau aman

            h.layoutPendingButtons.setVisibility(View.GONE);
            h.layoutReadyContent.setVisibility(View.VISIBLE);
        }

        // AKSI KETIKA TOMBOL DIKLIK
        h.btnBatalkan.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Batalkan: " + item.getName(), Toast.LENGTH_SHORT).show());
        h.btnDetailItem.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Detail: " + item.getName(), Toast.LENGTH_SHORT).show());
        h.btnKabari.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Sudah diambil: " + item.getName(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() { return items.size(); }

    // Dikasih public biar error "visibility scope" hilang
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDistance, tvDate, tvStatus, tvAddress;
        ImageView imgStatus;
        LinearLayout layoutPendingButtons, layoutReadyContent;

        // PENTING: Pakai CardView, BUKAN MaterialButton biar nggak crash!
        CardView btnBatalkan, btnDetailItem, btnKabari;

        public ViewHolder(View v) {
            super(v);
            tvName              = v.findViewById(R.id.tvPengajuanName);
            tvDistance          = v.findViewById(R.id.tvPengajuanDistance);
            tvDate              = v.findViewById(R.id.tvPengajuanDate);
            tvStatus            = v.findViewById(R.id.tvPengajuanStatus);
            tvAddress           = v.findViewById(R.id.tvPickupAddress);
            imgStatus           = v.findViewById(R.id.imgPengajuanStatus);
            layoutPendingButtons = v.findViewById(R.id.layoutPendingButtons);
            layoutReadyContent  = v.findViewById(R.id.layoutReadyContent);

            // Komponen Tombol
            btnBatalkan         = v.findViewById(R.id.btnBatalkan);
            btnDetailItem       = v.findViewById(R.id.btnDetailItem);
            btnKabari           = v.findViewById(R.id.btnKabari);
        }
    }
}