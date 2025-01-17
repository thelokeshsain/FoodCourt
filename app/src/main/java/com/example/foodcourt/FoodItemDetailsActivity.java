package com.example.foodcourt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class FoodItemDetailsActivity extends AppCompatActivity {

    private TextView nameTextView, descriptionTextView, priceTextView;
    private ImageView foodImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_details);

        // Initialize UI elements
        nameTextView = findViewById(R.id.nameTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        priceTextView = findViewById(R.id.priceTextView);
        foodImageView = findViewById(R.id.foodImageView);

        // Get the intent and food item details
        Intent intent = getIntent();
        String name = intent.getStringExtra("foodName");
        String description = intent.getStringExtra("foodDescription");
        double price = intent.getDoubleExtra("foodPrice", 0);
        String imageUrl = intent.getStringExtra("foodImageUrl");

        // Set the data to UI elements
        nameTextView.setText(name);
        descriptionTextView.setText(description);
        priceTextView.setText("Price: ₹" + price);

        // Load image using Glide
        Glide.with(this).load(imageUrl).into(foodImageView);
    }
}
