package com.example.pakailagi;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized utility for pushing entries to the `statusLog` Firebase node
 * and updating `user_stats` counters whenever item status changes.
 *
 * Usage:
 * StatusLogHelper.pushLog(itemId, requestId, "process", uid);
 * StatusLogHelper.onSelesai(itemId, requestId, receiverUid, donorUid);
 */
@SuppressWarnings("all")
public class StatusLogHelper {

    private static final String NODE_STATUS_LOG = "statusLog";
    private static final String NODE_USER_STATS = "user_stats";

    /**
     * Push a status log entry to /statusLog/{newPushId}.
     *
     * @param itemId     The hibahReq item key
     * @param requestId  The receiveReq key (can be "" for initial entry)
     * @param lateStatus One of: "process", "rejected", "selesai"
     * @param updatedBy  UID of the user triggering this change
     */
    public static void pushLog(String itemId, String requestId, String lateStatus, String updatedBy) {
        DatabaseReference logRef = FirebaseDatabase.getInstance()
                .getReference(NODE_STATUS_LOG)
                .push();

        Map<String, Object> entry = new HashMap<>();
        entry.put("itemId", itemId);
        entry.put("requestId", requestId);
        entry.put("latestatus", lateStatus);
        entry.put("updatedBy", updatedBy);
        entry.put("timestamp", ServerValue.TIMESTAMP);

        logRef.setValue(entry);
    }

    /**
     * Called when an item transaction reaches "selesai" status.
     * Pushes the final status log entry and increments both:
     * - user_stats/{receiverUid}/totalReceived
     * - user_stats/{donorUid}/totalDonated
     *
     * @param itemId      The hibahReq item key
     * @param requestId   The receiveReq key
     * @param receiverUid UID of the user who received the item
     * @param donorUid    UID of the original donor
     */
    public static void onSelesai(String itemId, String requestId,
            String receiverUid, String donorUid) {
        // 1. Log the selesai status
        pushLog(itemId, requestId, "selesai", receiverUid);

        // 2. Increment receiver's totalReceived counter
        DatabaseReference statsRef = FirebaseDatabase.getInstance().getReference(NODE_USER_STATS);
        statsRef.child(receiverUid).child("totalReceived")
                .setValue(ServerValue.increment(1));

        // 3. Increment donor's totalDonated counter
        if (donorUid != null && !donorUid.isEmpty()) {
            statsRef.child(donorUid).child("totalDonated")
                    .setValue(ServerValue.increment(1));
        }
    }

    /**
     * Convenience: log an "approved" → "process" state right after a
     * receiveReq is created (called from DetailBarangFragment).
     */
    public static void logInitialRequest(String itemId, String requestId, String requesterUid) {
        pushLog(itemId, requestId, "process", requesterUid);
    }

    /**
     * Called when admin rejects a request.
     *
     * @param itemId    The hibahReq item key
     * @param requestId The receiveReq key
     * @param adminUid  UID of admin performing rejection
     */
    public static void logRejected(String itemId, String requestId, String adminUid) {
        pushLog(itemId, requestId, "rejected", adminUid);
    }
}
