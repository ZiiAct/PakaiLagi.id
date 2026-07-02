package com.example.pakailagi;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for Admin item inspection with 3 dynamic modes:
 *
 * Mode 1 — Item Approval: Admin reviews a "pending" donated item.
 *          Approve → status = "approved", Reject → status = "rejected".
 *
 * Mode 2 — Request Approval: Admin reviews a user request (receiveReq) on an "approved" item.
 *          Approve → receiveReq status = "process", Reject → receiveReq status = "rejected".
 *
 * Mode 3 — Process & Completion: Admin confirms delivery for a "process" request.
 *          Konfirmasi → item availability = false, statuses = "selesai".
 */
@SuppressWarnings("all")
public class ItemInspectionActivity extends AppCompatActivity {

    // Intent extra keys
    private static final String EXTRA_ITEM_ID = "itemId";
    private static final String EXTRA_ITEM_NAME = "itemName";
    private static final String EXTRA_DONOR_NAME = "donorName";
    private static final String EXTRA_STATUS = "status";
    private static final String EXTRA_DESCRIPTION = "description";
    private static final String EXTRA_CONDITION = "condition";
    private static final String EXTRA_IMAGE_URL = "imageUrl";
    private static final String EXTRA_LOCATION = "location";
    private static final String EXTRA_CATEGORY = "category";
    private static final String EXTRA_INSPECTION_MODE = "inspectionMode";
    private static final String EXTRA_REQUEST_ID = "requestId";

    // Firebase references
    private DatabaseReference itemsRef;
    private DatabaseReference receiveReqRef;

    // Item data from intent
    private String itemId;
    private String itemName;
    private String requestId;
    private int inspectionMode;

    // Views
    private TextView tvInspectionTitle;
    private TextView tvItemIdBadge;
    private TextView tvSpecItemName;
    private TextView tvSpecCategory;
    private TextView tvSpecDonor;
    private TextView tvSpecCondition;
    private TextView tvDonorDescription;

    // Mode 2: Request notes
    private LinearLayout layoutRequestNotes;
    private TextView tvRequesterName;
    private TextView tvRequestNotes;

    // Mode 3: Delivery address
    private LinearLayout layoutDeliveryAddress;
    private TextView tvDeliveryRequester;
    private TextView tvDeliveryAddress;
    private TextView tvDeliveryStatus;

    // Action buttons
    private LinearLayout btnApprove;
    private LinearLayout btnReject;
    private LinearLayout btnKonfirmasi;
    private TextView tvBtnApproveText;
    private TextView tvBtnRejectText;
    private EditText etInternalNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_inspection);

        // Initialize Firebase
        itemsRef = FirebaseDatabase.getInstance().getReference("items");
        receiveReqRef = FirebaseDatabase.getInstance().getReference("receiveReq");

        // Read intent extras
        itemId = getIntent().getStringExtra(EXTRA_ITEM_ID);
        itemName = getIntent().getStringExtra(EXTRA_ITEM_NAME);
        requestId = getIntent().getStringExtra(EXTRA_REQUEST_ID);
        inspectionMode = getIntent().getIntExtra(EXTRA_INSPECTION_MODE, 1);

        String donorName = getIntent().getStringExtra(EXTRA_DONOR_NAME);
        String description = getIntent().getStringExtra(EXTRA_DESCRIPTION);
        String condition = getIntent().getStringExtra(EXTRA_CONDITION);
        String category = getIntent().getStringExtra(EXTRA_CATEGORY);
        String location = getIntent().getStringExtra(EXTRA_LOCATION);

        // Bind views
        bindViews();

        // Populate item data
        populateItemData(donorName, description, condition, category, location);

        // Setup back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Configure UI based on inspection mode
        switch (inspectionMode) {
            case 1:
                setupMode1_ItemApproval();
                break;
            case 2:
                setupMode2_RequestApproval();
                break;
            case 3:
                setupMode3_ProcessCompletion();
                break;
        }
    }

    private void bindViews() {
        tvInspectionTitle = findViewById(R.id.tvInspectionTitle);
        tvItemIdBadge = findViewById(R.id.tvItemIdBadge);
        tvSpecItemName = findViewById(R.id.tvSpecItemName);
        tvSpecCategory = findViewById(R.id.tvSpecCategory);
        tvSpecDonor = findViewById(R.id.tvSpecDonor);
        tvSpecCondition = findViewById(R.id.tvSpecCondition);
        tvDonorDescription = findViewById(R.id.tvDonorDescription);

        layoutRequestNotes = findViewById(R.id.layoutRequestNotes);
        tvRequesterName = findViewById(R.id.tvRequesterName);
        tvRequestNotes = findViewById(R.id.tvRequestNotes);

        layoutDeliveryAddress = findViewById(R.id.layoutDeliveryAddress);
        tvDeliveryRequester = findViewById(R.id.tvDeliveryRequester);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        tvDeliveryStatus = findViewById(R.id.tvDeliveryStatus);

        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);
        btnKonfirmasi = findViewById(R.id.btnKonfirmasi);
        tvBtnApproveText = findViewById(R.id.tvBtnApproveText);
        tvBtnRejectText = findViewById(R.id.tvBtnRejectText);
        etInternalNotes = findViewById(R.id.etInternalNotes);
    }

    private void populateItemData(String donorName, String description,
                                   String condition, String category, String location) {
        // Set item ID badge
        if (itemId != null && itemId.length() > 8) {
            tvItemIdBadge.setText("ID: " + itemId.substring(0, 12));
        } else {
            tvItemIdBadge.setText("ID: " + (itemId != null ? itemId : "-"));
        }

        tvSpecItemName.setText(itemName != null && !itemName.isEmpty() ? itemName : "-");
        tvSpecCategory.setText(category != null && !category.isEmpty() ? category : "-");
        tvSpecDonor.setText(donorName != null && !donorName.isEmpty() ? donorName : "Unknown");
        tvSpecCondition.setText(condition != null && !condition.isEmpty() ? condition : "-");

        // Fetch full item data from Firebase for description
        if (itemId != null && !itemId.isEmpty()) {
            itemsRef.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String fbDescription = snapshot.child("itemDescription").getValue(String.class);
                        String fbCondition = snapshot.child("itemCondition").getValue(String.class);
                        String fbCategory = snapshot.child("id_itemCategory").getValue(String.class);
                        String fbDonorId = snapshot.child("id_users").getValue(String.class);
                        String fbLocation = snapshot.child("pickupLocation").getValue(String.class);

                        if (fbDescription != null && !fbDescription.isEmpty()) {
                            tvDonorDescription.setText("\"" + fbDescription + "\"");
                        }
                        if (fbCondition != null && !fbCondition.isEmpty()) {
                            tvSpecCondition.setText(fbCondition);
                        }
                        if (fbCategory != null && !fbCategory.isEmpty()) {
                            tvSpecCategory.setText(formatCategory(fbCategory));
                        }
                        // If donorName is unknown, try to fetch from users
                        if ("Unknown".equals(tvSpecDonor.getText().toString()) && fbDonorId != null) {
                            fetchDonorName(fbDonorId);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }

        if (description != null && !description.isEmpty()) {
            tvDonorDescription.setText("\"" + description + "\"");
        }
    }

    /**
     * Fetch donor display name from the users node.
     */
    private void fetchDonorName(String userId) {
        FirebaseDatabase.getInstance().getReference("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("fullName").getValue(String.class);
                        if (name == null) name = snapshot.child("name").getValue(String.class);
                        if (name == null) name = snapshot.child("email").getValue(String.class);
                        if (name != null) {
                            tvSpecDonor.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    /**
     * Map category IDs to display names.
     */
    private String formatCategory(String catId) {
        if (catId == null) return "-";
        switch (catId) {
            case "cat_elektronik": return "Elektronik";
            case "cat_furniture": return "Perabotan";
            case "cat_buku": return "Buku";
            case "cat_olahraga": return "Olahraga";
            case "cat_pakaian": return "Pakaian";
            case "cat_lainnya": return "Lainnya";
            default: return catId;
        }
    }

    // ======================================================================
    // MODE 1: ITEM APPROVAL — Admin reviews a pending donated item
    // ======================================================================

    private void setupMode1_ItemApproval() {
        tvInspectionTitle.setText("Item\nInspection");

        // Hide request notes and delivery sections
        layoutRequestNotes.setVisibility(View.GONE);
        layoutDeliveryAddress.setVisibility(View.GONE);

        // Show approve/reject buttons, hide konfirmasi
        btnApprove.setVisibility(View.VISIBLE);
        btnReject.setVisibility(View.VISIBLE);
        btnKonfirmasi.setVisibility(View.GONE);

        tvBtnApproveText.setText("Setujui Hibah");
        tvBtnRejectText.setText("Tolak Hibah");

        btnApprove.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Persetujuan")
                    .setMessage("Setujui item \"" + itemName + "\" ?")
                    .setPositiveButton("Setujui", (dialog, which) -> approveItem())
                    .setNegativeButton("Batal", null)
                    .show();
        });

        btnReject.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Penolakan")
                    .setMessage("Tolak item \"" + itemName + "\" ?")
                    .setPositiveButton("Tolak", (dialog, which) -> rejectItem())
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void updateHibahReqStatus(String itemId, String status) {
        FirebaseDatabase.getInstance().getReference("hibahReq")
                .orderByChild("id_items").equalTo(itemId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.getRef().child("reqStatus").setValue(status);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void approveItem() {
        if (itemId == null) return;

        itemsRef.child(itemId).child("status").setValue("approved")
                .addOnSuccessListener(aVoid -> {
                    updateHibahReqStatus(itemId, "approved");

                    // Log to statusLog
                    String adminUid = getCurrentAdminUid();
                    StatusLogHelper.logApproved(itemId, "", adminUid);

                    Toast.makeText(this, "Item berhasil disetujui! ✅", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void rejectItem() {
        if (itemId == null) return;

        itemsRef.child(itemId).child("status").setValue("rejected")
                .addOnSuccessListener(aVoid -> {
                    updateHibahReqStatus(itemId, "rejected");

                    String adminUid = getCurrentAdminUid();
                    StatusLogHelper.logRejected(itemId, "", adminUid);

                    Toast.makeText(this, "Item ditolak. ❌", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // ======================================================================
    // MODE 2: REQUEST APPROVAL — Admin reviews a user request on an approved item
    // ======================================================================

    private void setupMode2_RequestApproval() {
        tvInspectionTitle.setText("Request\nInspection");

        // Show request notes section, hide delivery
        layoutRequestNotes.setVisibility(View.VISIBLE);
        layoutDeliveryAddress.setVisibility(View.GONE);

        // Show approve/reject buttons with updated text
        btnApprove.setVisibility(View.VISIBLE);
        btnReject.setVisibility(View.VISIBLE);
        btnKonfirmasi.setVisibility(View.GONE);

        tvBtnApproveText.setText("Setujui Permintaan");
        tvBtnRejectText.setText("Tolak Permintaan");

        // Fetch request details from receiveReq
        if (requestId != null) {
            fetchRequestDetails(requestId);
        }

        btnApprove.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Persetujuan")
                    .setMessage("Setujui permintaan untuk \"" + itemName + "\" ?")
                    .setPositiveButton("Setujui", (dialog, which) -> approveRequest())
                    .setNegativeButton("Batal", null)
                    .show();
        });

        btnReject.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Penolakan")
                    .setMessage("Tolak permintaan untuk \"" + itemName + "\" ?")
                    .setPositiveButton("Tolak", (dialog, which) -> rejectRequest())
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void fetchRequestDetails(String reqId) {
        receiveReqRef.child(reqId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String requesterName = snapshot.child("requesterName").getValue(String.class);
                String notes = snapshot.child("notes").getValue(String.class);
                String reason = snapshot.child("reason").getValue(String.class);

                if (requesterName != null) {
                    tvRequesterName.setText(requesterName);
                }

                // Show notes or reason if available
                String displayNotes = notes != null ? notes : (reason != null ? reason : "Tidak ada catatan.");
                tvRequestNotes.setText(displayNotes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void approveRequest() {
        if (requestId == null || itemId == null) return;

        // Update receiveReq status to "process"
        receiveReqRef.child(requestId).child("status").setValue("process")
                .addOnSuccessListener(aVoid -> {
                    String adminUid = getCurrentAdminUid();
                    StatusLogHelper.pushLog(itemId, requestId, "process", adminUid);

                    Toast.makeText(this, "Permintaan disetujui! Dalam proses. ✅", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void rejectRequest() {
        if (requestId == null || itemId == null) return;

        // Update receiveReq status to "rejected"
        receiveReqRef.child(requestId).child("status").setValue("rejected")
                .addOnSuccessListener(aVoid -> {
                    String adminUid = getCurrentAdminUid();
                    StatusLogHelper.logRejected(itemId, requestId, adminUid);

                    Toast.makeText(this, "Permintaan ditolak. ❌", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // ======================================================================
    // MODE 3: PROCESS & COMPLETION — Admin confirms delivery/handover
    // ======================================================================

    private void setupMode3_ProcessCompletion() {
        tvInspectionTitle.setText("Delivery\nConfirmation");

        // Hide request notes, show delivery address
        layoutRequestNotes.setVisibility(View.GONE);
        layoutDeliveryAddress.setVisibility(View.VISIBLE);

        // Hide approve/reject, show konfirmasi button
        btnApprove.setVisibility(View.GONE);
        btnReject.setVisibility(View.GONE);
        btnKonfirmasi.setVisibility(View.VISIBLE);

        // Hide checklist and internal notes (not relevant for completion)
        findViewById(R.id.tvChecklistTitle).setVisibility(View.GONE);
        findViewById(R.id.layoutChecklist1).setVisibility(View.GONE);
        findViewById(R.id.layoutChecklist2).setVisibility(View.GONE);
        findViewById(R.id.layoutChecklist3).setVisibility(View.GONE);
        findViewById(R.id.tvNotesLabel).setVisibility(View.GONE);
        etInternalNotes.setVisibility(View.GONE);

        // Fetch delivery details
        if (requestId != null) {
            fetchDeliveryDetails(requestId);
        }

        // Fetch pickup location from item
        if (itemId != null) {
            itemsRef.child(itemId).child("pickupLocation")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String location = snapshot.getValue(String.class);
                            if (location != null && !location.isEmpty()) {
                                tvDeliveryAddress.setText(location);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }

        btnKonfirmasi.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Penerimaan")
                    .setMessage("Konfirmasi bahwa barang \"" + itemName + "\" telah diterima oleh peminta?")
                    .setPositiveButton("Konfirmasi", (dialog, which) -> confirmCompletion())
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void fetchDeliveryDetails(String reqId) {
        receiveReqRef.child(reqId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String requesterName = snapshot.child("requesterName").getValue(String.class);
                String status = snapshot.child("status").getValue(String.class);

                if (requesterName != null) {
                    tvDeliveryRequester.setText(requesterName);
                }
                if (status != null) {
                    tvDeliveryStatus.setText("process".equals(status) ? "Dalam Proses" : status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void confirmCompletion() {
        if (requestId == null || itemId == null) return;

        // 1. Set item availability to false
        Map<String, Object> itemUpdates = new HashMap<>();
        itemUpdates.put("availability", false);
        itemsRef.child(itemId).updateChildren(itemUpdates);

        // 2. Update receiveReq status to "selesai"
        receiveReqRef.child(requestId).child("status").setValue("selesai");

        // 3. Get receiver and donor UIDs for StatusLogHelper.onSelesai()
        receiveReqRef.child(requestId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String receiverUid = snapshot.child("requesterId").getValue(String.class);
                String donorId = snapshot.child("donorId").getValue(String.class);

                // If donorId is "unknown", try fetching from item
                if (donorId == null || "unknown".equals(donorId)) {
                    itemsRef.child(itemId).child("id_users")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot itemSnap) {
                                    String realDonorId = itemSnap.getValue(String.class);
                                    StatusLogHelper.onSelesai(itemId, requestId,
                                            receiverUid != null ? receiverUid : "",
                                            realDonorId != null ? realDonorId : "");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                } else {
                    StatusLogHelper.onSelesai(itemId, requestId,
                            receiverUid != null ? receiverUid : "",
                            donorId);
                }

                Toast.makeText(ItemInspectionActivity.this,
                        "Transaksi selesai! Barang telah diterima. 🎉", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ItemInspectionActivity.this,
                        "Gagal menyelesaikan transaksi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ======================================================================
    // Helpers
    // ======================================================================

    private String getCurrentAdminUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : "admin";
    }
}
