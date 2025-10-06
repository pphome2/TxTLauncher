package com.wswdteam.txtlauncher;

import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_CITY;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_NOTE;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_WEATHER_HTML;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.MainActivity.privateAIUrl;
import static com.wswdteam.txtlauncher.MainActivity.systemMessage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CalendarView;
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
    public static String cityCode = "@CITY@";
    public static String queryWeatherHTMLFrame1 = "<html><body style='background-color:black;'><center>" +
            "<div style='margin:auto;display:block;'>";
    public static String queryWeatherHTMLFrame2 = "</div></center></body></html>";
    public static String queryWeatherHTML = "<iframe src='https://beepulo.idokep.hu/futar/@CITY@' scrolling='no' style='width:300px;height:260px;border:none;'>";
    public static String queryWeatherHTMLOrig = "<iframe src='https://beepulo.idokep.hu/futar/@CITY@' scrolling='no' style='width:300px;height:260px;border:none;'>";
    //public static String defaultHTML = "<html><body></body></html>";



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

        EditText editText = findViewById(R.id.widgetTextNote);
        editText.setMovementMethod(new ScrollingMovementMethod());
        editText.setOnTouchListener((v, event) -> {
            if (editText.hasFocus()) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_SCROLL:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        return true;
                    case MotionEvent.ACTION_MASK:
                        return true;
                }
            }
            return false;
        });

        CalendarView cv = findViewById(R.id.widgetCalendar);
        Calendar calendar = Calendar.getInstance();
        //calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cv.setDate(calendar.getTimeInMillis(), false, true);
        cv.setFirstDayOfWeek(calendar.getFirstDayOfWeek());
        cv.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            try {
                Intent cal = new Intent(Intent.ACTION_MAIN);
                cal.addCategory(Intent.CATEGORY_APP_CALENDAR);
                startActivity(cal);
                this.finish();
            } catch (Exception e) {
                systemMessage(getString(R.string.error_startapp));
            }
        });

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

        WebView wv = findViewById(R.id.widgetPrivateAI);
        wv.setOnTouchListener((view, motionEvent) -> {
            if (firstLoadAI) {
                firstLoadAI = false;
                wv.loadUrl(privateAIUrl);
                WebSettings webSettings = wv.getSettings();
                webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                webSettings.setJavaScriptEnabled(true);
            }
            return false;
        });

        WebView wv2 = findViewById(R.id.widgetWeather);
        wv2.setOnTouchListener((view, motionEvent) -> {
            if (firstLoadWv) {
                firstLoadWv = false;
                String qu = queryWeatherHTML;
                qu = qu.replaceFirst(cityCode, savedCity);
                String data = queryWeatherHTMLFrame1 + qu + queryWeatherHTMLFrame2;
                wv2.loadData(data, "text/html; charset=utf-8", "UTF-8");
                WebSettings webSettings = wv2.getSettings();
                webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                webSettings.setJavaScriptEnabled(true);
                //Log.d(DEBUG_TAG, qu);
            }
            return false;
        });
    }


    //
    // Widget nézet indítása
    //
    @Override
    public void onStart(){
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.enter_from_left, R.anim.exit_to_right);
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.enter_from_right, R.anim.exit_to_left);
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
        val = MainActivity.sharedPreferences.getString(SETTINGS_WEATHER_HTML, "");
        if (!val.isEmpty()) {
            queryWeatherHTML = val;
        }

        firstLoadAI = true;
        firstLoadWv = true;

        //Log.d(DEBUG_TAG, getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
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
        //Log.d(DEBUG_TAG, getString(R.string.stopped_activty) + ": "+ this.getClass().getSimpleName());
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


    //
    // Widget jegyzet címre kattintás
    //
    public void widgetClickNote(View view) {
        //1try {
            //Intent keepIntent = new Intent(Intent.ACTION_VIEW);
            //keepIntent.setPackage("com.google.android.apps.keep");
            //startActivity(keepIntent);
            //this.finish();
        //} catch (Exception e) {
            //systemMessage(getString(R.string.error_startapp));
        //}
    }


    //
    // Widget térkép mutatása
    //
    public void openWidgetMap(View view) {
        try {
            //Uri gmmIntentUri = Uri.parse("geo:0,0?q=restaurants");
            Uri mapUri = Uri.parse("geo:0,0");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            //mapIntent.setPackage("com.google.android.apps.maps");
            mapIntent.setPackage("com.google.android.app.");
            startActivity(mapIntent);
            this.finish();
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }



}