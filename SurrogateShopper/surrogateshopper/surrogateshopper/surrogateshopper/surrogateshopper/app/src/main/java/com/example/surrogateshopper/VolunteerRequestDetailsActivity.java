package com.example.surrogateshopper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class VolunteerRequestDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_request_details);

        // Set placeholder data for now
        TextView tvRequestDetails = findViewById(R.id.tvRequestDetails);
        TextView tvAddress = findViewById(R.id.tvAddress);

        tvRequestDetails.setText("Requested Items: Milk, Bread, Eggs");
        tvAddress.setText("Address: 3 Volt Street");

        // Button to mark request as taken
        findViewById(R.id.btnMarkAsTaken).setOnClickListener(v -> {
            Toast.makeText(this, "Request marked as taken!", Toast.LENGTH_SHORT).show();
        });

        // Button to go back
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to VolunteerViewRequestsActivity
                Intent intent = new Intent(VolunteerRequestDetailsActivity.this, VolunteerViewRequestsActivity.class);
                startActivity(intent);
            }
        });
    }
}



