package com.nixiedroid.bloomlwp.preferences;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.events.ApiKeyUpdate;
import com.nixiedroid.bloomlwp.util.L;
import org.greenrobot.eventbus.EventBus;

import static com.google.android.gms.common.ConnectionResult.SUCCESS;

public class PreferencesActivity extends AppCompatActivity {


    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted;
                        Boolean coarseLocationGranted;
                        Boolean backgroundLocationGranted = false;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                        } else {
                            fineLocationGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                            coarseLocationGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                        }
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            backgroundLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION, false);
                        }
                        if (backgroundLocationGranted != null && backgroundLocationGranted) {
                            L.v("background location granted");
                        }
                        if (fineLocationGranted != null && fineLocationGranted) {
                            L.v("fine location granted");
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            L.v("coarse location granted");
                        } else {
                            L.v("fine location denied");
                        }
                    }
            );

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.preferences_activity);

        ((EditText) findViewById(R.id.APIEditText)).setText(App.preferences().getString("API_KEY",""));

        findViewById(R.id.apiKeySetButton).setOnClickListener(this::setApiKey);

        findViewById(R.id.permissionButton).setOnClickListener(v -> requestPermission());

        ((TextView) findViewById(R.id.weatherCondition))
                .setText(App.preferences().getString("current_weather_condition", "unknown"));

        ((TextView) findViewById(R.id.weatherUpdateTime)).
                setText(App.preferences().getString("current_weather_update_time", "unknown"));

        ((TextView) findViewById(R.id.curLocation)).
                setText(App.preferences().getString("current_location", "unknown"));

        findViewById(R.id.updateWeatherNow).setOnClickListener(v ->
                App.preferences().edit().putLong("prevUpdateTime", 0).apply());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.d();
    }
    private void setApiKey(View view){
        String apiKeyString = view.toString();
        if (apiKeyString.length() == 32) {
            Toast.makeText(App.get(), R.string.api_key_apply_success, Toast.LENGTH_LONG).show();
            App.preferences().edit().putString("API_KEY", apiKeyString).apply();
            EventBus.getDefault().post(new ApiKeyUpdate(apiKeyString));
        } else {
            Toast.makeText(App.get(), R.string.api_key_apply_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void requestPermission() {
        boolean isGoogleAvailable;
        try {
            int checkResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(App.get());
            isGoogleAvailable = (checkResult == SUCCESS);
        } catch (NoClassDefFoundError e) {
            isGoogleAvailable = false;
        }
        if (isGoogleAvailable) {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.d();
    }
}

