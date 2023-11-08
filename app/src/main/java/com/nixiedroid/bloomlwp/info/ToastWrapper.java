package com.nixiedroid.bloomlwp.info;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.nixiedroid.bloomlwp.App;

public class ToastWrapper {
    public static void showToast(int textID) {
        new Handler(Looper.getMainLooper())
                .post(
                        () -> Toast.makeText(
                                App.get(),
                                textID,
                                Toast.LENGTH_LONG
                        ).show()
                );
    }
}
