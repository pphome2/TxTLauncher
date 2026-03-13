package com.wswdteam.txtlauncher;

import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_FAV_APP_TAG;
import static com.wswdteam.txtlauncher.MainActivity.adaptiveIcon;
import static com.wswdteam.txtlauncher.MainActivity.adaptiveIconColor;
import static com.wswdteam.txtlauncher.MainActivity.allAppData;
import static com.wswdteam.txtlauncher.MainActivity.defaultFontSize;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.MainActivity.favAppNum;
import static com.wswdteam.txtlauncher.MainActivity.homeStartAppIcon;

import static java.util.Collections.sort;

import android.annotation.SuppressLint;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
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


//
// Kedvenc app választás
//
public class FavoritesAddAppActivity extends AppCompatActivity {
    final ArrayList<String> appList = new ArrayList<>();
    final ArrayList<String> selApp = new ArrayList<>();
    public int selectedApp = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorites_addapp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tv = findViewById(R.id.addFavAppTitle);
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
    // Kedvenc app választás előkészítés
    //
    @Override
    public void onStart() {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.enter_from_right, R.anim.exit_to_left);
        //overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.enter_from_left, R.anim.exit_to_right);
        super.onStart();

        selApp.clear();
        String tag;
        String val;
        for (var i=0; i<favAppNum; i++) {
            tag = SETTINGS_FAV_APP_TAG + i;
            val = MainActivity.sharedPreferences.getString(tag, "");
            if (!val.isEmpty()) {
                selApp.add(val);
            }
        }
        favListSelect();
        //Log.d(DEBUG_TAG, getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
    }


    //
    // Kedvenc app választás leállítása
    //
    @Override
    public void onStop() {
        var settings = MainActivity.sharedPreferences.edit();
        String tag;
        sort(selApp);
        for (var i = 0; i < favAppNum; i++) {
            tag = SETTINGS_FAV_APP_TAG + i;
            if (i < selApp.size()) {
                settings.putString(tag, selApp.get(i));
            } else {
                settings.putString(tag, "");
            }
        }
        settings.apply();

        super.onStop();
        //Log.d(DEBUG_TAG, getString(R.string.stopped_activty) + ": "+ this.getClass().getSimpleName());
    }


    //
    // Kedvencek választás lista készítése
    //
    @SuppressLint("QueryPermissionsNeeded")
    public void favListSelect() {
        final ListView appTable = findViewById(R.id.appListFavSelect);
        appTable.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        final var adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appList) {
            @Override
            public String getItem(int position) {
                return super.getItem(position);
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                String appN = getItem(position);
                View row = super.getView(position, convertView, parent);
                TextView tvt = row.findViewById(android.R.id.text1);
                if (appN != null) {
                    tvt.setText(appN);
                    if (homeStartAppIcon) {
                        ResolveInfo thisApp = MainActivity.allApplicationsList.get(position);
                        Drawable appI = thisApp.loadIcon(MainActivity.packageMan);
                        int iconSize = (int) (32 * getContext().getResources().getDisplayMetrics().density);
                        int padding = (int) (12 * getContext().getResources().getDisplayMetrics().density);
                        Drawable iconToDisplay = getDrawable(appI, iconSize);
                        tvt.setCompoundDrawablesRelative(iconToDisplay, null, null, null);
                        tvt.setCompoundDrawablePadding(padding);
                    }
                    // ! tvt.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultFontSize + defaultPlusFontSize);
                    if (selApp.contains(appN)) {
                        tvt.setBackgroundColor(MainActivity.defaultSelectColor);
                    } else {
                        tvt.setBackgroundColor(MainActivity.defaultBackGroundColor);
                    }
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
                        iconToDisplay= ContextCompat.getDrawable(this.getContext(), R.drawable.app);
                        assert iconToDisplay != null;
                        iconToDisplay.setTint(adaptiveIconColor);
                    }
                } else {
                    if (adaptiveIcon) {
                        iconToDisplay = ContextCompat.getDrawable(this.getContext(), R.drawable.app);
                        assert iconToDisplay != null;
                        iconToDisplay.setTint(adaptiveIconColor);
                    } else {
                        iconToDisplay = appI;
                    }
                }
                assert iconToDisplay != null;
                iconToDisplay.setBounds(0, 0, iconSize, iconSize);
                return iconToDisplay;
            }
        };

        for (String[] allAppDatum : allAppData) {
            String appName = allAppDatum[0];
            appList.add(appName);
            if (selApp.contains(appName)) {
                selectedApp++;
                TextView tv = findViewById(R.id.addFavAppInfo);
                String st = favAppNum + " / " + selectedApp;
                tv.setText(st);
            }
        }

        appTable.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        appTable.setOnItemClickListener((parent, view, position, id) -> {
            var del = false;
            String iname = parent.getItemAtPosition(position).toString();
            int pos = 0;
            for (int en = 0; en < selApp.size(); en++) {
                String pname = selApp.get(en);
                if (pname.equals(iname)) {
                    pos = en;
                    del = true;
                }
            }
            if (del) {
                selApp.remove(pos);
                view.setBackgroundColor(MainActivity.defaultBackGroundColor);
                selectedApp--;
            } else {
                selApp.add(iname);
                view.setBackgroundColor(MainActivity.defaultSelectColor);
                selectedApp++;
            }
            TextView tv = findViewById(R.id.addFavAppInfo);
            String st1 = favAppNum + " / " + selectedApp;
            tv.setText(st1);
            // - String selectedP = (String) (appTable.getItemAtPosition(position));
            // - Log.d(DEBUG_TAG, selectedP);
        });
    }


    //
    // App kedvencek választás bezárása
    //
    public void closeFavAppSelButton(View view) {
        this.finish();
    }

}
