//package com.nixiedroid.bloomlwp.wallpapers.util;
//
//import android.graphics.PointF;
//import android.location.Location;
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.core.content.ContextCompat;
//import com.google.android.gms.awareness.Awareness;
//import com.google.android.gms.awareness.snapshot.LocationResult;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.ResultCallback;
//import com.nixiedroid.bloomlwp.App;
//import com.nixiedroid.bloomlwp.util.L;
//import org.jetbrains.annotations.NotNull;
//
//@SuppressWarnings("deprecation")
//public class SunriseUtil extends AbstractSunriseUtil {
//    private final GoogleApiClient client;
//    private boolean isGetting;
//    private int numConnectFails;
//
//    public SunriseUtil() {
//        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(App.get());
//        builder.addApi(Awareness.API);
//        this.client = builder.build();
//        this.client.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//            @Override
//            public void onConnected(Bundle bundle) {
//                L.d("connected");
//                numConnectFails = 0;
//                SunriseUtil.this.get();
//            }
//
//            @Override
//            public void onConnectionSuspended(int n) {
//                L.d();
//            }
//        });
//        this.client.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
//            @Override
//            public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {
//                numConnectFails++;
//                String stringBuilder = "failed " + connectionResult + "; num: " + SunriseUtil.this.numConnectFails;
//                L.w(stringBuilder);
//                if (SunriseUtil.this.numConnectFails < 5) {
//                    L.w("re-calling get()");
//                    SunriseUtil.this.get();
//                } else {
//                    L.w("giving up");
//                }
//            }
//        });
//    }
//    @Override
//    protected void doGet() {
//        if (ContextCompat.checkSelfPermission(App.get(), "android.permission.ACCESS_FINE_LOCATION") != 0) {
//            L.w("no permissions");
//            this.afterResult(AbstractSunriseUtil.Result.FAILED_NO_PERMISSION);
//            return;
//        }
//        if (this.isGetting) {
//            L.v("get already in progress, aborting");
//            return;
//        }
//        if (this.client.isConnecting()) {
//            L.v("is currently connecting; aborting");
//            return;
//        }
//        if (!this.client.isConnecting() && !this.client.isConnected()) {
//            L.v("going to connect() first, so aborting");
//            this.client.connect();
//            return;
//        }
//        this.isGetting = true;
//        try {
//            L.v("getting location from Awareness");
//            Awareness.SnapshotApi.getLocation(this.client).setResultCallback(new ResultCallback<LocationResult>() {
//                @Override
//                public void onResult(@NonNull @NotNull LocationResult locationResult) {
//                    isGetting = false;
//                    Location location = locationResult.getLocation();
//                    if (location == null) {
//                        L.w("location is null");
//                        afterResult(Result.FAILED);
//                        return;
//                    }
//                    float latitude = (float) location.getLatitude();
//                    float longitude = (float) location.getLongitude();
//                    L.v("location: " + latitude + ", " + longitude);
//                    float[] sunriseSunsetTime = AbstractSunriseUtil.getSunriseSunset(latitude, longitude);
//                    if (sunriseSunsetTime == null) {
//                        L.w("sunrise values from lat lon failed");
//                        afterResult(Result.FAILED);
//                        return;
//                    }
//                    L.v("sunrise/sunset: " + sunriseSunsetTime[0] + ", " + sunriseSunsetTime[1]);
//                    latLon = new PointF(latitude, longitude);
//                    sunriseDayPercent = sunriseSunsetTime[0];
//                    sunsetDayPercent = sunriseSunsetTime[1];
//                    TimeUtil.setSunriseSunsetPercents(sunriseSunsetTime[0], sunriseSunsetTime[1]);
//                    afterResult(Result.OKAY);
//                }
//            });
//        } catch (final SecurityException ex) {
//            L.w("no permissions");
//            this.afterResult(Result.FAILED_NO_PERMISSION);
//        }
//    }
//}
//
