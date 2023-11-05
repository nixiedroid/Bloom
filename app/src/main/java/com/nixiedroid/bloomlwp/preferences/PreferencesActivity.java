package com.nixiedroid.bloomlwp.preferences;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.weather.owm.ApiKey;
import com.nixiedroid.bloomlwp.weather.owm.WeatherManager;
import org.jetbrains.annotations.NotNull;

public class PreferencesActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 1;


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
    private boolean verifyGoogleAPIAvailability(){
        return GoogleApiAvailability.getInstance().
                isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
    }

    private void requestPermission() {
        if (verifyGoogleAPIAvailability()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int responseCode, @NotNull final String[] permissions, @NotNull final int[] grantResults) {
        L.d();
        if (responseCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                L.v("fine location granted");
            } else {
                L.v("fine location denied");
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("permission_result"));
        } else {
            super.onRequestPermissionsResult(responseCode, permissions, grantResults);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        L.d();
    }
}

