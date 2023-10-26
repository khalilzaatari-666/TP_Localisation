package com.vo1d.tpgps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.Manifest;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private double latitude;
    private double longitude;
    private double altitude;
    private float accuracy;
    OkHttpClient client;
    String insertUrl = "http://192.168.1.158:8080/api/v1/positions/create";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openMapButton = findViewById(R.id.openMapButton);

        openMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open the MapsActivity
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        client = new OkHttpClient();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (locationPermission) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 150, new
                    LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            altitude = location.getAltitude();
                            accuracy = location.getAccuracy();
                            @SuppressLint("StringFormatMatches") String msg = String.format(
                                    getResources().getString(R.string.new_location), latitude, longitude, altitude, accuracy);

                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            sendCurrentLocation();
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            String newStatus = "";
                            switch (status) {
                                case LocationProvider.OUT_OF_SERVICE:
                                    newStatus = "OUT_OF_SERVICE";
                                    break;
                                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                                    newStatus = "TEMPORARILY_UNAVAILABLE";
                                    break;
                                case LocationProvider.AVAILABLE:
                                    newStatus = "AVAILABLE";
                                    break;
                            }
                            String msg = String.format(getResources().getString(R.string.provider_new_status),
                                    provider, newStatus);
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                            sendCurrentLocation();
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            String msg = String.format(getResources().getString(R.string.provider_enabled),
                                    provider);
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            String msg = String.format(getResources().getString(R.string.provider_disabled),
                                    provider);
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Find the "Send Location" button and set an onClickListener
        Button sendLocationButton = findViewById(R.id.sendLocationButton);
        sendLocationButton.setOnClickListener(v -> {
            sendCurrentLocation();
        });
    }

    private void sendCurrentLocation() {
        // Format the current LocalDateTime in ISO 8601 format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String formattedDateTime = sdf.format(new Date());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getImei();

            // Create a JSON object with the ISO 8601 date-time format and IMEI
            JSONObject json = new JSONObject();
            try {
                json.put("latitude", latitude);
                json.put("longitude", longitude);
                json.put("datetime", formattedDateTime);
                json.put("imei", imei);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Create a request body with the JSON data
            RequestBody body = RequestBody.create(json.toString(), JSON);

            // Create the HTTP request
            Request request = new Request.Builder()
                    .url(insertUrl)
                    .post(body)
                    .build();

            // Send the request
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseData = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("ServerResponse", responseData);
                            }
                        });
                    } else {
                        Log.e("TAG", "Error: " + response.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Error: " + response.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }
}
