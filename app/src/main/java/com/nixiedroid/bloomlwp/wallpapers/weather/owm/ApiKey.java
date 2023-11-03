package com.nixiedroid.bloomlwp.wallpapers.weather.owm;

import android.content.Intent;
import android.text.Editable;
import android.widget.Toast;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.R;

public class ApiKey {
    private static void validateAPIKey(String apiKey) throws IllegalArgumentException {
        if (apiKey.length() != 32) throw new IllegalArgumentException("Invalid length");
    }
    public static void setApiKey(Editable key) {
        if (key == null) return;
        String apiKey = key.toString();
        try {
            validateAPIKey(apiKey);
            Toast.makeText(App.get(), R.string.api_key_apply_success, Toast.LENGTH_LONG).show();
            App.preferences().edit().putString("API_KEY", apiKey).apply();
            Intent intent = new Intent("preferences_api_key_update");
            intent.putExtra("preferences_api_key_update", apiKey);
            LocalBroadcastManager.getInstance(App.get()).sendBroadcast(intent);
        } catch (IllegalArgumentException ignored) {
        }
    }
}
