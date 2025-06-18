package com.wswdteam.txtlauncher;

import static com.wswdteam.txtlauncher.MainActivity.DEBUG_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_FAV_APP_TAG;
import static com.wswdteam.txtlauncher.MainActivity.allApplicationsList;
import static com.wswdteam.txtlauncher.MainActivity.defaultFontSize;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSize;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.MainActivity.favAppNum;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
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
        defaultFontSize = tv.getTextSize();
        tv.setTextSize(defaultFontSize + defaultPlusFontSizeTitle);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable appI = getDrawable(R.drawable.arrow_back);
        if (appI != null) {
            int ts = (int) tv.getTextSize();
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
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.enter_from_top, R.anim.exit_to_bottom);
        super.onStart();

        favAppName.clear();
        String tag;
        String val;
        String appName;
        for (var i=0; i<favAppNum; i++) {
            tag = SETTINGS_FAV_APP_TAG + i;
            val = MainActivity.sharedPreferences.getString(tag, "");
            if (!val.isEmpty()) {
                for (ResolveInfo app : allApplicationsList) {
                    appName = app.loadLabel(MainActivity.packageMan).toString();
                    if (appName.equals(val)) {
                        favAppName.add(appName);
                    }
                    //Log.e(DEBUG_TAG, val);
                }
            }
        }

        val = MainActivity.sharedPreferences.getString("AppIcon", "");
        if (!val.isEmpty()) {
            favAppIcon = !val.equals("0");
        }

        setFavApp();

        Log.e(DEBUG_TAG, getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
    }


    //
    // Kedvenc app lista leállítása
    //
    @Override
    public void onStop() {
        super.onStop();
        Log.e(DEBUG_TAG, getString(R.string.stopped_activty) + ": "+ this.getClass().getSimpleName());
    }


    //
    //  Fő nézet: alapértelmezett gombok beállítása
    //
    public void setFavApp(){
        final ArrayList<String> appFList1 = new ArrayList<>();
        final ArrayList<String> appFList2 = new ArrayList<>();
        final ListView favTable1 = findViewById(R.id.favAppList1);
        final ListView favTable2 = findViewById(R.id.favAppList2);
        final ArrayAdapter adapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, appFList1) {
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
                    if (favAppIcon) {
                        for (ResolveInfo app : allApplicationsList) {
                            String appName = app.loadLabel(MainActivity.packageMan).toString();
                            if (appName.equals(appN)) {
                                tvt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                                Drawable appI = app.loadIcon(MainActivity.packageMan);
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
        final ArrayAdapter adapter2 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, appFList2) {
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
                    if (favAppIcon) {
                        for (ResolveInfo app : allApplicationsList) {
                            String appName = app.loadLabel(MainActivity.packageMan).toString();
                            if (appName.equals(appN)) {
                                tvt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                                Drawable appI = app.loadIcon(MainActivity.packageMan);
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

        int anum = Math.min(favAppName.size(), favAppNum);
        int halfApp = anum / 2;
        for (var i=0; i < halfApp; i++){
            if (favAppName.size() > i) {
                if (!favAppName.get(i).isEmpty()) {
                    appFList1.add(favAppName.get(i));
                }
            }
        }
        for (var i=halfApp; i < favAppNum; i++){
            if (favAppName.size() > i) {
                if (!favAppName.get(i).isEmpty()) {
                    appFList2.add(favAppName.get(i));
                }
            }
        }

        favTable1.setAdapter(adapter1);
        adapter1.notifyDataSetChanged();
        favTable2.setAdapter(adapter2);
        adapter2.notifyDataSetChanged();

        favTable1.setOnItemClickListener((parent, view, position, id) -> {
            String selectedP = (String) (favTable1.getItemAtPosition(position));
            for (ResolveInfo app : allApplicationsList) {
                String appName = app.loadLabel(MainActivity.packageMan).toString();
                String pName = app.activityInfo.packageName;
                Log.d(DEBUG_TAG, appName);
                if (appName.equals(selectedP)) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(pName);
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                        this.finish();
                    }
                }
            }
            Log.e(DEBUG_TAG, selectedP);
        });
        favTable2.setOnItemClickListener((parent, view, position, id) -> {
            String selectedP = (String) (favTable2.getItemAtPosition(position));
            //MainActivity.startApp(selectedP);
            PackageManager pmx = getPackageManager();
            for (ResolveInfo app : allApplicationsList) {
                String appName = app.loadLabel(pmx).toString();
                String pName = app.activityInfo.packageName;
                if (appName.equals(selectedP)) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(pName);
                    if (launchIntent != null) {
                        startActivity(launchIntent);
                        this.finish();
                    }
                }
            }
            Log.e(DEBUG_TAG, selectedP);
        });
    }


    //
    // App kedvencek választás bezárása
    //
    public void closeFavAppButton(View view) {
        this.finish();
    }


}