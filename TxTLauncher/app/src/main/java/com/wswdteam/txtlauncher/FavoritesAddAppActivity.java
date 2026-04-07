package com.wswdteam.txtlauncher;

import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_FAV_APP_TAG;
import static com.wswdteam.txtlauncher.MainActivity.adaptiveIconColor;
import static com.wswdteam.txtlauncher.MainActivity.allAppData;
import static com.wswdteam.txtlauncher.MainActivity.defaultFontSize;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.MainActivity.defaultTextColor;
import static com.wswdteam.txtlauncher.MainActivity.favAppNum;
import static com.wswdteam.txtlauncher.MainActivity.homeStartAppIcon;
import static com.wswdteam.txtlauncher.MainActivity.iconSize;
import static com.wswdteam.txtlauncher.MainActivity.syslog;
import static com.wswdteam.txtlauncher.MainActivity.textColorMode;

import static java.util.Collections.sort;

import android.annotation.SuppressLint;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
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
        syslog(getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
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
        syslog(getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
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
                    if (textColorMode) {
                        tvt.setTextColor(adaptiveIconColor);
                    } else {
                        tvt.setTextColor(defaultTextColor);
                    }
                    if (homeStartAppIcon) {
                        int padding = iconSize / 2;
                        ResolveInfo thisApp = MainActivity.allApplicationsList.get(position);
                        Drawable appI = thisApp.loadIcon(MainActivity.packageMan);
                        Drawable iconToDisplay = MainActivity.getDrawable(row, appI, iconSize);
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
            // - syslog(selectedP);
        });
    }


    //
    // App kedvencek választás bezárása
    //
    public void closeFavAppSelButton(View view) {
        this.finish();
    }

}
