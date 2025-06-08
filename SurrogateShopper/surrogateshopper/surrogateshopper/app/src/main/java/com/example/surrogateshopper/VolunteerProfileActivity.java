package com.example.surrogateshopper;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class VolunteerProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_profile);

        findViewById(R.id.btnBackToRequests).setOnClickListener(v -> {
            finish(); // Go back to VolunteerViewRequestsActivity
        });
    }
}

