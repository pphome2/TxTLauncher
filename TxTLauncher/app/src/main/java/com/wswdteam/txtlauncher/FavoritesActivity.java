package com.wswdteam.txtlauncher;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static com.wswdteam.txtlauncher.MainActivity.FAV_APP_NUM;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_FAV_APP_TAG;
import static com.wswdteam.txtlauncher.MainActivity.adaptiveIconColor;
import static com.wswdteam.txtlauncher.MainActivity.allAppData;
import static com.wswdteam.txtlauncher.MainActivity.allApplicationsList;
import static com.wswdteam.txtlauncher.MainActivity.defaultBackGroundColor;
import static com.wswdteam.txtlauncher.MainActivity.defaultFontSize;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.MainActivity.defaultTextColor;
import static com.wswdteam.txtlauncher.MainActivity.homeStartAppIcon;
import static com.wswdteam.txtlauncher.MainActivity.iconSize;
import static com.wswdteam.txtlauncher.MainActivity.onecolFavorites;
import static com.wswdteam.txtlauncher.MainActivity.packageMan;
import static com.wswdteam.txtlauncher.MainActivity.sharedPreferences;
import static com.wswdteam.txtlauncher.MainActivity.syslog;
import static com.wswdteam.txtlauncher.MainActivity.systemMessage;
import static com.wswdteam.txtlauncher.MainActivity.textColorMode;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;


//
// Kedvencek lista és indítás
//
public class FavoritesActivity extends AppCompatActivity {
    public static List<String> favAppName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorites);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // teljes háttér színe
        getWindow().getDecorView().setBackgroundColor(defaultBackGroundColor);

        TextView tv = findViewById(R.id.favTitle);
        tv.setTextColor(defaultTextColor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, MainActivity.defaultFontSize + defaultPlusFontSizeTitle);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable appI = getDrawable(R.drawable.arrow_back);
        if (appI != null) {
            int ts = (int) defaultFontSize + (int) defaultPlusFontSizeTitle;
            appI.setBounds(0, 0, ts, ts);
            appI.setTint(defaultTextColor);
            tv.setCompoundDrawables(appI, null, null, null);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setCompoundDrawables(appI, null, null, null);
        }
    }


    //
    // Kedvenc app lista előkészítés
    //
    @Override
    public void onStart() {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.enter_from_top, R.anim.exit_to_bottom);
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.enter_from_bottom, R.anim.exit_to_top);
        super.onStart();

        favAppName.clear();
        String tag;
        String val;
        String appName;
        for (var i = 0; i < FAV_APP_NUM; i++) {
            tag = SETTINGS_FAV_APP_TAG + i;
            val = sharedPreferences.getString(tag, "");
            if (!val.isEmpty()) {
                for (String[] allAppDatum : allAppData) {
                    appName = allAppDatum[0];
                    if (appName.equals(val)) {
                        favAppName.add(appName);
                    }
                }
            }
        }
        setFavApp();
        syslog(getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
    }


    //
    // Kedvenc app lista leállítása
    //
    @Override
    public void onStop() {
        super.onStop();
    }


    //
    //  Fő nézet: alapértelmezett gombok beállítása
    //
    public void setFavApp(){
        final ArrayList<String> appFList1 = new ArrayList<>();
        final ArrayList<String> appFList2 = new ArrayList<>();
        final ListView favTable1 = findViewById(R.id.favAppList1);
        final ListView favTable2 = findViewById(R.id.favAppList2);
        final var adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appFList1) {
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
                                Drawable iconToDisplay = MainActivity.getDrawable(row, appI, iconSize);
                                tvt.setCompoundDrawablesRelative(iconToDisplay, null, null, null);
                                tvt.setCompoundDrawablePadding(padding);
                            }
                        }
                    } else {
                        // ! tvt.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultFontSize + defaultPlusFontSize);
                        tvt.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    tvt.setText(appN);
                    if (textColorMode) {
                        tvt.setTextColor(adaptiveIconColor);
                    } else {
                        tvt.setTextColor(defaultTextColor);
                    }
                    tvt.setCompoundDrawablePadding(30);
                    tvt.setPadding(10,10,10,10);
                    tvt.setEllipsize(TextUtils.TruncateAt.END);
                    tvt.setMaxLines(1);
                }
                return row;
            }
        };

        final var adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appFList2) {
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
                                Drawable iconToDisplay = MainActivity.getDrawable(row, appI, iconSize);
                                tvt.setCompoundDrawablesRelative(iconToDisplay, null, null, null);
                                tvt.setCompoundDrawablePadding(padding);
                            }
                        }
                    } else {
                        // ! tvt.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultFontSize + defaultPlusFontSize);
                        tvt.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    tvt.setText(appN);
                    if (textColorMode) {
                        tvt.setTextColor(adaptiveIconColor);
                    } else {
                        tvt.setTextColor(defaultTextColor);
                    }
                    tvt.setCompoundDrawablePadding(30);
                    tvt.setPadding(10,10,10,10);
                    tvt.setEllipsize(TextUtils.TruncateAt.END);
                    tvt.setMaxLines(1);
                }
                return row;
            }
        };

        if (!onecolFavorites) {
            ListView tx1 = findViewById(R.id.favAppList2);
            tx1.setVisibility(View.VISIBLE);
            ListView tx2 = findViewById(R.id.favAppList1);
            tx2.setPadding(0,0,0,0);
            double anum = Math.min(favAppName.size(), FAV_APP_NUM);
            double an = anum / 2;
            long halfA = Math.round(an);
            int halfApp = (int) halfA;
            for (var i = 0; i < halfApp; i++) {
                if (favAppName.size() > i) {
                    if (!favAppName.get(i).isEmpty()) {
                        appFList1.add(favAppName.get(i));
                    }
                }
            }
            for (var i = halfApp; i < FAV_APP_NUM; i++) {
                if (favAppName.size() > i) {
                    if (!favAppName.get(i).isEmpty()) {
                        appFList2.add(favAppName.get(i));
                    }
                }
            }
            if ((halfApp * 2) > favAppName.size()) {
                appFList2.add("");
            }
        } else {
            ListView t1 = findViewById(R.id.favAppList1);
            int dp = 30;
            int pixeldp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
            t1.setPadding(pixeldp,0,0,0);
            ListView t2 = findViewById(R.id.favAppList2);
            t2.setVisibility(INVISIBLE);
            t2.setVisibility(GONE);
            for (var i = 0; i < FAV_APP_NUM; i++) {
                if (favAppName.size() > i) {
                    if (!favAppName.get(i).isEmpty()) {
                        appFList1.add(favAppName.get(i));
                    }
                }
            }
        }

        favTable1.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();
        favTable2.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();

        favTable1.setOnItemClickListener((parent, view, position, id) -> {
            String selectedP = (String) (favTable1.getItemAtPosition(position));
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
                        MainActivity.startedAndroidApp = true;
                        this.finish();
                    } catch (Exception e) {
                        systemMessage(getString(R.string.error_startapp));
                    }
                    i = allAppData.length;
                }
            }
        });
        favTable2.setOnItemClickListener((parent, view, position, id) -> {
            String selectedP = (String) (favTable2.getItemAtPosition(position));
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
                        MainActivity.startedAndroidApp = true;
                        this.finish();
                    } catch (Exception e) {
                        systemMessage(getString(R.string.error_startapp));
                    }
                    i = allAppData.length;
                }
            }
        });
    }


    //
    // App kedvencek választás bezárása
    //
    public void closeFavAppButton(View view) {
        this.finish();
    }


}