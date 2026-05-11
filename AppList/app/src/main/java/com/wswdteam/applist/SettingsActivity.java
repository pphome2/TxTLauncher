package com.wswdteam.applist;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
import static com.wswdteam.applist.AppListApp.SETTINGS_ADAPTIVE_ICON_COLOR_TAG;
import static com.wswdteam.applist.AppListApp.SETTINGS_ADAPTIVE_ICON_TAG;
import static com.wswdteam.applist.AppListApp.SETTINGS_DARK_MODE_TAG;
import static com.wswdteam.applist.AppListApp.SETTINGS_RUN_AND_QUIT_TAG;
import static com.wswdteam.applist.AppListApp.SETTINGS_TEXT_COLOR_MODE_TAG;
import static com.wswdteam.applist.AppListApp.defaultBackGroundColor;
import static com.wswdteam.applist.AppListApp.defaultFontSize;
import static com.wswdteam.applist.AppListApp.defaultPlusFontSizeTitle;
import static com.wswdteam.applist.AppListApp.defaultTextColor;
import static com.wswdteam.applist.AppListApp.sharedPreferences;
import static com.wswdteam.applist.AppListApp.syslog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


//
// Beállítások nézet
//
public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settingsLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);

        // teljes háttér színe
        getWindow().getDecorView().setBackgroundColor(defaultBackGroundColor);

        TextView tv = findViewById(R.id.settingsViewTitle);
        tv.setTextColor(defaultTextColor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, AppListApp.defaultFontSize + defaultPlusFontSizeTitle);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable appI = getDrawable(R.drawable.arrow_back);
        if (appI != null) {
            int ts = (int) defaultFontSize + (int) defaultPlusFontSizeTitle;
            appI.setBounds(0, 0, ts, ts);
            appI.setTint(defaultTextColor);
            tv.setCompoundDrawables(appI, null, null, null);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setCompoundDrawables(appI, null, null, null);
        }
    }


    //
    // Beállítások indítása
    //
    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    public void onStart() {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.enter_from_top, R.anim.exit_to_bottom);
        //overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.enter_from_left, R.anim.exit_to_right);
        super.onStart();

        String val;
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch c1 = findViewById(R.id.runAndQuit);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch c2 = findViewById(R.id.adaptiveIcon);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch c3 = findViewById(R.id.textColorMode);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch c4 = findViewById(R.id.darkMode);

        c1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            var settings = AppListApp.sharedPreferences.edit();
            if (isChecked) {
                settings.putString(SETTINGS_RUN_AND_QUIT_TAG, "1");
            } else {
                settings.putString(SETTINGS_RUN_AND_QUIT_TAG, "0");
            }
            settings.apply();
        });
        c2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            var settings = AppListApp.sharedPreferences.edit();
            if (isChecked) {
                settings.putString(SETTINGS_ADAPTIVE_ICON_TAG, "1");
            } else {
                settings.putString(SETTINGS_ADAPTIVE_ICON_TAG, "0");
            }
            settings.apply();
        });
        c3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            var settings = AppListApp.sharedPreferences.edit();
            if (isChecked) {
                settings.putString(SETTINGS_TEXT_COLOR_MODE_TAG, "1");
            } else {
                settings.putString(SETTINGS_TEXT_COLOR_MODE_TAG, "0");
            }
            settings.apply();
        });
        c4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            var settings = AppListApp.sharedPreferences.edit();
            if (isChecked) {
                settings.putString(SETTINGS_DARK_MODE_TAG, "1");
            } else {
                settings.putString(SETTINGS_DARK_MODE_TAG, "0");
            }
            settings.apply();
        });
        val = AppListApp.sharedPreferences.getString(SETTINGS_RUN_AND_QUIT_TAG, "");
        if (!val.isEmpty()) {
            c1.setChecked(!val.equals("0"));
        }
        val = AppListApp.sharedPreferences.getString(SETTINGS_ADAPTIVE_ICON_TAG, "");
        if (!val.isEmpty()) {
            c2.setChecked(!val.equals("0"));
        }
        val = AppListApp.sharedPreferences.getString(SETTINGS_TEXT_COLOR_MODE_TAG, "");
        if (!val.isEmpty()) {
            c3.setChecked(!val.equals("0"));
        }
        val = AppListApp.sharedPreferences.getString(SETTINGS_DARK_MODE_TAG, "");
        if (!val.isEmpty()) {
            c4.setChecked(!val.equals("0"));
        }

        int buttonId;
        buttonId = AppListApp.sharedPreferences.getInt(SETTINGS_ADAPTIVE_ICON_COLOR_TAG, Integer.parseInt("0"));

        //EditText v1 = findViewById(R.id.editPrivateAI);

        //v1.addTextChangedListener(new TextWatcher() {
        //    public void afterTextChanged(Editable s) {
        //        var settings = AppListApp.sharedPreferences.edit();
        //        settings.putString(SETTINGS_URL_PRIVATEAI_TAG, String.valueOf(s).trim());
        //        settings.apply();
        //    }
        //    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        //    public void onTextChanged(CharSequence s, int start, int before, int count) {}
        //});

        //val = AppListApp.sharedPreferences.getString(SETTINGS_URL_PRIVATEAI_TAG, "");
        //if (!val.isEmpty()) {
        //    v1.setText(val);
        //} else {
        //    v1.setText(AppListApp.privateAIUrl);
        //}

        if (buttonId > 0) {
            RadioButton rb = findViewById(buttonId);
            if (rb != null) {
                rb.setText("✔");
            }
        }
        RadioGroup colorGroup = findViewById(R.id.colorRadioGroup);
        colorGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < group.getChildCount(); i++) {
                View view = group.getChildAt(i);
                if (view instanceof RadioButton) {
                    ((RadioButton) view).setText("");
                }
            }
            RadioButton selectedBtn = findViewById(checkedId);
            selectedBtn.setText("✔");
            sharedPreferences.edit().putInt(SETTINGS_ADAPTIVE_ICON_COLOR_TAG, checkedId).apply();
        });

        ScrollView sv = findViewById(R.id.settingsScrollFrame);
        sv.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (Math.abs(scrollY - oldScrollY) > 1) {
                AppListApp.hideKeyboard(this);
            }
        });


        syslog(getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
    }


    //
    // Beállítások leállítása
    //
    public void onStop() {
        super.onStop();
    }



    //
    // Kedvenc app választás indítása
    //
    public void openAddFavAppList(View view){
        startActivity(new Intent(SettingsActivity.this, FavoritesAddAppActivity.class));
    }


    //
    // Beállítások visszaállítása
    //
    public void resetButton(View view) {
        //EditText v1 = findViewById(R.id.editPrivateAI);
        //v1.setText(privateAIUrlOrig);
        //systemMessage(getString(R.string.settings_button_reset));
    }



    //
    // Beállítások nézet bezárása
    //
    public void closeSettingsButton(View view) {
        this.finish();
    }

}


