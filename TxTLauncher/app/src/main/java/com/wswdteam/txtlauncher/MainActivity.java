package com.wswdteam.txtlauncher;

import static com.wswdteam.txtlauncher.TxTLauncherApp.HOME_APP_NUM;
import static com.wswdteam.txtlauncher.TxTLauncherApp.adaptiveIcon;
import static com.wswdteam.txtlauncher.TxTLauncherApp.adaptiveIconColor;
import static com.wswdteam.txtlauncher.TxTLauncherApp.allApplicationsList;
import static com.wswdteam.txtlauncher.TxTLauncherApp.defaultBackGroundColor;
import static com.wswdteam.txtlauncher.TxTLauncherApp.defaultIcons;
import static com.wswdteam.txtlauncher.TxTLauncherApp.packageMan;
import static com.wswdteam.txtlauncher.TxTLauncherApp.savedBackgroundImage;
import static com.wswdteam.txtlauncher.TxTLauncherApp.textColorMode;
import static com.wswdteam.txtlauncher.TxTLauncherApp.darkMode;
import static com.wswdteam.txtlauncher.TxTLauncherApp.homeStartAppIcon;
import static com.wswdteam.txtlauncher.TxTLauncherApp.homeSysIcon;
import static com.wswdteam.txtlauncher.TxTLauncherApp.defaultLetterColor;
import static com.wswdteam.txtlauncher.TxTLauncherApp.defaultSelectColor;
import static com.wswdteam.txtlauncher.TxTLauncherApp.iconPadding;
import static com.wswdteam.txtlauncher.TxTLauncherApp.allAppData;
import static com.wswdteam.txtlauncher.TxTLauncherApp.backgroundImage;
import static com.wswdteam.txtlauncher.TxTLauncherApp.defaultFontSize;
import static com.wswdteam.txtlauncher.TxTLauncherApp.defaultIconColor;
import static com.wswdteam.txtlauncher.TxTLauncherApp.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.TxTLauncherApp.defaultTextColor;
import static com.wswdteam.txtlauncher.TxTLauncherApp.homeAppName;
import static com.wswdteam.txtlauncher.TxTLauncherApp.iconSize;
import static com.wswdteam.txtlauncher.TxTLauncherApp.packName;
import static com.wswdteam.txtlauncher.TxTLauncherApp.privateSearchUrl;
import static com.wswdteam.txtlauncher.TxTLauncherApp.weatherUrl;
import static com.wswdteam.txtlauncher.TxTLauncherApp.startedAndroidApp;
import static com.wswdteam.txtlauncher.TxTLauncherApp.syslog;
import static com.wswdteam.txtlauncher.TxTLauncherApp.systemMessage;
import static com.wswdteam.txtlauncher.TxTLauncherApp.generateAppList;
import static com.wswdteam.txtlauncher.TxTLauncherApp.getSettingsMain;
import static java.lang.Math.round;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.content.ComponentName;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    // gesture mód beállítások
    public static final int SWIPE_MIN_DISTANCE = 100;
    public static final int SWIPE_TRESHOLD = 50;

    // háttérkép kezelése
    private boolean firstPermissionRequest = true;
    public static String backgroundImageBackup = "";

    // dátum kiírás
    private boolean dateReady = false;

    // activity figyelés
    public static boolean startedAppAct = false;
    public static boolean startedWidgetAct = false;
    public static boolean startedSettingsAct = false;
    public static boolean startedFavAct = false;
    public static boolean startedHelp = false;


    //
    //  Fő nézet létrehozása
    //
    @SuppressLint({"PrivateResource", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tv = findViewById(R.id.mainTitle);
        defaultFontSize = tv.getTextSize();
        // méretezés: float scaledDensity = configuration.fontScale;
        // @float defaultTextSize = 14f;
        // - méretezetten: defaultPlusFontSize = (defaultFontSize * scaledDensity) - defaultFontSize + defaultPlusFontSize;
        // - méretezetten: defaultPlusFontSizeTitle = (defaultFontSize * scaledDensity) - defaultFontSize + defaultPlusFontSizeTitle;

        tv = findViewById(R.id.digitalClock);
        tv.setTextSize(tv.getTextSize());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultFontSize + (2 * defaultPlusFontSizeTitle));
        tv.setTextColor(defaultTextColor);
        tv = findViewById(R.id.digitalDate);
        tv.setTextSize(tv.getTextSize());
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultFontSize + (2 * defaultPlusFontSizeTitle));
        tv.setTextColor(defaultTextColor);

        // touch
        findViewById(R.id.mainView).setOnTouchListener(new View.OnTouchListener() {
            private final GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(@NonNull MotionEvent event) {
                    //syslog("Action double tap: lock");
                    TxTLauncherApp.lockApp(getBaseContext());
                    return super.onDoubleTap(event);
                }

                @Override
                public void onLongPress(@NonNull MotionEvent event) {
                    //syslog("Action long tap: favorite");
                    openFavActivity();
                    super.onLongPress(event);
                }

                @Override
                public boolean onScroll(@Nullable MotionEvent event, @NonNull MotionEvent event2, float distanceX,
                                        float distanceY) {
                    assert event != null;
                    var x1 = event.getX();
                    var x2 = event2.getX();
                    var y1 = event.getY();
                    var y2 = event2.getY();
                    // függőleges
                    if ((Math.abs(y2 - y1) > SWIPE_MIN_DISTANCE) && (Math.abs(x2 - x1) < SWIPE_TRESHOLD)) {
                        // fel vagy le
                        if (y2 < y1) {
                            // fel
                            if (!startedAppAct) {
                                openAppListActivity();
                            }
                        } else {
                            // le
                            if (!startedFavAct) {
                                openFavActivity();
                            }
                        }
                    }
                    // vízszintes
                    if ((Math.abs(x2 - x1) > SWIPE_MIN_DISTANCE) && (Math.abs(y2 - y1) < SWIPE_TRESHOLD)) {
                        if (x1 < x2) {
                            // jobbra
                            if (!startedWidgetAct) {
                                openWidgetActivity();
                            }
                        } else {
                            // balra
                            //openAndroidSystemSettings();
                            openGoogleDiscovery();
                        }
                    }
                    return super.onScroll(event, event2, distanceX, distanceY);
                }

                //@Override public boolean onScroll(@Nullable MotionEvent event, @NonNull MotionEvent event2, float distanceX, float distanceY) {
                //@Override public boolean onTouchEvent(MotionEvent event){}
                //@Override public boolean onDown(MotionEvent event) {}
                //@Override public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {}
                //@Override public void onLongPress(MotionEvent event) {}
                //@Override public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {}
                //@Override public void onShowPress(MotionEvent event) {}
                //@Override public boolean onSingleTapUp(MotionEvent event) {}
                //@Override public boolean onDoubleTap(MotionEvent event) {}
                //@Override public boolean onDoubleTapEvent(MotionEvent event) {}
                //@Override public boolean onSingleTapConfirmed(MotionEvent event) {}

            });

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        ImageView iv;
        iv = findViewById(R.id.applistButton);
        iv.setOnLongClickListener(v -> {
            //syslog("Action long tap: open favorite apps");
            startActivity(new Intent(this, FavoritesActivity.class));
            return true;
        });

        iv = findViewById(R.id.settingsButton);
        iv.setOnLongClickListener(v -> {
            //syslog("Action long tap: open system settings");
            openAndroidSystemSettingsButton(v);
            return true;
        });

        iv = findViewById(R.id.browserButton);
        iv.setOnLongClickListener(v -> {
            //syslog("Action long tap: open private search");
            openSearchBrowser(v);
            return true;
        });

        iv = findViewById(R.id.searchButton);
        iv.setOnLongClickListener(v -> {
            //syslog("Action long tap: open internet search");
            openSearchBrowser(v);
            return true;
        });

        // első indulás
        startedAndroidApp = true;
        generateAppList();
        getSettingsMain(this);
        setColors();
        // első indulás: nincs új háttér
        backgroundImageBackup = backgroundImage;
        timeInScreen();
        saveButtonImages();
        buttonPrepare();
        setHomaApp();
        backgroundPrepare();
    }


    //
    //  Fő nézet indítása
    //
    @Override
    public void onStart() {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.enter_from_bottom, R.anim.exit_to_top);
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.enter_from_top, R.anim.exit_to_bottom);
        super.onStart();
        dateReady = false;

        generateAppList();

        if (startedSettingsAct) {
            getSettingsMain(this);
            setColors();
            saveButtonImages();
            buttonPrepare();
            setHomaApp();
            backgroundPrepare();
        }

        startedAppAct = false;
        startedWidgetAct = false;
        startedSettingsAct = false;
        startedFavAct = false;
        startedHelp = false;
        dateReady = false;

        syslog(getString(R.string.started_activity) + ": " + this.getClass().getSimpleName());
    }


    //
    //  Fő nézet leállítása
    //
    @Override
    public void onStop() {
        super.onStop();
        //syslog(getString(R.string.stopped_activty) + ": " + this.getClass().getSimpleName());
    }


    //
    //  Fő nézet visszaállítása
    //
    @Override
    protected void onResume() {
        super.onResume();
        dateReady = false;
    }


    //
    //  Fő nézet megállítás
    //
    @Override
    protected void onPause() {
        super.onPause();
    }


    //
    //  Fő nézet kilépés
    //
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //
    // a háttér előkészítése
    //
    private void backgroundPrepare() {
        if (Objects.equals(backgroundImage, backgroundImageBackup)) {
            View view = findViewById(R.id.mainView);
            if (view == null) return;
            // régi kép
            File imgFile = new File(savedBackgroundImage);
            if (imgFile.exists()) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    if (bitmap != null) {
                        view.setBackground(new BitmapDrawable(getResources(), bitmap));
                    } else {
                        view.setBackground(null);
                    }
                } catch (OutOfMemoryError e) {
                    //e.printStackTrace();
                    systemMessage(getString(R.string.background_file_not_found));
                    view.setBackground(null);
                }
            } else {
                view.setBackground(null);
            }
        } else {
            // új kép
            setupBackground();
        }
    }



    //
    // háttér beállítása és mentése
    //
    private void setupBackground() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
            // jogodultdág kérése csak egyszer
            if (firstPermissionRequest) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PackageManager.PERMISSION_GRANTED);
                firstPermissionRequest = false;
            }
        }
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File sourceFile = new File(dir + "/" + backgroundImage);
        if (!sourceFile.exists()) {
            systemMessage(getString(R.string.background_file_not_found));
            backgroundImageBackup = backgroundImage;
            return;
        }
        View view = findViewById(R.id.mainView);
        int screenWidth = view.getWidth();
        int screenHeight = view.getHeight();
        try {
            Bitmap sourceBitmap = BitmapFactory.decodeFile(sourceFile.getAbsolutePath());
            if (sourceBitmap == null) throw new Exception(String.valueOf(R.string.background_file_not_found));
            int iWidth = sourceBitmap.getWidth();
            int iHeight = sourceBitmap.getHeight();
            float screenRatio = (float) screenHeight / screenWidth;
            float imageRatio = (float) iHeight / iWidth;
            int finalWidth, finalHeight;
            int startX = 0, startY = 0;
            if (imageRatio > screenRatio) {
                // A kép "magasabb", mint a kijelző -> alul-felül vágunk
                finalWidth = iWidth;
                finalHeight = Math.round(iWidth * screenRatio);
                startY = (iHeight - finalHeight) / 2;
            } else {
                // A kép "szélesebb", mint a kijelző -> két oldalt vágunk
                finalHeight = iHeight;
                finalWidth = Math.round(iHeight / screenRatio);
                startX = (iWidth - finalWidth) / 2;
            }
            Bitmap croppedBitmap = Bitmap.createBitmap(sourceBitmap, startX, startY, finalWidth, finalHeight);
            try (FileOutputStream out = new FileOutputStream(savedBackgroundImage)) {
                croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            // 6. Megjelenítés
            view.setBackground(new BitmapDrawable(getResources(), croppedBitmap));
            if (sourceBitmap != croppedBitmap) {
                sourceBitmap.recycle();
            }
            //savedBackgroundImage = croppedBitmap;
        } catch (Exception e) {
            //e.printStackTrace();
            systemMessage(getString(R.string.error_bitmap) + ": " + e.getMessage());
        }
    }



    //
    //  Fő nézet: beállítások betöltése
    //
    @SuppressLint("PrivateResource")
    public void setColors() {
        // szín beállítás a változókba
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            defaultBackGroundColor = getColor(com.google.android.material.R.color.design_dark_default_color_background);
            defaultTextColor = getColor(com.google.android.material.R.color.design_dark_default_color_on_surface);
            defaultSelectColor = getColor(com.google.android.material.R.color.design_dark_default_color_primary_variant);
            defaultLetterColor = getColor(com.google.android.material.R.color.design_dark_default_color_secondary_variant);
            defaultIconColor = getColor(com.google.android.material.R.color.design_dark_default_color_on_surface);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            defaultBackGroundColor = getColor(com.google.android.material.R.color.design_default_color_background);
            defaultTextColor = getColor(com.google.android.material.R.color.design_default_color_on_surface);
            defaultSelectColor = getColor(com.google.android.material.R.color.design_default_color_secondary_variant);
            defaultLetterColor = getColor(com.google.android.material.R.color.design_default_color_secondary);
            defaultIconColor = getColor(com.google.android.material.R.color.design_default_color_on_surface);
        }

        // teljes háttér színe
        getWindow().getDecorView().setBackgroundColor(defaultBackGroundColor);

        if (adaptiveIcon || textColorMode) {
            setIconColor(R.id.searchButton, adaptiveIconColor);
            setIconColor(R.id.weatherButton, adaptiveIconColor);
            setIconColor(R.id.settingsButton, adaptiveIconColor);
            setIconColor(R.id.aiButton, adaptiveIconColor);
            setIconColor(R.id.applistButton, adaptiveIconColor);
            setIconColor(R.id.dialButton, adaptiveIconColor);
            setIconColor(R.id.mailButton, adaptiveIconColor);
            setIconColor(R.id.applistButton, adaptiveIconColor);
            setIconColor(R.id.browserButton, adaptiveIconColor);
            setIconColor(R.id.cameraButton, adaptiveIconColor);
        } else {
            setIconColor(R.id.searchButton, defaultIconColor);
            setIconColor(R.id.weatherButton, defaultIconColor);
            setIconColor(R.id.settingsButton, defaultIconColor);
            setIconColor(R.id.aiButton, defaultIconColor);
            setIconColor(R.id.applistButton, defaultIconColor);
            setIconColor(R.id.dialButton, defaultIconColor);
            setIconColor(R.id.mailButton, defaultIconColor);
            setIconColor(R.id.applistButton, defaultIconColor);
            setIconColor(R.id.browserButton, defaultIconColor);
            setIconColor(R.id.cameraButton, defaultIconColor);
        }

        if (textColorMode) {
            final TextView tw = findViewById(R.id.digitalClock);
            tw.setTextColor(defaultTextColor);
            final TextView tdw = findViewById(R.id.digitalDate);
            tdw.setTextColor(defaultTextColor);
        } else {
            final TextView tw = findViewById(R.id.digitalClock);
            tw.setTextColor(defaultTextColor);
            final TextView tdw = findViewById(R.id.digitalDate);
            tdw.setTextColor(defaultTextColor);
        }
    }



    //
    //  Fő nézet: gombok eredeti képeinek mentése
    //
    public void saveButtonImages() {
        ImageView ivone;
        ivone = findViewById(R.id.dialButton);
        defaultIcons.add(ivone.getDrawable());
        ivone = findViewById(R.id.mailButton);
        defaultIcons.add(ivone.getDrawable());
        ivone = findViewById(R.id.applistButton);
        defaultIcons.add(ivone.getDrawable());
        ivone = findViewById(R.id.browserButton);
        defaultIcons.add(ivone.getDrawable());
        ivone = findViewById(R.id.cameraButton);
        defaultIcons.add(ivone.getDrawable());
    }




    //
    //  Fő nézet: alapértelmezett gombok beállítása
    //
    public void setIconColor(int id, int color) {
        ImageView btn;
        Drawable drw;
        btn = findViewById(id);
        drw =btn.getDrawable();
        if(drw !=null) {
            drw = DrawableCompat.wrap(drw).mutate();
            DrawableCompat.setTint(drw, color);
            btn.setImageDrawable(drw);
        }
    }



    //
    //  Fő nézet: alapértelmezett gombok beállítása
    //
    public void setHomaApp() {
        final ArrayList<String> appHList1 = new ArrayList<>();
        final ArrayList<String> appHList2 = new ArrayList<>();
        final ListView homeTable1 = findViewById(R.id.homeAppList1);
        final ListView homeTable2 = findViewById(R.id.homeAppList2);
        final var adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appHList1) {
            @Override
            public String getItem(int position) {
                return super.getItem(position);
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                String appN = getItem(position);
                View row = super.getView(position, convertView, parent);
                if (appN != null) {
                    TextView tvt = row.findViewById(android.R.id.text1);
                    if (homeStartAppIcon) {
                        ResolveInfo app;
                        String appName;
                        for (int i = 0; i < allAppData.length; i++) {
                            appName = allAppData[i][0];
                            if (appName.equals(appN)) {
                                int padding = iconSize / 2;
                                app = allApplicationsList.get(i);
                                tvt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                                Drawable appI = app.loadIcon(packageMan);
                                Drawable iconToDisplay = getDrawable(row, appI, iconSize);
                                tvt.setCompoundDrawablesRelative(iconToDisplay, null, null, null);
                                tvt.setCompoundDrawablePadding(padding);
                            }
                        }
                    } else {
                        // ! tvt.setTextSize(TypedValue.COMPLEX_UNIT_PX,defaultFontSize + defaultPlusFontSize);
                        tvt.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    tvt.setText(appN);
                    if (textColorMode) {
                        tvt.setTextColor(adaptiveIconColor);
                    } else {
                        tvt.setTextColor(defaultTextColor);
                    }
                    tvt.setCompoundDrawablePadding(30);
                    tvt.setPadding(10, 10, 10, 10);
                    tvt.setEllipsize(TextUtils.TruncateAt.END);
                    tvt.setMaxLines(1);
                }
                return row;
            }
        };


        final var adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appHList2) {
            @Override
            public String getItem(int position) {
                return super.getItem(position);
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                String appN = getItem(position);
                View row = super.getView(position, convertView, parent);
                if (appN != null) {
                    TextView tvt = row.findViewById(android.R.id.text1);
                    if (homeStartAppIcon) {
                        ResolveInfo app;
                        String appName;
                        int padding = iconSize / 2;
                        for (int i = 0; i < allAppData.length; i++) {
                            appName = allAppData[i][0];
                            if (appName.equals(appN)) {
                                app = allApplicationsList.get(i);
                                tvt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                                Drawable appI = app.loadIcon(packageMan);
                                Drawable iconToDisplay = getDrawable(row, appI, iconSize);
                                tvt.setCompoundDrawablesRelative(iconToDisplay, null, null, null);
                                tvt.setCompoundDrawablePadding(padding);
                            }
                        }
                    } else {
                        // ! tvt.setTextSize(TypedValue.COMPLEX_UNIT_PX,defaultFontSize + defaultPlusFontSize);
                        tvt.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    tvt.setText(appN);
                    if (textColorMode) {
                        tvt.setTextColor(adaptiveIconColor);
                    } else {
                        tvt.setTextColor(defaultTextColor);
                    }
                    tvt.setCompoundDrawablePadding(30);
                    tvt.setPadding(10, 10, 10, 10);
                    tvt.setEllipsize(TextUtils.TruncateAt.END);
                    tvt.setMaxLines(1);
                }
                return row;
            }

        };

        double anum = Math.min(homeAppName.size(),  HOME_APP_NUM);
        double an = anum / 2;
        long halfA = round(an);
        int halfApp = (int) halfA;
        for (var i = 0; i < halfApp; i++) {
            if (homeAppName.size() > i) {
                if (!homeAppName.get(i).isEmpty()) {
                    appHList1.add(homeAppName.get(i));
                }
            }
        }
        for (var i = halfApp; i <  HOME_APP_NUM; i++) {
            if (homeAppName.size() > i) {
                if (!homeAppName.get(i).isEmpty()) {
                    appHList2.add(homeAppName.get(i));
                }
            }
        }

        homeTable1.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();
        homeTable2.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();

        homeTable1.setOnItemClickListener((parent, view, position, id) -> {
            String selectedP = (String) (homeTable1.getItemAtPosition(position));
            for (int i=0; i<allAppData.length; i++) {
                String appName;
                appName = allAppData[i][0];
                if (selectedP.equals(appName)) {
                    String pkg = allAppData[i][1];
                    String cls = allAppData[i][2];
                    Intent intent = new Intent();
                    intent.setClassName(pkg, cls);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(intent);
                        startedAndroidApp = true;
                    } catch (Exception e) {
                        systemMessage(getString(R.string.error_startapp));
                    }
                    i = allAppData.length;
                }
            }
            //syslog(selectedP);
        });
        homeTable2.setOnItemClickListener((parent, view, position, id) -> {
            String selectedP = (String) (homeTable2.getItemAtPosition(position));
            for (int i=0; i<allAppData.length; i++) {
                String appName;
                appName = allAppData[i][0];
                if (selectedP.equals(appName)) {
                    String pkg = allAppData[i][1];
                    String cls = allAppData[i][2];
                    Intent intent = new Intent();
                    intent.setClassName(pkg, cls);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        startActivity(intent);
                        startedAndroidApp = true;
                    } catch (Exception e) {
                        systemMessage(getString(R.string.error_startapp));
                    }
                    i = allAppData.length;
                }
            }
            //syslog(selectedP);
        });
    }

    static Drawable getDrawable(View view, Drawable appI, int iconSize) {
        Drawable iconToDisplay;
        if (adaptiveIcon && appI instanceof AdaptiveIconDrawable) {
            AdaptiveIconDrawable adaptI = (AdaptiveIconDrawable) appI;
            Drawable monoIcon = adaptI.getMonochrome();
            if (monoIcon != null) {
                monoIcon.mutate();
                monoIcon.setTint(adaptiveIconColor);
                InsetDrawable insetIcon = new InsetDrawable(monoIcon, iconPadding);
                LayerDrawable layer = new LayerDrawable(new Drawable[]{insetIcon});
                layer.setLayerSize(0, iconSize, iconSize);
                iconToDisplay = layer;
            } else {
                iconToDisplay = getDefaultIcon(view, iconSize);
            }
        } else {
            if (adaptiveIcon) {
                iconToDisplay = getDefaultIcon(view, iconSize);
            } else {
                LayerDrawable layer = new LayerDrawable(new Drawable[]{appI});
                layer.setLayerSize(0, iconSize, iconSize);
                iconToDisplay = layer;
            }
        }
        assert iconToDisplay != null;
        iconToDisplay.setBounds(0, 0, iconSize, iconSize);
        return iconToDisplay;
    }


    // Segédfüggvény a kód tisztaságáért
    private static Drawable getDefaultIcon(View view, int iconSize) {
        Drawable d = ContextCompat.getDrawable(view.getContext(), R.drawable.app);
        if (d != null) {
            d.mutate();
            d.setTint(adaptiveIconColor);
            d.setBounds(0, 0, iconSize, iconSize);
            LayerDrawable layer = new LayerDrawable(new Drawable[]{d});
            layer.setLayerSize(0, iconSize, iconSize);
            return layer;
        }
        return null;
    }


    // fő ikonok kirajzolása
    private Drawable mainDrawable(Drawable appI, Drawable drI) {
        Drawable iconToDisplay;
        if (adaptiveIcon && appI instanceof AdaptiveIconDrawable) {
            AdaptiveIconDrawable adaptI = (AdaptiveIconDrawable) appI;
            Drawable monoIcon = adaptI.getMonochrome();
            if (monoIcon != null) {
                monoIcon.mutate();
                monoIcon.setTint(adaptiveIconColor);
                InsetDrawable insetIcon = new InsetDrawable(monoIcon, iconPadding);
                LayerDrawable layer = new LayerDrawable(new Drawable[]{insetIcon});
                layer.setLayerSize(0, iconSize, iconSize);
                iconToDisplay = layer;
            } else {
                iconToDisplay = getDefaultIconMain(iconSize);
            }
        } else {
            if (adaptiveIcon) {
                iconToDisplay = getDefaultIconMain(iconSize);
            } else {
                LayerDrawable layer = new LayerDrawable(new Drawable[]{appI});
                layer.setLayerSize(0, iconSize, iconSize);
                iconToDisplay = layer;
            }
        }
        if (!adaptiveIcon) {
            if (homeSysIcon) {
                iconToDisplay = appI;
            } else {
                iconToDisplay = drI;
            }
        }
        assert iconToDisplay != null;
        iconToDisplay.setBounds(0, 0, iconSize, iconSize);
        return iconToDisplay;
    }


    // Segédfüggvény a kód tisztaságáért
    private Drawable getDefaultIconMain(int iconSize) {
        Drawable d = ContextCompat.getDrawable(this, R.drawable.app);
        if (d != null) {
            d.mutate();
            d.setTint(adaptiveIconColor);
            d.setBounds(0, 0, iconSize, iconSize);
            LayerDrawable layer = new LayerDrawable(new Drawable[]{d});
            layer.setLayerSize(0, iconSize, iconSize);
            return layer;
        }
        return null;
    }



    //
    //  Fő nézet: alapértelmezett gombok előkészítése, alapértelmezett app kéeresése
    //
    public void buttonPrepare() {
        // dial
        Intent dialIn = new Intent(Intent.ACTION_DIAL, null);
        dialIn.addCategory(Intent.CATEGORY_DEFAULT);
        List<ResolveInfo> appDL = getPackageManager().queryIntentActivities(dialIn, 0);
        ResolveInfo appD = appDL.get(0);
        String packD = appD.activityInfo.packageName;
        ImageView dialF = findViewById(R.id.dialButton);
        dialF.clearColorFilter();
        if (homeSysIcon) {
            Drawable dialI = appD.loadIcon(packageMan);
            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable dialI2 = mainDrawable(dialI, getDrawable(R.drawable.call));
            if (adaptiveIcon) {
                dialF.setImageDrawable(dialI2);
            } else {
                dialF.setImageDrawable(dialI);
            }
        } else {
            dialF.setImageDrawable(defaultIcons.get(0));
            if (adaptiveIcon || textColorMode) {
                dialF.setColorFilter(adaptiveIconColor);
            } else {
                dialF.setColorFilter(defaultIconColor);
            }
        }
        packName.add(packD);
        // mail
        Intent mailIn = new Intent(Intent.ACTION_MAIN, null);
        mailIn.addCategory(Intent.CATEGORY_APP_EMAIL);
        List<ResolveInfo> mailDL = getPackageManager().queryIntentActivities(mailIn, 0);
        ResolveInfo mailD = mailDL.get(0);
        String packI = mailD.activityInfo.packageName;
        ImageView mailF = findViewById(R.id.mailButton);
        mailF.clearColorFilter();
        if (homeSysIcon) {
            Drawable mailI = mailD.loadIcon(packageMan);
            @SuppressLint("UseCompatLoadingForDrawables")
            Drawable mailI2 = mainDrawable(mailI, getDrawable(R.drawable.email));
            mailF.setImageDrawable(mailI2);
        } else {
            mailF.setImageDrawable(defaultIcons.get(1));
            if (adaptiveIcon || textColorMode) {
                mailF.setColorFilter(adaptiveIconColor);
            } else {
                mailF.setColorFilter(defaultIconColor);
            }
        }
        packName.add(packI);
        // app list
        ImageView appF = findViewById(R.id.applistButton);
        appF.clearColorFilter();
        if ((homeSysIcon) && (!adaptiveIcon)) {
            GradientDrawable border = new GradientDrawable();
            border.setColor(Color.TRANSPARENT);
            border.setStroke(2, Color.WHITE);
            border.setCornerRadius(10);
            appF.setBackground(border);
            appF.setPadding(15, 15, 15, 15);
        } else {
            appF.setBackground(null);
            appF.setPadding(0, 0, 0, 0);
        }
        appF.setImageDrawable(defaultIcons.get(2));
        if (adaptiveIcon) {
            appF.setColorFilter(adaptiveIconColor);
        } else {
            if (textColorMode) {
                appF.setColorFilter(adaptiveIconColor);
            } else {
                appF.setColorFilter(defaultIconColor);
            }
        }
        packName.add("");
        // browser
        // ! Intent broIn = new Intent(Intent.ACTION_MAIN);
        // ! broIn.addCategory(Intent.CATEGORY_APP_BROWSER);
        Intent broIn = new Intent(Intent.ACTION_VIEW, Uri.parse("https://"));
        broIn.addCategory(Intent.CATEGORY_DEFAULT);
        List<ResolveInfo> broDL = getPackageManager().queryIntentActivities(broIn, 0);
        ResolveInfo broD = broDL.get(0);
        String broB = broD.activityInfo.packageName;
        ImageView broF = findViewById(R.id.browserButton);
        broF.clearColorFilter();
        if (homeSysIcon) {
            Intent bI = new Intent(Intent.ACTION_VIEW, Uri.parse("https://"));
            ResolveInfo rI = getPackageManager().resolveActivity(bI, PackageManager.MATCH_DEFAULT_ONLY);
            Drawable broI;
            broI = Objects.requireNonNullElse(rI, broD).loadIcon(packageMan);
            @SuppressLint("UseCompatLoadingForDrawables") Drawable broI2 = mainDrawable(broI, getDrawable(R.drawable.internet));
            broF.setImageDrawable(broI2);
        } else {
            broF.setImageDrawable(defaultIcons.get(3));
            if (adaptiveIcon || textColorMode) {
                broF.setColorFilter(adaptiveIconColor);
            } else {
                broF.setColorFilter(defaultIconColor);
            }
        }
        packName.add(broB);
        // camera
        Intent camIn = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camIn.addCategory(Intent.CATEGORY_DEFAULT);
        List<ResolveInfo> camDL = getPackageManager().queryIntentActivities(camIn, 0);
        ResolveInfo camD = camDL.get(0);
        String camC = camD.activityInfo.packageName;
        ImageView camF = findViewById(R.id.cameraButton);
        camF.clearColorFilter();
        if (homeSysIcon) {
            Drawable camI = camD.loadIcon(packageMan);
            @SuppressLint("UseCompatLoadingForDrawables") Drawable camI2 = mainDrawable(camI, getDrawable(R.drawable.photo));
            camF.setImageDrawable(camI2);
        } else {
            camF.setImageDrawable(defaultIcons.get(4));
            if (adaptiveIcon || textColorMode) {
                camF.setColorFilter(adaptiveIconColor);
            } else {
                camF.setColorFilter(defaultIconColor);
            }
        }
        packName.add(camC);
    }


    //
    //  Fő nézet: gombok lenyomása, app start
    //
    public void startButtonApp(View view) {
        if (view.getId() == R.id.applistButton) {
            openAppListActivity();
        } else {
            String appp = getString(view);
            if (!appp.isEmpty()) {
                try {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(appp);
                    startActivity(launchIntent);
                    startedAndroidApp = true;
                } catch (Exception e) {
                    systemMessage(getString(R.string.error_startapp));
                }
            }
        }
    }

    private String getString(View view) {
        String appp = "";
        if (view.getId() == R.id.dialButton) {
            appp = packName.get(0);
            //msg = "Button start: dial";
        }
        if (view.getId() == R.id.mailButton) {
            appp = packName.get(1);
            //msg = "Button start: e-mail";
        }
        if (view.getId() == R.id.browserButton) {
            appp = packName.get(3);
            //msg = "Button start: browser";
        }
        if (view.getId() == R.id.cameraButton) {
            appp = packName.get(4);
            //msg = "Button start: camera";
        }
        return appp;
    }



    //
    //  Fő nézet: idő kiírása a képernyőre
    //
    public void timeInScreen() {
        // óra
        final TextView textView = findViewById(R.id.digitalClock);
        final TextView textDateView = findViewById(R.id.digitalDate);
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String currentTime = simpleTimeFormat.format(calendar.getTime());
                textView.setText(currentTime);
                if (!dateReady) {
                    String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
                    textDateView.setText(currentDate);
                    dateReady = true;
                }
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(runnable);
    }


    //
    //  Fő nézet: keresés
    //
    public void startSearch(View view) {
        try {
            Intent searchintent = new Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH);
            searchintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(searchintent);
            startedAndroidApp = true;
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
        //syslog("Button start: search");
    }


    //
    //  Fő nézet: google discovery
    //
    public void openGoogleDiscovery() {
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.googlequicksearchbox");
            startActivity(intent);
            startedAndroidApp = true;
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
        //syslog("Button start: search");
    }


    //
    //  Fő nézet: app lista nézet indítása
    //
    public void openAppListActivity() {
        //syslog("Action swipe up: openapplist");
        startedAppAct = true;
        startActivity(new Intent(this, AppListActivity.class));
    }


    //
    //  Fő nézet: app lista nézet indítása gombról
    //
    public void openAppListButton(View view) {
        //syslog("Action tap button: openapplist");
        try {
            startActivity(new Intent(this, AppListActivity.class));
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }


    //
    //  Fő nézet: beállítás nézet indítása
    //
    public void openSettingsActivity() {
        //syslog("Action long tap: open settings");
        startedSettingsAct = true;
        backgroundImageBackup = backgroundImage;
        startActivity(new Intent(this, SettingsActivity.class));
    }


    //
    //  Fő nézet: kedvensek nézet indítása
    //
    public void openFavActivity() {
        //syslog("Action swipe down: open fav avt");
        startedFavAct = true;
        startActivity(new Intent(this, FavoritesActivity.class));

    }


    //
    //  Fő nézet: widget nézet indítása
    //
    public void openWidgetActivity() {
        //syslog("Action swipe right: open widgets");
        startedWidgetAct = true;
        startActivity(new Intent(this, WidgetActivity.class));
    }


    //
    //  Fő nézet: óra app indítása
    //
    public void openClock(View view) {
        try {
            Intent mClockIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
            mClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mClockIntent);
            startedAndroidApp = true;
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }


    //
    //  Fő nézet: naptár app indítása
    //
    public void openCalendar(View view) {
        try {
            Intent cal = new Intent(Intent.ACTION_MAIN);
            cal.addCategory(Intent.CATEGORY_APP_CALENDAR);
            startActivity(cal);
            startedAndroidApp = true;
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }



    //
    // Rendszer beállítások indítása
    //
    public void openAndroidSystemSettings(View v) {
        openSettingsActivity();
    }


    //
    // Rendszer beállítások indítása
    //
    public void openAndroidSystemSettingsButton(View v) {
        try {
            // - startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            // - Intent intent = new Intent(Settings.ACTION_SETTINGS);
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            startedAndroidApp = true;
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }


    //
    // Internet keresás
    //
    public void openSearchBrowser(View v) {
        try {
            Intent launchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(privateSearchUrl));
            launchIntent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(launchIntent);
            startedAndroidApp = true;
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }


    //
    // Időjárás keresás
    //
    public void openWeatherSearch(View v) {
        try {
            Intent searchIntent;
            if (weatherUrl.contains("://")) {
                searchIntent = new Intent(Intent.ACTION_VIEW);
                searchIntent.setData(Uri.parse(weatherUrl));
                searchIntent.setPackage(packName.get(2));
                searchIntent.addCategory(Intent.CATEGORY_DEFAULT);
            } else {
                searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
                searchIntent.putExtra(SearchManager.QUERY, weatherUrl + "\n");
                searchIntent.setComponent(new ComponentName("com.google.android.googlequicksearchbox", "com.google.android.googlequicksearchbox.SearchActivity"));
                //searchIntent.setPackage("com.google.android.googlequicksearchbox");
            }
            startActivity(searchIntent);
            startedAndroidApp = true;
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }


    //
    // AI mutatása
    //
    public void openAI(View view) {
        try {
            Intent mapIntent = new Intent(Intent.ACTION_VOICE_COMMAND);
            startActivity(mapIntent);
            startedAndroidApp = true;
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }


}

