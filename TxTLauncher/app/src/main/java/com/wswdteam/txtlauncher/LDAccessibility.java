package com.wswdteam.txtlauncher;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.view.accessibility.AccessibilityEvent;

@SuppressLint("AccessibilityPolicy")
public class LDAccessibility extends AccessibilityService {

    private static LDAccessibility instance;

    public static LDAccessibility getInstance() {
        return instance;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
    }

    @Override
    public boolean onUnbind(android.content.Intent intent) {
        instance = null;
        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Nem szükséges eseménykezelés a zároláshoz
    }

    @Override
    public void onInterrupt() {
        // Hiba esetén teendő
    }

    public void lockScreen() {
        // Ez a hívás váltja ki a szoftveres lezárást (Android 9+)
        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN);
    }
}

