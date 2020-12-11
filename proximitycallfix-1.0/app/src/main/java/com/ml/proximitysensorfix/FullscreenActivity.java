package com.ml.proximitysensorfix;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    private BroadcastReceiver callFinisheBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finishAffinity();
            finish();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(callFinisheBroadcastReceiver);
        finishAffinity();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        LocalBroadcastManager.getInstance(this).registerReceiver(callFinisheBroadcastReceiver,new IntentFilter("call_ended"));

       /* View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        SwipeableButton button  = findViewById(R.id.unlock);
        button.setOnSwipedListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                finishAffinity();
                finish();
                return null;
            }
        });
        button.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        button.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(100);
                return false;
            }
        });
*/
    }

}
