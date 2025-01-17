package com.example.foodcourt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView foodRecyclerView;
    private FoodAdapter foodAdapter;
    private List<FoodItem> foodItemList;
    private DatabaseReference mDatabase;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Set up RecyclerView and Firebase database reference
        foodRecyclerView = findViewById(R.id.foodRecyclerView);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        foodItemList = new ArrayList<>();
        foodAdapter = new FoodAdapter(this, foodItemList);
        foodRecyclerView.setAdapter(foodAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("foodItems");

        // Fetch and display food items with ValueEventListener
        fetchFoodItems();

        // Set up Bottom Navigation View
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_Home) {
                Intent viewUsersIntent = new Intent(AdminDashboardActivity.this, AdminDashboardActivity.class);
                startActivity(viewUsersIntent);
                return true;

            } else if (itemId == R.id.nav_add_food) {
                Intent addFoodIntent = new Intent(AdminDashboardActivity.this, AddFoodActivity.class);
                startActivity(addFoodIntent);
                return true;

            }  else if (itemId == R.id.nav_view_users) {
                Intent viewUsersIntent = new Intent(AdminDashboardActivity.this, ViewUsersActivity.class);
                startActivity(viewUsersIntent);
                return true;
            } else if (itemId == R.id.nav_logout) {
                // Clear the admin session
                SharedPreferences sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isAdminLoggedIn", false);
                editor.apply();

                // Logout from Firebase Auth
                FirebaseAuth.getInstance().signOut();

                // Navigate back to the login activity
                Intent logoutIntent = new Intent(AdminDashboardActivity.this, AdminLoginActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                Toast.makeText(AdminDashboardActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            }
            return false;
        });
    }

    private void fetchFoodItems() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodItemList.clear();
                for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                    FoodItem foodItem = foodSnapshot.getValue(FoodItem.class);
                    foodItemList.add(foodItem);
                }
                foodAdapter.notifyDataSetChanged(); // Refresh the adapter after changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Failed to load food items", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserCount() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long userCount = snapshot.getChildrenCount();
                Toast.makeText(AdminDashboardActivity.this, "Total Users: " + userCount, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminDashboardActivity.this, "Failed to fetch user count", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
