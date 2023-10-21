//package com.nixiedroid.bloomlwp.wallpapers.util;
//
//import android.content.Context;
//import android.os.Bundle;
//import androidx.core.content.ContextCompat;
//import com.google.android.gms.awareness.Awareness;
//import com.google.android.gms.awareness.snapshot.WeatherResult;
//import com.google.android.gms.awareness.state.Weather;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.ResultCallback;
//import com.nixiedroid.bloomlwp.App;
//import com.nixiedroid.bloomlwp.util.L;
//@SuppressWarnings("deprecation")
//public class WeatherManager extends AbstractWeatherManager {
//    private final GoogleApiClient   client;
//    private boolean isGetting;
//    private int numConnectFails;
//    private final ResultCallback<WeatherResult> onWeatherResult;
//
//    public WeatherManager(Context ctx) {
//        super(ctx);
//        onWeatherResult = resultIn -> {
//            WeatherManager.this.isGetting = false;
//            if (WeatherManager.super.isStopped) {
//                L.v("is stopped, so not committing result");
//                WeatherManager.this.afterResult(Result.STOPPED);
//                return;
//            }
//            Weather weather = resultIn.getWeather();
//            if (weather == null) {
//                L.w("result is null");
//                L.w("Status code=" + resultIn.getStatus().getStatusCode() + " message=" + resultIn.getStatus().getStatusMessage());
//                L.w("sanitycheck: is client connected? " + WeatherManager.this.client.isConnected());
//                WeatherManager.this.afterResult(Result.FAILED);
//                return;
//            }
//            previousResult = result;
//            result = weatherVoFromWeather(weather);
//            WeatherManager.super.resultTime = TimeUtil.nowMs();
//            L.v("successful result: " + WeatherManager.super.result.toString());
//            afterResult(Result.OKAY, result);
//        };
//        GoogleApiClient.Builder RawClient = new GoogleApiClient.Builder(App.get());
//        RawClient.addApi(Awareness.API);
//        client = RawClient.build();
//        client.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//            public void onConnected(Bundle var1) {
//                L.d("connected");
//                numConnectFails = 0;
//                get();
//            }
//
//            public void onConnectionSuspended(int var1) {
//                L.d();
//            }
//        });
//        client.registerConnectionFailedListener(connectionResult -> {
//            numConnectFails++;
//            L.w("failed " + connectionResult + "; num: " + WeatherManager.this.numConnectFails);
//            if (WeatherManager.this.numConnectFails < 5) {
//                L.w("re-calling get()");
//                WeatherManager.this.get();
//                return;
//            }
//            L.w("giving up");
//        });
//    }
//
//    private WeatherVo weatherVoFromWeather(Weather var1) {
//        WeatherVo weather = new WeatherVo();
//        weather.conditions = var1.getConditions();
//        int[] var3 = weather.conditions;
//        if (var3 == null || var3.length == 0) {
//            L.w("bad value was returned; using 'UNKNOWN");
//            weather.conditions = new int[]{0};
//        }
//
//        return weather;
//    }
//
//    protected void doGet() {
//        boolean hasLocationPermission = ContextCompat.checkSelfPermission(App.get(), "android.permission.ACCESS_FINE_LOCATION") == 0;
//
//        if (!hasLocationPermission) {
//            L.w("no permissions");
//            this.afterResult(AbstractWeatherManager.Result.FAILED_NO_PERMISSION);
//            return;
//        }
//        if (isGetting) {
//            L.v("get already in progress, aborting");
//            return;
//        }
//        if (client.isConnecting()) {
//            L.v("is currently connecting; aborting");
//            return;
//        }
//        if (!client.isConnecting() && !client.isConnected()) {
//            L.v("going to connect() first, so aborting");
//            this.client.connect();
//            return;
//        }
//        this.isGetting = true;
//        try {
//            L.v("getting weather from Awareness");
//            Awareness.SnapshotApi.getWeather(this.client).setResultCallback(this.onWeatherResult);
//        } catch (SecurityException var3) {
//            L.w("no permissions");
//            this.afterResult(AbstractWeatherManager.Result.FAILED_NO_PERMISSION);
//        }
//    }
//}
