package com.example.user.www;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * This shows how to create a simple activity with a map and a marker on the map.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    GPSLocation myLocation;
    private LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("HO", "HI");
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        myLocation = new GPSLocation(this);

        Intent intent = getIntent();
        String get_lat = intent.getStringExtra("lat");
        String get_lng = intent.getStringExtra("lng");
        double lat = Double.parseDouble(get_lat);
        double lng = Double.parseDouble(get_lng);
        Log.d("aaa", String.valueOf(lat));
        Log.d("aaa", String.valueOf(lng));
        location = new LatLng(lat, lng);
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        //Log.i("userlocation", String.valueOf(myLocation.userLocation));
        //map.addMarker(new MarkerOptions().position(new LatLng(24, 121)).title("Marker"));
//        map.addMarker(new MarkerOptions().position(myLocation.userLocation).title("Tester"));
        map.addMarker(new MarkerOptions().position(location).title("Tester"));
    }
}
