package com.wswdteam.applist;

import static android.view.View.GONE;
import static com.wswdteam.applist.AppListApp.FAVORITE_LETTER;
import static com.wswdteam.applist.AppListApp.FAV_APP_NUM;
import static com.wswdteam.applist.AppListApp.SETTINGS_FAV_APP_TAG;
import static com.wswdteam.applist.AppListApp.adaptiveIcon;
import static com.wswdteam.applist.AppListApp.adaptiveIconColor;
import static com.wswdteam.applist.AppListApp.allAppData;
import static com.wswdteam.applist.AppListApp.allApplicationsList;
import static com.wswdteam.applist.AppListApp.defaultBackGroundColor;
import static com.wswdteam.applist.AppListApp.defaultFontSize;
import static com.wswdteam.applist.AppListApp.defaultIconColor;
import static com.wswdteam.applist.AppListApp.defaultLetterColor;
import static com.wswdteam.applist.AppListApp.defaultPlusFontSize;
import static com.wswdteam.applist.AppListApp.defaultTextColor;
import static com.wswdteam.applist.AppListApp.generateAppList;
import static com.wswdteam.applist.AppListApp.getSettingsMain;
import static com.wswdteam.applist.AppListApp.runAndQuit;
import static com.wswdteam.applist.AppListApp.setColors;
import static com.wswdteam.applist.AppListApp.hideKeyboard;
import static com.wswdteam.applist.AppListApp.iconPadding;
import static com.wswdteam.applist.AppListApp.iconSize;
import static com.wswdteam.applist.AppListApp.packageMan;
import static com.wswdteam.applist.AppListApp.sharedPreferences;
import static com.wswdteam.applist.AppListApp.syslog;
import static com.wswdteam.applist.AppListApp.systemMessage;
import static com.wswdteam.applist.AppListApp.textColorMode;
import static com.wswdteam.applist.AppListApp.startedAndroidApp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
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
import androidx.core.content.ContextCompat;
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
    int defaultIconWidth = 0;



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

        prepare();
    }


    //
    // App lista indítása
    //
    @Override
    protected void onStart() {
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
                    hideKeyboard(AppListActivity.this);
                }
            }
        });

        prepare();
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
    // előkészítés
    //
    public void prepare() {
        // első induláskor betöltés
        generateAppList();
        getSettingsMain(this);
        setColors(this);

        // teljes háttér színe
        getWindow().getDecorView().setBackgroundColor(defaultBackGroundColor);

        TextView tv = findViewById(R.id.invisibleText);
        defaultFontSize = tv.getTextSize();
        tv.setVisibility(GONE);

        // vissza gomb szine
        ImageView imv;
        imv = findViewById(R.id.quitAllAppListButton);
        imv.setColorFilter(defaultTextColor);

        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable appI = getDrawable(R.drawable.arrow_back);
        if (appI != null) {
            appI.setBounds(0, 0, (int) defaultFontSize, (int) defaultFontSize);
            appI.setTint(defaultTextColor);
            ViewGroup.LayoutParams layoutParams = imv.getLayoutParams();
            if (defaultIconWidth == 0) {
                defaultIconWidth = layoutParams.width + (int) defaultPlusFontSize;
            }
            layoutParams.width = defaultIconWidth + (int) defaultFontSize;
            layoutParams.height = defaultIconWidth + (int) defaultFontSize;
            imv.setLayoutParams(layoutParams);
        }

        // beállítások gomb szine
        imv = findViewById(R.id.settingsButton);
        imv.setColorFilter(defaultTextColor);

        appI = getDrawable(R.drawable.settings);
        if (appI != null) {
            appI.setBounds(0, 0, (int) defaultFontSize, (int) defaultFontSize);
            appI.setTint(defaultTextColor);
            ViewGroup.LayoutParams layoutParams = imv.getLayoutParams();
            if (defaultIconWidth == 0) {
                defaultIconWidth = layoutParams.width + (int) defaultPlusFontSize;
            }
            layoutParams.width = defaultIconWidth + (int) defaultFontSize;
            layoutParams.height = defaultIconWidth + (int) defaultFontSize;
            imv.setLayoutParams(layoutParams);
        }

        // teljes háttér színe
        ImageView iw = findViewById(R.id.settingsButton);
        if (adaptiveIcon || textColorMode) {
            //iw.setColorFilter(adaptiveIconColor);
        //} else {
            iw.setColorFilter(defaultIconColor);
        }
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
                            if (appN.equals(FAVORITE_LETTER)) {
                                tvt.setText(R.string.app_favorite);
                            } else {
                                tvt.setText(appN);
                            }
                            tvt.setCompoundDrawables(null, null, null, null);
                        } else {
                            if (appN.equals(FAVORITE_LETTER)) {
                                tvt.setTextColor(defaultLetterColor);
                                tvt.setText(appN+"W");
                                tvt.setCompoundDrawables(null, null, null, null);
                            } else {
                                if (textColorMode) {
                                    tvt.setTextColor(adaptiveIconColor);
                                } else {
                                    tvt.setTextColor(defaultTextColor);
                                }
                                tvt.setText(appN);
                                String appName;
                                for (int i = 0; i < allAppData.length; i++) {
                                    appName = allAppData[i][0];
                                    if ((appN.equals(appName)) || (appN.equals(appName + " "))) {
                                        int padding = iconSize / 2;
                                        ResolveInfo info = allApplicationsList.get(i);
                                        Drawable appI = info.loadIcon(packageMan);
                                        Drawable ic = getDrawable(row, appI, iconSize);
                                        tvt.setCompoundDrawablesRelative(ic, null, null, null);
                                        tvt.setCompoundDrawablePadding(padding);
                                    }
                                }
                            }
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
        letterList.clear();

        // kedvencek keresése
        letterList.add(FAVORITE_LETTER);
        appList.add(FAVORITE_LETTER);
        appPackList.add("");
        allappList.add(null);
        String tag;
        String val;
        for (var i = 0; i < FAV_APP_NUM; i++) {
            tag = SETTINGS_FAV_APP_TAG + i;
            val = sharedPreferences.getString(tag, "");
            if (!val.isEmpty()) {
                for (String[] allAppDatum : allAppData) {
                    String fappName = allAppDatum[0];
                    String pName = allAppDatum[1];
                    if (fappName.equals(val)) {
                        allappList.add(allAppDatum[2]);
                        appList.add(fappName + " ");
                        appPackList.add(pName);
                    }
                }
            }
        }

        // minden alkalmazás
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
                    if (runAndQuit) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    } else {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    try {
                        startActivity(intent);
                        startedAndroidApp = true;
                        if (runAndQuit) {
                            this.finishAndRemoveTask();
                        } else {
                            this.finish();
                        }
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
            if (selectedL.equals(FAVORITE_LETTER)) {
                lv.setSelection(0);
            } else {
                var i = 0;
                while (i < appList.size()) {
                    if (selectedL.equals(appList.get(i))) {
                        lv.setSelection(i);
                        i = appList.size();
                    }
                    i++;
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
    static Drawable getDrawable(View view, Drawable appI, int iconSize) {
        Drawable iconToDisplay;
        if (adaptiveIcon && appI instanceof AdaptiveIconDrawable) {
            AdaptiveIconDrawable adaptI = (AdaptiveIconDrawable) appI;
            Drawable monoIcon = adaptI.getMonochrome();
            if (monoIcon != null) {
                monoIcon.mutate();
                monoIcon.setTint(adaptiveIconColor);
                InsetDrawable insetIcon = new InsetDrawable(monoIcon, iconPadding);
                LayerDrawable layer = new LayerDrawable(new Drawable[]{insetIcon});
                layer.setLayerSize(0, iconSize, iconSize);
                iconToDisplay = layer;
            } else {
                iconToDisplay = getDefaultIcon(view, iconSize);
            }
        } else {
            if (adaptiveIcon) {
                iconToDisplay = getDefaultIcon(view, iconSize);
            } else {
                LayerDrawable layer = new LayerDrawable(new Drawable[]{appI});
                layer.setLayerSize(0, iconSize, iconSize);
                iconToDisplay = layer;
            }
        }
        assert iconToDisplay != null;
        iconToDisplay.setBounds(0, 0, iconSize, iconSize);
        return iconToDisplay;
    }


    // Segédfüggvény a kód tisztaságáért
    private static Drawable getDefaultIcon(View view, int iconSize) {
        Drawable d = ContextCompat.getDrawable(view.getContext(), R.drawable.app);
        if (d != null) {
            d.mutate();
            d.setTint(adaptiveIconColor);
            d.setBounds(0, 0, iconSize, iconSize);
            LayerDrawable layer = new LayerDrawable(new Drawable[]{d});
            layer.setLayerSize(0, iconSize, iconSize);
            return layer;
        }
        return null;
    }



    //
    // beállítások
    //
    public void startSettingsActivity(View v) {
        startActivity(new Intent(this, SettingsActivity.class));
    }



    //
    // App lista kilépés
    //
    public void closeAppViewButton(View view) {
        this.finish();
    }

}