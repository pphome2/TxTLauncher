package com.wswdteam.txtlauncher;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_ADAPTIVE_ICON_COLOR_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_ADAPTIVE_ICON_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_BACKGROUND_IMAGE;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_BACKGROUND_IMAGE_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_HOME_ICON_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_ONE_COLUMN_FAVORITES_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_SYS_ICON_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_URL_PRIVATEAI_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_URL_SEARCH_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_WEATHER_URL;
import static com.wswdteam.txtlauncher.MainActivity.backgroundImageOrig;
import static com.wswdteam.txtlauncher.MainActivity.defaultFontSize;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.MainActivity.packageUpdateTime;
import static com.wswdteam.txtlauncher.MainActivity.privateAIUrlOrig;
import static com.wswdteam.txtlauncher.MainActivity.privateSearchUrlOrig;
import static com.wswdteam.txtlauncher.MainActivity.sharedPreferences;
import static com.wswdteam.txtlauncher.MainActivity.syslog;
import static com.wswdteam.txtlauncher.MainActivity.systemMessage;
import static com.wswdteam.txtlauncher.MainActivity.weatherUrlOrig;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;


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

        TextView tv = findViewById(R.id.settingsViewTitle);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, MainActivity.defaultFontSize + defaultPlusFontSizeTitle);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable appI = getDrawable(R.drawable.arrow_back);
        if (appI != null) {
            int ts = (int) defaultFontSize + (int) defaultPlusFontSizeTitle;
            appI.setBounds(0, 0, ts, ts);
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
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch c1 = findViewById(R.id.sysIconCheck);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch c2 = findViewById(R.id.appIconCheck);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch c3 = findViewById(R.id.adaptiveIcon);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch c4 = findViewById(R.id.onecolFavorites);
        c1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            var settings = MainActivity.sharedPreferences.edit();
            if (isChecked) {
                settings.putString(SETTINGS_SYS_ICON_TAG, "1");
            } else {
                settings.putString(SETTINGS_SYS_ICON_TAG, "0");
            }
            settings.apply();
        });
        c2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            var settings = MainActivity.sharedPreferences.edit();
            if (isChecked) {
                settings.putString(SETTINGS_HOME_ICON_TAG, "1");
            } else {
                settings.putString(SETTINGS_HOME_ICON_TAG, "0");
            }
            settings.apply();
        });
        c3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            var settings = MainActivity.sharedPreferences.edit();
            if (isChecked) {
                settings.putString(SETTINGS_ADAPTIVE_ICON_TAG, "1");
            } else {
                settings.putString(SETTINGS_ADAPTIVE_ICON_TAG, "0");
            }
            settings.apply();
        });
        c4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            var settings = MainActivity.sharedPreferences.edit();
            if (isChecked) {
                settings.putString(SETTINGS_ONE_COLUMN_FAVORITES_TAG, "1");
            } else {
                settings.putString(SETTINGS_ONE_COLUMN_FAVORITES_TAG, "0");
            }
            settings.apply();
        });
        val = MainActivity.sharedPreferences.getString(SETTINGS_SYS_ICON_TAG, "");
        if (!val.isEmpty()) {
            c1.setChecked(!val.equals("0"));
        }
        val = MainActivity.sharedPreferences.getString(SETTINGS_HOME_ICON_TAG, "");
        if (!val.isEmpty()) {
            c2.setChecked(!val.equals("0"));
        }
        val = MainActivity.sharedPreferences.getString(SETTINGS_ADAPTIVE_ICON_TAG, "");
        if (!val.isEmpty()) {
            c3.setChecked(!val.equals("0"));
        }
        val = MainActivity.sharedPreferences.getString(SETTINGS_ONE_COLUMN_FAVORITES_TAG, "");
        if (!val.isEmpty()) {
            c4.setChecked(!val.equals("0"));
        }
        int buttonId;
        buttonId = MainActivity.sharedPreferences.getInt(SETTINGS_ADAPTIVE_ICON_COLOR_TAG, Integer.parseInt("0"));

        EditText v1 = findViewById(R.id.editPrivateAI);
        EditText v2 = findViewById(R.id.editUrlSearch);
        EditText v3 = findViewById(R.id.editWeatherLink);
        EditText v4 = findViewById(R.id.editBackgroundImage);

        v1.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                var settings = MainActivity.sharedPreferences.edit();
                settings.putString(SETTINGS_URL_PRIVATEAI_TAG, String.valueOf(s).trim());
                settings.apply();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        v2.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                var settings = MainActivity.sharedPreferences.edit();
                settings.putString(SETTINGS_URL_SEARCH_TAG, String.valueOf(s).trim());
                settings.apply();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        v3.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                var settings = MainActivity.sharedPreferences.edit();
                settings.putString(SETTINGS_WEATHER_URL, String.valueOf(s).trim());
                settings.apply();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        v4.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                var settings = MainActivity.sharedPreferences.edit();
                settings.putString(SETTINGS_BACKGROUND_IMAGE_TAG, String.valueOf(s).trim());
                settings.apply();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        val = MainActivity.sharedPreferences.getString(SETTINGS_URL_PRIVATEAI_TAG, "");
        if (!val.isEmpty()) {
            v1.setText(val);
        } else {
            v1.setText(MainActivity.privateAIUrl);
        }
        val = MainActivity.sharedPreferences.getString(SETTINGS_URL_SEARCH_TAG, "");
        if (!val.isEmpty()) {
            v2.setText(val);
        } else {
            v2.setText(MainActivity.privateSearchUrl);
        }
        val = MainActivity.sharedPreferences.getString(SETTINGS_WEATHER_URL, "");
        if (!val.isEmpty()) {
            v3.setText(val);
        } else {
            v3.setText(MainActivity.weatherUrl);
        }

        if (buttonId > 0) {
            RadioButton rb = findViewById(buttonId);
            rb.setText("✔");
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

        syslog(getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
    }


    //
    // Beállítások leállítása
    //
    public void onStop() {
        super.onStop();
    }


    //
    // App választás indítása
    //
    public void openAddAppList(View view){
        startActivity(new Intent(SettingsActivity.this, AddAppActivity.class));
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
        EditText v1 = findViewById(R.id.editPrivateAI);
        EditText v2 = findViewById(R.id.editUrlSearch);
        EditText v3 = findViewById(R.id.editWeatherLink);
        EditText v4 = findViewById(R.id.editBackgroundImage);
        v1.setText(privateAIUrlOrig);
        v2.setText(privateSearchUrlOrig);
        v3.setText(weatherUrlOrig);
        v4.setText(backgroundImageOrig);
    }



    //
    // Beállítások visszaállítása
    //
    public void resetButtonBackground(View view) {
        var settings = MainActivity.sharedPreferences.edit();
        settings.putString(SETTINGS_BACKGROUND_IMAGE, "");
        settings.apply();
        EditText v4 = findViewById(R.id.editBackgroundImage);
        v4.setText(backgroundImageOrig);
        File imgFile = new File(MainActivity.savedBackgroundImage);
        if (imgFile.exists()) {
            if (!imgFile.delete()) {
                systemMessage(getString(R.string.background_file_not_found));
            }
        }
    }


    //
    // App lista újraolvasása
    public void rereadAppListButton(View view) {
        packageUpdateTime = packageUpdateTime / 2;
        MainActivity.generateAppList();
        this.finish();
    }


    //
    // App választás indítása
    //
    public void helpButton(View view){
        startActivity(new Intent(SettingsActivity.this, HelpActivity.class));
    }


    //
    // Beállítások nézet bezárása
    //
    public void closeSettingsButton(View view) {
        this.finish();
    }

}


