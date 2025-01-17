package com.example.foodcourt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Set basic order details
        holder.username.setText(order.getUsername());
        holder.userEmail.setText(order.getUserEmail());
        holder.userPhone.setText(order.getUserPhone());
        holder.orderDate.setText("Order Date: " + order.getOrderDate());
        holder.orderTime.setText("Order Time: " + order.getOrderTime());
        holder.transactionStatus.setText("Transaction Status: " + order.getTransactionStatus());

        // Format and set order items
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            StringBuilder itemsBuilder = new StringBuilder();
            for (OrderItem item : order.getOrderItems()) {
                itemsBuilder.append(item.getName())
                        .append("(Qty: ").append(item.getQuantity())
                        .append(", Price: ₹").append(item.getPrice())
                        .append(")\n");
            }
            holder.orderItems.setText(itemsBuilder.toString().trim());
        } else {
            holder.orderItems.setText("No items available for this order.");
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView username, userEmail, userPhone, orderDate, orderTime, transactionStatus, orderItems;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            userEmail = itemView.findViewById(R.id.userEmail);
            userPhone = itemView.findViewById(R.id.userPhone);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderTime = itemView.findViewById(R.id.orderTime);
            transactionStatus = itemView.findViewById(R.id.transactionStatus);
            orderItems = itemView.findViewById(R.id.orderItems); // New TextView for items
        }
    }

}
