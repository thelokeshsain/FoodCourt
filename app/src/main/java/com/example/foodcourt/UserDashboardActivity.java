package com.example.foodcourt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
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

public class UserDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<FoodItem> foodItemList;
    private DatabaseReference mDatabase;
    private BottomNavigationView bottomNavigationView;
    private Cart cart;
    private TextView welcomeTextView;
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);


        cart = new Cart();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        foodItemList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("foodItems");

        userAdapter = new UserAdapter(UserDashboardActivity.this, foodItemList, cart);
        recyclerView.setAdapter(userAdapter);
        fetchFoodItems();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        welcomeTextView = findViewById(R.id.welcomeTextView); // Assume this TextView is added to your XML layout

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = sharedPreferences.getString("name", "Default Name"); // Default value if not found

        welcomeTextView.setText("Welcome, " + userName +"\n" + "to FoodCourt");

        // Restore selected item on configuration changes
        if (savedInstanceState != null) {
            int selectedItemId = savedInstanceState.getInt("selected_item", R.id.nav_Home);
            bottomNavigationView.setSelectedItemId(selectedItemId);
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_Home) {
            Toast.makeText(this, "Home Selected", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.nav_order_history) {
            startActivity(new Intent(this, OrderHistoryActivity.class));
            return true;
        } else if (itemId == R.id.nav_cart) {
            Intent cartIntent = new Intent(this, CartActivity.class);
            cartIntent.putExtra("cart", cart);
            startActivity(cartIntent);
            return true;
        }else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;

        }
        else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent logoutIntent = new Intent(this, RegistrationActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }
        return false;
    }

    private void fetchFoodItems() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foodItemList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FoodItem foodItem = snapshot.getValue(FoodItem.class);
                    if (foodItem != null) {
                        foodItemList.add(foodItem);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UserDashboardActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the currently selected item ID
        outState.putInt("selected_item", bottomNavigationView.getSelectedItemId());
    }

    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() != R.id.nav_Home) {
            // Set Home as selected item when back is pressed and we are not on Home
            bottomNavigationView.setSelectedItemId(R.id.nav_Home);
        } else {
            super.onBackPressed();
        }
    }
}
