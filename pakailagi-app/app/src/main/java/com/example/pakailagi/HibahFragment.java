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
    private static final String SERVER_BASE_URL = "http://10.0.2.2:8080";
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

    public HibahFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hibah, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ViewGroup mainLayout = (ViewGroup) ((android.widget.ScrollView) view).getChildAt(0);

            // 1. Ikon Info (Dialog)
            View iconInfo = ((ViewGroup) mainLayout.getChildAt(0)).getChildAt(1);
            iconInfo.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                    .setTitle("Info Hibah")
                    .setMessage("Pastikan barang dalam kondisi layak pakai dan sesuai deskripsi.")
                    .setPositiveButton("Paham", null).show());

            // 2. Upload Foto — Buka Galeri & Tampilkan ke ImageView
            ViewGroup fotoGrid = (ViewGroup) mainLayout.getChildAt(5);
            View boxUtama = fotoGrid.getChildAt(0);
            ivImage = (ImageView) ((CardView) fotoGrid.getChildAt(1)).getChildAt(0);

            boxUtama.setOnClickListener(v -> {
                // Buka galeri, hanya tampilkan file gambar
                getContent.launch("image/*");
            });

            // 3. Dropdown Pilih Kategori
            View dropdownKategori = mainLayout.getChildAt(11);
            TextView tvKategori = (TextView) ((android.widget.RelativeLayout) dropdownKategori).getChildAt(0);
            dropdownKategori.setOnClickListener(v -> {
                String[] categories = {"Elektronik", "Perabotan", "Buku", "Olahraga", "Lainnya"};
                new AlertDialog.Builder(getContext())
                        .setTitle("Pilih Kategori")
                        .setItems(categories, (dialog, which) -> {
                            tvKategori.setText(categories[which]);
                            tvKategori.setTextColor(Color.parseColor("#212529"));
                        }).show();
            });

            // 4. Instruksi Pengambilan (Chips berubah warna saat diklik)
            ViewGroup chipsRow = (ViewGroup) mainLayout.getChildAt(17);
            TextView chipPickup = (TextView) chipsRow.getChildAt(0);
            TextView chipCOD = (TextView) chipsRow.getChildAt(1);
            TextView chipKurir = (TextView) mainLayout.getChildAt(18);

            View.OnClickListener chipListener = v -> {
                // Reset semua ke abu-abu
                chipPickup.setBackgroundResource(R.drawable.bg_chip_outline_grey);
                chipPickup.setTextColor(Color.parseColor("#6C757D"));
                chipCOD.setBackgroundResource(R.drawable.bg_chip_outline_grey);
                chipCOD.setTextColor(Color.parseColor("#6C757D"));
                chipKurir.setBackgroundResource(R.drawable.bg_chip_outline_grey);
                chipKurir.setTextColor(Color.parseColor("#6C757D"));

                // Ubah yang diklik jadi hijau
                TextView clicked = (TextView) v;
                clicked.setBackgroundResource(R.drawable.bg_chip_green_solid);
                clicked.setTextColor(Color.WHITE);
            };

            chipPickup.setOnClickListener(chipListener);
            chipCOD.setOnClickListener(chipListener);
            chipKurir.setOnClickListener(chipListener);

            // 5. Tombol Submit — Validasi gambar lalu upload ke server
            int childCount = mainLayout.getChildCount();
            CardView btnSubmit = (CardView) mainLayout.getChildAt(childCount - 2);
            btnSubmit.setOnClickListener(v -> {
                if (selectedImageUri != null) {
                    // Upload gambar ke Spring Boot server di background thread
                    uploadKeServer(selectedImageUri);
                } else {
                    Toast.makeText(getContext(), "Pilih gambar dulu!", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            // Silent catch untuk mencegah crash jika struktur layout berubah
        }
    }

    /**
     * Meng-copy InputStream dari ContentResolver ke file sementara di cache directory.
     * Ini lebih reliable daripada getRealPathFromURI yang tidak bisa digunakan di Android 10+.
     *
     * @param uri URI gambar yang dipilih dari galeri
     * @return File sementara yang berisi data gambar, atau null jika gagal
     */
    private File copyUriToTempFile(Uri uri) {
        try {
            ContentResolver contentResolver = requireActivity().getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream == null) return null;

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
     * Setelah berhasil mendapat URL dari server, simpan data hibah ke Firebase Realtime Database.
     * Dijalankan di background thread (enqueue) agar UI tidak freeze.
     *
     * @param imageUri URI gambar yang dipilih dari galeri
     */
    private void uploadKeServer(Uri imageUri) {
        // Gunakan ContentResolver + temp file (compatible dengan Android 10+ Scoped Storage)
        File imageFile = copyUriToTempFile(imageUri);
        if (imageFile == null || !imageFile.exists()) {
            Toast.makeText(getContext(), "Tidak bisa membaca file gambar!", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();

        // Bangun multipart request body (hanya file, data lain disimpan langsung ke Firebase)
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        imageFile.getName(),
                        RequestBody.create(imageFile, MediaType.parse("image/*"))
                )
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
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(),
                                    "Gagal terhubung ke server. Cek koneksi & IP server.",
                                    Toast.LENGTH_LONG).show()
                    );
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Hapus file sementara
                imageFile.delete();

                if (getActivity() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    // Server mengembalikan URL gambar Google Drive
                    String imageUrl = response.body().string().trim();

                    // Simpan data hibah ke Firebase Realtime Database
                    simpanKeFirebase(imageUrl);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(),
                                    "Upload gagal: " + errorBody,
                                    Toast.LENGTH_LONG).show()
                    );
                }
            }
        });
    }

    /**
     * Menyimpan data hibah ke Firebase Realtime Database setelah upload gambar berhasil.
     * Data disimpan di node "/hibah_items/{pushId}".
     *
     * @param imageUrl URL gambar publik dari Google Drive
     */
    private void simpanKeFirebase(String imageUrl) {
        DatabaseReference hibahRef = FirebaseDatabase.getInstance().getReference("hibah_items");

        // Ambil user ID dari Firebase Auth (jika sudah login)
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (currentUser != null) ? currentUser.getUid() : "anonymous";

        // Siapkan data hibah
        Map<String, Object> hibahData = new HashMap<>();
        hibahData.put("namaBarang", "Barang Hibah"); // TODO: Ambil dari input field di layout
        hibahData.put("imageUrl", imageUrl);
        hibahData.put("userId", userId);
        hibahData.put("timestamp", System.currentTimeMillis());
        hibahData.put("status", "PENDING");

        // Push data ke Firebase (auto-generate key)
        hibahRef.push().setValue(hibahData)
                .addOnSuccessListener(aVoid -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(),
                                    "Barang berhasil dihibahkan!",
                                    Toast.LENGTH_LONG).show();
                            // Kembali ke halaman Home
                            View homeNav = getActivity().findViewById(R.id.nav_home_layout);
                            if (homeNav != null) homeNav.performClick();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(),
                                        "Gagal menyimpan ke database: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show()
                        );
                    }
                });
    }
}