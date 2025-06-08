package com.example.surrogateshopper;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String LOGIN_URL = "https://lamp.ms.wits.ac.za/home/s2799528/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 🔧 Force light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // 🔧 Switch from splash theme to app theme
        setTheme(R.style.Theme_Surrogateshopper);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(username, password);
        });

        findViewById(R.id.btnCreateAccount).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
        });
    }

    private void loginUser(String username, String password) {
        executorService.execute(() -> {
            try {
                JSONObject response = performLoginRequest(username, password);

                runOnUiThread(() -> {
                    try {
                        if (response != null && response.getString("status").equals("success")) {
                            String userType = response.getString("userType");
                            String userId = response.getString("userId");

                            // Store globally
                            UserSession.getInstance(getApplicationContext()).setUser(username, userId);

                            if (userType.equals("shopper")) {
                                Toast.makeText(LoginActivity.this, "Login successful as Shopper", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, ShopperRequestItemsActivity.class));
                            } else if (userType.equals("volunteer")) {
                                Toast.makeText(LoginActivity.this, "Login successful as Volunteer", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, VolunteerViewRequestsActivity.class));
                            }
                        } else {
                            String message = (response != null) ? response.getString("message") : "Network error";
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private JSONObject performLoginRequest(String username, String password) throws Exception {
        URL url = new URL(LOGIN_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        String postData = "username=" + username + "&password=" + password;
        try (OutputStream os = connection.getOutputStream()) {
            os.write(postData.getBytes(StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } finally {
            connection.disconnect();
        }

        return new JSONObject(response.toString());
    }

    @Override
    protected void onDestroy() {
        executorService.shutdown();
        super.onDestroy();
    }
}

