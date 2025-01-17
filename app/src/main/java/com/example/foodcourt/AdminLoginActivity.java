package com.example.foodcourt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private DatabaseReference mDatabase;

    // Default admin credentials
    private static final String DEFAULT_ADMIN_EMAIL = "lokeshadmin@gmail.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin@123";

    // SharedPreferences for session management
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "AdminSession";
    private static final String IS_ADMIN_LOGGED_IN = "isAdminLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Check if admin is already logged in
        if (sharedPreferences.getBoolean(IS_ADMIN_LOGGED_IN, false)) {
            navigateToAdminDashboard();
            finish();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("admin");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(view -> loginAdmin());
    }

    private void loginAdmin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        if (email.equals(DEFAULT_ADMIN_EMAIL) && password.equals(DEFAULT_ADMIN_PASSWORD)) {
            // Check if admin exists in Firebase Database
            mDatabase.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        String adminId = mDatabase.push().getKey();
                        Admin admin = new Admin(email, password);
                        mDatabase.child(adminId).setValue(admin)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AdminLoginActivity.this, "Admin created successfully!", Toast.LENGTH_SHORT).show();
                                        saveAdminSession();
                                    } else {
                                        Toast.makeText(AdminLoginActivity.this, "Failed to create admin", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        saveAdminSession();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AdminLoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(AdminLoginActivity.this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAdminSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_ADMIN_LOGGED_IN, true);
        editor.apply();
        navigateToAdminDashboard();
    }

    private void navigateToAdminDashboard() {
        startActivity(new Intent(AdminLoginActivity.this, AdminDashboardActivity.class));
        finish();
    }
}
