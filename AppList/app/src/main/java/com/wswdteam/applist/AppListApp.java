package com.wswdteam.applist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class AppListApp extends android.app.Application {
    //
    // verzió, fejlesztő adatok
    //
    public static String APP_VERSION = "1.0.0";
    public static String APP_NAME = "AppList";
    public static String APP_WEB_PAGE = "https://github.com/pphome2/TxTLauncher";

    //
    // Log üzenetekhez használható
    //
    public static boolean developerMode = false;

    // debug jelölő a log-ba
    public static String DEBUG_TAG = APP_NAME + "_App";

    // általános constansok
    public static final int FAV_APP_NUM = 10;

    // beállítások mentése jelölők
    public static String PRIVATE_SETTINGS_TAG = APP_NAME + "_config";
    public static String SETTINGS_APP_VERSION = APP_VERSION;
    public static String SETTINGS_FAV_APP_TAG = "FavApp";
    public static String SETTINGS_RUN_AND_QUIT_TAG = "RunAndQuit";
    public static String SETTINGS_ADAPTIVE_ICON_TAG = "AdaptiveIcon";
    public static String SETTINGS_ADAPTIVE_ICON_COLOR_TAG = "AdaptiveIconColor";
    public static String SETTINGS_TEXT_COLOR_MODE_TAG = "TextColorMode";
    public static String SETTINGS_DARK_MODE_TAG = "DarkMode";
    public static String FAVORITE_LETTER = "#";

    // beállítások alapértelmezett tartalma
    public static SharedPreferences sharedPreferences;
    public static PackageManager packageMan;


    // beállítások változói
    public static boolean runAndQuit = false;
    public static boolean adaptiveIcon = false;
    public static boolean textColorMode = false;
    public static boolean darkMode = true;
    public static int adaptiveIconColor = 0;
    public static int defaultIconColor = 0;

    // app-ok kezelése
    public static ArrayList<ResolveInfo> allApplicationsList = new ArrayList<>();
    public static Context AppContext;
    public static String[][] allAppData;

    // szöveg és szín beállítások
    public static float defaultFontSize = 0;
    public static float defaultPlusFontSize = 10;
    public static float defaultPlusFontSizeTitle = 15;
    public static int defaultBackGroundColor = 0;
    public static int defaultSelectColor = 0;
    public static int defaultTextColor = 0;
    public static int defaultLetterColor = 0;
    public static int iconSize;
    public static int iconPadding;
    public static boolean startedAndroidApp = true;
    public static String info = APP_NAME + " " + APP_VERSION + " - " + APP_WEB_PAGE;


    @Override
    public void onCreate() {
        super.onCreate();

        AppContext = this.getApplicationContext();
        sharedPreferences = getSharedPreferences(PRIVATE_SETTINGS_TAG, MODE_PRIVATE);
        packageMan = getPackageManager();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // sötét mód beállítása
        Configuration configuration = getResources().getConfiguration();
        int currentNightMode = configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        darkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES;

        iconSize = (int) (32 * this.getResources().getDisplayMetrics().density);
        float density = ((float) iconSize / 2);
        // * getContext().getResources().getDisplayMetrics().density;
        iconPadding = (int) (-1 * density);

        defaultFontSize = (float) iconSize / 2;

        if (sharedPreferences.getString(SETTINGS_APP_VERSION, "").isEmpty()) {
            var settings = AppListApp.sharedPreferences.edit();
            settings.putString(SETTINGS_APP_VERSION, APP_VERSION);
            settings.putString(SETTINGS_RUN_AND_QUIT_TAG, "0");
            settings.putString(SETTINGS_ADAPTIVE_ICON_TAG, "0");
            settings.putString(SETTINGS_TEXT_COLOR_MODE_TAG, "0");
            if (darkMode) {
                settings.putString(SETTINGS_DARK_MODE_TAG, "1");
            } else {
                settings.putString(SETTINGS_DARK_MODE_TAG, "0");
            }
            settings.apply();
        }

        syslog(info);
        syslog(getString(R.string.started_activity) + ": " + this.getClass().getSimpleName());
    }



    //
    //  Fő nézet: telepített app-ok bellvasása
    //
    public static void generateAppList() {
        if (startedAndroidApp) {
            startedAndroidApp = false;
            allApplicationsList.clear();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> apps = packageMan.queryIntentActivities(intent, PackageManager.GET_META_DATA);
            apps.sort(new ResolveInfo.DisplayNameComparator(packageMan));
            allApplicationsList.addAll(apps);
            allAppData = new String[apps.size()][3];
            String appName;
            String pkgName;
            String[] parts;
            String formerAppName = "";
            String formerPkgName = "";
            for (int i=0; i<apps.size(); i++) {
                ResolveInfo info = apps.get(i);
                appName = info.loadLabel(packageMan).toString();
                pkgName= info.activityInfo.packageName;
                parts = pkgName.split("\\.");
                if (parts.length < 2) {
                    pkgName = "0";
                } else {
                    pkgName = parts[1];
                }
                if (appName.equals(formerAppName)) {
                    appName = appName + " (" + pkgName + ")";
                    if (i > 0) {
                        allAppData[i - 1][0] = formerAppName + " (" + formerPkgName + ")";
                    }
                }
                formerAppName = appName;
                formerPkgName = pkgName;
                allAppData[i][0] = appName;
                allAppData[i][1] = info.activityInfo.packageName;
                allAppData[i][2] = info.activityInfo.name;
            }
        }
    }



    //
    //  Fő nézet: beállítások betöltése
    //
    public static void getSettingsMain(Context cont) {
        String val;
        val = sharedPreferences.getString(SETTINGS_RUN_AND_QUIT_TAG, "");
        if (!val.isEmpty()) {
            runAndQuit = !val.equals("0");
        }
        val = sharedPreferences.getString(SETTINGS_ADAPTIVE_ICON_TAG, "");
        if (!val.isEmpty()) {
            adaptiveIcon = !val.equals("0");
        }
        int buttonId;
        buttonId = sharedPreferences.getInt(SETTINGS_ADAPTIVE_ICON_COLOR_TAG, Integer.parseInt("0"));
        if (buttonId == R.id.btnRed) {
            adaptiveIconColor = ContextCompat.getColor(cont, R.color.red);
        }
        if (buttonId == R.id.btnWhite) {
            adaptiveIconColor = ContextCompat.getColor(cont, R.color.white);
        }
        if (buttonId == R.id.btnBlack) {
            adaptiveIconColor = ContextCompat.getColor(cont, R.color.black);
        }
        if (buttonId == R.id.btnGray) {
            adaptiveIconColor = ContextCompat.getColor(cont, R.color.gray);
        }
        if (buttonId == R.id.btnBlue) {
            adaptiveIconColor = ContextCompat.getColor(cont, R.color.blue);
        }
        if (buttonId == R.id.btnGreen) {
            adaptiveIconColor = ContextCompat.getColor(cont, R.color.green);
        }
        if (buttonId == R.id.btnPurple) {
            adaptiveIconColor = ContextCompat.getColor(cont, R.color.purple);
        }
        if (buttonId == R.id.btnOlive) {
            adaptiveIconColor = ContextCompat.getColor(cont, R.color.olive);
        }
        if (buttonId == R.id.btnLevander) {
            adaptiveIconColor = ContextCompat.getColor(cont, R.color.levander);
        }
        if (buttonId == R.id.btnLightblue) {
            adaptiveIconColor = ContextCompat.getColor(cont, R.color.lightblue);
        }
        if (adaptiveIconColor == 0) {
            adaptiveIconColor = defaultIconColor;
        }

        val = sharedPreferences.getString(SETTINGS_TEXT_COLOR_MODE_TAG, "");
        if (!val.isEmpty()) {
            textColorMode = !val.equals("0");
        }

        val = sharedPreferences.getString(SETTINGS_DARK_MODE_TAG, "");
        if (!val.isEmpty()) {
            darkMode = !val.equals("0");
        }
    }



    //
    // színek beállítása
    //
    @SuppressLint("PrivateResource")
    public static void setColors(Context v) { //
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            defaultBackGroundColor = v.getColor(com.google.android.material.R.color.design_dark_default_color_background);
            defaultTextColor = v.getColor(com.google.android.material.R.color.design_dark_default_color_on_surface);
            defaultSelectColor = v.getColor(com.google.android.material.R.color.design_dark_default_color_primary_variant);
            defaultLetterColor = v.getColor(com.google.android.material.R.color.design_dark_default_color_secondary_variant);
            defaultIconColor = v.getColor(com.google.android.material.R.color.design_dark_default_color_on_surface);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            defaultBackGroundColor = v.getColor(com.google.android.material.R.color.design_default_color_background);
            defaultTextColor = v.getColor(com.google.android.material.R.color.design_default_color_on_surface);
            defaultSelectColor = v.getColor(com.google.android.material.R.color.design_default_color_secondary_variant);
            defaultLetterColor = v.getColor(com.google.android.material.R.color.design_default_color_secondary);
            defaultIconColor = v.getColor(com.google.android.material.R.color.design_default_color_on_surface);
        }

    }



    //
    //  Billentyűzet elrejtése
    //
    public static void hideKeyboard(Activity act) {
        View v = act.getCurrentFocus();
        if (v != null) {
            InputMethodManager im = (InputMethodManager) act.getSystemService(INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
        }
    }



    //
    //  Fő nézet: rendszerüzenet
    //
    public static void systemMessage(String mtext) {
        Toast.makeText(AppContext, mtext, Toast.LENGTH_SHORT).show();
    }


    //
    //  Fő nézet: degug log üzenet
    //
    public static void syslog(String mtext) {
        if (developerMode) {
            Log.d(DEBUG_TAG, mtext);
        }
    }


}
