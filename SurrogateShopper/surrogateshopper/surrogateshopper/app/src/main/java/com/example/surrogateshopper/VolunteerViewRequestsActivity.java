package com.example.surrogateshopper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class VolunteerViewRequestsActivity extends AppCompatActivity {
    private ListView lvRequests;
    private Button btnVolunteerProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_view_requests);

        lvRequests = findViewById(R.id.lvRequests);
        btnVolunteerProfile = findViewById(R.id.btnVolunteerProfile);

        // Temporary sample requests
        String[] sampleRequests = {"Milk and Bread", "Groceries", "Medicine Pickup"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sampleRequests);
        lvRequests.setAdapter(adapter);

        lvRequests.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, VolunteerRequestDetailsActivity.class);
            startActivity(intent);
        });

        btnVolunteerProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, VolunteerProfileActivity.class);
            startActivity(intent);
        });
    }
}

