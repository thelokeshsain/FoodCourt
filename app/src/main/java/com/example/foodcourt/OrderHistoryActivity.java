package com.example.foodcourt;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private DatabaseReference ordersRef;
    private FirebaseAuth mAuth; // Firebase Authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Initialize RecyclerView
        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance();
        orderList = new ArrayList<>();
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        // Fetch the logged-in user's order history
        fetchOrderHistory();
    }

    private void fetchOrderHistory() {
        String currentUserEmail = mAuth.getCurrentUser().getEmail(); // Get logged-in user's email

        if (currentUserEmail == null) {
            Toast.makeText(this, "User is not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show a loading message or indicator if necessary
        Toast.makeText(this, "Loading orders...", Toast.LENGTH_SHORT).show();

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear(); // Clear the list to avoid duplication
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null && currentUserEmail.equals(order.getUserEmail())) {
                        // Add only orders related to the logged-in user
                        orderList.add(order);
                    }
                }

                // Update the adapter with filtered orders
                if (orderAdapter == null) {
                    orderAdapter = new OrderAdapter(OrderHistoryActivity.this, orderList);
                    orderRecyclerView.setAdapter(orderAdapter);
                } else {
                    orderAdapter.notifyDataSetChanged(); // Notify adapter of data change
                }

                // Show a message if no orders are found
                if (orderList.isEmpty()) {
                    Toast.makeText(OrderHistoryActivity.this, "No orders found for your account.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(OrderHistoryActivity.this, "Failed to load orders.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
