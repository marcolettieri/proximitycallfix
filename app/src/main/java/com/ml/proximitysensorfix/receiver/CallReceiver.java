package com.ml.proximitysensorfix.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class CallReceiver extends BroadcastReceiver {


    private static boolean isIntentResolved(Context ctx, Intent intent ){
        return (intent!=null && ctx.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null);
    }

    public static boolean isMIUI(Context ctx) {
        return isIntentResolved(ctx, new Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT))
                || isIntentResolved(ctx, new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")))
                || isIntentResolved(ctx, new Intent("miui.intent.action.POWER_HIDE_MODE_APP_LIST").addCategory(Intent.CATEGORY_DEFAULT))
                || isIntentResolved(ctx, new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.powercenter.PowerSettings")));
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        /*TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
        if ((tm!=null&& tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK && !(tm.getCallState() == TelephonyManager.CALL_STATE_RINGING)) ||
                (intent.getAction()!=null && intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL"))) {
            Log.d("RECEIVER","CHIAMATA IN CORSO");
            Intent i = new Intent(context,FullscreenActivity.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

        } else if(intent.getStringExtra("state")==null || (intent.getStringExtra("state")!=null && !"RINGING".equals(intent.getStringExtra("state")))){
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Log.e("RECEIVER", key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                }
            }
            Log.d("RECEIVER","CHIAMATA CHIUSA "+intent.getAction());
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent().setAction("call_ended"));
        }*/
    }
}
