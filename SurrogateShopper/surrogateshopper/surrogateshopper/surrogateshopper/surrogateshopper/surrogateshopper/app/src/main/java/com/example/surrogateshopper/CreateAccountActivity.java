package com.example.surrogateshopper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Button btnRegisterShopper = findViewById(R.id.btnRegisterShopper);
        Button btnRegisterVolunteer = findViewById(R.id.btnRegisterVolunteer);

        btnRegisterShopper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle shopper account registration
                // After creating shopper account, go back to login
                finish();
            }
        });

        btnRegisterVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle volunteer account registration
                // After creating volunteer account, go back to login
                finish();
            }
        });
    }
}



