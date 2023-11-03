package com.nixiedroid.bloomlwp.wallpapers.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import androidx.core.content.ContextCompat;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class LocationManger {
    private static final LocationManger instance = new LocationManger();
    private final Coord lastKnownLocation = new Coord();
    protected long previousResultTime;
    LocationManager locationManager;

    private LocationManger() {
        locationManager = (LocationManager) App.get().getSystemService(LOCATION_SERVICE);
        updateLocation();
    }

    public static LocationManger get() {
        return instance;
    }

    private void updateLocation() {

        if (ContextCompat.checkSelfPermission(App.get(), Manifest.permission.ACCESS_FINE_LOCATION) != 0) {
            L.w("no permissions");
            return;
        }
        List<String> providers = locationManager.getProviders(true);
        Location location;
        for (String provider : providers) {
            location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                L.v("LocationManger: lat=" + location.getLatitude() + " lon=" + location.getLongitude());
                lastKnownLocation.setLon(location.getLongitude());
                lastKnownLocation.setLat(location.getLatitude());
            }
        }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                getActiveLocation(LocationManager.NETWORK_PROVIDER);
            } else getActiveLocation(LocationManager.GPS_PROVIDER);
        L.d("Location is null. Probably, disabled");

    }
    @SuppressLint({"deprecated", "MissingPermission"})
    private void getActiveLocation(String provider){
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                locationManager.getCurrentLocation(LocationManager.NETWORK_PROVIDER,
                        null, null, null);
            }
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, null, null);
    }

    public Coord getCoord() {
        if (TimeUtil.elapsedRealTimeSince(previousResultTime) < 30 * 60 * 1000) {
            L.v("Location Throttling");
            L.v("LocationManger: lat=" + lastKnownLocation.getLat() + " lon=" + lastKnownLocation.getLon());
            return lastKnownLocation;
        }
        updateLocation();
        return lastKnownLocation;
    }
}
