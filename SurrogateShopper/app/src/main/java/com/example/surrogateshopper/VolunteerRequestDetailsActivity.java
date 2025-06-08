package com.example.surrogateshopper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class VolunteerRequestDetailsActivity extends AppCompatActivity {

    private JSONObject requestDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_request_details);

        TextView tvRequestDetails = findViewById(R.id.tvRequestDetails);
        TextView tvAddress = findViewById(R.id.tvAddress);
        TextView tvCreatedAt = findViewById(R.id.tvCreatedAt);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnMarkAsTaken = findViewById(R.id.btnMarkAsTaken);

        String jsonString = getIntent().getStringExtra("requestData");

        try {
            requestDetails = new JSONObject(jsonString);
            String requestList = requestDetails.getString("RequestList");
            String createdAt = requestDetails.optString("CreatedAt", "Unknown date");
            String address = requestDetails.optString("StreetAddress", "No address provided");
            String city = requestDetails.optString("City", "Unknown city");
            String province = requestDetails.optString("ProvinceState", "Unknown province");
            String email = requestDetails.optString("Email", "No email provided");
            String phone = requestDetails.optString("PhoneNumber", "No phone number provided");

            tvRequestDetails.setText("Requested Items: " + requestList);
            tvAddress.setText("\uD83D\uDCCD Address: " + address + ", " + city + ", " + province);
            tvCreatedAt.setText("\uD83D\uDCC5 Requested On: " + createdAt);
            tvEmail.setText("✉\uFE0F Email: " + email);
            tvPhoneNumber.setText("\uD83D\uDCDE Phone: " + phone);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load request details.", Toast.LENGTH_SHORT).show();
        }

        btnMarkAsTaken.setOnClickListener(v -> {
            try {
                String requestId = requestDetails.getString("RequestID");
                String volunteerId = UserSession.getInstance(getApplicationContext()).getUserId();
                new MarkRequestAsTakenTask().execute(requestId, volunteerId);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error marking request as taken.", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, VolunteerViewRequestsActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private class MarkRequestAsTakenTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String requestId = params[0];
            String volunteerId = params[1];

            HttpURLConnection conn = null;
            BufferedReader reader = null;
            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2799528/MarkRequestTaken.php");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                String data = "requestId=" + URLEncoder.encode(requestId, "UTF-8") +
                        "&volunteerId=" + URLEncoder.encode(volunteerId, "UTF-8");

                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                InputStream is = (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            } finally {
                if (conn != null) conn.disconnect();
                if (reader != null) try { reader.close(); } catch (Exception ignored) {}
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.toLowerCase().contains("success")) {
                Toast.makeText(VolunteerRequestDetailsActivity.this, "Request marked as taken!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(VolunteerRequestDetailsActivity.this, "Failed to update request: " + result, Toast.LENGTH_LONG).show();
            }
        }
    }
}


