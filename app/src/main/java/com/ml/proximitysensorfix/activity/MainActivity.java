package com.ml.proximitysensorfix.activity;

import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.ml.proximitysensorfix.service.LockAccessibilityService;
import com.ml.proximitysensorfix.service.ProximitySensorService;
import com.ml.proximitysensorfix.R;
import com.ml.proximitysensorfix.receiver.AdminReceiver;

public class MainActivity extends AppCompatActivity {
    private static boolean isIntentResolved(Context ctx, Intent intent) {
        return (intent != null && ctx.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null);
    }

    public static boolean isMIUI(Context ctx) {
        return isIntentResolved(ctx, new Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT))
                || isIntentResolved(ctx, new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")))
                || isIntentResolved(ctx, new Intent("miui.intent.action.POWER_HIDE_MODE_APP_LIST").addCategory(Intent.CATEGORY_DEFAULT))
                || isIntentResolved(ctx, new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.powercenter.PowerSettings")));
    }

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefs = getSharedPreferences("data", MODE_PRIVATE);
        final AppCompatCheckBox useLight = findViewById(R.id.use_light);
        useLight.setChecked(prefs.getBoolean("lightEnabled", false));
        useLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().putBoolean("lightEnabled", useLight.isChecked()).apply();
                startService(MainActivity.this);
            }
        });
        Button button = findViewById(R.id.buttonPermission);
        final DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        final   AccessibilityManager     accessibilityService = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean active = accessibilityService.isEnabled() || devicePolicyManager.isAdminActive(new ComponentName(MainActivity.this, AdminReceiver.class));
                if (!active) {
                    startActivity(new Intent(MainActivity.this, PermissionsActivity.class));
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                    // set title
                    alertDialogBuilder.setTitle(R.string.permission_gived);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startService(getApplicationContext());
                                }
                            }).setNeutralButton(R.string.retry, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(MainActivity.this, PermissionsActivity.class));
                        }
                    });
                    alertDialogBuilder.show();
                }

            }
        });
        boolean active = accessibilityService.isEnabled()|| devicePolicyManager.isAdminActive(new ComponentName(this, AdminReceiver.class));

        if (active) {
            startService(this);
        }
        AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);
        event.setPackageName(getApplicationContext().getPackageName());
        event.setEnabled(true);
        event.setClassName(LockAccessibilityService.class.getName());
        event.getText().add(getString(R.string.accessibility_service_text));
        accessibilityService.sendAccessibilityEvent(event);
    }

    public static void startService(Context context) {
        Intent service = new Intent(context, ProximitySensorService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service);
        } else {
            context.startService(service);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.review) {
            final ReviewManager manager = ReviewManagerFactory.create(this);
            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
                @Override
                public void onComplete(@NonNull Task<ReviewInfo> task) {
                    try {
                        if (task.isSuccessful()) {
                            // We can get the ReviewInfo object
                            ReviewInfo reviewInfo = task.getResult();
                            Task<Void> flow = manager.launchReviewFlow(MainActivity.this, reviewInfo);


                            flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                                // set title
                                                alertDialogBuilder.setTitle("Thanks!!!");
                                                // set dialog message
                                                alertDialogBuilder
                                                        .setCancelable(false)
                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {

                                                            }
                                                        });
                                                alertDialogBuilder.show();
                                            } catch (Exception ignored) {

                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            if(task.getException()!=null)
                            task.getException().printStackTrace();
                            OpenAppInPlayStore();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            return true;
        }
        if (id == R.id.uninstall) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

            // set title
            alertDialogBuilder.setTitle(R.string.uninstall_app);
            alertDialogBuilder.setMessage(R.string.uninstall_app_description);
            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                startActivity(new Intent().setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings")));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
            alertDialogBuilder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @SuppressLint("InlinedApi")
    private void OpenAppInPlayStore(){

        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }

    }

}
