package com.example.foodcourt;

import android.content.Context;
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

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<FoodItem> foodItemList;
    private Cart cart;

    public UserAdapter(Context context, List<FoodItem> foodItemList, Cart cart) {
        this.context = context;
        this.foodItemList = foodItemList;
        this.cart = cart;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_food, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);
        holder.foodName.setText(foodItem.getName());
        holder.foodPrice.setText(String.format("₹%.2f", foodItem.getPrice()));
        holder.foodDescription.setText(foodItem.getDescription());
        holder.foodQuantity.setText(String.valueOf(foodItem.getQuantity()));

        if (foodItem.getImageUrl() != null && !foodItem.getImageUrl().isEmpty()) {
            Glide.with(context).load(foodItem.getImageUrl()).into(holder.foodImage);
        }

        holder.incrementButton.setOnClickListener(v -> {
            if (cart.getItems().contains(foodItem)) {
                foodItem.setQuantity(foodItem.getQuantity() + 1);
                Toast.makeText(context, "Increased quantity in cart", Toast.LENGTH_SHORT).show();
            } else {
                foodItem.setQuantity(1); // Set quantity to 1 when adding a new item
                cart.addItemToCart(foodItem);
                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
            }
            holder.foodQuantity.setText(String.valueOf(foodItem.getQuantity()));
            notifyItemChanged(position);
        });

        holder.decrementButton.setOnClickListener(v -> {
            if (foodItem.getQuantity() > 0) {
                foodItem.setQuantity(foodItem.getQuantity() - 1);
                holder.foodQuantity.setText(String.valueOf(foodItem.getQuantity()));
                if (foodItem.getQuantity() == 0) {
                    cart.removeItemFromCart(foodItem);
                }
                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Cannot decrement below zero", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodPrice, foodDescription, foodQuantity;
        ImageView foodImage;
        Button incrementButton, decrementButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodDescription = itemView.findViewById(R.id.foodDescription);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodQuantity = itemView.findViewById(R.id.foodQuantity);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
        }
    }
}
