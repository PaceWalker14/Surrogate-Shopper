package com.example.surrogateshopper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShopperSendMessage extends AppCompatActivity {

    private EditText etMessage;
    private Button btnSend;
    private int userId;        // shopperId (logged-in user)
    private int volunteerId;   // from Intent extra

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopper_send_message);

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // Get volunteer info from Intent
        volunteerId = getIntent().getIntExtra("volunteerId", -1);
        String volunteerUsername = getIntent().getStringExtra("volunteerUsername");

        if (volunteerId == -1 || volunteerUsername == null) {
            Toast.makeText(this, "Invalid volunteer info", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get logged-in shopper's userId from UserSession
        userId = -1; // default invalid
        try {
            String userIdStr = UserSession.getInstance(this).getUserId();
            if (userIdStr != null) {
                userId = Integer.parseInt(userIdStr);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (userId == -1) {
            Toast.makeText(this, "Failed to retrieve your Shopper ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSend.setOnClickListener(view -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessageToVolunteer(userId, volunteerId, message);
                Toast.makeText(this, "Message Sent, thank you for your feedback!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ShopperSendMessage.this, ShopperRequestItemsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please enter a message.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessageToVolunteer(int userId, int volunteerId, String message) {
        new Thread(() -> {
            try {
                URL url = new URL("https://lamp.ms.wits.ac.za/home/s2799528/ThankVolunteer.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                // Build JSON payload with the correct userId and volunteerId
                JSONObject json = new JSONObject();
                json.put("userId", userId);
                json.put("volunteerId", volunteerId);
                json.put("message", message);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.toString().getBytes("UTF-8"));
                }

                int status = conn.getResponseCode();
                InputStream is = (status >= 200 && status < 400) ? conn.getInputStream() : conn.getErrorStream();

                StringBuilder responseStr = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseStr.append(line);
                    }
                }

                String responseString = responseStr.toString();

                JSONObject response = new JSONObject(responseString);
                boolean success = response.optBoolean("success", false);
                String serverMsg = response.optString("message", "");


                conn.disconnect();

            } catch (Exception e) {
                Log.e("NETWORK_ERROR", "Error: " + e.getMessage(), e);
            }
        }).start();
    }
}





