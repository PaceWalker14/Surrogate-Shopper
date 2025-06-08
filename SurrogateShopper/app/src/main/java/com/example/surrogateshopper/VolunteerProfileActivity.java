package com.example.surrogateshopper;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class VolunteerProfileActivity extends AppCompatActivity {
    private TextView tvThankYouMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_messages);

        tvThankYouMessages = findViewById(R.id.tvThankYouMessages);
        Button btnBack = findViewById(R.id.btnBackToRequests);

        btnBack.setOnClickListener(v -> finish());

        String volunteerId = UserSession.getInstance(getApplicationContext()).getUserId();

        fetchFeedback(volunteerId);
    }

    private void fetchFeedback(String volunteerId) {
        String url = "https://lamp.ms.wits.ac.za/home/s2799528/get_feedback.php?volunteerId=" + volunteerId;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> tvThankYouMessages.setText("Failed to fetch messages."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string();

                runOnUiThread(() -> {
                    try {
                        JSONObject obj = new JSONObject(jsonResponse);
                        if (obj.getBoolean("success")) {
                            JSONArray feedbackArray = obj.getJSONArray("feedback");
                            StringBuilder messages = new StringBuilder();

                            for (int i = 0; i < feedbackArray.length(); i++) {
                                JSONObject item = feedbackArray.getJSONObject(i);
                                String msg = item.getString("Message");
                                String date = item.getString("MessageDate");
                                String username = item.optString("Username", "Unknown");

                                messages.append("• ").append(msg)
                                        .append("\n  — from ").append(username)
                                        .append(" on ").append(date)
                                        .append("\n\n");
                            }


                            tvThankYouMessages.setText(messages.toString().isEmpty() ? "No messages yet." : messages);
                        } else {
                            tvThankYouMessages.setText("No messages found.");
                        }
                    } catch (Exception e) {
                        tvThankYouMessages.setText("Error parsing server response.");
                    }
                });
            }
        });
    }
}

