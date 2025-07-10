package com.wswdteam.txtlauncher_alt.ui.tools;

import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_CITY;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_NOTE;
import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_WEATHER_HTML;
import static com.wswdteam.txtlauncher_alt.MainActivity.privateAIUrl;
import static com.wswdteam.txtlauncher_alt.MainActivity.systemMessage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.wswdteam.txtlauncher_alt.MainActivity;
import com.wswdteam.txtlauncher_alt.R;
import com.wswdteam.txtlauncher_alt.databinding.FragmentToolsBinding;


public class ToolsFragment extends Fragment {

    private FragmentToolsBinding binding;


    public static boolean firstLoadAI = true;
    public static boolean firstLoadWv = true;
    public static String savedCity = "";
    public static String cityCode = "@CITY@";
    public static String queryWeatherHTMLFrame1 = "<html><body style='background-color:black;'><center>" +
            "<div style='margin:auto;display:block;'>";
    public static String queryWeatherHTMLFrame2 = "</div></center></body></html>";
    public static String queryWeatherHTML = "<iframe src='https://beepulo.idokep.hu/futar/@CITY@' scrolling='no' style='width:300px;height:260px;border:none;'>";
    public static String queryWeatherHTMLOrig = "<iframe src='https://beepulo.idokep.hu/futar/@CITY@' scrolling='no' style='width:300px;height:260px;border:none;'>";
    //public static String defaultHTML = "<html><body></body></html>";



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //ToolsViewModel toolsViewModel = new ViewModelProvider(this).get(ToolsViewModel.class);
        binding = FragmentToolsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    //
    // Minden eszköz
    //
    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText et = getActivity().findViewById(R.id.widgetTextNote);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                var settings = MainActivity.sharedPreferences.edit();
                settings.putString(SETTINGS_NOTE, String.valueOf(s).trim());
                settings.apply();
            }
        });

        CalendarView cv = getActivity().findViewById(R.id.widgetCalendar);
        cv.setOnDateChangeListener((view2, year, month, dayOfMonth) -> {
            try {
                Intent cal = new Intent(Intent.ACTION_MAIN);
                cal.addCategory(Intent.CATEGORY_APP_CALENDAR);
                startActivity(cal);
                getActivity().finish();
            } catch (Exception e) {
                systemMessage(getString(R.string.error_startapp));
            }
        });

        SearchView sv = getActivity().findViewById(R.id.widgetSearch);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                WebView wv = getActivity().findViewById(R.id.widgetPrivateAI);
                wv.loadUrl(MainActivity.privateSearchUrl + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        SearchView csv = getActivity().findViewById(R.id.widgetCitySearch);
        csv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                var settings = MainActivity.sharedPreferences.edit();
                settings.putString(SETTINGS_CITY, query);
                settings.apply();
                WebView wv = getActivity().findViewById(R.id.widgetWeather);
                String qu = queryWeatherHTML;
                qu = qu.replaceFirst(cityCode, query);
                String data = queryWeatherHTMLFrame1 + qu + queryWeatherHTMLFrame2;
                wv.loadData(data, "text/html; charset=utf-8", "UTF-8");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        WebView wv = getActivity().findViewById(R.id.widgetPrivateAI);
        wv.setOnTouchListener((view3, motionEvent) -> {
            if (firstLoadAI) {
                firstLoadAI = false;
                wv.loadUrl(privateAIUrl);
                WebSettings webSettings = wv.getSettings();
                webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                webSettings.setJavaScriptEnabled(true);
            }
            return false;
        });

        WebView wv2 = getActivity().findViewById(R.id.widgetWeather);
        wv2.setOnTouchListener((view4, motionEvent) -> {
            if (firstLoadWv) {
                firstLoadWv = false;
                String qu = queryWeatherHTML;
                qu = qu.replaceFirst(cityCode, savedCity);
                String data = queryWeatherHTMLFrame1 + qu + queryWeatherHTMLFrame2;
                wv2.loadData(data, "text/html; charset=utf-8", "UTF-8");
                WebSettings webSettings = wv2.getSettings();
                webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                webSettings.setJavaScriptEnabled(true);
            }
            return false;
        });

        String val = MainActivity.sharedPreferences.getString(SETTINGS_NOTE, "");
        if (!val.isEmpty()) {
            EditText tv = getActivity().findViewById(R.id.widgetTextNote);
            tv.setText(val);
        }
        val = MainActivity.sharedPreferences.getString(SETTINGS_CITY, "");
        if (!val.isEmpty()) {
            SearchView svi = getActivity().findViewById(R.id.widgetCitySearch);
            svi.setQuery(val, false);
            savedCity = val;
        }
        val = MainActivity.sharedPreferences.getString(SETTINGS_WEATHER_HTML, "");
        if (!val.isEmpty()) {
            queryWeatherHTML = val;
        }

        firstLoadAI = true;
        firstLoadWv = true;
    }


    //
    // Widget AI indítása
    //
    @SuppressLint("SetJavaScriptEnabled")
    public void startAI(View view) {
        WebView wv = getActivity().findViewById(R.id.widgetPrivateAI);
        wv.loadUrl(privateAIUrl);
        WebSettings webSettings = wv.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
    }


    //
    // Widget térkép mutatása
    //
    public void openWidgetMap(View view) {
        try {
            Uri mapUri = Uri.parse("geo:0,0");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.app.");
            startActivity(mapIntent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }


}