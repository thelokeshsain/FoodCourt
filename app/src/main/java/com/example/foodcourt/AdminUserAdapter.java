package com.example.foodcourt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.AdminUserViewHolder> {

    private Context context;
    private List<AdminUser> adminUserList;

    public AdminUserAdapter(Context context, List<AdminUser> adminUserList) {
        this.context = context;
        this.adminUserList = adminUserList;
    }

    @NonNull
    @Override
    public AdminUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user, parent, false);
        return new AdminUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserViewHolder holder, int position) {
        AdminUser adminUser = adminUserList.get(position);

        holder.name.setText(adminUser.getName());
        holder.email.setText(adminUser.getEmail());
        holder.phone.setText(adminUser.getPhone());
        holder.address.setText(adminUser.getAddress());
        holder.gender.setText(adminUser.getGender());

        // Display orders
        StringBuilder ordersBuilder = new StringBuilder();
        for (Order order : adminUser.getOrders()) {
            ordersBuilder.append("Order Date: ").append(order.getOrderDate()).append("\n");
            ordersBuilder.append("Order Time: ").append(order.getOrderTime()).append("\n");
            ordersBuilder.append("Transaction Status: ").append(order.getTransactionStatus()).append("\n");

            if (order.getOrderItems() != null) {
                ordersBuilder.append("Items:\n");
                for (OrderItem item : order.getOrderItems()) {
                    ordersBuilder.append("- ").append(item.getName()).append(": ₹").append(item.getPrice()).append("\n");
                }
            }

            ordersBuilder.append("\n"); // Separate orders visually
        }
        holder.orders.setText(ordersBuilder.toString().trim());
    }


    @Override
    public int getItemCount() {
        return adminUserList.size();
    }

    public static class AdminUserViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, phone, address, gender, orders;

        public AdminUserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);
            gender = itemView.findViewById(R.id.gender);
            orders = itemView.findViewById(R.id.orders);
        }
    }
}
