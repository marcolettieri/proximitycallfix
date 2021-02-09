package com.ml.proximitysensorfix.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

import com.ml.proximitysensorfix.R;
@RequiresApi(api = Build.VERSION_CODES.P)
public class LockAccessibilityService extends AccessibilityService {
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED && event.getText().size()>0 &&
                event.getText().get(0).equals(getString(R.string.accessibility_service_text))) {
            lockDevice();
        }

    }

    private void lockDevice() {
        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
    }
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.i("Service", "Connected");
        try{
            Intent service = new Intent(getApplicationContext(), ProximitySensorService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplicationContext().startForegroundService(service);
            } else {
                getApplicationContext().startService(service);
            }
        }catch (Exception ignored){}
    }
    @Override
    public void onInterrupt() {
        Log.i("Service", "Interrupted");

    }
}
