package com.example.pakailagi;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    private static final String TAG = "HibahFragment";

    // URL server Spring Boot (Render)
    private static final String SERVER_BASE_URL = "https://pakailagi-id.onrender.com";
    private static final String UPLOAD_URL = SERVER_BASE_URL + "/api/hibah/upload";

    private Uri selectedImageUri = null;

    // Referensi view yang diakses dari dalam lambda
    private ImageView ivImage = null;
    private CardView cardPreviewImage = null;

    // Launcher untuk membuka galeri foto
    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    if (ivImage != null) {
                        ivImage.setImageURI(uri);
                    }
                    // Tampilkan CardView preview gambar
                    if (cardPreviewImage != null) {
                        cardPreviewImage.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(getContext(), "Gambar dipilih!", Toast.LENGTH_SHORT).show();
                }
            });

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

        // Inisialisasi semua view
        ivImage = view.findViewById(R.id.ivImage);
        cardPreviewImage = view.findViewById(R.id.cardPreviewImage);

        EditText etNamaBarang = view.findViewById(R.id.etNamaBarang);
        TextView tvKategori = view.findViewById(R.id.tvKategori);
        View layoutKategori = view.findViewById(R.id.layoutKategori);
        EditText etDeskripsiBarang = view.findViewById(R.id.etDeskripsiBarang);
        EditText etKontakHibah = view.findViewById(R.id.etKontakHibah);
        EditText etLokasiWaktu = view.findViewById(R.id.etLokasiWaktu);

        // Chip pengiriman
        TextView chipPickup = view.findViewById(R.id.chipPickup);
        TextView chipCOD = view.findViewById(R.id.chipCOD);
        TextView chipKurir = view.findViewById(R.id.chipKurir);

        // Chip kondisi barang
        TextView chipGood = view.findViewById(R.id.chipGood);
        TextView chipFair = view.findViewById(R.id.chipFair);
        TextView chipPoor = view.findViewById(R.id.chipPoor);

        CardView btnSubmit = view.findViewById(R.id.btnSubmitHibah);

        // Pastikan card preview awalnya tersembunyi
        if (cardPreviewImage != null) {
            cardPreviewImage.setVisibility(View.GONE);
        }

        // Klik box utama → buka galeri
        View boxUtama = view.findViewById(R.id.boxUtama);
        if (boxUtama != null) {
            boxUtama.setOnClickListener(v -> getContent.launch("image/*"));
        }

        // Pilih Kategori via dialog
        if (layoutKategori != null && tvKategori != null) {
            layoutKategori.setOnClickListener(v -> {
                String[] categories = { "Elektronik", "Perabotan", "Buku", "Olahraga", "Pakaian", "Lainnya" };
                new AlertDialog.Builder(getContext())
                        .setTitle("Pilih Kategori")
                        .setItems(categories, (dialog, which) -> {
                            tvKategori.setText(categories[which]);
                            tvKategori.setTextColor(Color.parseColor("#212529"));
                        }).show();
            });
        }

        // Listener chip pengiriman — satu aktif, lainnya non-aktif
        View.OnClickListener deliveryChipListener = v -> {
            resetChip(chipPickup);
            resetChip(chipCOD);
            resetChip(chipKurir);
            activateChip((TextView) v);
        };
        if (chipPickup != null)
            chipPickup.setOnClickListener(deliveryChipListener);
        if (chipCOD != null)
            chipCOD.setOnClickListener(deliveryChipListener);
        if (chipKurir != null)
            chipKurir.setOnClickListener(deliveryChipListener);

        // Listener chip kondisi barang
        View.OnClickListener conditionChipListener = v -> {
            resetChip(chipGood);
            resetChip(chipFair);
            resetChip(chipPoor);
            activateChip((TextView) v);
        };
        if (chipGood != null)
            chipGood.setOnClickListener(conditionChipListener);
        if (chipFair != null)
            chipFair.setOnClickListener(conditionChipListener);
        if (chipPoor != null)
            chipPoor.setOnClickListener(conditionChipListener);

        // Submit
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> {
                String namaBarang = etNamaBarang != null ? etNamaBarang.getText().toString().trim() : "";
                String kategori = (tvKategori != null && !tvKategori.getText().toString().equals("Pilih Kategori"))
                        ? tvKategori.getText().toString().trim()
                        : "Lainnya";
                String deskripsi = etDeskripsiBarang != null ? etDeskripsiBarang.getText().toString().trim() : "";
                String kontak = etKontakHibah != null ? etKontakHibah.getText().toString().trim() : "";
                String lokasiWaktu = etLokasiWaktu != null ? etLokasiWaktu.getText().toString().trim() : "";

                // Baca kondisi barang dari chip yang aktif (textColor putih = aktif)
                String kondisiBarang = "Good";
                if (chipFair != null && chipFair.getCurrentTextColor() == Color.WHITE) {
                    kondisiBarang = "Fair";
                } else if (chipPoor != null && chipPoor.getCurrentTextColor() == Color.WHITE) {
                    kondisiBarang = "Poor";
                }

                // Baca opsi pengiriman dari chip aktif
                String deliveryOption = "Pick Up Langsung";
                if (chipCOD != null && chipCOD.getCurrentTextColor() == Color.WHITE)
                    deliveryOption = "Ketemuan (COD)";
                if (chipKurir != null && chipKurir.getCurrentTextColor() == Color.WHITE)
                    deliveryOption = "Kirim via Kurir";
                if (chipPickup != null && chipPickup.getCurrentTextColor() == Color.WHITE)
                    deliveryOption = "Pick Up Langsung";

                // Validasi input
                if (TextUtils.isEmpty(namaBarang)) {
                    Toast.makeText(getContext(), "Isi nama barang terlebih dahulu.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(deskripsi)) {
                    Toast.makeText(getContext(), "Isi deskripsi barang terlebih dahulu.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(kontak)) {
                    Toast.makeText(getContext(), "Isi nomor kontak terlebih dahulu.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedImageUri == null) {
                    Toast.makeText(getContext(), "Pilih gambar barang terlebih dahulu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getContext(), "Mengupload gambar ke server...", Toast.LENGTH_SHORT).show();
                uploadKeServer(selectedImageUri, namaBarang, kategori, deskripsi,
                        kondisiBarang, deliveryOption, lokasiWaktu, kontak);
            });
        }
    }

    /** Reset chip ke state tidak aktif (outline abu-abu). */
    private void resetChip(TextView chip) {
        if (chip == null)
            return;
        chip.setBackgroundResource(R.drawable.bg_chip_outline_grey);
        chip.setTextColor(Color.parseColor("#6C757D"));
    }

    /** Set chip ke state aktif (solid hijau). */
    private void activateChip(TextView chip) {
        if (chip == null)
            return;
        chip.setBackgroundResource(R.drawable.bg_chip_green_solid);
        chip.setTextColor(Color.WHITE);
    }

    /**
     * Copy URI dari galeri ke file temp (compatible Android 10+ Scoped Storage).
     */
    private File copyUriToTempFile(Uri uri) {
        try {
            ContentResolver cr = requireActivity().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            if (inputStream == null)
                return null;

            File tempFile = new File(requireActivity().getCacheDir(),
                    "upload_" + System.currentTimeMillis() + ".jpg");
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
            Log.e(TAG, "copyUriToTempFile gagal: " + e.getMessage());
            return null;
        }
    }

    /**
     * Upload gambar ke server Spring Boot, lalu simpan data ke Firebase.
     * Menggunakan OkHttpClient dengan timeout yang diperpanjang
     * karena server Render bisa cold-start hingga 30 detik.
     */
    private void uploadKeServer(Uri imageUri, String namaBarang, String kategori,
            String deskripsi, String kondisiBarang,
            String deliveryOption, String lokasiWaktu, String kontak) {

        File imageFile = copyUriToTempFile(imageUri);
        if (imageFile == null || !imageFile.exists()) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(
                        () -> Toast.makeText(getContext(), "Gagal membaca file gambar!", Toast.LENGTH_SHORT).show());
            }
            return;
        }

        // Timeout diperpanjang untuk mengantisipasi cold-start Render (free tier)
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        imageFile.getName(),
                        RequestBody.create(imageFile, MediaType.parse("image/jpeg")))
                .build();

        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();

        Log.d(TAG, "Mulai upload ke: " + UPLOAD_URL);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                imageFile.delete();
                Log.e(TAG, "Upload ke server GAGAL: " + e.getMessage());

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Tanya user apakah ingin menyimpan tanpa gambar
                        new AlertDialog.Builder(getContext())
                                .setTitle("Server Tidak Terjangkau")
                                .setMessage("Gambar tidak bisa diupload ke server (" + e.getMessage() + ").\n\n" +
                                        "Simpan data barang tanpa gambar?")
                                .setPositiveButton("Simpan Tanpa Gambar",
                                        (d, w) -> simpanKeFirebase("", namaBarang, kategori, deskripsi,
                                                kondisiBarang, deliveryOption, lokasiWaktu, kontak))
                                .setNegativeButton("Batal", null)
                                .show();
                    });
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                imageFile.delete();
                if (getActivity() == null)
                    return;

                String body = response.body() != null ? response.body().string().trim() : "";
                Log.d(TAG, "Response server — code: " + response.code() + ", body: " + body);

                if (response.isSuccessful()) {
                    // Server mengembalikan URL gambar Google Drive
                    simpanKeFirebase(body, namaBarang, kategori, deskripsi,
                            kondisiBarang, deliveryOption, lokasiWaktu, kontak);
                } else {
                    final String errorMsg = body;
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                            "Upload gagal (" + response.code() + "): " + errorMsg,
                            Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    /**
     * Mapping nama kategori ke id_itemCategory format Firebase.
     */
    private String mapKategoriToId(String kategori) {
        switch (kategori) {
            case "Elektronik":
                return "cat_elektronik";
            case "Perabotan":
                return "cat_furniture";
            case "Buku":
                return "cat_buku";
            case "Olahraga":
                return "cat_olahraga";
            case "Pakaian":
                return "cat_pakaian";
            default:
                return "cat_lainnya";
        }
    }

    /**
     * Menyimpan data item ke Firebase Realtime Database di node /items/{randomId}.
     * ID item dibuat random menggunakan UUID agar tidak bentrok.
     */
    private void simpanKeFirebase(String imageUrl, String namaBarang, String kategori,
            String deskripsi, String kondisiBarang,
            String deliveryOption, String lokasiWaktu, String kontak) {

        DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("items");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (currentUser != null) ? currentUser.getUid() : "anonymous";

        // Random item ID: "item_" + 8 karakter hex acak dari UUID
        String randomSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String itemId = "item_" + randomSuffix;

        Map<String, Object> itemData = new HashMap<>();
        itemData.put("itemName", namaBarang);
        itemData.put("id_itemCategory", mapKategoriToId(kategori));
        itemData.put("itemDescription", deskripsi);
        itemData.put("itemCondition", kondisiBarang);
        itemData.put("itemImage", imageUrl);
        itemData.put("id_users", userId);
        itemData.put("contact", kontak);
        itemData.put("availability", true);
        itemData.put("deliveryOption", deliveryOption);
        itemData.put("lokasiWaktu", lokasiWaktu);
        itemData.put("createdAt", System.currentTimeMillis());

        Log.d(TAG, "Menyimpan ke Firebase items/" + itemId);

        itemsRef.child(itemId).setValue(itemData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Firebase write SUKSES untuk itemId: " + itemId);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(),
                                    "Barang berhasil dihibahkan! 🎉",
                                    Toast.LENGTH_LONG).show();
                            // Kembali ke Home
                            View homeNav = getActivity().findViewById(R.id.nav_home_layout);
                            if (homeNav != null)
                                homeNav.performClick();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firebase write GAGAL: " + e.getMessage());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(),
                                "Gagal menyimpan ke database: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
                    }
                });
    }
}