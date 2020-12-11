package com.ml.proximitysensorfix.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import org.jetbrains.annotations.NotNull;

public class AdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(@NotNull Context context, @NotNull Intent intent) {
        //Some Code Here
    }

    @Override
    public CharSequence onDisableRequested(@NotNull Context context, @NotNull Intent intent)
    {
        //Some Code Here
        return "";
    }

    @Override
    public void onDisabled(@NotNull Context context, @NotNull Intent intent)
    {
    }

    @Override
    public void onPasswordChanged(@NotNull Context context, @NotNull Intent intent)
    {
        //Some Code here
    }

    @Override
    public void onPasswordFailed(@NotNull Context context, @NotNull Intent intent)
    {
        //Some Code Here
    }

    @Override
    public void onPasswordSucceeded(@NotNull Context context, @NotNull Intent intent)
    {
        //Some Code Here
    }
}
