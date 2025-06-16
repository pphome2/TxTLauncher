package com.wswdteam.txtlauncher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    public final String TXT_VERSION = "1.0.1";

    public final String SETTINGS_VERSION_TAG = "TxTVersion";

    public static String privateAIUrl = "https://duckduckgo.com/?q=DuckDuckGo+AI+Chat&ia=chat&duckai=1";
    public static String privateSearchUrl = "https://duckduckgo.com/?q=";
    public static String backgroundImage = "image.png";

    public static String privateAIUrlOrig = "https://duckduckgo.com/?q=DuckDuckGo+AI+Chat&ia=chat&duckai=1";
    public static String privateSearchUrlOrig = "https://duckduckgo.com/?q=";
    public static String backgroundImageOrig = "image.png";

    public static String DEBUG_TAG = "TxTLauncher_App";
    public static String PRIVATE_SETTINGS_TAG = "TxTLauncher_App";
    public static String SETTINGS_APP_TAG = "HomeApp";
    public static String SETTINGS_FAV_APP_TAG = "FavApp";
    public static String SETTINGS_SYS_ICON_TAG = "SysIcon";
    public static String SETTINGS_HOME_ICON_TAG = "AppIcon";
    public static String SETTINGS_URL_PRIVATEAI_TAG = "PrivateAI";
    public static String SETTINGS_URL_SEARCH_TAG = "Search";
    public static String SETTINGS_BACKGROUND_IMAGE_TAG = "BackgroundImage";
    public static String SETTINGS_BACKGROUND_IMAGE = "FormattedBackgroundImage";
    public static String SETTINGS_NOTE = "AppNote";

    public static String SETTINGS_CITY = "AppCity";
    public static String SETTINGS_WEATHER_HTML1 = "WHtml1";
    public static String SETTINGS_WEATHER_HTML2 = "WHtml2";

    public static SharedPreferences sharedPreferences;
    public static PackageManager packageMan;

    public static boolean isDarkMode = true;
    public static boolean homeSysIcon = false;
    public static boolean homeStartAppIcon = false;

    public static int homeAppNum = 10;
    public static int favAppNum = 20;

    public static ArrayList<ResolveInfo> allApplicationsList = new ArrayList<>();
    public static List<String> packName = new ArrayList<>();
    public static List<String> homeAppName = new ArrayList<>();
    public static List<Drawable> defaultIcons = new ArrayList<>();
    public static Context AppContext;

    private static final int SWIPE_MIN_DISTANCE = 100;
    private static final int SWIPE_TRESHOLD = 50;

    public boolean startedAppAct = false;
    public boolean startedWidgetAct = false;
    public boolean startedSettingsAct = false;
    public boolean startedFavAct = false;
    private boolean dateReady = false;
    private boolean firstPermissionRequest = true;
    public String backgroundImageBackup = "";
    public Bitmap savedBackgroundImage = null;
    public int screenHeight;
    public int screenWidth;
    public static long packageUpdateTime;
    public static float defaultFontSize = 0;
    public static float defaultPlusFontSize = 2;
    public static float defaultPlusFontSizeTitle = 1;


    //
    //  Fő nézet létrehozása
    //
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

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        isDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES;

        if (isDarkMode) {
            Log.d(MainActivity.DEBUG_TAG, getString(R.string.started) + " " + getString(R.string.dark_mode));
            //mview.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_on_background));
        } else {
            Log.d(MainActivity.DEBUG_TAG, getString(R.string.started) + " " + getString(R.string.light_mode));
            //mview.setBackgroundColor(getResources().getColor(com.google.android.material.R.color.design_default_color_background));
        }

        AppContext = this.getApplicationContext();
        sharedPreferences = getSharedPreferences(PRIVATE_SETTINGS_TAG, MODE_PRIVATE);
        packageMan = getPackageManager();

        TextView tv = findViewById(R.id.mainTitle);
        defaultFontSize = tv.getTextSize();

        ImageView imageView = findViewById(R.id.applistButton);
        imageView.setOnLongClickListener(v -> {
            Log.d(DEBUG_TAG, "Action long tap: open settings");
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        });

        // verzió
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

        timeInScreen();
        generateAppList();

        // sötét téma beállítása
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // touch
        findViewById(R.id.mainView).setOnTouchListener(new View.OnTouchListener() {
            private final GestureDetector gestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(@NonNull MotionEvent event) {
                    Log.d(DEBUG_TAG, "Action double tap: lock");
                    lockApp();
                    return super.onDoubleTap(event);
                }

                @Override
                public void onLongPress(@NonNull MotionEvent event) {
                    Log.d(DEBUG_TAG, "Action long tap: setting");
                    openSettingsActivity();
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
                    if ((Math.abs(y2 - y1) > SWIPE_MIN_DISTANCE) && (Math.abs(x2 - x1) < SWIPE_TRESHOLD)){
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
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            List<ResolveInfo> packm = getPackageManager().queryIntentActivities(intent, 0);
                            ResolveInfo packmres = packm.get(0);
                            String appp = packmres.activityInfo.packageName;
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(appp);
                            if (launchIntent != null) {
                                startActivity(launchIntent);
                            }
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
            public boolean onTouch (View v, MotionEvent event){
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        saveButtonImages();
        buttonPrepare();
        getSettings();
        setHomaApp();
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        backgroundSavedImageSet();
    }



    //
    //  Fő nézet indítása
    //
    @Override
    public void onStart() {
        super.onStart();

        generateAppList();

        if (startedSettingsAct) {
            getSettings();
            setHomaApp();
            buttonPrepare();
            backgroundImageSet();
        }

        startedAppAct = false;
        startedWidgetAct = false;
        startedSettingsAct = false;
        startedFavAct = false;
        dateReady = false;

        Log.e(DEBUG_TAG, getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
    }


    //
    //  Fő nézet leállítása
    //
    @Override
    public void onStop() {
        super.onStop();
        Log.e(DEBUG_TAG, getString(R.string.stopped_activty) + ": "+ this.getClass().getSimpleName());
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
    // mentett háttékép beállítása
    //
    public void backgroundSavedImageSet() {
        View mv = findViewById(R.id.mainView);
        if (savedBackgroundImage != null) {
            Drawable drawable = new BitmapDrawable(getResources(), savedBackgroundImage);
            mv.setBackground(drawable);
            backgroundImageBackup = backgroundImage;
        } else {
            mv.setBackground(null);
        }
    }


    //
    // háttérkép beállítása
    //
    public void backgroundImageSet(){
        if  (backgroundImage.isEmpty()) {
            // nincs megadott háttér fájl
            backgroundSavedImageSet();
        } else {
            // megadott háttér fájl
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
                // jogodultdág kérése csak egyster
                if (firstPermissionRequest) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PackageManager.PERMISSION_GRANTED);
                    firstPermissionRequest = false;
                }
            } else {
                if (!backgroundImage.equals(backgroundImageBackup)) {
                    // változott a beállításokban
                    backgroundImageBackup = backgroundImage;
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(dir + "/" + backgroundImage);
                    View mv = findViewById(R.id.mainView);
                    if (file.exists()) {
                        // létező fájl, átalakítás
                        Drawable drawable = null;
                        try {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            int iWidth = bitmap.getWidth();
                            int iHeight = bitmap.getHeight();
                            float screenRatio = (float) screenHeight / screenWidth;
                            int cutStartW;
                            int cutStartH;
                            int cutEndW;
                            int cutEndH;
                            int ch;
                            // fekvő vagy álló
                            if (iWidth > iHeight) {
                                // fekvó
                                int iw = Math.round((iHeight / screenRatio));
                                ch = Math.round((float)((iWidth - iw) / 2));
                                cutStartW = ch;
                                cutEndW = iw;
                                cutStartH = 0;
                                cutEndH = iHeight;
                            } else {
                                // álló
                                int iw = Math.round((iHeight / screenRatio));
                                if (iw < iWidth) {
                                    ch = Math.round((float)((iWidth - iw) / 2));
                                    cutStartW = ch;
                                    cutEndW = iw;
                                    cutStartH = 0;
                                    cutEndH = iHeight;
                                } else {
                                    ch = Math.round((iHeight - (iWidth * screenRatio)) / 2);
                                    cutStartW = 0;
                                    cutEndW = iWidth;
                                    cutStartH = iHeight + ch;
                                    cutEndH = Math.round((iw * screenRatio));
                                }
                            }
                            Bitmap newBitmap = Bitmap.createBitmap(bitmap, cutStartW, cutStartH, cutEndW, cutEndH);
                            //Log.e(DEBUG_TAG, screenWidth+" "+screenHeight+" "+screenRatio+" "+iWidth+" "+iHeight+" "+cutStartW+" "+cutEndW+" "+xWidth+" "+xHeight);
                            savedBackgroundImage = newBitmap;
                            drawable = new BitmapDrawable(getResources(), newBitmap);
                            mv.setBackground(drawable);

                            // átalakított kép mentése
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            newBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] b = baos.toByteArray();
                            String encoded = Base64.encodeToString(b, Base64.DEFAULT);
                            var settings = MainActivity.sharedPreferences.edit();
                            settings.putString(SETTINGS_BACKGROUND_IMAGE, encoded);
                            settings.apply();
                        } catch (Exception e) {
                            systemMessage(getString(R.string.error_bitmap));
                            mv.setBackground(null);
                        }

                        // háttér beállítás
                        if (drawable != null) {
                            mv.setBackground(drawable);
                        }
                    } else {
                        // nincs háttér
                        systemMessage(getString(R.string.background_file_not_found));
                        //backgroundSavedImageSet();
                    }
                }
            }
        }
    }


    //
    //  Fő nézet: gombok eredeti képeinek mentése
    //
    public void saveButtonImages(){
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
    //  Fő nézet: beállítások betöltése
    //
    public void getSettings(){
        homeAppName.clear();
        String tag;
        String val;
        String appName;
        for (var i=0; i<homeAppNum; i++) {
            tag = SETTINGS_APP_TAG + i;
            val = sharedPreferences.getString(tag, "");
            if (!val.isEmpty()) {
                for (ResolveInfo app : allApplicationsList) {
                    appName = app.loadLabel(packageMan).toString();
                    if (appName.equals(val)) {
                        homeAppName.add(appName);
                    }
                }
            }
        }

        val = sharedPreferences.getString(SETTINGS_SYS_ICON_TAG, "");
        if (!val.isEmpty()) {
            homeSysIcon = !val.equals("0");
        }
        val = sharedPreferences.getString(SETTINGS_HOME_ICON_TAG, "");
        if (!val.isEmpty()) {
            homeStartAppIcon = !val.equals("0");
        }

        val = sharedPreferences.getString(SETTINGS_URL_PRIVATEAI_TAG, "");
        if (!val.isEmpty()) {
            privateAIUrl = val;
        }
        val = sharedPreferences.getString(SETTINGS_URL_SEARCH_TAG, "");
        if (!val.isEmpty()) {
            privateSearchUrl = val;
        }
        val = sharedPreferences.getString(SETTINGS_BACKGROUND_IMAGE_TAG, "");
        if (!val.isEmpty()) {
            backgroundImage = val;
        }

        // háttérkép visszaolvasás
        if (savedBackgroundImage == null) {
            String encoded = MainActivity.sharedPreferences.getString(SETTINGS_BACKGROUND_IMAGE, "");
            if (!encoded.isEmpty()) {
                byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
                savedBackgroundImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            }
        }
    }


    //
    //  Fő nézet: alapértelmezett gombok beállítása
    //
    public void setHomaApp(){
        final ArrayList<String> appHList1 = new ArrayList<>();
        final ArrayList<String> appHList2 = new ArrayList<>();
        final ListView homeTable1 = findViewById(R.id.homeAppList1);
        final ListView homeTable2 = findViewById(R.id.homeAppList2);
        final var adapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, appHList1) {
            @Override
            public String getItem(int position) {
                return (String) super.getItem(position);
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                String appN = getItem(position);
                View row = super.getView(position, convertView, parent);
                if (appN != null) {
                    TextView tvt = row.findViewById(android.R.id.text1);
                    if (homeStartAppIcon) {
                        for (ResolveInfo app : MainActivity.allApplicationsList) {
                            String appName = app.loadLabel(packageMan).toString();
                            if (appName.equals(appN)) {
                                tvt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                                Drawable appI = app.loadIcon(packageMan);
                                int ts = (int) tvt.getTextSize() + 40;
                                appI.setBounds(0, 0, ts, ts);
                                tvt.setCompoundDrawables(appI, null, null, null);
                            }
                        }
                    } else {
                        tvt.setTextSize(defaultFontSize +defaultPlusFontSize);
                        tvt.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    tvt.setText(appN);
                    tvt.setCompoundDrawablePadding(30);
                    tvt.setPadding(10,10,10,10);
                    tvt.setEllipsize(TextUtils.TruncateAt.END);
                    tvt.setMaxLines(1);
                }
                return row;
            }
        };
        final var adapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, appHList2) {
            @Override
            public String getItem(int position) {
                return (String) super.getItem(position);
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                String appN = getItem(position);
                View row = super.getView(position, convertView, parent);
                if (appN != null) {
                    TextView tvt = row.findViewById(android.R.id.text1);
                    if (homeStartAppIcon) {
                        for (ResolveInfo app : MainActivity.allApplicationsList) {
                            String appName = app.loadLabel(packageMan).toString();
                            if (appName.equals(appN)) {
                                tvt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                                Drawable appI = app.loadIcon(packageMan);
                                int ts = (int) tvt.getTextSize() + 40;
                                appI.setBounds(0, 0, ts, ts);
                                tvt.setCompoundDrawables(appI, null, null, null);
                            }
                        }
                    } else {
                        tvt.setTextSize(defaultFontSize + defaultPlusFontSize);
                        tvt.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    tvt.setText(appN);
                    tvt.setCompoundDrawablePadding(30);
                    tvt.setPadding(10,10,10,10);
                    tvt.setEllipsize(TextUtils.TruncateAt.END);
                    tvt.setMaxLines(1);
                }
                return row;
            }
        };

        int anum = Math.min(homeAppName.size(), homeAppNum);
        int halfApp = anum / 2;
        for (var i=0; i < halfApp; i++){
            if (homeAppName.size() > i) {
                if (!homeAppName.get(i).isEmpty()) {
                    appHList1.add(homeAppName.get(i));
                }
            }
        }
        for (var i=halfApp; i < homeAppNum; i++){
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
            //MainActivity.startApp(selectedP);
            PackageManager pmx = getPackageManager();
            for (ResolveInfo app : MainActivity.allApplicationsList) {
                String appName = app.loadLabel(pmx).toString();
                String pName = app.activityInfo.packageName;
                Log.d(DEBUG_TAG, appName);
                if (appName.equals(selectedP)) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(pName);
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                    }
                }
            }
            Log.e(DEBUG_TAG, selectedP);
        });
        homeTable2.setOnItemClickListener((parent, view, position, id) -> {
            String selectedP = (String) (homeTable2.getItemAtPosition(position));
            //MainActivity.startApp(selectedP);
            PackageManager pmx = getPackageManager();
            for (ResolveInfo app : MainActivity.allApplicationsList) {
                String appName = app.loadLabel(pmx).toString();
                String pName = app.activityInfo.packageName;
                if (appName.equals(selectedP)) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(pName);
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                    }
                }
            }
            Log.e(DEBUG_TAG, selectedP);
        });
    }


    //
    //  Fő nézet: alapértelmezett gombok előkészítése, alapértelmezett app kéeresése
    //
    public void buttonPrepare() {
        //final PackageManager pm1 = getPackageManager();
        // dial
        Intent dialIn = new Intent(Intent.ACTION_DIAL, null);
        dialIn.addCategory(Intent.CATEGORY_DEFAULT);
        List<ResolveInfo> appDL = getPackageManager().queryIntentActivities(dialIn, 0);
        ResolveInfo appD = appDL.get(0);
        String packD = appD.activityInfo.packageName;
        ImageView dialF = findViewById(R.id.dialButton);
        if (homeSysIcon) {
            Drawable dialI = appD.loadIcon(packageMan);
            dialF.setImageDrawable(dialI);
        } else {
            dialF.setImageDrawable(defaultIcons.get(0));
        }
        MainActivity.packName.add(packD);
        // mail
        Intent mailIn = new Intent(Intent.ACTION_MAIN, null);
        mailIn.addCategory(Intent.CATEGORY_APP_EMAIL);
        List<ResolveInfo> mailDL = getPackageManager().queryIntentActivities(mailIn, 0);
        ResolveInfo mailD = mailDL.get(0);
        String packI = mailD.activityInfo.packageName;
        ImageView mailF = findViewById(R.id.mailButton);
        if (homeSysIcon) {
            Drawable mailI = mailD.loadIcon(packageMan);
            mailF.setImageDrawable(mailI);
        } else {
            mailF.setImageDrawable(defaultIcons.get(1));
        }
        MainActivity.packName.add(packI);
        // app list
        ImageView appF = findViewById(R.id.applistButton);
        appF.setImageDrawable(defaultIcons.get(2));
        // browser
        Intent broIn = new Intent(Intent.ACTION_VIEW, Uri.parse("https://"));
        broIn.addCategory(Intent.CATEGORY_DEFAULT);
        List<ResolveInfo> broDL = getPackageManager().queryIntentActivities(broIn, 0);
        ResolveInfo broD = broDL.get(0);
        String broB = broD.activityInfo.packageName;
        ImageView broF = findViewById(R.id.browserButton);
        if (homeSysIcon) {
            Drawable broI = broD.loadIcon(packageMan);
            broF.setImageDrawable(broI);
        } else {
            broF.setImageDrawable(defaultIcons.get(3));
        }
        MainActivity.packName.add(broB);
        // camera
        Intent camIn = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camIn.addCategory(Intent.CATEGORY_DEFAULT);
        List<ResolveInfo> camDL = getPackageManager().queryIntentActivities(camIn, 0);
        ResolveInfo camD = camDL.get(0);
        String camC = camD.activityInfo.packageName;
        ImageView camF = findViewById(R.id.cameraButton);
        if (homeSysIcon) {
            Drawable camI = camD.loadIcon(packageMan);
            camF.setImageDrawable(camI);
        } else {
            camF.setImageDrawable(defaultIcons.get(4));
        }
        MainActivity.packName.add(camC);
    }


    //
    //  Fő nézet: gombok lenyomása, app start
    //
    public void startButtonApp(View view) {
        String msg = "";
        var appp = "";
        if (view.getId() == R.id.dialButton) {
            appp = MainActivity.packName.get(0);
            msg = "Button start: dial";
        }
        if (view.getId() == R.id.mailButton) {
            appp = MainActivity.packName.get(1);
            msg = "Button start: e-mail";
        }
        if (view.getId() == R.id.browserButton) {
            appp = MainActivity.packName.get(2);
            msg = "Button start: browser";
        }
        if (view.getId() == R.id.cameraButton) {
            appp = MainActivity.packName.get(3);
            msg = "Button start: camera";
        }
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(appp);
        if (launchIntent != null) {
            startActivity(launchIntent);
            Log.d(DEBUG_TAG, msg);
        }
    }


    //
    //  Fő nézet: keresés
    //
    public void startSearch(View view) {
        Intent sIn = new Intent(Intent.ACTION_ASSIST, null);
        sIn.addCategory(Intent.CATEGORY_DEFAULT);
        List<ResolveInfo> sDL = getPackageManager().queryIntentActivities(sIn, 0);
        ResolveInfo sD = sDL.get(0);
        String sP = sD.activityInfo.packageName;
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(sP);
        if (launchIntent != null) {
            startActivity(launchIntent);
            Log.d(DEBUG_TAG, "Button start: search" + sP);
        }
    }


    //
    //  Fő nézet: telepített app-ok bellvasása
    //
    public static void generateAppList() {
        // másodperc
        long currentTime = System.currentTimeMillis()/1000;
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
    //  Fő nézet: idő kiírása a képernyőre
    //
    public void timeInScreen() {
        // óra
        final TextView textView = findViewById(R.id.digitalClock);
        final TextView textDateView = findViewById(R.id.digitalDate);
        final Handler handler = new Handler (Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String currentTime = simpleTimeFormat.format(calendar.getTime());
                textView.setText(currentTime);
                if (!dateReady) {
                    //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                    //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.", Locale.getDefault());
                    //String currentDate = simpleDateFormat.format(calendar.getTime());
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
    //  Fő nézet: app lista nézet indítása
    //
    public void openAppListActivity(){
        Log.d(DEBUG_TAG,"Action tap: openapplist");
        startedAppAct = true;
        startActivity(new Intent(MainActivity.this, AppListActivity.class));
    }


    //
    //  Fő nézet: app lista nézet indítása gombról
    //
    public void openAppListButton(View view){
        Log.d(DEBUG_TAG,"Action tap button: openapplist");
        startedAppAct = true;
        startActivity(new Intent(MainActivity.this, AppListActivity.class));
    }


    //
    //  Fő nézet: beállítás nézet indítása
    //
    public void openSettingsActivity() {
        Log.d(DEBUG_TAG,"Action long tap: open settings");
        startedSettingsAct = true;
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));

    }


    //
    //  Fő nézet: kedvensek nézet indítása
    //
    public void openFavActivity() {
        Log.d(DEBUG_TAG,"Action long tap: open fav avt");
        startedFavAct = true;
        startActivity(new Intent(MainActivity.this, FavoritesActivity.class));

    }


    //
    //  Fő nézet: widget nézet indítása
    //
    public void openWidgetActivity() {
        Log.d(DEBUG_TAG,"Action long tap: open widgets");
        startedWidgetAct = true;
        startActivity(new Intent(MainActivity.this, WidgetActivity.class));

    }


    //
    //  Fő nézet: óra app indítása
    //
    public void openClock(View view){
        Intent mClockIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
        mClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mClockIntent);
    }


    //
    //  Fő nézet: eszköz zárolása
    //
    public void lockApp(){
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        try {
            devicePolicyManager.lockNow();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.service_disable), Toast.LENGTH_SHORT).show();
            adminService();
        }
    }


    //
    //  Fő nézet: admin
    //
    public void adminService() {
        startActivity(new Intent().setComponent(new ComponentName("com.android.settings", "com.android.settings.DeviceAdminSettings")));
    }


    //
    // Első indítás
    //
    public void newInstall() {}


    //
    // Frissítés utáni első indítás
    //
    public void updateInstall() {}


    //
    //  Fő nézet: rendszerüzenet
    //
    public static void systemMessage(String mtext) {
        Toast.makeText(MainActivity.AppContext, mtext, Toast.LENGTH_SHORT).show();
    }

}

