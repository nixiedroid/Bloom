package com.nixiedroid.bloomlwp.wallpapers.weather;

import android.Manifest;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;

public class LocationManger {
    private final FusedLocationProviderClient fusedLocationClient;

    private final Coord lastKnownLocation = new Coord();

    private static final LocationManger instance = new LocationManger();

    private LocationManger() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(App.get());
        getLocation();
    }

    public static LocationManger get(){
        return instance;
    }
    private void getLocation() {
        if (ContextCompat.checkSelfPermission(App.get(), Manifest.permission.ACCESS_COARSE_LOCATION) != 0) {
            L.w("no permissions");
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                L.v("LocationManger: lat=" + location.getLatitude() + " lon="+location.getLongitude());
                lastKnownLocation.setLon(location.getLongitude());
                lastKnownLocation.setLat(location.getLatitude());
                return;
            }
            L.d("LocationManger is null. Probably, disabled");
        });
    }
    public Coord getCoord(){
        getLocation();
        return lastKnownLocation;
    }
}
