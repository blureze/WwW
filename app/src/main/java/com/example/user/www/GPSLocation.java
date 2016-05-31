package com.example.user.www;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Swc on 16/5/25.
 */
public class GPSLocation {
    Context mContext;
    LocationManager service;
    Criteria criteria;
    String provider;
    Location location;
    LatLng userLocation;
    public GPSLocation(Context mContext) {
        this.mContext = mContext;
        this.service = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        //this.criteria = new Criteria();
        if (service.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        }
        if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //this.provider = service.getBestProvider(criteria, false);
            this.location = service.getLastKnownLocation(provider);
            //Log.i("lat", String.valueOf(location.getLatitude()));
            this.userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            Log.i("??", "Yes");
        }
        else Log.i("??", "NO");
    }


}
