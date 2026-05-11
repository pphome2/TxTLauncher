package com.wswdteam.txtlauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class TxTLauncherApp extends android.app.Application {
    //
    // verzió, fejlesztő adatok
    //
    public static String TXT_VERSION = "1.0.1";
    public static String TXT_APP_NAME = "TxTLauncher";
    public static String TXT_WEB_PAGE = "https://github.com/pphome2/TxTLauncher";

    //
    // Log üzenetekhez használható
    //
    public static boolean developerMode = false;

    // verzió mentése
    public static final String SETTINGS_VERSION_TAG = "TxTVersion";

    // debug jelölő a log-ba
    public static String DEBUG_TAG = "TxTLauncher_App";

    // általános constansok
    public static final int HOME_APP_NUM = 10;
    public static final int FAV_APP_NUM = 20;

    // beállítások mentése jelölők
    public static String PRIVATE_SETTINGS_TAG = "TxTLauncher_App";
    public static String SETTINGS_APP_TAG = "HomeApp";
    public static String SETTINGS_FAV_APP_TAG = "FavApp";
    public static String SETTINGS_SYS_ICON_TAG = "SysIcon";
    public static String SETTINGS_HOME_ICON_TAG = "AppIcon";
    public static String SETTINGS_ADAPTIVE_ICON_TAG = "AdaptiveIcon";
    public static String SETTINGS_ADAPTIVE_ICON_COLOR_TAG = "AdaptiveIconColor";
    public static String SETTINGS_TEXT_COLOR_MODE_TAG = "TextColorMode";
    public static String SETTINGS_DARK_MODE_TAG = "DarkMode";
    public static String SETTINGS_SHOW_ARROWS_TAG = "ShowArrows";
    public static String SETTINGS_SHOW_MAIN_CONTROL_ICONS = "ShowMainIcons";
    public static String SETTINGS_EXTRA_TOOLS = "ExtraTools";
    public static String SETTINGS_ONE_COLUMN_FAVORITES_TAG = "oneColFavorites";
    public static String SETTINGS_URL_PRIVATEAI_TAG = "PrivateAI";
    public static String SETTINGS_URL_SEARCH_TAG = "Search";
    public static String SETTINGS_WEATHER_URL_TAG = "Weather";
    public static String SETTINGS_BACKGROUND_IMAGE_TAG = "BackgroundImage";
    public static String SETTINGS_NOTE_TAG = "AppNote";

    // beállítások alapértelmezett tartalma
    public static String privateAIUrl = "https://duckduckgo.com/?q=DuckDuckGo+AI+Chat&ia=chat&duckai=1";
    public static String privateSearchUrl = "https://duckduckgo.com/?q=";
    public static String privateAIUrlOrig = "https://duckduckgo.com/?q=DuckDuckGo+AI+Chat&ia=chat&duckai=1";
    public static String privateSearchUrlOrig = "https://duckduckgo.com/?q=";
    public static boolean extraTools = false;
    public static String weatherUrl = "";
    public static String backgroundImage = "";
    public static String weatherUrlOrig = "";
    public static String backgroundImageOrig = "";
    public static String savedBackgroundImage = "";

    // rendszer változók
    public static SharedPreferences sharedPreferences;
    public static PackageManager packageMan;
    public static Handler timeHandler = new Handler(Looper.getMainLooper());
    public static Runnable timeRunnable;

    // beállítások változói
    public static boolean homeSysIcon = false;
    public static boolean homeStartAppIcon = false;
    public static boolean adaptiveIcon = false;
    public static boolean onecolFavorites = false;
    public static boolean textColorMode = false;
    public static boolean darkMode = true;
    public static boolean showArrows = false;
    public static boolean showMainIcons = true;
    public static int adaptiveIconColor = 0;
    public static int defaultIconColor = 0;

    // app-ok kezelése
    public static ArrayList<ResolveInfo> allApplicationsList = new ArrayList<>();
    public static List<String> packName = new ArrayList<>();
    public static List<String> homeAppName = new ArrayList<>();
    public static List<Drawable> defaultIcons = new ArrayList<>();
    public static Context AppContext;
    public static long packageUpdateTime;
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

        versionCheck();

        weatherUrl = getString(R.string.search_weather);
        TxTLauncherApp.weatherUrlOrig = getString(R.string.search_weather);

        iconSize = (int) (32 * this.getResources().getDisplayMetrics().density);
        float density = ((float) iconSize / 2);// * getContext().getResources().getDisplayMetrics().density;
        iconPadding = (int) (-1 * density);

        savedBackgroundImage = String.format("%s/launcher_bg.png", getFilesDir());

        syslog(getString(R.string.started_activity) + ": " + this.getClass().getSimpleName());
    }



    //
    //  Fő nézet: telepített app-ok bellvasása
    //
    public static void generateAppList() {
        // másodperc
        long currentTime = System.currentTimeMillis() / 1000;
        // 5 perc és újra olvas
        // - if ((currentTime - packageUpdateTime) > 300) {
        // -    startedAndroidApp = true;
        // - }
        if (startedAndroidApp) {
            packageUpdateTime = currentTime;
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
            // végrehajtási idő
            // currentTime = (System.currentTimeMillis() / 1000) - currentTime;
            //syslog(String.valueOf(currentTime));
            // ! kiírás:  systemMessage(String.valueOf(currentTime));
        }
    }



    //
    //  Fő nézet: beállítások betöltése
    //
    public static void getSettingsMain(Context cont) {
        homeAppName.clear();
        String tag;
        String val;
        String appName;
        for (var i = 0; i <  HOME_APP_NUM; i++) {
            tag = SETTINGS_APP_TAG + i;
            val = sharedPreferences.getString(tag, "");
            if (!val.isEmpty()) {
                for (String[] allAppDatum : allAppData) {
                    appName = allAppDatum[0];
                    if (appName.equals(val)) {
                        homeAppName.add(appName);
                    }
                }
            }
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

        if (adaptiveIcon) {
            homeSysIcon = true;
            homeStartAppIcon = true;
        } else {
            val = sharedPreferences.getString(SETTINGS_SYS_ICON_TAG, "");
            if (!val.isEmpty()) {
                homeSysIcon = !val.equals("0");
            }
            val = sharedPreferences.getString(SETTINGS_HOME_ICON_TAG, "");
            if (!val.isEmpty()) {
                homeStartAppIcon = !val.equals("0");
            }
        }

        val = sharedPreferences.getString(SETTINGS_TEXT_COLOR_MODE_TAG, "");
        if (!val.isEmpty()) {
            textColorMode = !val.equals("0");
        }

        val = sharedPreferences.getString(SETTINGS_DARK_MODE_TAG, "");
        if (!val.isEmpty()) {
            darkMode = !val.equals("0");
        }

        val = sharedPreferences.getString(SETTINGS_SHOW_ARROWS_TAG, "");
        if (!val.isEmpty()) {
            showArrows = !val.equals("0");
        }
        val = sharedPreferences.getString(SETTINGS_SHOW_MAIN_CONTROL_ICONS, "");
        if (!val.isEmpty()) {
            showMainIcons = !val.equals("0");
        }

        val = sharedPreferences.getString(SETTINGS_EXTRA_TOOLS, "");
        if (!val.isEmpty()) {
            extraTools = !val.equals("0");
        }

        val = sharedPreferences.getString(SETTINGS_URL_PRIVATEAI_TAG, "");
        if (!val.isEmpty()) {
            privateAIUrl = val;
        }
        val = sharedPreferences.getString(SETTINGS_URL_SEARCH_TAG, "");
        if (!val.isEmpty()) {
            privateSearchUrl = val;
        }
        val = sharedPreferences.getString(SETTINGS_WEATHER_URL_TAG, "");
        if (!val.isEmpty()) {
            weatherUrl = val;
        }
        val = sharedPreferences.getString(SETTINGS_BACKGROUND_IMAGE_TAG, "");
        if (!val.isEmpty()) {
            backgroundImage = val;
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
    //  Fő nézet: eszköz zárolása
    //
    public static void lockApp(Context cont) {
        LDAccessibility service = LDAccessibility.getInstance();
        if (service != null) {
            service.lockScreen();
        } else {
            systemMessage(cont.getString(R.string.error_lock));
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cont.startActivity(intent);
            startedAndroidApp = true;
        }
    }



    //
    // verzió ellenőrzése
    //
    public void versionCheck() {
        // régi beállítások törlése
        // - String valx = sharedPreferences.getString(SETTINGS_BACKGROUND_IMAGE, "");
        // - if (!valx.isEmpty()) {
        // -    var settings = sharedPreferences.edit();
        // -    settings.remove(SETTINGS_BACKGROUND_IMAGE);
        // -    settings.apply();
        // - }

        //newInstall();
        String val = sharedPreferences.getString(SETTINGS_VERSION_TAG, "");
        if (val.isEmpty()) {
            var settings = sharedPreferences.edit();
            settings.putString(SETTINGS_VERSION_TAG, TXT_VERSION);
            settings.apply();
            newInstall();
            systemMessage(getString(R.string.new_install));
        } else {
            if (!val.equals(TXT_VERSION)) {
                var settings = sharedPreferences.edit();
                settings.putString(SETTINGS_VERSION_TAG, TXT_VERSION);
                settings.apply();
                updateInstall();
                systemMessage(getString(R.string.update_install));
            }
        }
    }



    //
    // Első indítás
    //
    public void newInstall() {
        //
        // első indítás: alap beállítások
        //
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = getString(R.string.news_firstrun);
        builder.setTitle(getString(R.string.news_firstrun_title))
                .setMessage(msg)
                .setPositiveButton(getString(R.string.button_next), (dialog, id) ->
                        startActivity(new Intent(this, HelpActivity.class)));
        AlertDialog alert = builder.create();
        alert.show();
    }



    //
    // Frissítés utáni első indítás
    //
    public void updateInstall() {
        //
        // esetleges beállítások és egyebek módosítása az új verzióhoz
        //

        // feladatok

        // üzenet
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = getString(R.string.news_update);
        builder.setTitle(getString(R.string.news_update_title))
                .setMessage(msg)
                .setPositiveButton(getString(R.string.button_next), (dialog, id) -> {
                    //startActivity(new Intent(this, HelpActivity.class));
                });
        AlertDialog alert = builder.create();
        alert.show();
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
