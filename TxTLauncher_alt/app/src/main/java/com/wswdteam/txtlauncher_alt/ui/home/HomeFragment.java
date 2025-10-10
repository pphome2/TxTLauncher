package com.wswdteam.txtlauncher_alt.ui.home;

import static android.content.Context.MODE_PRIVATE;
import static com.wswdteam.txtlauncher_alt.MainActivity.PRIVATE_SETTINGS_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_APP_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_BACKGROUND_IMAGE;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_BACKGROUND_IMAGE_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_HOME_ICON_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_SYS_ICON_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_URL_PRIVATEAI_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_URL_SEARCH_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SWIPE_MIN_DISTANCE;
import static com.wswdteam.txtlauncher_alt.MainActivity.SWIPE_TRESHOLD;
import static com.wswdteam.txtlauncher_alt.MainActivity.allApplicationsList;
import static com.wswdteam.txtlauncher_alt.MainActivity.backgroundImage;
import static com.wswdteam.txtlauncher_alt.MainActivity.backgroundImageBackup;
import static com.wswdteam.txtlauncher_alt.MainActivity.defaultFontSize;
import static com.wswdteam.txtlauncher_alt.MainActivity.defaultIcons;
import static com.wswdteam.txtlauncher_alt.MainActivity.defaultPlusFontSize;
import static com.wswdteam.txtlauncher_alt.MainActivity.firstPermissionRequest;
import static com.wswdteam.txtlauncher_alt.MainActivity.homeAppName;
import static com.wswdteam.txtlauncher_alt.MainActivity.homeAppNum;
import static com.wswdteam.txtlauncher_alt.MainActivity.homeStartAppIcon;
import static com.wswdteam.txtlauncher_alt.MainActivity.homeSysIcon;
import static com.wswdteam.txtlauncher_alt.MainActivity.lockApp;
import static com.wswdteam.txtlauncher_alt.MainActivity.packName;
import static com.wswdteam.txtlauncher_alt.MainActivity.packageMan;
import static com.wswdteam.txtlauncher_alt.MainActivity.privateAIUrl;
import static com.wswdteam.txtlauncher_alt.MainActivity.privateSearchUrl;
import static com.wswdteam.txtlauncher_alt.MainActivity.savedBackgroundImage;
import static com.wswdteam.txtlauncher_alt.MainActivity.screenHeight;
import static com.wswdteam.txtlauncher_alt.MainActivity.screenWidth;
import static com.wswdteam.txtlauncher_alt.MainActivity.sharedPreferences;
import static com.wswdteam.txtlauncher_alt.MainActivity.systemMessage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.wswdteam.txtlauncher_alt.HelpActivity;
import com.wswdteam.txtlauncher_alt.MainActivity;
import com.wswdteam.txtlauncher_alt.R;
import com.wswdteam.txtlauncher_alt.databinding.FragmentHomeBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    public boolean dateReady = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    //
    // Home
    //
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tv = view.findViewById(R.id.mainTitle);
        defaultFontSize = tv.getTextSize();

        MainActivity.generateAppList();
        // kattintás figyelése
        TextView cl1 = view.findViewById(R.id.digitalDate);
        cl1.setOnClickListener(this::openClock);

        TextView cl2 = view.findViewById(R.id.digitalClock);
        cl2.setOnClickListener(this::openClock);

        ImageView set = view.findViewById(R.id.settingsButton);
        set.setOnClickListener(this::openAndroidSystemSettingsButton);

        ImageView hbut = view.findViewById(R.id.helpButton);
        hbut.setOnClickListener(this::openHelp);

        ImageView telbut = view.findViewById(R.id.dialButton);
        telbut.setOnClickListener(this::startButtonApp0);

        ImageView mailbut = view.findViewById(R.id.mailButton);
        mailbut.setOnClickListener(this::startButtonApp1);

        ImageView listbut = view.findViewById(R.id.searchButton);
        listbut.setOnClickListener(this::startButtonSearch);

        ImageView brobut = view.findViewById(R.id.browserButton);
        brobut.setOnClickListener(this::startButtonApp3);

        ImageView cambut = view.findViewById(R.id.cameraButton);
        cambut.setOnClickListener(this::startButtonApp4);

        // touch
        view.findViewById(R.id.AppFrame).setOnTouchListener(new View.OnTouchListener() {
            private final GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(@NonNull MotionEvent event) {
                    //Log.d(DEBUG_TAG, "Action double tap: lock");
                    lockApp();
                    systemMessage("Zár");
                    return super.onDoubleTap(event);
                }

                @Override
                public void onLongPress(@NonNull MotionEvent event) {
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
                    if ((Math.abs(y2 - y1) > MainActivity.SWIPE_MIN_DISTANCE) && (Math.abs(x2 - x1) < MainActivity.SWIPE_TRESHOLD)) {
                        // fel vagy le
                        if (y2 < y1) {
                            // fel
                            openAllApp();
                        } else {
                            // le
                            openFavApp();
                        }
                    }
                    // vízszintes
                    if ((Math.abs(x2 - x1) > SWIPE_MIN_DISTANCE) && (Math.abs(y2 - y1) < SWIPE_TRESHOLD)) {
                        //
                        openGoogleDiscovery();
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

        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        saveButtonImages();
        buttonPrepare();
        backgroundSavedImageSet();
    }

    @Override
    public void onResume() {
        super.onResume();

        dateReady = false;
        timeInScreen();
        getSettings();
        backgroundImageSet();
        setHomaApp();
    }


    //
    // mentett háttékép beállítása
    //
    public void backgroundSavedImageSet() {
        assert getView() != null;
        View mv = getView().findViewById(R.id.homeContainer);
        if (savedBackgroundImage != null) {
            Drawable drawable = new BitmapDrawable(getResources(), savedBackgroundImage);
            mv.setBackground(drawable);
            backgroundImageBackup = backgroundImage;
        }
    }


    //
    // háttérkép beállítása
    //
    public void backgroundImageSet() {
        if (backgroundImage.isEmpty()) {
            // nincs megadott háttér fájl
            backgroundSavedImageSet();
        } else {
            // megadott háttér fájl
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
                // jogodultdág kérése csak egyszer
                if (firstPermissionRequest) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PackageManager.PERMISSION_GRANTED);
                    firstPermissionRequest = false;
                }
            } else {
                if (!backgroundImage.equals(backgroundImageBackup)) {
                    // változott a beállításokban
                    backgroundImageBackup = backgroundImage;
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File file = new File(dir + "/" + backgroundImage);
                    //View mv = getView().findViewById(R.id.viewPad);
                    assert getView() != null;
                    View mv = getView().findViewById(R.id.homeContainer);

                    if (file.exists()) {
                        // létező fájl, átalakítás
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
                            int iw = Math.round((iHeight / screenRatio));
                            if (iWidth > iHeight) {
                                // fekvó
                                ch = Math.round((float) ((iWidth - iw) / 2));
                                cutStartW = ch;
                                cutEndW = iw;
                                cutStartH = 0;
                                cutEndH = iHeight;
                            } else {
                                // álló
                                if (iw < iWidth) {
                                    ch = Math.round((float) ((iWidth - iw) / 2));
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
                            //Log.d(DEBUG_TAG, screenWidth+" "+screenHeight+" "+screenRatio+" "+iWidth+" "+iHeight+" "+cutStartW+" "+cutEndW);
                            Bitmap newBitmap = Bitmap.createBitmap(bitmap, cutStartW, cutStartH, cutEndW, cutEndH);
                            savedBackgroundImage = newBitmap;
                            Drawable drawable = new BitmapDrawable(getResources(), newBitmap);
                            mv.setBackground(drawable);

                            // átalakított kép mentése
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            newBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            byte[] b = baos.toByteArray();
                            String encoded = Base64.encodeToString(b, Base64.DEFAULT);
                            var settings = sharedPreferences.edit();
                            settings.putString(SETTINGS_BACKGROUND_IMAGE, encoded);
                            settings.apply();
                        } catch (Exception e) {
                            systemMessage(getString(R.string.error_bitmap));
                            //mv.setBackground(null);
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
    public void saveButtonImages() {
        ImageView ivone;
        assert getView() != null;
        ivone = getView().findViewById(R.id.dialButton);
        defaultIcons.add(ivone.getDrawable());
        ivone = getView().findViewById(R.id.mailButton);
        defaultIcons.add(ivone.getDrawable());
        ivone = getView().findViewById(R.id.searchButton);
        defaultIcons.add(ivone.getDrawable());
        ivone = getView().findViewById(R.id.browserButton);
        defaultIcons.add(ivone.getDrawable());
        ivone = getView().findViewById(R.id.cameraButton);
        defaultIcons.add(ivone.getDrawable());
    }


    //
//  Fő nézet: beállítások betöltése
//
    public void getSettings() {
        homeAppName.clear();
        String tag;
        String val;
        String appName;
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PRIVATE_SETTINGS_TAG, MODE_PRIVATE);
        for (var i = 0; i < homeAppNum; i++) {
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
            String encoded = sharedPreferences.getString(SETTINGS_BACKGROUND_IMAGE, "");
            if (!encoded.isEmpty()) {
                byte[] imageAsBytes = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
                savedBackgroundImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            }
        }
    }


    //
    //  Fő nézet: alapértelmezett gombok beállítása
    //
    public void setHomaApp() {
        final ArrayList<String> appHList1 = new ArrayList<>();
        final ArrayList<String> appHList2 = new ArrayList<>();
        assert getView() != null;
        final ListView homeTable1 = getView().findViewById(R.id.homeAppList1);
        final ListView homeTable2 = getView().findViewById(R.id.homeAppList2);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getView().getContext(), android.R.layout.simple_list_item_1, appHList1) {
            @Override
            public String getItem(int position) {
                return Objects.requireNonNull(super.getItem(position));
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                String appN = getItem(position);
                View row = super.getView(position, convertView, parent);
                TextView tvt = row.findViewById(android.R.id.text1);
                if (homeStartAppIcon) {
                    for (ResolveInfo app : allApplicationsList) {
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
                tvt.setPadding(10, 10, 10, 10);
                tvt.setEllipsize(TextUtils.TruncateAt.END);
                tvt.setMaxLines(1);
                return row;
            }
        };

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getView().getContext(), android.R.layout.simple_list_item_1, appHList2) {
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
                        for (ResolveInfo app : allApplicationsList) {
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
                    tvt.setPadding(10, 10, 10, 10);
                    tvt.setEllipsize(TextUtils.TruncateAt.END);
                    tvt.setMaxLines(1);
                }
                return row;
            }
        };        double anum = Math.min(homeAppName.size(), homeAppNum);

        double an = anum / 2;
        long halfA = Math.round(an);
        int halfApp = (int) halfA;
        for (var i = 0; i < halfApp; i++) {
            if (homeAppName.size() > i) {
                if (!homeAppName.get(i).isEmpty()) {
                    appHList1.add(homeAppName.get(i));
                }
            }
        }
        for (var i = halfApp; i < homeAppNum; i++) {
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
            for (ResolveInfo app : allApplicationsList) {
                String appName = app.loadLabel(packageMan).toString();
                String pName = app.activityInfo.packageName;
                if (appName.equals(selectedP)) {
                    try {
                        Intent launchIntent = packageMan.getLaunchIntentForPackage(pName);
                        if (launchIntent != null) {
                            startActivity(launchIntent);
                        }
                    } catch (Exception e) {
                        systemMessage(getString(R.string.error_startapp));
                    }
                }
            }
        });
        homeTable2.setOnItemClickListener((parent, view, position, id) -> {
            String selectedP = (String) (homeTable2.getItemAtPosition(position));
            for (ResolveInfo app : allApplicationsList) {
                String appName = app.loadLabel(packageMan).toString();
                String pName = app.activityInfo.packageName;
                if (appName.equals(selectedP)) {
                    try {
                        Intent launchIntent = packageMan.getLaunchIntentForPackage(pName);
                        if (launchIntent != null) {
                            startActivity(launchIntent);
                        }
                    } catch (Exception e) {
                        systemMessage(getString(R.string.error_startapp));
                    }
                }
            }
            //Log.d(DEBUG_TAG, selectedP);
        });
    }


    //
//  Fő nézet: alapértelmezett gombok előkészítése, alapértelmezett app kéeresése
//
    public void buttonPrepare() {
        final PackageManager packageMan = requireActivity().getPackageManager();
        // dial
        Intent dialIn = new Intent(Intent.ACTION_DIAL, null);
        dialIn.addCategory(Intent.CATEGORY_DEFAULT);
        List<ResolveInfo> appDL = packageMan.queryIntentActivities(dialIn, 0);
        ResolveInfo appD = appDL.get(0);
        String packD = appD.activityInfo.packageName;
        assert getView() != null;
        ImageView dialF = getView().findViewById(R.id.dialButton);
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
        List<ResolveInfo> mailDL = packageMan.queryIntentActivities(mailIn, 0);
        ResolveInfo mailD = mailDL.get(0);
        String packI = mailD.activityInfo.packageName;
        ImageView mailF = getView().findViewById(R.id.mailButton);
        if (homeSysIcon) {
            Drawable mailI = mailD.loadIcon(packageMan);
            mailF.setImageDrawable(mailI);
        } else {
            mailF.setImageDrawable(defaultIcons.get(1));
        }
        MainActivity.packName.add(packI);
        // keresés
        Intent searchintent = new Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH);
        searchintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        List<ResolveInfo> searchDL = packageMan.queryIntentActivities(searchintent, 0);
        ResolveInfo searchD = searchDL.get(0);
        String searchB = searchD.activityInfo.packageName;
        ImageView searchF = getView().findViewById(R.id.searchButton);
        if (homeSysIcon) {
            Drawable searchI = searchD.loadIcon(packageMan);
            searchF.setImageDrawable(searchI);
        } else {
            searchF.setImageDrawable(defaultIcons.get(2));
        }
        MainActivity.packName.add(searchB);
        // browser
        Intent broIn = new Intent(Intent.ACTION_VIEW, Uri.parse(privateSearchUrl));
        //broIn.setData(Uri.parse(privateSearchUrl));
        broIn.addCategory(Intent.CATEGORY_DEFAULT);
        List<ResolveInfo> broDL = packageMan.queryIntentActivities(broIn, 0);
        ResolveInfo broD = broDL.get(0);
        String broB = broD.activityInfo.packageName;
        ImageView broF = getView().findViewById(R.id.browserButton);
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
        List<ResolveInfo> camDL = packageMan.queryIntentActivities(camIn, 0);
        ResolveInfo camD = camDL.get(0);
        String camC = camD.activityInfo.packageName;
        ImageView camF = getView().findViewById(R.id.cameraButton);
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
    public void startButtonApp0(View view) {
        final PackageManager pm = requireActivity().getPackageManager();
        try {
            Intent launchIntent = pm.getLaunchIntentForPackage(packName.get(0));
            assert launchIntent != null;
            startActivity(launchIntent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }

    public void startButtonApp1(View view) {
        final PackageManager pm = requireActivity().getPackageManager();
        try {
            Intent launchIntent = pm.getLaunchIntentForPackage(packName.get(1));
            assert launchIntent != null;
            startActivity(launchIntent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }

    public void startButtonApp3(View view) {
        final PackageManager pm = requireActivity().getPackageManager();
        try {
            Intent launchIntent = pm.getLaunchIntentForPackage(packName.get(3));
            assert launchIntent != null;
            launchIntent.setData(Uri.parse(privateSearchUrl));
            startActivity(launchIntent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }

    public void startButtonApp4(View view) {
        final PackageManager pm = requireActivity().getPackageManager();
        try {
            Intent launchIntent = pm.getLaunchIntentForPackage(packName.get(4));
            assert launchIntent != null;
            startActivity(launchIntent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }



    //
    //  Fő nézet: idő kiírása a képernyőre
    //
    public void timeInScreen() {
        // óra
        final TextView textView = requireActivity().findViewById(R.id.digitalClock);
        final TextView textDateView = requireActivity().findViewById(R.id.digitalDate);
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
    public void startButtonSearch(View view) {
        try {
            Intent searchintent = new Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH);
            searchintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(searchintent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
        //Log.d(DEBUG_TAG, "Button start: search");
    }


    //
    //  Fő nézet: google discovery
    //
    public void openGoogleDiscovery() {
        try {
            final PackageManager packageMan = requireActivity().getPackageManager();
            Intent intent = packageMan.getLaunchIntentForPackage("com.google.android.googlequicksearchbox");
            assert intent != null;
            startActivity(intent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }


    //
    //  Fő nézet: óra app indítása
    //
    public void openClock(View view) {
        try {
            Intent mClockIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
            mClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mClockIntent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }


    //
    //  Fő nézet: leírás
    //
    public void openHelp(View view) {
        startActivity(new Intent(getActivity(), HelpActivity.class));
    }


    //
    // Rendszer beállítások indítása
    //
    public void openAndroidSystemSettingsButton(View v) {
        try {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }


    //
    // Minden app lapra váltás
    //
    public void openAllApp() {
        requireActivity().findViewById(R.id.navigation_allapps).callOnClick();
    }


    //
    // Kedvenc app lapra váltás
    //
    public void openFavApp() {
        requireActivity().findViewById(R.id.navigation_favorites).callOnClick();
    }


}
