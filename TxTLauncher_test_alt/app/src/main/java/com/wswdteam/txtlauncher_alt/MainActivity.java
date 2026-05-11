package com.wswdteam.txtlauncher_alt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.wswdteam.txtlauncher_alt.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String TXT_VERSION = "1.0.1";
    public static String TXT_APP_NAME = "TxTLauncher-alt";
    public static String TXT_WEB_PAGE = "https://github.com/pphome2/TxTLauncher";
    public static boolean firstPermissionRequest;

    public final String SETTINGS_VERSION_TAG = "TxTVersion";

    public static String privateAIUrl = "https://duckduckgo.com/?q=DuckDuckGo+AI+Chat&ia=chat&duckai=1";
    public static String privateSearchUrl = "https://duckduckgo.com/?q=";
    public static String backgroundImage = "image.png";

    public static String privateAIUrlOrig = "https://duck.ai";
    public static String privateSearchUrlOrig = "https://duckduckgo.com";
    public static String backgroundImageOrig = "image.png";

    public static String PRIVATE_SETTINGS_TAG = "TxTLauncher_Alt_App";
    public static String SETTINGS_APP_TAG = "HomeApp";
    public static String SETTINGS_FAV_APP_TAG = "FavApp";
    public static String SETTINGS_SYS_ICON_TAG = "SysIcon";
    public static String SETTINGS_HOME_ICON_TAG = "AppIcon";
    public static String SETTINGS_URL_PRIVATEAI_TAG = "PrivateAI";
    public static String SETTINGS_URL_SEARCH_TAG = "Search";
    public static String SETTINGS_BACKGROUND_IMAGE_TAG = "BackgroundImage";
    public static String SETTINGS_BACKGROUND_IMAGE = "FormattedBackgroundImage";
    public static String SETTINGS_NOTE = "AppNote";

    public static SharedPreferences sharedPreferences;
    public static PackageManager packageMan;

    public static boolean isDarkMode = true;
    public static boolean homeSysIcon = false;
    public static boolean homeStartAppIcon = false;

    public static int homeAppNum = 10;
    public static int favAppNum = 20;

    public static final String lineSeparator = "#";

    public static ArrayList<ResolveInfo> allApplicationsList = new ArrayList<>();
    public static List<String> packName = new ArrayList<>();
    public static List<String> homeAppName = new ArrayList<>();
    public static List<Drawable> defaultIcons = new ArrayList<>();
    public static Context AppContext;

    public static final int SWIPE_MIN_DISTANCE = 100;
    public static final int SWIPE_TRESHOLD = 50;

    public static String backgroundImageBackup = "";
    public static Bitmap savedBackgroundImage = null;
    public static int screenHeight;
    public static int screenWidth;
    public static long packageUpdateTime;
    public static float defaultFontSize = 0;
    public static float defaultPlusFontSize = 0;
    public static float defaultPlusFontSizeTitle = 1;
    public static DevicePolicyManager devicePolicyManager;
    public static AppBarConfiguration appBarConfiguration;
    public static int defaultBackGroundColor = 0;
    public static int defaultSelectColor = 0;
    public static int defaultLetterColor = 0;
    public static int defaultTextColor = 0;




    @SuppressLint({"SourceLockedOrientationActivity", "PrivateResource"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_favorites, R.id.navigation_tools, R.id.navigation_settings, R.id.navigation_allapps)
                .build();


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        isDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES;
        // sötét téma beállítása
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        if (isDarkMode) {
            findViewById(R.id.nav_host_fragment_activity_main).setBackgroundColor(getColor(com.google.android.material.R.color.design_dark_default_color_background));
            defaultBackGroundColor = getColor(com.google.android.material.R.color.design_dark_default_color_background);
            defaultTextColor = getColor(com.google.android.material.R.color.design_default_color_background);
            defaultSelectColor = getColor(com.google.android.material.R.color.design_dark_default_color_primary_variant);
            defaultLetterColor = getColor(com.google.android.material.R.color.design_default_color_secondary_variant);
        } else {
            findViewById(R.id.nav_host_fragment_activity_main).setBackgroundColor(getColor(com.google.android.material.R.color.design_default_color_background));
            defaultBackGroundColor = getColor(com.google.android.material.R.color.design_default_color_background);
            defaultTextColor = getColor(com.google.android.material.R.color.design_dark_default_color_background);
            defaultSelectColor = getColor(com.google.android.material.R.color.design_default_color_secondary_variant);
            defaultLetterColor = getColor(com.google.android.material.R.color.design_dark_default_color_primary_variant);
        }


        AppContext = this.getApplicationContext();
        sharedPreferences = getSharedPreferences(PRIVATE_SETTINGS_TAG, MODE_PRIVATE);
        packageMan = getPackageManager();

        // lock
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        // sötét téma beállítása
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // verzió ellenőrzés
        firstPermissionRequest = true;
        adminService();
        versionCheck();
        generateAppList();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }


    //
    //  Fő nézet: telepített app-ok bellvasása
    //
    public static void generateAppList() {
        // másodperc
        long currentTime = System.currentTimeMillis() / 1000;
        // 5 perc
        if ((currentTime - packageUpdateTime) > 300) {
            packageUpdateTime = currentTime;
            MainActivity.allApplicationsList.clear();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> apps = packageMan.queryIntentActivities(intent, PackageManager.GET_META_DATA);
            apps.sort(new ResolveInfo.DisplayNameComparator(packageMan));
            MainActivity.allApplicationsList.addAll(apps);
        }
    }


    //
    //  Fő nézet: eszköz zárolása
    //
    public static void lockApp() {
        try {
            devicePolicyManager.lockNow();
        } catch (Exception e) {
            //Toast.makeText(, getString(R.string.service_disable), Toast.LENGTH_SHORT).show();
            //adminService();
        }
    }


    //
    //  Fő nézet: admin
    //
    public void adminService() {
        try {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminComponent = new ComponentName(this, LDAdmin.class);
            if (!devicePolicyManager.isAdminActive(adminComponent)) {
                startActivity(new Intent().setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings")));
            }
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }


    //
    // verzió ellenőrzése
    //
    public void versionCheck() {
        //newInstall();
        String val = sharedPreferences.getString(SETTINGS_VERSION_TAG, "");
        if (val.isEmpty()) {
            var settings = sharedPreferences.edit();
            settings.putString(SETTINGS_VERSION_TAG, TXT_VERSION);
            settings.apply();
            newInstall();
            systemMessage(getString(R.string.news_firstrun_title));
        } else {
            if (!val.equals(TXT_VERSION)) {
                var settings = sharedPreferences.edit();
                settings.putString(SETTINGS_VERSION_TAG, TXT_VERSION);
                settings.apply();
                updateInstall();
                systemMessage(getString(R.string.news_update_title));
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
        msg = msg.replaceAll(lineSeparator, "\n");
        builder.setTitle(getString(R.string.news_firstrun_title))
                .setMessage(msg)
                .setPositiveButton(getString(R.string.button_next), (dialog, id) -> {
                            //startActivity(new Intent(MainActivity.this, HelpActivity.class));
                        });
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = getString(R.string.news_update);
        msg = msg.replaceAll(lineSeparator, "\n");
        builder.setTitle(getString(R.string.news_update_title))
                .setMessage(msg)
                .setPositiveButton(getString(R.string.button_next), (dialog, id) -> {
                    //startActivity(new Intent(MainActivity.this, HelpActivity.class));
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    //
    //  Fő nézet: rendszerüzenet
    //
    public static void systemMessage(String mtext) {
        Toast.makeText(MainActivity.AppContext, mtext, Toast.LENGTH_SHORT).show();
    }


}