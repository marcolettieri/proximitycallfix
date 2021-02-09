package com.ml.proximitysensorfix.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.ml.proximitysensorfix.R;
import com.ml.proximitysensorfix.activity.MainActivity;
import com.ml.proximitysensorfix.receiver.AdminReceiver;
import com.ml.proximitysensorfix.receiver.StartupReceiver;

public class ProximitySensorService extends Service implements SensorEventListener {

    final String CHANNEL_ID = "proximity_foreground";
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Proximity Sensor Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if(manager!=null) {
                manager.deleteNotificationChannel(CHANNEL_ID);
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
    SensorManager sm;
    PowerManager powerManager;
    AudioManager audioManager;
    DevicePolicyManager devicePolicyManager;
    SharedPreferences preferences;
    AccessibilityManager accessibilityService;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotificationChannel();
        preferences = getSharedPreferences("data",MODE_PRIVATE);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Proximity Service in Background")
                .setSmallIcon(R.drawable.appicoclearblack)
                .setColorized(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setContentIntent(pendingIntent)
                .build();
        try {
            NotificationManager manager = ContextCompat.getSystemService(this, NotificationManager.class);
            if (manager != null) {
                manager.cancel(1);
            }
        }catch (Exception ignored){}
        startForeground(1,notification);
        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        Sensor proxSensor=sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        Sensor lightSensor= sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        sm.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (preferences.getBoolean("lightEnabled",false)) {
            Log.d("ACTIVE LIGHT", "ENABLED");
            sm.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        //wakeLock = powerManager.newWakeLock(field, "AppName:myWakeLog");
        audioManager=(AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        //do heavy work on a background thread
        //stopSelf();
        devicePolicyManager = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        accessibilityService = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public boolean isCallActive(){
        return audioManager.getMode() == AudioManager.MODE_IN_CALL && !audioManager.isSpeakerphoneOn();
    }

    private void lockNow() {
        if (preferences.getBoolean("adminEnabled",true)&&devicePolicyManager.isAdminActive(new ComponentName(this, AdminReceiver.class))) {
            devicePolicyManager.lockNow();
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && accessibilityService.isEnabled()) {
            AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);
            event.setPackageName(getApplicationContext().getPackageName());
            event.setEnabled(true);
            event.setClassName(LockAccessibilityService.class.getName());
            event.getText().add(getString(R.string.accessibility_service_text));
            accessibilityService.sendAccessibilityEvent(event);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        boolean active= accessibilityService.isEnabled() || devicePolicyManager.isAdminActive(new ComponentName(this, AdminReceiver.class));
        if (active && isCallActive()) {
                boolean isScreenAwake = (Build.VERSION.SDK_INT < 20? powerManager.isScreenOn():powerManager.isInteractive());
                if (isScreenAwake) {
                    if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                        if (event.values[0] == 0) {
                            Log.d("ACTIVE", "PROXIMITY DETECTED");
                            lockNow();
                        } else if (preferences.getBoolean("lightEnabled",false)  && event.sensor.getType() == Sensor.TYPE_LIGHT) {
                            Log.d("ACTIVE LIGHT", ""+event.values[0]);
                            if (event.values[0] <preferences.getInt("lightLevel",10)) {
                                lockNow();
                            }
                        }
                    }
                }
        }
        /*if (true || isCallActive()) {

            if (event.values[0] == 0) {

                if (!wakeLock.isHeld()) {
                    Log.d("APP", "BLOCK");
                    wakeLock.acquire();
                }

            } else {
                if (wakeLock.isHeld()) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("APP", "RELEASE");
                    wakeLock.release();
                }
            }
        }*/
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Log.i("EXIT", "ondestroy!");
            if (sm != null)
                sm.unregisterListener(this);
        }catch (Exception ignored){}
        Intent broadcastIntent = new Intent(this, StartupReceiver.class);
        sendBroadcast(broadcastIntent);
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
