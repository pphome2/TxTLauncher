package com.wswdteam.txtlauncher;

import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_ADAPTIVE_ICON_COLOR_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_APP_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_HOME_ICON_TAG;
import static com.wswdteam.txtlauncher.MainActivity.adaptiveIcon;
import static com.wswdteam.txtlauncher.MainActivity.adaptiveIconColor;
import static com.wswdteam.txtlauncher.MainActivity.allAppData;
import static com.wswdteam.txtlauncher.MainActivity.defaultFontSize;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.MainActivity.homeAppNum;

import static java.util.Collections.sort;

import android.annotation.SuppressLint;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle; import android.util.TypedValue;
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
// App választás főképernyőre előkészítés
//
public class AddAppActivity extends AppCompatActivity {
    final ArrayList<String> appList = new ArrayList<>();
    final ArrayList<String> selApp = new ArrayList<>();
    boolean showicons = false;
    public int selectedAppNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_app);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.widgetLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tv = findViewById(R.id.addappTitle);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, MainActivity.defaultFontSize + defaultPlusFontSizeTitle);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable appI = getDrawable(R.drawable.arrow_back);
        if (appI != null) {
            int ts = (int) defaultFontSize + (int) defaultPlusFontSizeTitle;
            appI.setBounds(0, 0, ts, ts);
            tv.setCompoundDrawables(appI, null, null, null);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setCompoundDrawables(appI, null, null, null);
        }

        String val;
        val = MainActivity.sharedPreferences.getString(SETTINGS_HOME_ICON_TAG, "");
        if (!val.isEmpty()) {
            showicons = !val.equals("0");
        }

        int buttonId;
        buttonId = MainActivity.sharedPreferences.getInt(SETTINGS_ADAPTIVE_ICON_COLOR_TAG, Integer.parseInt("0"));
        if (buttonId == R.id.btnRed) { adaptiveIconColor = ContextCompat.getColor(this, R.color.red); }
        if (buttonId == R.id.btnWhite) { adaptiveIconColor = ContextCompat.getColor(this, R.color.white); }
        if (buttonId == R.id.btnBlack) { adaptiveIconColor = ContextCompat.getColor(this, R.color.black); }
        if (buttonId == R.id.btnGray) { adaptiveIconColor = ContextCompat.getColor(this, R.color.gray); }
        if (buttonId == R.id.btnBlue) { adaptiveIconColor = ContextCompat.getColor(this, R.color.blue); }
        if (buttonId == R.id.btnGreen) { adaptiveIconColor = ContextCompat.getColor(this, R.color.green); }
        if (adaptiveIconColor == 0) { adaptiveIconColor = ContextCompat.getColor(this, android.R.color.system_accent1_400); }

    }


    //
    // App választás főképernyőre előkészítés
    //
    @Override
    public void onStart() {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.enter_from_right, R.anim.exit_to_left);
        //overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.enter_from_left, R.anim.exit_to_right);
        super.onStart();

        selApp.clear();
        String tag;
        String val;
        for (var i=0; i<homeAppNum; i++) {
            tag = SETTINGS_APP_TAG + i;
            val = MainActivity.sharedPreferences.getString(tag, "");
            if (!val.isEmpty()) {
                selApp.add(val);
            }
        }
        appListSelect();
        //Log.d(DEBUG_TAG, getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
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
        //Log.d(DEBUG_TAG, getString(R.string.stopped_activty) + ": "+ this.getClass().getSimpleName());
    }


    //
    // App választás lista készítése
    //
    @SuppressLint("QueryPermissionsNeeded")
    public void appListSelect() {
        final ListView appTable = findViewById(R.id.appListHomeSelect);
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
                if ((appN != null) && (!appN.isEmpty())) {
                    tvt.setText(appN);
                    if (showicons) {
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
                        iconToDisplay = appI;
                    }
                } else {
                    iconToDisplay = appI;
                }
                iconToDisplay.setBounds(0, 0, iconSize, iconSize);
                return iconToDisplay;
            }

        };

        for (String[] allAppDatum : allAppData) {
            String appName = allAppDatum[0];
            appList.add(appName);
            if (selApp.contains(appName)) {
                selectedAppNum++;
                TextView tv = findViewById(R.id.addappInfo);
                String st = homeAppNum + " / " + selectedAppNum;
                tv.setText(st);
            }
        }

        appTable.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        appTable.setOnItemClickListener((parent, view, position, id) -> {
            var del = false;
            String pname;
            String iname = parent.getItemAtPosition(position).toString();
            int pos = 0;
            for (int en = 0; en < selApp.size(); en++) {
                pname = selApp.get(en);
                if (pname.equals(iname)) {
                    pos = en;
                    del = true;
                }
            }
            if (del) {
                selApp.remove(pos);
                view.setBackgroundColor(MainActivity.defaultBackGroundColor);
                selectedAppNum--;
            } else {
                selApp.add(iname);
                view.setBackgroundColor(MainActivity.defaultSelectColor);
                selectedAppNum++;
            }
            TextView tv = findViewById(R.id.addappInfo);
            String st1 = homeAppNum + " / " + selectedAppNum;
            tv.setText(st1);
            // - String selectedP = (String) (appTable.getItemAtPosition(position));
            // - Log.d(DEBUG_TAG, selectedP);
        });
    }


    //
    // App választás bezárása
    //
    public void closeAddAppButton(View view) {
        this.finish();
    }

    public void closeAppViewButton(View view) {
        this.finish();
    }
}
