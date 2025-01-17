package com.example.foodcourt;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewUsersActivity extends AppCompatActivity {

    private RecyclerView usersRecyclerView;
    private AdminUserAdapter adminUserAdapter;
    private List<AdminUser> adminUserList;
    private TextView totalUsersTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        // Initialize RecyclerView
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adminUserList = new ArrayList<>();
        adminUserAdapter = new AdminUserAdapter(this, adminUserList);
        usersRecyclerView.setAdapter(adminUserAdapter);
        totalUsersTextView = findViewById(R.id.totalUsersTextView);

        // Fetch all users and their orders
        fetchUsersAndOrders();
        fetchUserCount();
    }

    private void fetchUsersAndOrders() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot usersSnapshot) {
                HashMap<String, AdminUser> userMap = new HashMap<>();

                // Load user data
                for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    String name = userSnapshot.child("name").getValue(String.class);
                    String phone = userSnapshot.child("phone").getValue(String.class);
                    String address = userSnapshot.child("address").getValue(String.class);
                    String gender = userSnapshot.child("gender").getValue(String.class);

                    AdminUser adminUser = new AdminUser(name, email, phone, address, gender, new ArrayList<>());
                    userMap.put(email, adminUser);
                }

                // Load orders and link to users
                ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ordersSnapshot) {
                        for (DataSnapshot orderSnapshot : ordersSnapshot.getChildren()) {
                            String userEmail = orderSnapshot.child("userEmail").getValue(String.class);

                            if (userMap.containsKey(userEmail)) {
                                Order order = orderSnapshot.getValue(Order.class);
                                if (order != null) {
                                    userMap.get(userEmail).getOrders().add(order);
                                }
                            }
                        }

                        // Update RecyclerView with data
                        adminUserList.clear();
                        adminUserList.addAll(userMap.values());
                        adminUserAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ViewUsersActivity.this, "Failed to fetch orders", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewUsersActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchUserCount() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long userCount = snapshot.getChildrenCount();
                totalUsersTextView.setText("Total Users: " + userCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewUsersActivity.this, "Failed to fetch user count", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
