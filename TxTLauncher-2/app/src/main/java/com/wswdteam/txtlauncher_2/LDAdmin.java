package com.wswdteam.txtlauncher_2;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;


//
// Adminisztrátor képenyő zároláshoz
//
public class LDAdmin extends DeviceAdminReceiver {
    @Override
    public void onEnabled(@NonNull Context context, @NonNull Intent intent) {
    }

    @Override
    public void onDisabled(@NonNull Context context, @NonNull Intent intent) {
    }
}
