package com.example.foodcourt;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context context;
    private List<FoodItem> foodItemList;
    private DatabaseReference mDatabase;

    public FoodAdapter(Context context, List<FoodItem> foodItemList) {
        this.context = context;
        this.foodItemList = foodItemList;
        mDatabase = FirebaseDatabase.getInstance().getReference("foodItems");
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);
        holder.foodName.setText(foodItem.getName());
        holder.foodPrice.setText(String.format("₹%.2f", foodItem.getPrice()));
        holder.foodDescription.setText(foodItem.getDescription());
        Glide.with(context).load(foodItem.getImageUrl()).into(holder.foodImage);

        // Open EditFoodActivity on item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditFoodActivity.class);
            intent.putExtra("foodId", foodItem.getId());
            intent.putExtra("name", foodItem.getName());
            intent.putExtra("description", foodItem.getDescription());
            intent.putExtra("price", foodItem.getPrice());
            intent.putExtra("imageUrl", foodItem.getImageUrl());
            context.startActivity(intent);
        });

        // Delete button functionality
        holder.deleteButton.setOnClickListener(v -> {
            deleteFoodItem(foodItem.getId(), position);
        });
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodPrice, foodDescription;
        ImageView foodImage;
        Button deleteButton;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodDescription = itemView.findViewById(R.id.foodDescription);
            foodImage = itemView.findViewById(R.id.foodImage);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    // Method to delete a food item
    // Method to delete a food item
    private void deleteFoodItem(String foodId, int position) {
        // Check if the position is valid before attempting to delete
        if (position >= 0 && position < foodItemList.size()) {
            // Remove from local list first
            foodItemList.remove(position);
            notifyItemRemoved(position);

            // Now attempt to remove from Firebase
            mDatabase.child(foodId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Successfully deleted from Firebase
                        if (foodItemList.isEmpty()) {
                            Toast.makeText(context, "All food items deleted!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Food item deleted successfully!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // If deletion from Firebase fails, add the item back to the list
                        foodItemList.add(position, new FoodItem(foodId, "Sample Name", "Sample Description", 0.0, "Sample Image URL")); // Add a placeholder to prevent index out of bounds
                        notifyItemInserted(position);
                        Toast.makeText(context, "Failed to delete food item from Firebase", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(context, "Invalid item position", Toast.LENGTH_SHORT).show();
        }
    }

}
