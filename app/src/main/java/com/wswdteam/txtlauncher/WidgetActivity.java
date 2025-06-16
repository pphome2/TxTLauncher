package com.wswdteam.txtlauncher;

import static com.wswdteam.txtlauncher.MainActivity.DEBUG_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_CITY;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_NOTE;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_WEATHER_HTML1;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_WEATHER_HTML2;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.MainActivity.privateAIUrl;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


//
// Widget nézet létrehozása
//
public class WidgetActivity extends AppCompatActivity {

    public static boolean firstLoadAI = true;
    public static boolean firstLoadWv = true;
    public static String savedCity = "";
    public static String queryWeatherHTMLFrame1 = "<html><body style='background-color:black;'><center>" +
            "<div style='margin:auto;display:block;'>";
    public static String queryWeatherHTMLFrame2 = "</div></center></body></html>";
    public static String queryWeatherHTML1 = "<iframe src='https://beepulo.idokep.hu/futar/";
    public static String queryWeatherHTML2 = "' scrolling='no' style='width:300px;height:260px;border:none;'>";
    public static String queryWeatherHTML1Orig = "<iframe src='https://beepulo.idokep.hu/futar/";
    public static String queryWeatherHTML2Orig = "' scrolling='no' style='width:300px;height:260px;border:none;'>";

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_widget);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.widgetLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tv = findViewById(R.id.widgetTitle);
        tv.setTextSize(tv.getTextSize() + defaultPlusFontSizeTitle);
        int ts = (int) tv.getTextSize();
        @SuppressLint("UseCompatLoadingForDrawables") Drawable appI = getDrawable(R.drawable.arrow_back);
        if (appI != null) {
            appI.setBounds(0, 0, ts, ts);
            tv.setCompoundDrawables(appI, null, null, null);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setCompoundDrawables(appI, null, null, null);
        }

        SearchView sv = findViewById(R.id.widgetSearch);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                WebView wv = findViewById(R.id.widgetPrivateAI);
                wv.loadUrl(MainActivity.privateSearchUrl + query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        SearchView csv = findViewById(R.id.widgetCitySearch);
        csv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                var settings = MainActivity.sharedPreferences.edit();
                settings.putString(SETTINGS_CITY, query);
                settings.apply();
                WebView wv = findViewById(R.id.widgetWeather);
                String data = queryWeatherHTMLFrame1 + queryWeatherHTML1 + query + queryWeatherHTML2 + queryWeatherHTMLFrame2;
                wv.loadData(data, "text/html; charset=utf-8", "UTF-8");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        WebView wv = findViewById(R.id.widgetPrivateAI);
        wv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (firstLoadAI) {
                    firstLoadAI = false;
                    wv.loadUrl(privateAIUrl);
                    WebSettings webSettings = wv.getSettings();
                    webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                    webSettings.setJavaScriptEnabled(true);
                }
                return false;
            }
        });

        WebView wv2 = findViewById(R.id.widgetWeather);
        wv2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (firstLoadWv) {
                    firstLoadWv = false;
                    String data = queryWeatherHTMLFrame1 + queryWeatherHTML1 + savedCity + queryWeatherHTML2 + queryWeatherHTMLFrame2;
                    wv2.loadData(data, "text/html; charset=utf-8", "UTF-8");
                    WebSettings webSettings = wv2.getSettings();
                    webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                    webSettings.setJavaScriptEnabled(true);
                }
                return false;
            }
        });
    }


    //
    // Widget nézet indítása
    //
    @Override
    public void onStart(){
        super.onStart();
        String val = MainActivity.sharedPreferences.getString(SETTINGS_NOTE, "");
        if (!val.isEmpty()) {
            EditText tv = findViewById(R.id.widgetTextNote);
            tv.setText(val);
        }
        val = MainActivity.sharedPreferences.getString(SETTINGS_CITY, "");
        if (!val.isEmpty()) {
            SearchView sv = findViewById(R.id.widgetCitySearch);
            sv.setQuery(val, false);
            savedCity = val;
        }
        val = MainActivity.sharedPreferences.getString(SETTINGS_WEATHER_HTML1, "");
        if (!val.isEmpty()) {
            queryWeatherHTML1 = val;
        }
        val = MainActivity.sharedPreferences.getString(SETTINGS_WEATHER_HTML2, "");
        if (!val.isEmpty()) {
            queryWeatherHTML2 = val;
        }
        Log.e(DEBUG_TAG, getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
    }


    //
    // Widget nézet leállítása
    //
    @Override
    public void onStop(){
        super.onStop();
        //appWidgetHost.stopListening();

        var settings = MainActivity.sharedPreferences.edit();
        EditText tv = findViewById(R.id.widgetTextNote);
        String text = tv.getText().toString();
        settings.putString(SETTINGS_NOTE, text);
        settings.apply();
        Log.e(DEBUG_TAG, getString(R.string.stopped_activty) + ": "+ this.getClass().getSimpleName());
    }


    //
    // Widget nézet bezárása
    //
    public void closeWidgetButton(View view) {
        this.finish();
    }


    //
    // Widget AI indítása
    //
    @SuppressLint("SetJavaScriptEnabled")
    public void startAI(View view) {
        WebView wv = findViewById(R.id.widgetPrivateAI);
        wv.loadUrl(privateAIUrl);
        WebSettings webSettings = wv.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setJavaScriptEnabled(true);
    }


}