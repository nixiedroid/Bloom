package com.nixiedroid.bloomlwp.wallpapers.weather;

import android.Manifest;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import androidx.core.content.ContextCompat;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;

import static android.content.Context.LOCATION_SERVICE;

public class LocationManger {
    private static final LocationManger instance = new LocationManger();
    private final Coord lastKnownLocation = new Coord();
    protected long previousResultTime;
    LocationManager locationManager;

    private LocationManger() {
        locationManager = (LocationManager) App.get().getSystemService(LOCATION_SERVICE);
        getLocation();
    }

    public static LocationManger get() {
        return instance;
    }

    private void getLocation() {

        if (ContextCompat.checkSelfPermission(App.get(), Manifest.permission.ACCESS_COARSE_LOCATION) != 0) {
            L.w("no permissions");
            return;
        }

        L.d(locationManager.getProviders(true).toString());
        Location location = null;

        if (ContextCompat.checkSelfPermission(App.get(), Manifest.permission.ACCESS_FINE_LOCATION) == 0) {
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (location != null) {
            L.v("LocationManger: lat=" + location.getLatitude() + " lon=" + location.getLongitude());
            lastKnownLocation.setLon(location.getLongitude());
            lastKnownLocation.setLat(location.getLatitude());
        } else {
//            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                getActiveLocation(LocationManager.NETWORK_PROVIDER);
//            } else getActiveLocation(LocationManager.GPS_PROVIDER);
        }
        L.d("Location is null. Probably, disabled");

    }
    private void getActiveLocation(String provider){
        if (ContextCompat.checkSelfPermission(App.get(), Manifest.permission.ACCESS_FINE_LOCATION) == 0) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                locationManager.getCurrentLocation(LocationManager.NETWORK_PROVIDER,
                        null, null, null);
            }
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, null, null);
        }
    }

    public Coord getCoord() {
        if (TimeUtil.elapsedRealTimeSince(previousResultTime) < 30 * 60 * 1000) {
            L.v("Location Throttling");
            L.v("LocationManger: lat=" + lastKnownLocation.getLat() + " lon=" + lastKnownLocation.getLon());
            return lastKnownLocation;
        }
        ;
        getLocation();
        return lastKnownLocation;
    }
}
