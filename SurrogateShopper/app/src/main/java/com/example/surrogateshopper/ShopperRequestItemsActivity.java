package com.example.surrogateshopper;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ShopperRequestItemsActivity extends AppCompatActivity {
    private ListView lvPastVolunteers;
    private EditText etNewItem;
    private Button btnSubmitItem, btnGoToProfile, btnLogout;

    private ListView lvRequestedItems; // New ListView for requested items
    private ArrayList<String> requestedItems = new ArrayList<>();
    private ArrayAdapter<String> requestedItemsAdapter;

    private ArrayList<String> volunteerUsernames = new ArrayList<>();
    private HashMap<String, Integer> usernameToIdMap = new HashMap<>();

    private int shopperId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopper_request_items);

        lvPastVolunteers = findViewById(R.id.lvPastVolunteers);
        etNewItem = findViewById(R.id.etNewItem);
        btnSubmitItem = findViewById(R.id.btnSubmitItem);
        btnGoToProfile = findViewById(R.id.btnGoToProfile);
        btnLogout = findViewById(R.id.btnLogout); // <-- NEW button reference

        lvRequestedItems = findViewById(R.id.lvRequestedItems); // Initialize new ListView

        try {
            String userIdStr = UserSession.getInstance(this).getUserId();
            shopperId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            return;
        }

        fetchPastVolunteers();
        fetchRequestedItems();  // Fetch pending requested items on activity load

        btnSubmitItem.setOnClickListener(v -> submitNewItemRequest());
        btnGoToProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ShopperProfileActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ShopperRequestItemsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear back stack
            startActivity(intent);
            finish();
        });

        lvPastVolunteers.setOnItemClickListener((parent, view, position, id) -> {
            String username = volunteerUsernames.get(position);
            int volunteerId = usernameToIdMap.get(username);

            Intent intent = new Intent(ShopperRequestItemsActivity.this, ShopperSendMessage.class);
            intent.putExtra("volunteerId", volunteerId);
            intent.putExtra("volunteerUsername", username);
            intent.putExtra("shopperId", shopperId);
            startActivity(intent);
        });
    }

    private void fetchPastVolunteers() {
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://lamp.ms.wits.ac.za/home/s2799528/GetPastVolunteers.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("userId", shopperId);

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonParam.toString().getBytes());
                    os.flush();
                    os.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    reader.close();
                    return sb.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result == null) {
                    Toast.makeText(ShopperRequestItemsActivity.this, "Failed to load volunteers", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject response = new JSONObject(result);
                    if (response.getBoolean("success")) {
                        volunteerUsernames.clear();
                        usernameToIdMap.clear();

                        JSONArray volunteers = response.getJSONArray("volunteers");
                        for (int i = 0; i < volunteers.length(); i++) {
                            JSONObject obj = volunteers.getJSONObject(i);
                            String username = obj.getString("username");
                            int id = obj.getInt("volunteerId");

                            volunteerUsernames.add(username);
                            usernameToIdMap.put(username, id);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ShopperRequestItemsActivity.this,
                                android.R.layout.simple_list_item_1, volunteerUsernames);
                        lvPastVolunteers.setAdapter(adapter);
                    } else {
                        Toast.makeText(ShopperRequestItemsActivity.this, "No past volunteers found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ShopperRequestItemsActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void fetchRequestedItems() {
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://lamp.ms.wits.ac.za/home/s2799528/GetRequestedItems.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("shopperId", shopperId);

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonParam.toString().getBytes());
                    os.flush();
                    os.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    reader.close();
                    return sb.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result == null) {
                    Toast.makeText(ShopperRequestItemsActivity.this, "Failed to load requested items", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject response = new JSONObject(result);
                    if (response.getBoolean("success")) {
                        requestedItems.clear();

                        JSONArray items = response.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            String item = items.getString(i);
                            requestedItems.add(item);
                        }

                        if (requestedItemsAdapter == null) {
                            requestedItemsAdapter = new ArrayAdapter<>(ShopperRequestItemsActivity.this,
                                    android.R.layout.simple_list_item_1, requestedItems);
                            lvRequestedItems.setAdapter(requestedItemsAdapter);
                        } else {
                            requestedItemsAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(ShopperRequestItemsActivity.this, "No pending items found", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ShopperRequestItemsActivity.this, "Error parsing item response", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void submitNewItemRequest() {
        String itemList = etNewItem.getText().toString().trim();
        if (itemList.isEmpty()) {
            Toast.makeText(this, "Please enter items to request", Toast.LENGTH_SHORT).show();
            return;
        }

        new AsyncTask<String, Void, String>() {
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL("https://lamp.ms.wits.ac.za/home/s2799528/ShopperRequestItems.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");

                    JSONObject request = new JSONObject();
                    request.put("userId", shopperId);
                    request.put("RequestList", params[0]);

                    OutputStream os = conn.getOutputStream();
                    os.write(request.toString().getBytes());
                    os.flush();
                    os.close();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    reader.close();
                    return sb.toString();

                } catch (Exception e) {
                    return null;
                }
            }

            protected void onPostExecute(String result) {
                if (result == null) {
                    Toast.makeText(ShopperRequestItemsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    JSONObject response = new JSONObject(result);
                    boolean success = response.getBoolean("success");

                    if (success) {
                        Toast.makeText(ShopperRequestItemsActivity.this, "Request submitted", Toast.LENGTH_SHORT).show();
                        etNewItem.setText("");
                        fetchRequestedItems(); // Refresh the list after adding new request
                    } else {
                        String message = response.optString("message", "Failed to submit request");
                        Toast.makeText(ShopperRequestItemsActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ShopperRequestItemsActivity.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(itemList);
    }
}


