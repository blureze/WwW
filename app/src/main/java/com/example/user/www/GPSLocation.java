package com.example.user.www;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

public class GPSLocation {
    Context mContext;
    LocationManager locationMgr;
    String provider;
    Location location;
    LatLng userLocation;
    LocationListener locationlsr = new LocationListener() {
        double latitude;
        double longitude;
        @Override
        public void onLocationChanged(Location loc) {
            latitude = loc.getLatitude();
            longitude = loc.getLongitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    public GPSLocation(Context mContext) {
        this.mContext = mContext;
        locationMgr = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        }
        if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationMgr.requestLocationUpdates(provider, 0, 0, locationlsr);
            location = locationMgr.getLastKnownLocation(provider);
            if(location == null) {
                location = locationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            //location = new Location(provider);
            this.userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }

        //Log.i("lat", String.valueOf(location.getLatitude())); 
        //Log.i("??", "Yes"); 
    }

}