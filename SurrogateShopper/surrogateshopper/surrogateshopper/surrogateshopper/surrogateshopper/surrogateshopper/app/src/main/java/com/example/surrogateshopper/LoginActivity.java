package com.example.surrogateshopper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            // For now just skip to Shopper page
            Intent intent = new Intent(LoginActivity.this, ShopperRequestItemsActivity.class);// or ShopperRequestItemsActivity.class
            startActivity(intent);                                                                              // VolunteerViewRequestsActivity.class
        });

        findViewById(R.id.btnCreateAccount).setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(intent);
        });
    }
}

