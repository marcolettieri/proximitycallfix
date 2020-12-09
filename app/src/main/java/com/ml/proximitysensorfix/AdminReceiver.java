package com.ml.proximitysensorfix;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import org.jetbrains.annotations.NotNull;

public class AdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(@NotNull Context context, Intent intent) {
        //Some Code Here
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent)
    {
        //Some Code Here
        return "";
    }

    @Override
    public void onDisabled(Context context, Intent intent)
    {
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent)
    {
        //Some Code here
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent)
    {
        //Some Code Here
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent)
    {
        //Some Code Here
    }
}
