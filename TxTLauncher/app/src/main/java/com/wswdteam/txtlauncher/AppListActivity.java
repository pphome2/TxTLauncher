package com.wswdteam.txtlauncher;

import static com.wswdteam.txtlauncher.MainActivity.adaptiveIconColor;
import static com.wswdteam.txtlauncher.MainActivity.allAppData;
import static com.wswdteam.txtlauncher.MainActivity.allApplicationsList;
import static com.wswdteam.txtlauncher.MainActivity.defaultBackGroundColor;
import static com.wswdteam.txtlauncher.MainActivity.defaultFontSize;
import static com.wswdteam.txtlauncher.MainActivity.defaultLetterColor;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSize;
import static com.wswdteam.txtlauncher.MainActivity.defaultTextColor;
import static com.wswdteam.txtlauncher.MainActivity.homeStartAppIcon;
import static com.wswdteam.txtlauncher.MainActivity.iconSize;
import static com.wswdteam.txtlauncher.MainActivity.syslog;
import static com.wswdteam.txtlauncher.MainActivity.systemMessage;
import static com.wswdteam.txtlauncher.MainActivity.textColorMode;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

//
// App lista előkészítése
//
public class AppListActivity extends AppCompatActivity {

    final ArrayList<String> allappList = new ArrayList<>();
    final ArrayList<String> appList = new ArrayList<>();
    final ArrayList<String> appPackList = new ArrayList<>();
    final ArrayList<String> letterList = new ArrayList<>();

    final ArrayList<String> allappListBackup = new ArrayList<>();
    final ArrayList<String> appListBackup = new ArrayList<>();
    final ArrayList<String> appPackListBackup = new ArrayList<>();



    @SuppressLint({"MissingInflatedId", "PrivateResource"})
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

                // teljes háttér színe
        getWindow().getDecorView().setBackgroundColor(defaultBackGroundColor);
        // vissza gomb szine
        ImageView imv = findViewById(R.id.quitAllAppListButton);
        imv.setColorFilter(defaultTextColor);

        @SuppressLint("UseCompatLoadingForDrawables") Drawable appI = getDrawable(R.drawable.arrow_back);
        if (appI != null) {
            appI.setBounds(0, 0, (int) defaultFontSize, (int) defaultFontSize);
            appI.setTint(defaultTextColor);
            //ImageView iv = findViewById(R.id.quitAllAppListButton);
            ViewGroup.LayoutParams layoutParams = imv.getLayoutParams();
            layoutParams.width = layoutParams.width + (int) defaultPlusFontSize;
            layoutParams.height = layoutParams.height + (int) defaultPlusFontSize;
            imv.setLayoutParams(layoutParams);
        }
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
        sv.setOnClickListener(v -> sv.setIconified(false));

        ListView scv = findViewById(R.id.allAppListTable);
        scv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    MainActivity.hideKeyboard(AppListActivity.this);
                }
            }
        });

        syslog(getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
    }


    //
    // App lista leállítása
    //
    @Override
    public void onStop() {
        super.onStop();
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
                if (appN != null) {
                    if (appN.isEmpty()) {
                        tvt.setText("");
                        tvt.setCompoundDrawables(null, null, null, null);
                    } else {
                        if (appN.length() == 1) {
                            tvt.setTextColor(defaultLetterColor);
                            tvt.setText(appN);
                            tvt.setCompoundDrawables(null, null, null, null);
                            // ! tvt.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultFontSize + defaultPlusFontSizeTitle);
                        } else {
                            if (textColorMode) {
                                tvt.setTextColor(adaptiveIconColor);
                            } else {
                                tvt.setTextColor(defaultTextColor);
                            }
                            tvt.setText(appN);
                            if (homeStartAppIcon) {
                                String appName;
                                for (int i = 0; i < allAppData.length; i++) {
                                    appName = allAppData[i][0];
                                    if ((appN.equals(appName))||(appN.equals(appName + " "))) {
                                        int padding = iconSize / 2;
                                        ResolveInfo info = allApplicationsList.get(i);
                                        Drawable appI = info.loadIcon(MainActivity.packageMan);
                                        Drawable ic = MainActivity.getDrawable(row, appI, iconSize);
                                        tvt.setCompoundDrawablesRelative(ic, null, null, null);
                                        tvt.setCompoundDrawablePadding(padding);
                                    }
                                }
                            }
                            // ! Ftvt.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultFontSize + defaultPlusFontSize);
                        }
                    }
                }
                return row;
            }

        };

        final ListView letterTable = findViewById(R.id.allLetterListTable);
        final var adapterletters = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, letterList){
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
                    if (!appN.isEmpty()) {
                        if (appN.length() == 1) {
                            tvt.setTextColor(defaultLetterColor);
                            tvt.setText(appN);
                            tvt.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                            tvt.setCompoundDrawablePadding(0);
                            ViewGroup.LayoutParams params = tvt.getLayoutParams();
                            int totalH = parent.getHeight();
                            if (totalH > 0) {
                                int itemC = getCount();
                                int goodItemHeight = (int) (48 * parent.getContext().getResources().getDisplayMetrics().density);
                                int maxItemFit = totalH / goodItemHeight;
                                if (itemC > maxItemFit) {
                                    params.height = totalH / itemC;
                                } else {
                                    params.height = goodItemHeight;
                                }
                            }
                            tvt.setLayoutParams(params);
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
        for (String[] allAppDatum : allAppData) {
            String appName = allAppDatum[0];
            String pName = allAppDatum[1];
            if (first != appName.charAt(0)) {
                first = appName.charAt(0);
                String s = String.valueOf(first);
                if (firstapp) {
                    firstapp = false;
                }
                letterList.add(s);
                appList.add(s);
                appPackList.add("");
                allappList.add(null);
            }
            allappList.add(allAppDatum[2]);
            appList.add(appName + " ");
            appPackList.add(pName);
        }

        letterTable.setAdapter(adapterletters);
        adapterletters.notifyDataSetChanged();
        appTable.setAdapter(adapterallapp);
        adapterallapp.notifyDataSetChanged();
        appListBackup.addAll(appList);
        allappListBackup.addAll(allappList);
        appPackListBackup.addAll(appPackList);

        appTable.setOnItemClickListener((parent, view, position, id) -> {
            String selectedL = (String) (appTable.getItemAtPosition(position));
            for (int i=0; i<allAppData.length; i++) {
                String appName;
                appName = allAppData[i][0];
                if ((selectedL.equals(appName))||(selectedL.equals(appName + " "))) {
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

        letterTable.setOnItemClickListener((parent, view, position, id) -> {
            String selectedL = (String) (letterTable.getItemAtPosition(position));
            ListView lv = findViewById(R.id.allAppListTable);
            var i = 0;
            while (i < appList.size()) {
                if (selectedL.equals(appList.get(i))) {
                    lv.setSelection(i);
                    i = appList.size();
                }
                i++;
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
                ListView lv = findViewById(R.id.allLetterListTable);
                if (nt.isEmpty()) {
                    lv.setVisibility(View.VISIBLE);
                    appList.addAll(appListBackup);
                    allappList.addAll(allappListBackup);
                    appPackList.addAll(appPackListBackup);
                } else {
                    lv.setVisibility(View.INVISIBLE);
                    for (String[] allAppDatum : allAppData) {
                        String appName = allAppDatum[0];
                        String pName = allAppDatum[1];
                        String aName = allAppDatum[2];
                        if (appName.toLowerCase().contains(nt)) {
                            appList.add(appName);
                            appPackList.add(pName);
                            allappList.add(aName);
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