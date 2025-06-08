package com.example.surrogateshopper;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.*;

public class CreateAccountActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private EditText etFirstName, etLastName, etUsername, etPassword;
    private EditText etEmail, etPhoneNumber, etStreetAddress, etCity;
    private EditText etStateProvince, etZipCode, etBirthDate;
    private RadioGroup rgAccountType;
    private Button btnRegisterShopper, btnRegisterVolunteer;
    private LinearLayout shopperExtraFields;

    private String gpsLat = "0", gpsLong = "0";
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        etFirstName      = findViewById(R.id.etFirstName);
        etLastName       = findViewById(R.id.etLastName);
        etUsername       = findViewById(R.id.etNewUsername);
        etPassword       = findViewById(R.id.etNewPassword);
        etEmail          = findViewById(R.id.etEmail);
        etPhoneNumber    = findViewById(R.id.etPhoneNumber);
        etStreetAddress  = findViewById(R.id.etStreetAddress);
        etCity           = findViewById(R.id.etCity);
        etStateProvince  = findViewById(R.id.etStateProvince);
        etZipCode        = findViewById(R.id.etZipCode);
        etBirthDate      = findViewById(R.id.etBirthDate);

        rgAccountType        = findViewById(R.id.rgAccountType);
        btnRegisterShopper   = findViewById(R.id.btnRegisterShopper);
        btnRegisterVolunteer = findViewById(R.id.btnRegisterVolunteer);
        shopperExtraFields   = findViewById(R.id.shopperExtraFields);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        requestLocationPermissions();

        rgAccountType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbRequestAssistance) {
                shopperExtraFields.setVisibility(View.VISIBLE);
                btnRegisterShopper.setVisibility(View.VISIBLE);
                btnRegisterVolunteer.setVisibility(View.GONE);
            } else {
                shopperExtraFields.setVisibility(View.GONE);
                btnRegisterShopper.setVisibility(View.GONE);
                btnRegisterVolunteer.setVisibility(View.VISIBLE);
            }
        });

        btnRegisterShopper.setOnClickListener(v -> {
            if (validateInputs(true)) {
                saveUserData("shopper");
            }
        });

        btnRegisterVolunteer.setOnClickListener(v -> {
            if (validateInputs(false)) {
                saveUserData("volunteer");
            }
        });
    }

    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        gpsLat  = String.valueOf(location.getLatitude());
                        gpsLong = String.valueOf(location.getLongitude());

                        Geocoder geocoder = new Geocoder(CreateAccountActivity.this, Locale.getDefault());
                        try {
                            List<Address> results = geocoder.getFromLocation(
                                    location.getLatitude(),
                                    location.getLongitude(),
                                    1
                            );
                            if (results != null && !results.isEmpty()) {
                                Address addr = results.get(0);
                                String street = "";
                                if (addr.getThoroughfare() != null) {
                                    street = addr.getThoroughfare();
                                    if (addr.getSubThoroughfare() != null) {
                                        street = addr.getSubThoroughfare() + " " + street;
                                    }
                                }
                                etStreetAddress.setText(street);

                                if (addr.getLocality() != null) {
                                    etCity.setText(addr.getLocality());
                                }
                                if (addr.getAdminArea() != null) {
                                    etStateProvince.setText(addr.getAdminArea());
                                }
                                if (addr.getPostalCode() != null) {
                                    etZipCode.setText(addr.getPostalCode());
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },
                null
        );
    }

    private boolean validateInputs(boolean isShopper) {
        if (etFirstName.getText().toString().trim().isEmpty() ||
                etLastName.getText().toString().trim().isEmpty() ||
                etUsername.getText().toString().trim().isEmpty() ||
                etPassword.getText().toString().trim().isEmpty() ||
                etEmail.getText().toString().trim().isEmpty() ||
                etPhoneNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (isShopper &&
                (etStreetAddress.getText().toString().trim().isEmpty() ||
                        etCity.getText().toString().trim().isEmpty() ||
                        etStateProvince.getText().toString().trim().isEmpty() ||
                        etZipCode.getText().toString().trim().isEmpty() ||
                        etBirthDate.getText().toString().trim().isEmpty())) {
            Toast.makeText(this, "Please complete all shopper fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveUserData(String registrationType) {
        String endpoint = registrationType.equals("shopper") ?
                "CreateShopper.php" : "CreateVolunteer.php";
        String url = "https://lamp.ms.wits.ac.za/home/s2799528/" + endpoint;

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        JSONObject json = new JSONObject();
        try {
            json.put("firstName", etFirstName.getText().toString().trim());
            json.put("lastName" , etLastName.getText().toString().trim());
            json.put("username" , etUsername.getText().toString().trim());
            json.put("password" , etPassword.getText().toString().trim());
            json.put("email"    , etEmail.getText().toString().trim());
            json.put("phone"    , etPhoneNumber.getText().toString().trim());
            json.put("createdDate", currentDate);

            if (registrationType.equals("shopper")) {
                json.put("address"  , etStreetAddress.getText().toString().trim());
                json.put("city"     , etCity.getText().toString().trim());
                json.put("province" , etStateProvince.getText().toString().trim());
                json.put("zip"      , etZipCode.getText().toString().trim());

                // Fix birthdate format here
                String inputBirthDate = etBirthDate.getText().toString().trim(); // e.g., "09/18/2004"
                String reformattedDate = "";
                try {
                    Date parsed = new SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(inputBirthDate);
                    reformattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(parsed);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Invalid birthdate format. Use MM/DD/YYYY", Toast.LENGTH_SHORT).show();
                    return;
                }
                json.put("birthDate", reformattedDate);

                json.put("gpsLat"   , gpsLat);
                json.put("gpsLong"  , gpsLong);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error building JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(CreateAccountActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() == null) {
                    runOnUiThread(() ->
                            Toast.makeText(CreateAccountActivity.this, "Empty response from server", Toast.LENGTH_SHORT).show());
                    return;
                }

                String responseBody = response.body().string().trim();
                try {
                    JSONObject result = new JSONObject(responseBody);
                    boolean success = result.optBoolean("success", false);
                    String message = result.optString("message", "Unknown error");
                    runOnUiThread(() -> {
                        Toast.makeText(CreateAccountActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (success) finish();
                    });
                } catch (JSONException e) {
                    runOnUiThread(() ->
                            Toast.makeText(CreateAccountActivity.this, "Invalid server response: " + responseBody, Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}




