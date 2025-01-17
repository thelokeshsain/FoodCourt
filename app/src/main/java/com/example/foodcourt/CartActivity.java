package com.example.foodcourt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 100;

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalAmount;
    private List<FoodItem> cartItems;
    private Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Check if user is authenticated
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CartActivity.this, LoginActivity.class));
            finish();  // Close CartActivity
            return;
        }

        // Request for storage permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        totalAmount = findViewById(R.id.totalAmount);

        // Retrieve the Cart passed from UserDashboardActivity
        cart = (Cart) getIntent().getSerializableExtra("cart");
        cartItems = (cart != null) ? cart.getItems() : new ArrayList<>(); // Default empty list

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartItems);
        cartRecyclerView.setAdapter(cartAdapter);

        calculateTotalAmount();

        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                proceedToCheckout();
            } else {
                Toast.makeText(CartActivity.this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateTotalAmount() {
        double total = 0.0;
        for (FoodItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        totalAmount.setText(String.format("Total: ₹%.2f", total));
    }

    private void proceedToCheckout() {
        double totalAmount = calculateTotal(); // Total amount to be paid

        // Create an AlertDialog to confirm payment
        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
        builder.setTitle("Confirm Payment")
                .setMessage("Your total amount is ₹" + totalAmount + ". Do you want to proceed with the payment?")
                .setPositiveButton("Proceed", (dialog, which) -> {
                    // If the user clicks "Proceed", handle the payment or save order
                    Toast.makeText(CartActivity.this, "Proceeding with payment", Toast.LENGTH_SHORT).show();
                    saveOrderAndGenerateReceipt();  // Save the order and generate a receipt
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // If the user clicks "Cancel", dismiss the dialog
                    Toast.makeText(CartActivity.this, "Payment cancelled", Toast.LENGTH_SHORT).show();
                })
                .create()
                .show();
    }

    private double calculateTotal() {
        double total = 0.0;
        for (FoodItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK || data != null) {
                String response = data.getStringExtra("response");
                handleUPIResponse(response);
            } else {
                Toast.makeText(this, "Payment cancelled.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleUPIResponse(String response) {
        if (response == null) {
            Toast.makeText(this, "Payment failed. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (response.contains("success")) {
            Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
            saveOrderAndGenerateReceipt();
        } else if (response.contains("pending")) {
            Toast.makeText(this, "Payment is pending.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Payment failed. Please check your UPI app.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveOrderAndGenerateReceipt() {
        // Retrieve user details from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = sharedPreferences.getString("name", "User Name");
        String userEmail = sharedPreferences.getString("email", "user@example.com");
        String userPhone = sharedPreferences.getString("phone", "+91xxxxxxxxxx");
        String transactionId = System.currentTimeMillis() + ""; // Generate a unique transaction ID

        // Get current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String orderDate = dateFormat.format(new Date());

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        String orderTime = timeFormat.format(new Date());

        // Create OrderItem list from Cart
        List<OrderItem> orderItems = new ArrayList<>();
        for (FoodItem item : cartItems) {
            orderItems.add(new OrderItem(item.getName(), item.getPrice(), item.getQuantity()));
        }

        // Save order to Firebase
        saveOrderToFirebase(userName, userEmail, userPhone, transactionId, orderDate, orderTime, orderItems);

        // Generate receipt and save to file
        generateReceipt(this,userName, userEmail, userPhone, orderDate, orderTime, orderItems,calculateTotal());

        // Pass data to OrderDetailActivity
        Intent intent = new Intent(CartActivity.this, OrderDetailActivity.class);
        // Pass the order time
        startActivity(intent);  // Start OrderDetailActivity
    }

    private String generateOrderSummary() {
        StringBuilder summary = new StringBuilder();
        for (FoodItem item : cartItems) {
            summary.append(item.getName())
                    .append(" (Qty: ").append(item.getQuantity())
                    .append(", Price: ₹").append(item.getPrice())
                    .append(")\n");
        }
        return summary.toString();
    }

    private void saveOrderToFirebase(String username, String userEmail, String userPhone, String transactionId, String orderDate, String orderTime, List<OrderItem> orderItems) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        String orderId = ordersRef.push().getKey(); // Generate a new order ID
        Order order = new Order(username, userEmail, userPhone, "Successful", orderDate, orderTime, orderItems); // Pass orderItems here

        ordersRef.child(orderId).setValue(order)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Order saved successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save order.", Toast.LENGTH_SHORT).show());
    }



    // Updated code to use scoped storage for Android 10+ (API 29)
    private void generateReceipt(Context context, String userName, String userEmail, String userPhone, String orderDate, String orderTime, List<OrderItem> orderItems, double totalAmount) {
        // Get the Downloads directory
        File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Ensure the directory exists
        if (!downloadDirectory.exists()) {
            downloadDirectory.mkdirs();
        }

        // Create a unique filename for the receipt
        String filePath = new File(downloadDirectory, "FoodCourt_Receipt_" + System.currentTimeMillis() + ".pdf").getAbsolutePath();

        Document document = new Document();
        try {
            // Create and write the PDF
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Add logo
            InputStream inputStream = context.getResources().openRawResource(R.drawable.logo2);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();

            Image logo = Image.getInstance(imageBytes);
            logo.scaleToFit(80, 80); // Resize the logo
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);

            // Add Title
            Paragraph title = new Paragraph("FoodCourt Receipt", new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add Customer Details Table
            PdfPTable customerTable = new PdfPTable(2);
            customerTable.setWidthPercentage(100);
            customerTable.setSpacingBefore(10);
            customerTable.addCell("Customer Name:");
            customerTable.addCell(userName);
            customerTable.addCell("Email:");
            customerTable.addCell(userEmail);
            customerTable.addCell("Phone:");
            customerTable.addCell(userPhone);
            customerTable.addCell("Order Date:");
            customerTable.addCell(orderDate);
            customerTable.addCell("Order Time:");
            customerTable.addCell(orderTime);
            document.add(customerTable);

            // Add a line break
            document.add(new Paragraph("\n"));

            // Add Order Items Table
            PdfPTable orderTable = new PdfPTable(3);
            orderTable.setWidthPercentage(100);
            orderTable.setSpacingBefore(10);
            orderTable.addCell("Item Name");
            orderTable.addCell("Quantity");
            orderTable.addCell("Price");

            for (OrderItem item : orderItems) {
                orderTable.addCell(item.getName());
                orderTable.addCell(String.valueOf(item.getQuantity()));
                orderTable.addCell("₹" + String.format(Locale.getDefault(), "%.2f", item.getPrice()));
            }

            // Add Total Amount Row
            PdfPCell totalCell = new PdfPCell(new Paragraph("Total Amount"));
            totalCell.setColspan(2);
            totalCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            orderTable.addCell(totalCell);
            orderTable.addCell("₹" + String.format(Locale.getDefault(), "%.2f", totalAmount));

            document.add(orderTable);

            // Add Footer
            Paragraph footer = new Paragraph("\nThank you for choosing FoodCourt!\nWe hope to serve you again soon!",
                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.ITALIC));
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

            document.close();

            // Notify user that the receipt was generated
            Toast.makeText(context, "Receipt generated: " + filePath, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error generating receipt: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
//            else {
//                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//            }
        }
    }
}
