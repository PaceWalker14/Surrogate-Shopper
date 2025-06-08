package com.example.surrogateshopper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ShopperRequestItemsActivity extends AppCompatActivity {
    private EditText etItemRequest, etThankYouMessage;
    private Button btnSubmitRequest, btnGoToProfile, btnPostThanks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopper_request_items);

        etItemRequest = findViewById(R.id.etItemRequest);
        etThankYouMessage = findViewById(R.id.etThankYouMessage);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        btnGoToProfile = findViewById(R.id.btnGoToProfile);
        btnPostThanks = findViewById(R.id.btnPostThanks);

        btnSubmitRequest.setOnClickListener(v -> {
            String requestedItem = etItemRequest.getText().toString().trim();
            if (!requestedItem.isEmpty()) {
                // Later save to database
                Toast.makeText(this, "Request submitted: " + requestedItem, Toast.LENGTH_SHORT).show();
                etItemRequest.setText(""); // Clear field
            } else {
                Toast.makeText(this, "Please enter an item.", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoToProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ShopperProfileActivity.class);
            startActivity(intent);
        });

        btnPostThanks.setOnClickListener(v -> {
            String thankYouMessage = etThankYouMessage.getText().toString().trim();
            if (!thankYouMessage.isEmpty()) {
                // Later save to database
                Toast.makeText(this, "Thank you posted: " + thankYouMessage, Toast.LENGTH_SHORT).show();
                etThankYouMessage.setText(""); // Clear field
            } else {
                Toast.makeText(this, "Please write a thank you message first.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

