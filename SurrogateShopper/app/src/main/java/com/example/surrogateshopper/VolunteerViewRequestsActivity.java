package com.example.surrogateshopper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class VolunteerViewRequestsActivity extends AppCompatActivity {
    private ListView lvRequests;
    private Button btnVolunteerProfile;
    private Button btnLogout; // <-- Added logout button
    private ArrayList<String> requestNames = new ArrayList<>();
    private ArrayList<JSONObject> fullRequestObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_view_requests);

        lvRequests = findViewById(R.id.lvRequests);
        btnVolunteerProfile = findViewById(R.id.btnViewMessages);
        btnLogout = findViewById(R.id.btnLogout); // <-- Initialize button

        new FetchRequestsTask().execute("https://lamp.ms.wits.ac.za/home/s2799528/VolunteerViewRequests.php");

        lvRequests.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, VolunteerRequestDetailsActivity.class);
            intent.putExtra("requestData", fullRequestObjects.get(position).toString());
            startActivity(intent);
        });

        btnVolunteerProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, VolunteerProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(VolunteerViewRequestsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
            finish();
        });
    }

    private class FetchRequestsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                return response.toString();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(VolunteerViewRequestsActivity.this, "Failed to fetch requests.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject json = new JSONObject(result);
                if (json.getBoolean("success")) {
                    JSONArray requests = json.getJSONArray("requests");

                    for (int i = 0; i < requests.length(); i++) {
                        JSONObject obj = requests.getJSONObject(i);
                        fullRequestObjects.add(obj);
                        requestNames.add(obj.getString("RequestList"));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(VolunteerViewRequestsActivity.this, android.R.layout.simple_list_item_1, requestNames);
                    lvRequests.setAdapter(adapter);
                } else {
                    Toast.makeText(VolunteerViewRequestsActivity.this, "No requests found.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(VolunteerViewRequestsActivity.this, "Error parsing response.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

