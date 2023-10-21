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
import com.nixiedroid.bloomlwp.wallpapers.util.TimeUtil;

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
        private DeviceEventsReceiver deviceEventsReceiver;
        private final int displayRotation;
        private GestureDetectorCompat gestureDetector;
        private Sensor gravitySensor;
        private GravitySensorListener gravitySensorListener;
        private int gravitySensorType;
        private KeyguardManager keyMan;
        private UtRenderer renderer;
        private SensorManager sensorManager;
        private final float[] tempGrav = new float[3];

        public UtEngine() {
            this.displayRotation = ((DisplayManager)App.get().getSystemService(Context.DISPLAY_SERVICE)).getDisplay(0).getRotation();
        }

        private void registerReceivers() {
            this.deviceEventsReceiver = new DeviceEventsReceiver();
            UtWallpaperService.this.registerReceiver(this.deviceEventsReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
            UtWallpaperService.this.registerReceiver(this.deviceEventsReceiver, new IntentFilter("android.intent.action.SCREEN_ON"));
            UtWallpaperService.this.registerReceiver(this.deviceEventsReceiver, new IntentFilter("android.intent.action.USER_PRESENT"));
        }

        private void registerSensors() {
            if (UtWallpaperService.this.handlesGravity()) {
                this.sensorManager.registerListener(this.gravitySensorListener, this.gravitySensor, 2);
            }
        }

        private void unregisterReceivers() {
            try {
                UtWallpaperService.this.unregisterReceiver(this.deviceEventsReceiver);
            }
            catch (Exception ignored) {
            }
        }

        private void unregisterSensors() {
            if (UtWallpaperService.this.handlesGravity()) {
                this.sensorManager.unregisterListener(this.gravitySensorListener);
            }
        }


        protected void initGravitySensor() {
            if (this.sensorManager.getDefaultSensor(1) != null) {
                for (Sensor sensor : this.sensorManager.getSensorList(1)) {
                    L.v("accelerometer sensor - vendor: " + sensor.getVendor() + " version: " + sensor.getVersion() + " power: " + sensor.getPower());
                }
            }
            if (this.sensorManager.getDefaultSensor(9) != null) {
                for (Sensor sensor2 : this.sensorManager.getSensorList(9)) {
                    L.v("gravity sensor - vendor: " + sensor2.getVendor() + " version: " + sensor2.getVersion() + " power: " + sensor2.getPower());
                }
            }
            this.gravitySensorType = this.sensorManager.getDefaultSensor(9) != null ? 9 : 1;
            L.v("sensor type: " +
                    (this.gravitySensorType == 9 ? "gravity" : "accelerometer"));
            this.gravitySensor = this.sensorManager.getDefaultSensor(this.gravitySensorType);
            this.gravitySensorListener = new GravitySensorListener();

        }

        @Override
        public WallpaperColors onComputeColors() {
            UtRenderer utRenderer = this.renderer;
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
                this.setTouchEventsEnabled(UtWallpaperService.this.handlesTouchesAndGestures());
                this.setOffsetNotificationsEnabled(UtWallpaperService.this.handlesTouchesAndGestures());
                this.renderer = UtWallpaperService.this.makeRenderer();
                this.renderer.setEngine(this);
                this.renderer.setIsPreview(this.isPreview());
                this.setRenderer(this.renderer);
                UtWallpaperService.this.onWallpaperAttached(this.renderer);
                this.registerReceivers();
                this.sensorManager = (SensorManager)UtWallpaperService.this.getSystemService(Context.SENSOR_SERVICE);
                if (UtWallpaperService.this.handlesGravity()) {
                    this.initGravitySensor();
                }
                this.keyMan = (KeyguardManager)UtWallpaperService.this.getSystemService(Context.KEYGUARD_SERVICE);
                if (UtWallpaperService.this.handlesTouchesAndGestures()) {
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
            if (UtWallpaperService.this.handlesGravity()) {
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
            this.renderer.onSurfaceRedrawNeeded();
        }

        @Override
        public void onTouchEvent(MotionEvent motionEvent) {
            if (UtWallpaperService.this.handlesTouchesAndGestures() && this.renderer != null) {
                this.gestureDetector.onTouchEvent(motionEvent);
                this.renderer.onTouchEvent(motionEvent);
            }
        }

        @Override
        public void onVisibilityChanged(boolean bl) {
            L.d(String.valueOf(bl));
            if (bl) {
                this.registerSensors();
                if (this.keyMan.isKeyguardLocked()) {
                    L.d("and is on lockscreen");
                    this.renderer.onVisibleAtLockScreen();
                } else {
                    this.renderer.onVisible();
                }
            } else {
                this.unregisterSensors();
                this.renderer.onNotVisible();
            }
        }

        public class DeviceEventsReceiver
        extends BroadcastReceiver {
            @Override
            public void onReceive(Context object, Intent intent) {
                if (UtEngine.this.renderer == null) {
                    return;
                }
                String action = intent.getAction();
                char c = 65535;
                int hashCode = action.hashCode();

                if (hashCode != -2128145023) {
                    if (hashCode != -1454123155) {
                        if (hashCode == 823795052 && action.equals("android.intent.action.USER_PRESENT")) {
                            c = 2;
                        }
                    } else if (action.equals("android.intent.action.SCREEN_ON")) {
                        c = 1;
                    }
                } else if (action.equals("android.intent.action.SCREEN_OFF")) {
                    c = 0;
                }

                if (c == 0) {
                    L.d("screen-off");
                    if (handlesGravity()) {
                        renderer.onScreenOff();
                        sensorManager.unregisterListener((SensorEventListener) this);
                    }
                    renderer.onScreenOff();
                } else if (c == 1) {
                    L.d("screen-on");
                    renderer.onScreenOn();
                } else if (c == 2) {
                    TimeUtil.setUnlockTime();
                    L.d("user_present");
                    renderer.onUserPresent();
                }

            }
        }

        public class GestureListener
        extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                UtEngine.this.renderer.onFling(motionEvent, motionEvent2, f, f);
                return true;
            }
        }

        public class GravitySensorListener
        implements SensorEventListener {
            @Override
            public void onAccuracyChanged(Sensor sensor, int n) {
            }

            @Override
            public void onSensorChanged(SensorEvent object) {
                if (object.sensor.getType() == UtEngine.this.gravitySensorType) {
                    Util.adjustGravityVectorForDisplayRotation(UtEngine.this.displayRotation, object.values, UtEngine.this.tempGrav);
                    float[] grav = UtEngine.this.tempGrav;
                    grav[0] = grav[0] / 9.81f;
                    grav[1] = grav[1] / 9.81f;
                    grav[2] = grav[2] / 9.81f;
                    UtEngine.this.renderer.onGravitySensor(UtEngine.this.tempGrav[0], UtEngine.this.tempGrav[1], UtEngine.this.tempGrav[2]);
                }
            }
        }
    }
}

