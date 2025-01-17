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

public class EditFoodActivity extends AppCompatActivity {

    private EditText nameEditText, descriptionEditText, priceEditText, imageEditText;
    private Button updateFoodButton;

    private DatabaseReference mDatabase;
    private String foodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        mDatabase = FirebaseDatabase.getInstance().getReference("foodItems");

        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        imageEditText = findViewById(R.id.imageEditText);
        updateFoodButton = findViewById(R.id.updateFoodButton);

        // Get food item data from intent
        foodId = getIntent().getStringExtra("foodId");
        nameEditText.setText(getIntent().getStringExtra("name"));
        descriptionEditText.setText(getIntent().getStringExtra("description"));
        priceEditText.setText(String.valueOf(getIntent().getDoubleExtra("price", 0)));
        imageEditText.setText(getIntent().getStringExtra("imageUrl"));

        updateFoodButton.setOnClickListener(view -> updateFoodItem());
    }

    private void updateFoodItem() {
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
        FoodItem foodItem = new FoodItem(foodId, name, description, price, imageUrl);

        mDatabase.child(foodId).setValue(foodItem)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditFoodActivity.this, "Food item updated successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    } else {
                        Toast.makeText(EditFoodActivity.this, "Failed to update food item", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
