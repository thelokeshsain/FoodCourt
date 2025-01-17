package com.example.foodcourt;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddFoodActivity extends AppCompatActivity {

    private EditText nameEditText, descriptionEditText, priceEditText, imageEditText;
    private Button addFoodButton;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        mDatabase = FirebaseDatabase.getInstance().getReference("foodItems");

        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        imageEditText = findViewById(R.id.imageEditText);
        addFoodButton = findViewById(R.id.addFoodButton);

        addFoodButton.setOnClickListener(view -> addFoodItem());
    }

    private void addFoodItem() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String imageUrl = imageEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required");
            return;
        }
        if (TextUtils.isEmpty(description)) {
            descriptionEditText.setError("Description is required");
            return;
        }
        if (TextUtils.isEmpty(priceString)) {
            priceEditText.setError("Price is required");
            return;
        }
        if (TextUtils.isEmpty(imageUrl)) {
            imageEditText.setError("Image URL is required");
            return;
        }

        double price = Double.parseDouble(priceString);
        String foodId = mDatabase.push().getKey(); // Generate unique key for food item
        FoodItem foodItem = new FoodItem(foodId, name, description, price, imageUrl);

        mDatabase.child(foodId).setValue(foodItem)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddFoodActivity.this, "Food item added successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    } else {
                        Toast.makeText(AddFoodActivity.this, "Failed to add food item", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


