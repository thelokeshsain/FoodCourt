package com.example.foodcourt;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String username;
    private String userEmail;
    private String userPhone;
    private String transactionStatus;
    private String orderDate;
    private String orderTime;
    private List<OrderItem> orderItems; // List to store item details

    public Order() {
        // Default constructor required for Firebase
    }

    public Order(String username, String userEmail, String userPhone, String transactionStatus,
                 String orderDate, String orderTime, List<OrderItem> orderItems) {
        this.username = username;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.transactionStatus = transactionStatus;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.orderItems = orderItems;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
