package com.example.foodcourt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class OrderDetailActivity extends AppCompatActivity {


    private TextView collectionTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Initialize views
        collectionTimeTextView = findViewById(R.id.collectionTimeTextView);

        // Retrieve order details from the intent
        collectionTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OrderDetailActivity.this, "Thanks for ordering! Please collect your order within 30 minutes.", Toast.LENGTH_LONG).show();
            }
        });

}
}
