package com.example.pakailagi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pakailagi.R;
import com.example.pakailagi.LoginActivity;
import com.example.pakailagi.data.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminProfileFragment extends Fragment {

    private LinearLayout btnLogout;
    private TextView tvAdminName, tvAdminEmail, tvAdminPhone, tvAdminEmailDetail, tvAdminRole;
    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    private DatabaseReference userRef;

    public AdminProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize Firebase Auth and SessionManager
        mAuth = FirebaseAuth.getInstance();
        sessionManager = SessionManager.getInstance(requireContext());
        
        // Initialize views
        btnLogout = view.findViewById(R.id.btnLogout);
        tvAdminName = view.findViewById(R.id.tvAdminName);
        tvAdminEmail = view.findViewById(R.id.tvAdminEmail);
        tvAdminPhone = view.findViewById(R.id.tvAdminPhone);
        tvAdminEmailDetail = view.findViewById(R.id.tvAdminEmailDetail);
        tvAdminRole = view.findViewById(R.id.tvAdminRole);
        
        // Load admin information
        loadAdminInfo();
        
        // Setup logout button
        btnLogout.setOnClickListener(v -> handleLogout());
        
        // Setup back button
        View btnBack = view.findViewById(R.id.btnBackProfil);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                // Navigate back to home
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }
    }

    private void loadAdminInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            String userEmail = currentUser.getEmail();
            
            // Set admin name - use display name or email prefix
            if (userName != null && !userName.isEmpty()) {
                tvAdminName.setText(userName);
            } else if (userEmail != null) {
                tvAdminName.setText(userEmail.split("@")[0]);
            } else {
                tvAdminName.setText("Admin");
            }
            
            // Set admin email
            if (userEmail != null) {
                tvAdminEmail.setText(userEmail);
                tvAdminEmailDetail.setText(userEmail);
            } else {
                tvAdminEmail.setText("admin@pakailagi.com");
                tvAdminEmailDetail.setText("admin@pakailagi.com");
            }
            
            // Load additional info from Firebase Database
            userRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentUser.getUid());
            
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Load phone number
                        String phone = snapshot.child("phone").getValue(String.class);
                        if (phone != null && !phone.isEmpty()) {
                            tvAdminPhone.setText(phone);
                        } else {
                            tvAdminPhone.setText("-");
                        }
                        
                        // Load full name if available
                        String fullName = snapshot.child("fullName").getValue(String.class);
                        if (fullName != null && !fullName.isEmpty()) {
                            tvAdminName.setText(fullName);
                        }
                        
                        // Load role
                        String role = snapshot.child("role").getValue(String.class);
                        if (role != null && !role.isEmpty()) {
                            tvAdminRole.setText(role.substring(0, 1).toUpperCase() + role.substring(1));
                        } else {
                            tvAdminRole.setText("Administrator");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error silently, keep defaults
                }
            });
        }
    }

    private void handleLogout() {
        // Clear session data
        sessionManager.clearSession();
        
        // Sign out from Firebase
        mAuth.signOut();
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        // Redirect to login screen
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        // Close current activity properly
        requireActivity().finishAffinity();
    }
}
