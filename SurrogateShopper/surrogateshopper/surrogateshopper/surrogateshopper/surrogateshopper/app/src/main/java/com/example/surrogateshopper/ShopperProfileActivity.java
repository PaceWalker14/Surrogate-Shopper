package com.example.surrogateshopper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ShopperProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopper_profile);

        // Initialize the Back button
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to ShopperRequestItemsActivity
                Intent intent = new Intent(ShopperProfileActivity.this, ShopperRequestItemsActivity.class);
                startActivity(intent);
            }
        });

        // Later you can fetch actual username/location here
    }
}


