package com.nixiedroid.bloomlwp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.nixiedroid.bloomlwp.wallpapers.weather.TimeUtil;

public class TimezoneChangedReceiver
extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction())) {
            TimeUtil.updateTimezone();
        }
    }
}

