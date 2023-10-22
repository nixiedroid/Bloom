package com.nixiedroid.bloomlwp.wallpapers.base;

import android.app.KeyguardManager;
import android.app.WallpaperColors;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.service.wallpaper.WallpaperService;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import androidx.core.view.GestureDetectorCompat;
import com.nixiedroid.bloomlwp.App;
import com.nixiedroid.bloomlwp.util.L;
import com.nixiedroid.bloomlwp.util.Util;
import com.nixiedroid.bloomlwp.util.gl.GlUtil;

public abstract class UtWallpaperService
        extends GLWallpaperService {
    @Override
    protected int defaultGlSurfaceViewRenderMode() {
        return 0;
    }

    protected abstract boolean handlesGravity();

    protected abstract boolean handlesTouchesAndGestures();

    protected abstract UtRenderer makeRenderer();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public WallpaperService.Engine onCreateEngine() {
        return new UtEngine();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.d();
    }

    protected void onWallpaperAttached(UtRenderer utRenderer) {
    }

    public class UtEngine
            extends GLWallpaperService.GLEngine {
        private final int displayRotation;
        private final float[] tempGrav = new float[3];
        private DeviceEventsReceiver deviceEventsReceiver;
        private GestureDetectorCompat gestureDetector;
        private Sensor gravitySensor;
        private GravitySensorListener gravitySensorListener;
        private int gravitySensorType;
        private KeyguardManager keyMan;
        private UtRenderer renderer;
        private SensorManager sensorManager;

        public UtEngine() {
            this.displayRotation = (
                    (DisplayManager)App.get().getSystemService(Context.DISPLAY_SERVICE)
            ).getDisplay(0).getRotation();
        }

        private void registerReceivers() {
            this.deviceEventsReceiver = new DeviceEventsReceiver();
            UtWallpaperService.this.registerReceiver(this.deviceEventsReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
            UtWallpaperService.this.registerReceiver(this.deviceEventsReceiver, new IntentFilter("android.intent.action.SCREEN_ON"));
            UtWallpaperService.this.registerReceiver(this.deviceEventsReceiver, new IntentFilter("android.intent.action.USER_PRESENT"));
        }

        private void registerSensors() {
            if (UtWallpaperService.this.handlesGravity()) {
                this.sensorManager.registerListener(gravitySensorListener, gravitySensor, 2);
            }
        }

        private void unregisterReceivers() {
            try {
                unregisterReceiver(deviceEventsReceiver);
            } catch (Exception ignored) {
            }
        }

        private void unregisterSensors() {
            if (handlesGravity()) {
                sensorManager.unregisterListener(gravitySensorListener);
            }
        }


        protected void initGravitySensor() {
            if (this.sensorManager.getDefaultSensor(1) != null) {
                for (Sensor sensor : sensorManager.getSensorList(1)) {
                    L.v("accelerometer sensor - vendor: " + sensor.getVendor() + " version: " + sensor.getVersion() + " power: " + sensor.getPower());
                }
            }
            if (this.sensorManager.getDefaultSensor(9) != null) {
                for (Sensor sensor2 : sensorManager.getSensorList(9)) {
                    L.v("gravity sensor - vendor: " + sensor2.getVendor() + " version: " + sensor2.getVersion() + " power: " + sensor2.getPower());
                }
            }
            this.gravitySensorType = sensorManager.getDefaultSensor(9) != null ? 9 : 1;
            L.v("sensor type: " + (gravitySensorType == 9 ? "gravity" : "accelerometer"));
            gravitySensor = sensorManager.getDefaultSensor(gravitySensorType);
            gravitySensorListener = new GravitySensorListener();
        }

        @Override
        public WallpaperColors onComputeColors() {
            UtRenderer utRenderer = renderer;
            if (utRenderer == null) {
                return null;
            }
            return utRenderer.onComputeWallpaperColors();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            if (GlUtil.supportsEs2(App.get())) {
                this.setEGLContextClientVersion(2);
                this.setPreserveEGLContextOnPause(true);
                this.setTouchEventsEnabled(handlesTouchesAndGestures());
                this.setOffsetNotificationsEnabled(handlesTouchesAndGestures());
                this.renderer = makeRenderer();
                this.renderer.setEngine(this);
                this.renderer.setIsPreview(this.isPreview());
                this.setRenderer(this.renderer);
                onWallpaperAttached(this.renderer);
                this.registerReceivers();
                this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                if (handlesGravity()) {
                    this.initGravitySensor();
                }
                this.keyMan = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                if (handlesTouchesAndGestures()) {
                    this.gestureDetector = new GestureDetectorCompat(UtWallpaperService.this, new GestureListener());
                }
                return;
            }
            throw new Error("Unsupported environment");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            this.unregisterReceivers();
            if (handlesGravity()) {
                this.sensorManager.unregisterListener(this.gravitySensorListener);
            }
            this.renderer.onDestroy();
            this.renderer = null;
        }

        @Override
        public void onOffsetsChanged(float f, float f2, float f3, float f4, int n, int n2) {
            this.renderer.onOffsetChanged(f);
        }

        @Override
        public void onSurfaceRedrawNeeded(SurfaceHolder surfaceHolder) {
            renderer.onSurfaceRedrawNeeded();
        }

        @Override
        public void onTouchEvent(MotionEvent motionEvent) {
            if (handlesTouchesAndGestures() && renderer != null) {
                gestureDetector.onTouchEvent(motionEvent);
                renderer.onTouchEvent();
            }
        }

        @Override
        public void onVisibilityChanged(boolean isVisible) {
            L.d(String.valueOf(isVisible));
            if (isVisible) {
                this.registerSensors();
                if (keyMan.isKeyguardLocked()) {
                    L.d("and is on lockscreen");
                    renderer.onVisibleAtLockScreen();
                } else {
                    renderer.onVisible();
                }
            } else {
                unregisterSensors();
                renderer.onNotVisible();
            }
        }

        public class DeviceEventsReceiver
                extends BroadcastReceiver {
            @Override
            public void onReceive(Context object, Intent intent) {
                if (renderer == null) {
                    return;
                }
                switch (intent.getAction()) {
                    case "android.intent.action.SCREEN_OFF":
                        L.d("screen-off");
                        if (handlesGravity()) {
                            renderer.onScreenOff();
                            sensorManager.unregisterListener(gravitySensorListener);
                            break;
                        }
                        renderer.onScreenOff();
                        break;
                    case "android.intent.action.SCREEN_ON":
                        L.d("screen-on");
                        renderer.onScreenOn();
                        break;
                    case "android.intent.action.USER_PRESENT":
                        L.d("user_present");
                        renderer.onUserPresent();
                        break;
                }
            }
        }

        public class GestureListener
                extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                renderer.onFling();
                return true;
            }
        }

        public class GravitySensorListener
                implements SensorEventListener {
            @Override
            public void onAccuracyChanged(Sensor sensor, int n) {
            }

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == gravitySensorType) {
                    Util.adjustGravityVectorForDisplayRotation(displayRotation, sensorEvent.values, tempGrav);
                    float[] grav = tempGrav;
                    grav[0] = grav[0] / 9.81f;
                    grav[1] = grav[1] / 9.81f;
                    grav[2] = grav[2] / 9.81f;
                    renderer.onGravitySensor(tempGrav[0], tempGrav[1], tempGrav[2]);
                }
            }
        }
    }
}

