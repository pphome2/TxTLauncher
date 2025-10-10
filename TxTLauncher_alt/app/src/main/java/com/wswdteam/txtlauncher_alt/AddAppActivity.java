package com.wswdteam.txtlauncher_alt;

import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_APP_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_HOME_ICON_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.defaultBackGroundColor;
import static com.wswdteam.txtlauncher_alt.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher_alt.MainActivity.defaultSelectColor;
import static com.wswdteam.txtlauncher_alt.MainActivity.homeAppNum;

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

public class AddAppActivity extends AppCompatActivity {

    final ArrayList<String> appList = new ArrayList<>();
    final ArrayList<String> selApp = new ArrayList<>();
    boolean showicons = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_app);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tv = findViewById(R.id.addappTitle);
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
    // App választás főképernyőre előkészítés
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
        for (var i = 0; i < homeAppNum; i++) {
            tag = SETTINGS_APP_TAG + i;
            val = MainActivity.sharedPreferences.getString(tag, "");
            if (!val.isEmpty()) {
                selApp.add(val);
            }
        }
        appListSelect();
    }


    //
    // App választás leállítása
    //
    @Override
    public void onStop() {
        var settings = MainActivity.sharedPreferences.edit();
        String tag;
        sort(selApp);
        for (var i = 0; i < homeAppNum; i++) {
            tag = SETTINGS_APP_TAG + i;
            if (i < selApp.size()) {
                settings.putString(tag, selApp.get(i));
                //Log.d(DEBUG_TAG, selApp.get(i));
            } else {
                settings.putString(tag, "");
            }
        }
        settings.apply();

        super.onStop();
    }


    //
    // App választás lista készítése
    //
    @SuppressLint("QueryPermissionsNeeded")
    public void appListSelect() {
        final ListView appHTable = this.findViewById(R.id.appListHomeSelect);
        appHTable.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        final var adapteraa = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appList) {
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

        appHTable.setAdapter(adapteraa);
        adapteraa.notifyDataSetChanged();

        appHTable.setOnItemClickListener((adapterxaa, view, position, id) -> {
            var del = false;
            String pname;
            int delitem = 0;
            for (int en = 0; en < selApp.size(); en++) {
                pname = selApp.get(en);
                String iname = adapterxaa.getItemAtPosition(position).toString();
                if (pname.equals(iname)) {
                    delitem = en;
                    del = true;
                }
            }
            if (!del) {
                try {
                    String iname = adapterxaa.getItemAtPosition(position).toString();
                    selApp.add(iname);
                    view.setBackgroundColor(defaultSelectColor);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                selApp.remove(delitem);
                view.setBackgroundColor(defaultBackGroundColor);
            }
        });
    }


    //
    // App választás bezárása
    //
    public void closeAddAppButton(View view) {
        this.finish();
    }
}
