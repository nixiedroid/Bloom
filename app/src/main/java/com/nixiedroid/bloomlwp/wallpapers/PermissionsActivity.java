package com.nixiedroid.bloomlwp.wallpapers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;

public class PermissionsActivity extends Activity {
    private final int REQUEST_CODE = 1;
    public static void launchActivity(Context context, int n) {
        Intent intent = new Intent();
        intent.setClass(context, PermissionsActivity.class);
        intent.putExtra("wallpaper_requestor", n);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        App.get().startActivity(intent);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        L.d();
        this.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        int n = this.getIntent().getIntExtra("wallpaper_requestor", -1);
        if (n != 1) {
            if (n != 2) {
                L.e("logic error");
                this.finish();
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION"}, REQUEST_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.d();
    }

    @Override

    public void onRequestPermissionsResult(int responseCode, final String[] permissions, final int[] grantResults) {
        L.d();
        if (responseCode == REQUEST_CODE) {
            final int length = grantResults.length;
            boolean isGranted = false;
            if (length > 0) {
                if (grantResults[0] == 0) {
                   isGranted = true;
                }
            }
            String s;
            if (isGranted) {
                s = "fine location granted";
            } else {
                s = "fine location denied";
            }
            L.v(s);
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("permission_result"));
            this.finish();
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

