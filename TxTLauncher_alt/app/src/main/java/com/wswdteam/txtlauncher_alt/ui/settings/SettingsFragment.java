package com.wswdteam.txtlauncher_alt.ui.settings;

import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_BACKGROUND_IMAGE_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_HOME_ICON_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_SYS_ICON_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_URL_PRIVATEAI_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_URL_SEARCH_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_WEATHER_HTML;
import static com.wswdteam.txtlauncher_alt.MainActivity.backgroundImageOrig;
import static com.wswdteam.txtlauncher_alt.MainActivity.packageUpdateTime;
import static com.wswdteam.txtlauncher_alt.MainActivity.privateAIUrlOrig;
import static com.wswdteam.txtlauncher_alt.MainActivity.privateSearchUrlOrig;
import static com.wswdteam.txtlauncher_alt.ui.tools.ToolsFragment.queryWeatherHTMLOrig;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.wswdteam.txtlauncher_alt.AddAppActivity;
import com.wswdteam.txtlauncher_alt.AddFavAppActivity;
import com.wswdteam.txtlauncher_alt.HelpActivity;
import com.wswdteam.txtlauncher_alt.MainActivity;
import com.wswdteam.txtlauncher_alt.R;
import com.wswdteam.txtlauncher_alt.databinding.FragmentSettingsBinding;
import com.wswdteam.txtlauncher_alt.ui.tools.ToolsFragment;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //SettingsViewModel toolsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    //
    // Beállítások indítása
    //
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // kattintás figyelése
        TextView addapp = view.findViewById(R.id.addAppButton);
        addapp.setOnClickListener(this::openAddAppList);

        TextView addfavapp = view.findViewById(R.id.addFavAppButton);
        addfavapp.setOnClickListener(this::openAddFavAppList);

        Button reset = view.findViewById(R.id.buttonReset);
        reset.setOnClickListener(this::resetButton);

        Button reread = view.findViewById(R.id.buttonReadAppList);
        reread.setOnClickListener(this::rereadAppListButton);

        Button help = view.findViewById(R.id.buttonHelp);
        help.setOnClickListener(this::helpButton);

        // beállítások betöltése
        String val;
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch c1 = view.findViewById(R.id.sysIconCheck);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch c2 = view.findViewById(R.id.appIconCheck);
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
        val = MainActivity.sharedPreferences.getString(SETTINGS_SYS_ICON_TAG, "");
        if (!val.isEmpty()) {
            c1.setChecked(!val.equals("0"));
        }
        val = MainActivity.sharedPreferences.getString(SETTINGS_HOME_ICON_TAG, "");
        if (!val.isEmpty()) {
            c2.setChecked(!val.equals("0"));
        }

        EditText v1 = view.findViewById(R.id.editPrivateAI);
        EditText v2 = view.findViewById(R.id.editUrlSearch);
        EditText v3 = view.findViewById(R.id.editBackgroundImage);
        EditText v4 = view.findViewById(R.id.editWeatherHtml);

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
                settings.putString(SETTINGS_BACKGROUND_IMAGE_TAG, String.valueOf(s).trim());
                settings.apply();
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        v4.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                var settings = MainActivity.sharedPreferences.edit();
                settings.putString(SETTINGS_WEATHER_HTML, String.valueOf(s).trim());
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
        val = MainActivity.sharedPreferences.getString(SETTINGS_BACKGROUND_IMAGE_TAG, "");
        if (!val.isEmpty()) {
            v3.setText(val);
        } else {
            v3.setText(MainActivity.backgroundImage);
        }

        val = MainActivity.sharedPreferences.getString(SETTINGS_WEATHER_HTML, "");
        if (!val.isEmpty()) {
            v4.setText(val);
        } else {
            v4.setText(queryWeatherHTMLOrig);
        }

    }


    //
    // App választás indítása
    //
    public void openAddAppList(View view){
        Intent intent = new Intent(getActivity(), AddAppActivity.class);
        startActivity(intent);
    }


    //
    // Kedvenc app választás indítása
    //
    public void openAddFavAppList(View view){
        Intent intent = new Intent(getActivity(), AddFavAppActivity.class);
        startActivity(intent);
    }


    //
    // Beállítások visszaállítása
    //
    public void resetButton(View view) {
        EditText v1 = getActivity().findViewById(R.id.editPrivateAI);
        EditText v2 = getActivity().findViewById(R.id.editUrlSearch);
        EditText v3 = getActivity().findViewById(R.id.editBackgroundImage);
        EditText v4 = getActivity().findViewById(R.id.editWeatherHtml);
        v1.setText(privateAIUrlOrig);
        v2.setText(privateSearchUrlOrig);
        v3.setText(backgroundImageOrig);
        v4.setText(queryWeatherHTMLOrig);
    }


    //
    // App lista újraolvasása
    public void rereadAppListButton(View view) {
        packageUpdateTime = packageUpdateTime / 2;
        MainActivity.generateAppList();
    }


    //
    // App választás indítása
    //
    public void helpButton(View view){
        startActivity(new Intent(getActivity(), HelpActivity.class));
    }

}