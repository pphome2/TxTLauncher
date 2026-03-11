package com.wswdteam.txtlauncher;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_ADAPTIVE_ICON_COLOR_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_ADAPTIVE_ICON_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_FAV_APP_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_HOME_ICON_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_ONE_COLUMN_FAVORITES_TAG;
import static com.wswdteam.txtlauncher.MainActivity.adaptiveIcon;
import static com.wswdteam.txtlauncher.MainActivity.adaptiveIconColor;
import static com.wswdteam.txtlauncher.MainActivity.allAppData;
import static com.wswdteam.txtlauncher.MainActivity.allApplicationsList;
import static com.wswdteam.txtlauncher.MainActivity.defaultFontSize;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.MainActivity.favAppNum;
import static com.wswdteam.txtlauncher.MainActivity.onecolFavorites;
import static com.wswdteam.txtlauncher.MainActivity.packageMan;
import static com.wswdteam.txtlauncher.MainActivity.sharedPreferences;
import static com.wswdteam.txtlauncher.MainActivity.systemMessage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
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
import androidx.core.content.ContextCompat;
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
    public static boolean favAppIcon = false;

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

        TextView tv = findViewById(R.id.favTitle);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, MainActivity.defaultFontSize + defaultPlusFontSizeTitle);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable appI = getDrawable(R.drawable.arrow_back);
        if (appI != null) {
            int ts = (int) defaultFontSize + (int) defaultPlusFontSizeTitle;
            appI.setBounds(0, 0, ts, ts);
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
        for (var i=0; i<favAppNum; i++) {
            tag = SETTINGS_FAV_APP_TAG + i;
            val = sharedPreferences.getString(tag, "");
            if (!val.isEmpty()) {
                for (String[] allAppDatum : allAppData) {
                    appName = allAppDatum[0];
                    if (appName.equals(val)) {
                        favAppName.add(appName);
                    }
                    //Log.d(DEBUG_TAG, val);
                }
            }
        }

        val = sharedPreferences.getString(SETTINGS_HOME_ICON_TAG, "");
        if (!val.isEmpty()) {
            favAppIcon = !val.equals("0");
        }
        val = sharedPreferences.getString(SETTINGS_ONE_COLUMN_FAVORITES_TAG, "");
        if (!val.isEmpty()) {
            onecolFavorites = !val.equals("0");
        }
        val = sharedPreferences.getString(SETTINGS_ADAPTIVE_ICON_TAG, "");
        if (!val.isEmpty()) {
            adaptiveIcon = !val.equals("0");
        }
        int buttonId;
        buttonId = sharedPreferences.getInt(SETTINGS_ADAPTIVE_ICON_COLOR_TAG, Integer.parseInt("0"));
        if (buttonId == R.id.btnRed) { adaptiveIconColor = ContextCompat.getColor(this, R.color.red); }
        if (buttonId == R.id.btnWhite) { adaptiveIconColor = ContextCompat.getColor(this, R.color.white); }
        if (buttonId == R.id.btnBlack) { adaptiveIconColor = ContextCompat.getColor(this, R.color.black); }
        if (buttonId == R.id.btnGray) { adaptiveIconColor = ContextCompat.getColor(this, R.color.gray); }
        if (buttonId == R.id.btnBlue) { adaptiveIconColor = ContextCompat.getColor(this, R.color.blue); }
        if (buttonId == R.id.btnGreen) { adaptiveIconColor = ContextCompat.getColor(this, R.color.green); }
        if (adaptiveIconColor == 0) { adaptiveIconColor = ContextCompat.getColor(this, android.R.color.system_accent1_400); }


        setFavApp();

        //Log.d(DEBUG_TAG, getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
    }


    //
    // Kedvenc app lista leállítása
    //
    @Override
    public void onStop() {
        super.onStop();
        //Log.d(DEBUG_TAG, getString(R.string.stopped_activty) + ": "+ this.getClass().getSimpleName());
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
                    if (favAppIcon) {
                        ResolveInfo app;
                        String appName;
                        for (int i = 0; i < allAppData.length; i++) {
                            appName = allAppData[i][0];
                            if (appName.equals(appN)) {
                                app = allApplicationsList.get(i);
                                tvt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                                Drawable appI = app.loadIcon(packageMan);
                                int iconSize = (int) (32 * getContext().getResources().getDisplayMetrics().density);
                                int padding = (int) (12 * getContext().getResources().getDisplayMetrics().density);
                                Drawable iconToDisplay = getDrawable(appI, iconSize);
                                tvt.setCompoundDrawablesRelative(iconToDisplay, null, null, null);
                                tvt.setCompoundDrawablePadding(padding);
                            }
                        }
                    } else {
                        // ! tvt.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultFontSize + defaultPlusFontSize);
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

            @NonNull
            private Drawable getDrawable(Drawable appI, int iconSize) {
                Drawable iconToDisplay;
                if (adaptiveIcon && appI instanceof AdaptiveIconDrawable) {
                    AdaptiveIconDrawable adaptI = (AdaptiveIconDrawable) appI;
                    Drawable monoIcon = adaptI.getMonochrome();
                    if (monoIcon != null) {
                        monoIcon.mutate();
                        monoIcon.setTint(adaptiveIconColor);
                        int padding2 = (int) (-16 * getContext().getResources().getDisplayMetrics().density);
                        InsetDrawable insetIcon = new InsetDrawable(monoIcon, padding2, padding2, padding2, padding2);
                        LayerDrawable layer = new LayerDrawable(new Drawable[]{insetIcon});
                        layer.setLayerSize(0, iconSize, iconSize);
                        layer.setBounds(0, 0, iconSize, iconSize);
                        iconToDisplay = layer;
                    } else {
                        iconToDisplay = appI;
                    }
                } else {
                    iconToDisplay = appI;
                }
                iconToDisplay.setBounds(0, 0, iconSize, iconSize);
                return iconToDisplay;
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
                    if (favAppIcon) {
                        ResolveInfo app;
                        String appName;
                        for (int i = 0; i < allAppData.length; i++) {
                            appName = allAppData[i][0];
                            if (appName.equals(appN)) {
                                app = allApplicationsList.get(i);
                                tvt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                                Drawable appI = app.loadIcon(packageMan);
                                int iconSize = (int) (32 * getContext().getResources().getDisplayMetrics().density);
                                int padding = (int) (12 * getContext().getResources().getDisplayMetrics().density);
                                Drawable iconToDisplay = getDrawable(appI, iconSize);
                                tvt.setCompoundDrawablesRelative(iconToDisplay, null, null, null);
                                tvt.setCompoundDrawablePadding(padding);
                            }
                        }
                    } else {
                        // ! tvt.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultFontSize + defaultPlusFontSize);
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

            @NonNull
            private Drawable getDrawable(Drawable appI, int iconSize) {
                Drawable iconToDisplay;
                if (adaptiveIcon && appI instanceof AdaptiveIconDrawable) {
                    AdaptiveIconDrawable adaptI = (AdaptiveIconDrawable) appI;
                    Drawable monoIcon = adaptI.getMonochrome();
                    if (monoIcon != null) {
                        monoIcon.mutate();
                        monoIcon.setTint(adaptiveIconColor);
                        int padding2 = (int) (-16 * getContext().getResources().getDisplayMetrics().density);
                        InsetDrawable insetIcon = new InsetDrawable(monoIcon, padding2, padding2, padding2, padding2);
                        LayerDrawable layer = new LayerDrawable(new Drawable[]{insetIcon});
                        layer.setLayerSize(0, iconSize, iconSize);
                        layer.setBounds(0, 0, iconSize, iconSize);
                        iconToDisplay = layer;
                    } else {
                        iconToDisplay = appI;
                    }
                } else {
                    iconToDisplay = appI;
                }
                iconToDisplay.setBounds(0, 0, iconSize, iconSize);
                return iconToDisplay;
            }
        };

        if (!onecolFavorites) {
            ListView tx1 = findViewById(R.id.favAppList2);
            tx1.setVisibility(View.VISIBLE);
            ListView tx2 = findViewById(R.id.favAppList1);
            tx2.setPadding(0,0,0,0);
            double anum = Math.min(favAppName.size(), favAppNum);
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
            for (var i = halfApp; i < favAppNum; i++) {
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
            for (var i = 0; i < favAppNum; i++) {
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
                        this.finish();
                    } catch (Exception e) {
                        systemMessage(getString(R.string.error_startapp));
                    }
                    i = allAppData.length;
                }
            }
            //Log.d(DEBUG_TAG, selectedP);
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
                        this.finish();
                    } catch (Exception e) {
                        systemMessage(getString(R.string.error_startapp));
                    }
                    i = allAppData.length;
                }
            }
            //Log.d(DEBUG_TAG, selectedP);
        });
    }


    //
    // App kedvencek választás bezárása
    //
    public void closeFavAppButton(View view) {
        this.finish();
    }


}