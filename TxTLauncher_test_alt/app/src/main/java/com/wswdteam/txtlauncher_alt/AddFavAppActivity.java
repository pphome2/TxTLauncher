package com.wswdteam.txtlauncher_alt;

import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_FAV_APP_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_HOME_ICON_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.defaultBackGroundColor;
import static com.wswdteam.txtlauncher_alt.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher_alt.MainActivity.defaultSelectColor;
import static com.wswdteam.txtlauncher_alt.MainActivity.favAppNum;

import static java.util.Collections.sort;

import android.annotation.SuppressLint;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

public class AddFavAppActivity extends AppCompatActivity {

    final ArrayList<String> appList = new ArrayList<>();
    final ArrayList<String> selApp = new ArrayList<>();
    boolean showicons = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_fav_app);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tv = findViewById(R.id.addFavAppTitle);
        tv.setTextSize(tv.getTextSize() + defaultPlusFontSizeTitle);
        int ts = (int) tv.getTextSize();
        @SuppressLint("UseCompatLoadingForDrawables") Drawable appI = getDrawable(R.drawable.arrow_back);
        if (appI != null) {
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
        super.onStart();

        selApp.clear();
        String tag;
        String val;
        val = MainActivity.sharedPreferences.getString(SETTINGS_HOME_ICON_TAG, "");
        if (!val.isEmpty()) {
            showicons = !val.equals("0");
        }
        for (var i=0; i<favAppNum; i++) {
            tag = SETTINGS_FAV_APP_TAG + i;
            val = MainActivity.sharedPreferences.getString(tag, "");
            if (!val.isEmpty()) {
                selApp.add(val);
            }
        }
        favListSelect();
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
        final ListView appFTable = findViewById(R.id.appListFavSelect);
        appFTable.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        final var adapterfa = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appList) {
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
                    if (showicons) {
                        ResolveInfo thisApp = MainActivity.allApplicationsList.get(position);
                        Drawable appI = thisApp.loadIcon(MainActivity.packageMan);
                        int ts = (int) tvt.getTextSize() + 25;
                        appI.setBounds(0, 0, ts, ts);
                        tvt.setCompoundDrawables(appI, null, null, null);
                        tvt.setCompoundDrawablePadding(30);
                        tvt.setPadding(10, 10, 10, 10);
                    }
                    if (selApp.contains(appN)) {
                        tvt.setBackgroundColor(MainActivity.defaultSelectColor);
                    } else {
                        tvt.setBackgroundColor(MainActivity.defaultBackGroundColor);
                    }

                }
                return row;
            }

        };

        for (ResolveInfo app : MainActivity.allApplicationsList) {
            String appName = app.loadLabel(MainActivity.packageMan).toString();
            appList.add(appName);
        }

        appFTable.setAdapter(adapterfa);
        adapterfa.notifyDataSetChanged();

        appFTable.setOnItemClickListener((adapterfav, view, position, id) -> {
            var del = false;
            int delitem = 0;
            for (int en = 0; en < selApp.size(); en++) {
                String pname = selApp.get(en);
                String iname = adapterfav.getItemAtPosition(position).toString();
                if (pname.equals(iname)) {
                    delitem = en;
                    del = true;
                }
            }
            if (!del) {
                String iname = adapterfav.getItemAtPosition(position).toString();
                selApp.add(iname);
                view.setBackgroundColor(defaultSelectColor);
            } else {
                selApp.remove(delitem);
                view.setBackgroundColor(defaultBackGroundColor);
            }
            //String selectedP = (String) (appTable.getItemAtPosition(position));
        });
    }


    //
// App kedvencek választás bezárása
//
    public void closeFavAppSelButton(View view) {
        this.finish();
    }


}