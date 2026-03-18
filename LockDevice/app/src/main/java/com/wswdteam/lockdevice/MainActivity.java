package com.wswdteam.lockdevice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Boolean first = true;

    // android:theme="@style/Theme.LockDevice"
    // android:theme="@android:style/Theme.NoDisplay"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            com.wswdteam.lockdevice.LDAccessibility service = com.wswdteam.lockdevice.LDAccessibility.getInstance();
            if (service != null) {
                service.lockScreen();
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            quitApp();
        } catch (Exception e) {
<<<<<<< HEAD
            var compName = new ComponentName(this, LDAdmin.class);
            boolean active = devicePolicyManager.isAdminActive(compName);
            if (active) {
                devicePolicyManager.lockNow();
                quitApp();
            } else {
                Toast.makeText(this, R.string.service_disable, Toast.LENGTH_SHORT).show();
                adminService();
                //quitApp();
            }
=======
            Toast.makeText(this, R.string.devadmin, Toast.LENGTH_SHORT).show();
            quitApp();
>>>>>>> 418d35b (20260303)
        }
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        } catch (Exception e2) {
            quitApp();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (!first) {
            quitApp();
        } else {
            first = false;
        }
    }

<<<<<<< HEAD
    public void adminService() {
        startActivity(new Intent().setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings")));
    }

=======
>>>>>>> 418d35b (20260303)
    public void quitApp() {
        finishAndRemoveTask();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }


}