package com.example.surrogateshopper;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShopperProfileActivity extends AppCompatActivity {

    private TextView tvFirstName, tvLastName, tvUsername, tvEmail;
    private TextView tvPhoneNumber, tvStreetAddress, tvCityStateZip;
    private TextView tvLocation, tvBirthDate, tvAccountCreated, tvAccountType;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final String PROFILE_URL = "https://lamp.ms.wits.ac.za/home/s2799528/shopperprofile.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopper_profile);

        // Initialize TextViews
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvStreetAddress = findViewById(R.id.tvStreetAddress);
        tvCityStateZip = findViewById(R.id.tvCityStateZip);
        tvLocation = findViewById(R.id.tvLocation);
        tvBirthDate = findViewById(R.id.tvBirthDate);
        tvAccountCreated = findViewById(R.id.tvAccountCreated);
        tvAccountType = findViewById(R.id.tvAccountType);

        // Back Button
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, ShopperRequestItemsActivity.class));
        });

        // Edit Buttons
        Button btnEditPhone = findViewById(R.id.btnEditPhone);
        Button btnEditEmail = findViewById(R.id.btnEditEmail);

        btnEditPhone.setOnClickListener(v -> showEditDialog("Phone Number", tvPhoneNumber.getText().toString(), "EditPhone.php", "PhoneNumber"));
        btnEditEmail.setOnClickListener(v -> showEditDialog("Email", tvEmail.getText().toString(), "EditEmail.php", "Email"));

        // Load user profile using UserSession
        String username = UserSession.getInstance(getApplicationContext()).getUsername();
        String userId = UserSession.getInstance(getApplicationContext()).getUserId();

        if (username != null && !username.isEmpty()) {
            loadUserProfileData("username", username);
        } else if (userId != null && !userId.isEmpty()) {
            loadUserProfileData("userId", userId);
        } else {
            Toast.makeText(this, "No user information found", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserProfileData(String key, String value) {
        executorService.execute(() -> {
            try {
                String postData = key + "=" + value;
                URL url = new URL(PROFILE_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(postData.getBytes(StandardCharsets.UTF_8));
                }

                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                }
                processResponse(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void processResponse(String jsonResponse) {
        try {
            JSONObject userObject = new JSONObject(jsonResponse);
            updateUIWithUserData(userObject);
        } catch (JSONException e) {
            try {
                JSONArray arr = new JSONArray(jsonResponse);
                if (arr.length() > 0) updateUIWithUserData(arr.getJSONObject(0));
            } catch (JSONException ex) {
                runOnUiThread(() -> Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void updateUIWithUserData(JSONObject userData) {
        runOnUiThread(() -> {
            tvAccountType.setText(" Shopper");
            tvFirstName.setText(userData.optString("FirstName", "N/A"));
            tvLastName.setText(userData.optString("LastName", "N/A"));
            tvUsername.setText(userData.optString("Username", "N/A"));
            tvEmail.setText(userData.optString("Email", "N/A"));
            tvPhoneNumber.setText(userData.optString("PhoneNumber", "N/A"));
            tvStreetAddress.setText(userData.optString("StreetAddress", "N/A"));

            String city = userData.optString("City", "N/A");
            String state = userData.optString("ProvinceState", "N/A");
            String zip = userData.optString("ZipCode", "N/A");
            tvCityStateZip.setText(city + ", " + state + " " + zip);

            String lat = userData.optString("GPSLatitude", "N/A");
            String lng = userData.optString("GPSLongitude", "N/A");
            tvLocation.setText(lat + ", " + lng);

            tvBirthDate.setText(userData.optString("Birthdate", "N/A"));
            tvAccountCreated.setText(userData.optString("AccountCreatedOn", "N/A"));
        });
    }

    private void showEditDialog(String title, String currentValue, String phpEndpoint, String fieldName) {
        EditText input = new EditText(this);
        input.setText(currentValue);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(this)
                .setTitle("Edit " + title)
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newValue = input.getText().toString();
                    updateFieldOnServer(phpEndpoint, fieldName, newValue);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateFieldOnServer(String phpFile, String key, String value) {
        executorService.execute(() -> {
            try {
                String userId = UserSession.getInstance(getApplicationContext()).getUserId();
                String postData = "userId=" + userId + "&" + key + "=" + value;
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2799528/" + phpFile);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(postData.getBytes(StandardCharsets.UTF_8));
                }

                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
                    loadUserProfileData("userId", userId); // reload updated profile
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        executorService.shutdown();
        super.onDestroy();
    }
}

