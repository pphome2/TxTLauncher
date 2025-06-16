package com.wswdteam.txtlauncher;

import static com.wswdteam.txtlauncher.MainActivity.DEBUG_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_FAV_APP_TAG;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSize;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.MainActivity.favAppNum;

import android.annotation.SuppressLint;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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
        for (var i=0; i<favAppNum; i++) {
            tag = SETTINGS_FAV_APP_TAG + i;
            val = MainActivity.sharedPreferences.getString(tag, "");
            if (!val.isEmpty()) {
                selApp.add(val);
            }
        }
        favListSelect();
        Log.e(DEBUG_TAG, getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
    }


    //
    // Kedvenc app választás leállítása
    //
    @Override
    public void onStop() {
        var settings = MainActivity.sharedPreferences.edit();
        String tag;
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
        Log.e(DEBUG_TAG, getString(R.string.stopped_activty) + ": "+ this.getClass().getSimpleName());
    }


    //
    // Kedvencek választás lista készítése
    //
    @SuppressLint("QueryPermissionsNeeded")
    public void favListSelect() {
        final ListView appTable = findViewById(R.id.appListFavSelect);
        appTable.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, appList) {
            @Override
            public String getItem(int position) {
                return (String) super.getItem(position);
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                String appN = getItem(position);
                View row = super.getView(position, convertView, parent);
                TextView tvt = row.findViewById(android.R.id.text1);
                if (appN != null) {
                    tvt.setText(appN);
                    ResolveInfo thisApp = MainActivity.allApplicationsList.get(position);
                    Drawable appI = thisApp.loadIcon(MainActivity.packageMan);
                    int ts = (int) tvt.getTextSize() + 25;
                    appI.setBounds(0, 0, ts, ts);
                    tvt.setCompoundDrawables(appI, null, null, null);
                    tvt.setCompoundDrawablePadding(30);
                    tvt.setPadding(10,10,10,10);
                    if (selApp.contains(appN)) {
                        tvt.setBackgroundColor(getColor(com.google.android.material.R.color.design_default_color_secondary));
                    } else {
                        tvt.setBackgroundColor(getColor(com.google.android.material.R.color.design_default_color_on_background));
                    }
                }
                return row;
            }

        };

        for (ResolveInfo app : MainActivity.allApplicationsList) {
            String appName = app.loadLabel(MainActivity.packageMan).toString();
            appList.add(appName);
        }

        appTable.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        appTable.setOnItemClickListener((parent, view, position, id) -> {
            var del = false;
            for (int en = 0; en < selApp.size(); en++) {
                String pname = selApp.get(en);
                String iname = parent.getItemAtPosition(position).toString();
                if (pname.equals(iname)) {
                    selApp.remove(en);
                    view.setBackgroundColor(getColor(com.google.android.material.R.color.design_default_color_on_background));
                    del = true;
                }
            }
            if (!del) {
                String iname = parent.getItemAtPosition(position).toString();
                selApp.add(iname);
                view.setBackgroundColor(getColor(com.google.android.material.R.color.design_default_color_secondary));
            }
            String selectedP = (String) (appTable.getItemAtPosition(position));
            Log.e(DEBUG_TAG, selectedP);
        });
    }


    //
    // App kedvencek választás bezárása
    //
    public void closeFavAppSelButton(View view) {
        this.finish();
    }

}
