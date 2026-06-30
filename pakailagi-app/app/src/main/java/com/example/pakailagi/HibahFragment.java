package com.example.pakailagi;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressWarnings("all")
public class HibahFragment extends Fragment {

    // Ganti dengan URL server Render kamu setelah deploy
    // Contoh: "https://pakailagi-server.onrender.com"
    // Untuk emulator lokal gunakan: "http://10.0.2.2:8080"
    private static final String SERVER_BASE_URL = "https://pakailagi-id.onrender.com";
    private static final String UPLOAD_URL = SERVER_BASE_URL + "/api/hibah/upload";

    private Uri selectedImageUri = null;

    // Launcher untuk membuka galeri foto
    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    // Tampilkan gambar yang dipilih ke ImageView di layout
                    if (ivImage != null) {
                        ivImage.setImageURI(uri);
                        ivImage.setColorFilter(null); // Hapus filter warna sebelumnya
                    }
                    Toast.makeText(getContext(), "Gambar dipilih!", Toast.LENGTH_SHORT).show();
                }
            });

    // Referensi ImageView disimpan di field agar bisa diakses dari launcher
    private ImageView ivImage = null;

    public HibahFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hibah, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ImageView ivImageView = view.findViewById(R.id.ivImage);
            if (ivImageView != null) {
                ivImage = ivImageView;
            }

            EditText etNamaBarang = view.findViewById(R.id.etNamaBarang);
            TextView tvKategori = view.findViewById(R.id.tvKategori);
            View layoutKategori = view.findViewById(R.id.layoutKategori);
            EditText etDeskripsiBarang = view.findViewById(R.id.etDeskripsiBarang);
            EditText etLokasiWaktu = view.findViewById(R.id.etLokasiWaktu);
            TextView chipPickup = view.findViewById(R.id.chipPickup);
            TextView chipCOD = view.findViewById(R.id.chipCOD);
            TextView chipKurir = view.findViewById(R.id.chipKurir);
            CardView btnSubmit = view.findViewById(R.id.btnSubmitHibah);

            View boxUtama = view.findViewById(R.id.boxUtama);
            if (boxUtama != null) {
                boxUtama.setOnClickListener(v -> getContent.launch("image/*"));
            }

            if (layoutKategori != null && tvKategori != null) {
                layoutKategori.setOnClickListener(v -> {
                    String[] categories = { "Elektronik", "Perabotan", "Buku", "Olahraga", "Lainnya" };
                    new AlertDialog.Builder(getContext())
                            .setTitle("Pilih Kategori")
                            .setItems(categories, (dialog, which) -> {
                                tvKategori.setText(categories[which]);
                                tvKategori.setTextColor(Color.parseColor("#212529"));
                            }).show();
                });
            }

            View.OnClickListener chipListener = v -> {
                if (chipPickup != null) {
                    chipPickup.setBackgroundResource(R.drawable.bg_chip_outline_grey);
                    chipPickup.setTextColor(Color.parseColor("#6C757D"));
                }
                if (chipCOD != null) {
                    chipCOD.setBackgroundResource(R.drawable.bg_chip_outline_grey);
                    chipCOD.setTextColor(Color.parseColor("#6C757D"));
                }
                if (chipKurir != null) {
                    chipKurir.setBackgroundResource(R.drawable.bg_chip_outline_grey);
                    chipKurir.setTextColor(Color.parseColor("#6C757D"));
                }

                TextView clicked = (TextView) v;
                clicked.setBackgroundResource(R.drawable.bg_chip_green_solid);
                clicked.setTextColor(Color.WHITE);
            };

            if (chipPickup != null) chipPickup.setOnClickListener(chipListener);
            if (chipCOD != null) chipCOD.setOnClickListener(chipListener);
            if (chipKurir != null) chipKurir.setOnClickListener(chipListener);

            if (btnSubmit != null) {
                btnSubmit.setOnClickListener(v -> {
                    String namaBarang = etNamaBarang != null ? etNamaBarang.getText().toString().trim() : "";
                    String kategori = tvKategori != null ? tvKategori.getText().toString().trim() : "Lainnya";
                    String deskripsi = etDeskripsiBarang != null ? etDeskripsiBarang.getText().toString().trim() : "";
                    String lokasiWaktu = etLokasiWaktu != null ? etLokasiWaktu.getText().toString().trim() : "";
                    String deliveryOption = "Pick Up Langsung";

                    if (chipCOD != null && chipCOD.getCurrentTextColor() == Color.WHITE) {
                        deliveryOption = "Ketemuan (COD)";
                    } else if (chipKurir != null && chipKurir.getCurrentTextColor() == Color.WHITE) {
                        deliveryOption = "Kirim via Kurir";
                    }
                    if (chipPickup != null && chipPickup.getCurrentTextColor() == Color.WHITE) {
                        deliveryOption = "Pick Up Langsung";
                    }

                    if (TextUtils.isEmpty(namaBarang)) {
                        Toast.makeText(getContext(), "Isi nama barang terlebih dahulu.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(deskripsi)) {
                        Toast.makeText(getContext(), "Isi deskripsi barang terlebih dahulu.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(lokasiWaktu)) {
                        Toast.makeText(getContext(), "Isi lokasi / waktu pengambilan terlebih dahulu.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (selectedImageUri == null) {
                        Toast.makeText(getContext(), "Pilih gambar dulu!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    uploadKeServer(selectedImageUri, namaBarang, kategori, deskripsi, deliveryOption, lokasiWaktu);
                });
            }

        } catch (Exception e) {
            // Silent catch untuk mencegah crash jika struktur layout berubah
        }
    }

    /**
     * Meng-copy InputStream dari ContentResolver ke file sementara di cache
     * directory.
     * Ini lebih reliable daripada getRealPathFromURI yang tidak bisa digunakan di
     * Android 10+.
     *
     * @param uri URI gambar yang dipilih dari galeri
     * @return File sementara yang berisi data gambar, atau null jika gagal
     */
    private File copyUriToTempFile(Uri uri) {
        try {
            ContentResolver contentResolver = requireActivity().getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null)
                return null;

            // Buat file sementara di cache directory
            File tempFile = new File(requireActivity().getCacheDir(), "upload_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Mengirim gambar ke endpoint Spring Boot menggunakan OkHttp multipart.
     * Setelah berhasil mendapat URL dari server, simpan data hibah ke Firebase
     * Realtime Database.
     * Dijalankan di background thread (enqueue) agar UI tidak freeze.
     *
     * @param imageUri URI gambar yang dipilih dari galeri
     */
    private void uploadKeServer(Uri imageUri, String namaBarang, String kategori,
                                  String deskripsi, String deliveryOption, String lokasiWaktu) {
        // Gunakan ContentResolver + temp file (compatible dengan Android 10+ Scoped
        // Storage)
        File imageFile = copyUriToTempFile(imageUri);
        if (imageFile == null || !imageFile.exists()) {
            Toast.makeText(getContext(), "Tidak bisa membaca file gambar!", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();

        // Bangun multipart request body (hanya file, data lain disimpan langsung ke
        // Firebase)
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        imageFile.getName(),
                        RequestBody.create(imageFile, MediaType.parse("image/*")))
                .build();

        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();

        // Jalankan request di background thread
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Hapus file sementara
                imageFile.delete();

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                            "Gagal terhubung ke server. Cek koneksi & IP server.",
                            Toast.LENGTH_LONG).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Hapus file sementara
                imageFile.delete();

                if (getActivity() == null)
                    return;

                if (response.isSuccessful() && response.body() != null) {
                    // Server mengembalikan URL gambar Google Drive
                    String imageUrl = response.body().string().trim();

                    // Simpan data hibah ke Firebase Realtime Database
                    simpanKeFirebase(imageUrl, namaBarang, kategori, deskripsi, deliveryOption, lokasiWaktu);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                            "Upload gagal: " + errorBody,
                            Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    /**
     * Menyimpan data hibah ke Firebase Realtime Database setelah upload gambar
     * berhasil.
     * Data disimpan di node "/hibah_items/{pushId}".
     *
     * @param imageUrl URL gambar publik dari Google Drive
     */
    private void simpanKeFirebase(String imageUrl, String namaBarang, String kategori,
                                    String deskripsi, String deliveryOption, String lokasiWaktu) {
        DatabaseReference hibahRef = FirebaseDatabase.getInstance().getReference("hibah_items");
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests");

        // Ambil user ID dari Firebase Auth (jika sudah login)
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (currentUser != null) ? currentUser.getUid() : "anonymous";

        // Siapkan data hibah
        Map<String, Object> hibahData = new HashMap<>();
        hibahData.put("namaBarang", namaBarang);
        hibahData.put("kategori", kategori);
        hibahData.put("deskripsi", deskripsi);
        hibahData.put("deliveryOption", deliveryOption);
        hibahData.put("lokasiWaktu", lokasiWaktu);
        hibahData.put("imageUrl", imageUrl);
        hibahData.put("userId", userId);
        hibahData.put("timestamp", System.currentTimeMillis());
        hibahData.put("status", "PENDING");

        // Push data ke Firebase (auto-generate key)
        String newHibahKey = hibahRef.push().getKey();
        if (newHibahKey == null) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                        "Gagal membuat data hibah baru.",
                        Toast.LENGTH_LONG).show());
            }
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("/hibah_items/" + newHibahKey, hibahData);

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("userId", userId);
        requestData.put("hibahId", newHibahKey);
        requestData.put("namaBarang", namaBarang);
        requestData.put("kategori", kategori);
        requestData.put("deskripsi", deskripsi);
        requestData.put("deliveryOption", deliveryOption);
        requestData.put("lokasiWaktu", lokasiWaktu);
        requestData.put("imageUrl", imageUrl);
        requestData.put("status", "PENDING");
        requestData.put("createdAt", System.currentTimeMillis());

        updates.put("/requests/" + newHibahKey, requestData);

        FirebaseDatabase.getInstance().getReference().updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(),
                                    "Barang berhasil dihibahkan dan masuk ke request admin!",
                                    Toast.LENGTH_LONG).show();
                            View homeNav = getActivity().findViewById(R.id.nav_home_layout);
                            if (homeNav != null) homeNav.performClick();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                                "Gagal menyimpan ke database: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
                    }
                });
    }
}