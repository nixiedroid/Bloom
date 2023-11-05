package com.nixiedroid.bloomlwp.weather;

import android.Manifest;
import android.content.Context;
import android.location.LocationManager;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static android.content.Context.LOCATION_SERVICE;

public class LocationManger {
    private static final LocationManger instance = new LocationManger();
    private final Coord lastKnownLocation = new Coord();
    protected long previousResultTime;
    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationClient;
    private static boolean isGoogleAvailable = true;

    private LocationManger() {

        try {
            Method m = LocationServices.class.getDeclaredMethod("getFusedLocationProviderClient", Context.class);
            fusedLocationClient = (FusedLocationProviderClient) m.invoke(null,App.get());
        } catch (NoSuchMethodException e) {
            isGoogleAvailable = false;
            locationManager = (LocationManager) App.get().getSystemService(LOCATION_SERVICE);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        updateLocationGoogle();
    }

    public static LocationManger get() {
        return instance;
    }

//    private void updateLocationNonGoogle() {
//        if (ContextCompat.checkSelfPermission(App.get(), Manifest.permission.ACCESS_FINE_LOCATION) != 0) {
//            L.w("no permissions");
//            return;
//        }
//        List<String> providers = locationManager.getProviders(true);
//        Location location;
//        for (String provider : providers) {
//            location = locationManager.getLastKnownLocation(provider);
//            if (location != null) {
//                L.v("LocationManger: lat=" + location.getLatitude() + " lon=" + location.getLongitude());
//                lastKnownLocation.setLon(location.getLongitude());
//                lastKnownLocation.setLat(location.getLatitude());
//            }
//        }
//            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                getActiveLocation(LocationManager.NETWORK_PROVIDER);
//            } else getActiveLocation(LocationManager.GPS_PROVIDER);
//        L.d("Location is null. Probably, disabled");
//
//    }
//    @SuppressLint({"deprecated", "MissingPermission"})
//    private void getActiveLocation(String provider){
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
//                locationManager.getCurrentLocation(LocationManager.NETWORK_PROVIDER,
//                        null, null, null);
//            }
//            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, null, null);
//    }
    private void updateLocationGoogle(){
        if (!isGoogleAvailable) return;
        if (ContextCompat.checkSelfPermission(App.get(), Manifest.permission.ACCESS_COARSE_LOCATION) != 0) {
            L.w("no permissions");
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                L.v("LocationManger: lat=" + location.getLatitude() + " lon=" + location.getLongitude());
                lastKnownLocation.setLon(location.getLongitude());
                lastKnownLocation.setLat(location.getLatitude());
                previousResultTime = System.currentTimeMillis();
            }
           // L.v(fusedLocationClient.getLocationAvailability().toString());
           // fusedLocationClient.getCurrentLocation(new);
        });
    }

    public Coord getCoord() {
        if (TimeUtil.elapsedRealTimeSince(previousResultTime) < 30 * 60 * 1000) {
            L.v("Location Throttling");
            L.v("LocationManger: lat=" + lastKnownLocation.getLat() + " lon=" + lastKnownLocation.getLon());
            return lastKnownLocation;
        }
        updateLocationGoogle();
        return lastKnownLocation;
    }
}
