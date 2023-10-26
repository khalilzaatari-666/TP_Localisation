package com.vo1d.tpgps;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Fetch positions from the backend and display them as markers
        fetchPositionsAndDisplayMarkers();
    }

    private void fetchPositionsAndDisplayMarkers() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.1.158:8080/api/v1/positions/all")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("Tag",responseData);

                    try {
                        JSONArray positionsArray = new JSONArray(responseData);
                        displayMarkersForPositions(positionsArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d("Error in Response","Response was not sucessful");
                }
            }
        });
    }

    private void displayMarkersForPositions(JSONArray positionsArray) throws JSONException {
        Handler handler = new Handler(Looper.getMainLooper());

        for (int i = 0; i < positionsArray.length(); i++) {
            JSONObject positionObject = positionsArray.getJSONObject(i);
            double latitude = positionObject.getDouble("latitude");
            double longitude = positionObject.getDouble("longitude");

            handler.post(() -> {
                // Create a LatLng object from the latitude and longitude
                LatLng location = new LatLng(latitude, longitude);

                // Add a marker to the map
                Marker marker = mMap.addMarker(new MarkerOptions().position(location));
            });
        }
    }
}
