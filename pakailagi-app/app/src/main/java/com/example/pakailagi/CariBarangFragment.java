package com.example.pakailagi;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pakailagi.adapter.CariBarangAdapter;
import com.example.pakailagi.model.CariBarangItem;
import java.util.ArrayList;
import java.util.List;

public class CariBarangFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cari_barang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View cariHeader = view.findViewById(R.id.cariHeader);
        final int chLeft   = cariHeader.getPaddingLeft();
        final int chTop    = cariHeader.getPaddingTop();
        final int chRight  = cariHeader.getPaddingRight();
        final int chBottom = cariHeader.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            int sbH = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            cariHeader.setPadding(chLeft, chTop + sbH, chRight, chBottom);
            return insets;
        });

        view.findViewById(R.id.btnCariBack).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateTo(R.id.nav_home);
            }
        });

        RecyclerView recycler = view.findViewById(R.id.recyclerCariBarang);
        recycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recycler.setAdapter(new CariBarangAdapter(getDummyItems(), item -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showDetailBarang(item.getName(), item.getLocation());
            }
        }));
    }

    private List<CariBarangItem> getDummyItems() {
        List<CariBarangItem> list = new ArrayList<>();
        list.add(new CariBarangItem("Meja Belajar Kayu", "Jakarta Selatan"));
        list.add(new CariBarangItem("Sepeda Anak 16 inch", "Bekasi"));
        list.add(new CariBarangItem("Sofa Bed Minimalis", "Tangerang"));
        list.add(new CariBarangItem("Printer Canon G2010", "Tangerang"));
        list.add(new CariBarangItem("Tas Ransel Hiking", "Bogor"));
        list.add(new CariBarangItem("Kursi Kantor Ergonomis", "Bogor"));
        list.add(new CariBarangItem("Lemari Baju 3 Pintu", "Depok"));
        list.add(new CariBarangItem("Laptop Asus VivoBook", "Jakarta Timur"));
        return list;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().getWindow().setStatusBarColor(
                    requireContext().getColor(R.color.cari_header));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().getWindow().setStatusBarColor(
                    requireContext().getColor(R.color.primary_green));
        }
    }
}
