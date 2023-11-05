package com.nixiedroid.bloomlwp.preferences;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.weather.owm.ApiKey;
import com.nixiedroid.bloomlwp.weather.owm.WeatherManager;

public class PreferencesActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.preferences_activity);

        String defaultApiKey = getApplicationContext().getResources().getString(R.string.OWM_API_KEY);
        String currentApiKey = App.preferences().getString("API_KEY", defaultApiKey);

        EditText apiKeyEdit = findViewById(R.id.APIEditText);

        apiKeyEdit.setText((currentApiKey.equals("0")?"not set":currentApiKey));

        findViewById(R.id.apiKeySetButton).setOnClickListener(v -> ApiKey.setApiKey(apiKeyEdit.getText()));


        findViewById(R.id.permissionButton).setOnClickListener(v -> requestPermission());

        ((TextView) findViewById(R.id.weatherCondition))
                .setText(App.preferences().getString("current_weather_condition", "unknown"));

        ((TextView) findViewById(R.id.weatherUpdateTime)).
                setText(App.preferences().getString("current_weather_update_time", "unknown"));

        ((TextView) findViewById(R.id.curLocation)).
                setText(App.preferences().getString("current_location", "unknown"));

        findViewById(R.id.updateWeatherNow).setOnClickListener(v -> WeatherManager.updateWeatherNow());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.d();
    }


    private void requestPermission() {
            locationPermissionRequest.launch(new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
    }
    ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (fineLocationGranted != null && fineLocationGranted) {
                            L.v("fine location granted");
                        } else if (coarseLocationGranted != null && coarseLocationGranted) {
                            L.v("fine location granted");
                        } else {
                            L.v("fine location denied");
                        }
                        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("permission_result"));
                    }
            );

    @Override
    protected void onStop() {
        super.onStop();
        L.d();
    }
}

