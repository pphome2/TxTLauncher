package com.wswdteam.txtlauncher;

import static com.wswdteam.txtlauncher.MainActivity.systemMessage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;


//
// App lista előkészítése
//
public class AppListActivity extends AppCompatActivity {

    final ArrayList<ResolveInfo> allappList = new ArrayList<>();
    final ArrayList<String> appList = new ArrayList<>();
    final ArrayList<String> appPackList = new ArrayList<>();

    final ArrayList<ResolveInfo> allappListBackup = new ArrayList<>();
    final ArrayList<String> appListBackup = new ArrayList<>();
    final ArrayList<String> appPackListBackup = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_app_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.allAppLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }


    //
    // App lista indítása
    //
    @Override
    protected void onStart() {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.enter_from_bottom, R.anim.exit_to_top);
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.enter_from_top, R.anim.exit_to_bottom);
        super.onStart();

        appListView();
        SearchView sv = findViewById(R.id.allAppListSearch);
        sv.setQuery("", false);
        sv.setIconified(true);
        //Log.d(DEBUG_TAG, getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
    }


    //
    // App lista leállítása
    //
    @Override
    public void onStop() {
        super.onStop();
        //Log.d(DEBUG_TAG, getString(R.string.stopped_activty) + ": "+ this.getClass().getSimpleName());
    }


    //
    // App lista nézet készítése
    //
    @SuppressLint("QueryPermissionsNeeded")
    public void appListView() {
        char first = '\0';
        final ListView appTable = findViewById(R.id.allAppListTable);
        final var adapterallapp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appList){
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
                var tid = getTheme();
                if (appN != null) {
                    if (appN.isEmpty()) {
                        tvt.setText("");
                        tvt.setCompoundDrawables(null, null, null, null);
                    } else {
                        if (appN.length() == 1) {
                            tvt.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_secondary, tid));
                            tvt.setText(appN);
                            tvt.setCompoundDrawables(null, null, null, null);
                        } else {
                            tvt.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_on_primary, tid));
                            tvt.setText(appN);
                            ResolveInfo thisApp = allappList.get(position);
                            Drawable appI = thisApp.loadIcon(MainActivity.packageMan);
                            int ts = (int) tvt.getTextSize() + 25;
                            appI.setBounds(0, 0, ts, ts);
                            tvt.setCompoundDrawables(appI, null, null, null);
                            tvt.setCompoundDrawablePadding(30);
                            tvt.setPadding(10,10,10,10);
                        }
                    }
                }
                return row;
            }
        };

        appList.clear();
        appPackList.clear();
        allappList.clear();
        boolean firstapp = true;
        for (ResolveInfo app : MainActivity.allApplicationsList) {
            String appName = app.loadLabel(MainActivity.packageMan).toString();
            String pName = app.activityInfo.packageName;
            if (first != appName.charAt(0)) {
                first = appName.charAt(0);
                String s = String.valueOf(first);
                if (firstapp) {
                    firstapp = false;
                //} else {
                    //appList.add("");
                    //appPackList.add("");
                    //allappList.add(null);
                }
                appList.add(s);
                appPackList.add("");
                allappList.add(null);
            }
            allappList.add(app);
            appList.add(appName + " ");
            appPackList.add(pName);
        }

        appTable.setAdapter(adapterallapp);
        adapterallapp.notifyDataSetChanged();
        appListBackup.addAll(appList);
        allappListBackup.addAll(allappList);
        appPackListBackup.addAll(appPackList);

        appTable.setOnItemClickListener((parent, view, position, id) -> {
            //view.setBackgroundColor(getColor(com.google.android.material.R.color.design_default_color_primary));
            String selectedP= (String) (appTable.getItemAtPosition(position));
            //Log.d(DEBUG_TAG, selectedP);
            for (int i=0; i<appList.size(); i++) {
                String appp = appList.get(i);
                if (Objects.equals(appp, selectedP)) {
                    try {
                        appp = appPackList.get(i);
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(appp);
                        if (launchIntent != null) {
                            startActivity(launchIntent);
                            this.finish();
                        }
                    } catch (Exception e) {
                        systemMessage(getString(R.string.error_startapp));
                    }
                    //Log.d(DEBUG_TAG, R.string.starting_other_app + " " + appp);
                }
            }
        });


        SearchView sv = findViewById(R.id.allAppListSearch);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String nt = newText.toLowerCase(Locale.getDefault());
                appList.clear();
                allappList.clear();
                appPackList.clear();
                if (nt.isEmpty()) {
                    appList.addAll(appListBackup);
                    allappList.addAll(allappListBackup);
                    appPackList.addAll(appPackListBackup);
                } else {
                    for (ResolveInfo app : MainActivity.allApplicationsList) {
                        String appName = app.loadLabel(MainActivity.packageMan).toString();
                        String pName = app.activityInfo.packageName;
                        if (appName.toLowerCase().contains(nt)) {
                            appList.add(appName);
                            appPackList.add(pName);
                            allappList.add(app);
                        }
                    }
                }
                if (appList.isEmpty()){
                    appList.add("");
                }
                adapterallapp.notifyDataSetChanged();
                return true;
            }
        });

    }


    //
    // App lista kilépés
    //
    public void closeAppViewButton(View view) {
        this.finish();
    }

}