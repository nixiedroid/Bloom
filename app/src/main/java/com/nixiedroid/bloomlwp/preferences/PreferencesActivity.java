package com.nixiedroid.bloomlwp.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;
import com.nixiedroid.bloomlwp.util.L;
import org.jetbrains.annotations.NotNull;

public class PreferencesActivity extends AppCompatActivity {
    protected final SharedPreferences cachePrefs;
    private final int REQUEST_CODE = 1;

    public PreferencesActivity() {
        cachePrefs = App.get().createDeviceProtectedStorageContext().getSharedPreferences("cache", 0);
    }

    public void setApiKey(Editable key) {
        if (key == null) return;
        String apiKey = key.toString();
        try {
            validateAPIKey(apiKey);
            Toast.makeText(getApplicationContext(), R.string.api_key_apply_success, Toast.LENGTH_LONG).show();
            cachePrefs.edit().putString("API_KEY", apiKey).apply();
        } catch (IllegalArgumentException ignored) {
        }
    }

    public void validateAPIKey(String apiKey) throws IllegalArgumentException {
        if (apiKey.length() != 32) throw new IllegalArgumentException("Invalid lendth");
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        L.d();
        setContentView(R.layout.preferences_activity);

        findViewById(R.id.permissionButton).setOnClickListener(v ->
        {
            Toast.makeText(getApplicationContext(), "Click", Toast.LENGTH_LONG).show();
            requestPermission();
        });

        EditText apiKeyEditText = findViewById(R.id.APIEditText);
        String currentApiKey = cachePrefs.getString("API_KEY", "0");
        if (!currentApiKey.equals("0")) apiKeyEditText.setText(currentApiKey);

        findViewById(R.id.apiKeySetButton).setOnClickListener(v -> setApiKey(apiKeyEditText.getText()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.d();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int responseCode, @NotNull final String[] permissions, @NotNull final int[] grantResults) {
        L.d();
        if (responseCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
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

