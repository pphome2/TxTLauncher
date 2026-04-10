package com.wswdteam.txtlauncher;

import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_NOTE_TAG;
import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_URL_SEARCH_TAG;
import static com.wswdteam.txtlauncher.MainActivity.adaptiveIcon;
import static com.wswdteam.txtlauncher.MainActivity.adaptiveIconColor;
import static com.wswdteam.txtlauncher.MainActivity.defaultBackGroundColor;
import static com.wswdteam.txtlauncher.MainActivity.defaultFontSize;
import static com.wswdteam.txtlauncher.MainActivity.defaultIconColor;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.MainActivity.defaultTextColor;
import static com.wswdteam.txtlauncher.MainActivity.hideKeyboard;
import static com.wswdteam.txtlauncher.MainActivity.privateAIUrl;
import static com.wswdteam.txtlauncher.MainActivity.syslog;
import static com.wswdteam.txtlauncher.MainActivity.systemMessage;
import static com.wswdteam.txtlauncher.MainActivity.textColorMode;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DateFormat;


//
// Widget nézet létrehozása
//
public class WidgetActivity extends AppCompatActivity {

    public static boolean firstLoadAI = true;


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

        // teljes háttér színe
        getWindow().getDecorView().setBackgroundColor(defaultBackGroundColor);

        TextView tv = findViewById(R.id.widgetTitle);
        tv.setTextColor(defaultTextColor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, MainActivity.defaultFontSize + defaultPlusFontSizeTitle);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable appI = getDrawable(R.drawable.arrow_back);
        if (appI != null) {
            int ts = (int) defaultFontSize + (int) defaultPlusFontSizeTitle;
            appI.setBounds(0, 0, ts, ts);
            appI.setTint(defaultTextColor);
            tv.setCompoundDrawables(appI, null, null, null);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setCompoundDrawables(appI, null, null, null);
        }

        EditText editText = findViewById(R.id.widgetTextNote);
        editText.setBackgroundTintList(ColorStateList.valueOf(Color.DKGRAY));
        editText.setMovementMethod(new ScrollingMovementMethod());
        editText.setOnTouchListener((v, event) -> {
            if (editText.hasFocus()) {
                editText.clearFocus();
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
        EditText editT = findViewById(R.id.widgetTextNote);
        Calendar calendar = Calendar.getInstance();
        cv.setDate(calendar.getTimeInMillis(), false, true);
        cv.setFirstDayOfWeek(calendar.getFirstDayOfWeek());
        cv.setOnLongClickListener(v -> {
            try {
                Intent cal = new Intent(Intent.ACTION_MAIN);
                cal.addCategory(Intent.CATEGORY_APP_CALENDAR);
                startActivity(cal);
                finish();
            } catch (Exception e) {
                systemMessage(getString(R.string.error_startapp));
            }
            return false;
        });
        cv.setOnDateChangeListener((view, year, mounth, day) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(year, mounth, day);
            DateFormat df = DateFormat.getDateInstance();
            String date = df.format(calendar1.getTime());
            String text = editT.getText().toString();
            if (text.isEmpty()) {
                editT.setText(date + "  ");
            } else {
                editT.setText(text + "\n" + date + "  ");
            }
            editT.requestFocus();
            editT.setSelection(editT.getText().length());
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(editT, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        if (adaptiveIcon) {
            setIconColorW(R.id.wdialButton, adaptiveIconColor);
            setIconColorW(R.id.wmailButton, adaptiveIconColor);
            setIconColorW(R.id.whelpButton, adaptiveIconColor);
            setIconColorW(R.id.wbrowserButton, adaptiveIconColor);
            setIconColorW(R.id.wcameraButton, adaptiveIconColor);
            setIconColorW(R.id.wapplistButton, adaptiveIconColor);
            setIconColorW(R.id.wfavlistButton, adaptiveIconColor);
            setIconColorW(R.id.waiButton, adaptiveIconColor);
            setIconColorW(R.id.wdiscoveryButton, adaptiveIconColor);
            setIconColorW(R.id.wmapButton, adaptiveIconColor);
        } else {
            setIconColorW(R.id.wdialButton, defaultIconColor);
            setIconColorW(R.id.wmailButton, defaultIconColor);
            setIconColorW(R.id.whelpButton, defaultIconColor);
            setIconColorW(R.id.wbrowserButton, defaultIconColor);
            setIconColorW(R.id.wcameraButton, defaultIconColor);
            setIconColorW(R.id.wapplistButton, defaultIconColor);
            setIconColorW(R.id.wfavlistButton, defaultIconColor);
            setIconColorW(R.id.waiButton, defaultIconColor);
            setIconColorW(R.id.wdiscoveryButton, defaultIconColor);
            setIconColorW(R.id.wmapButton, defaultIconColor);
        }

        int textColor;
        int iconColor;
        if (textColorMode) {
            textColor = adaptiveIconColor;
            iconColor = adaptiveIconColor;
        } else {
            textColor = defaultTextColor;
            if (adaptiveIcon) {
                iconColor = adaptiveIconColor;
            } else {
                iconColor = defaultTextColor;
            }
        }
        TextView tvx;
        setIconColorW(R.id.wdialButton, iconColor);
        tvx = findViewById(R.id.wdialButtonTitle);
        tvx.setTextColor(textColor);
        setIconColorW(R.id.wmailButton, iconColor);
        tvx = findViewById(R.id.wmailButtonTitle);
        tvx.setTextColor(textColor);
        setIconColorW(R.id.whelpButton, iconColor);
        tvx = findViewById(R.id.whelpButtonTitle);
        tvx.setTextColor(textColor);
        setIconColorW(R.id.wbrowserButton, iconColor);
        tvx = findViewById(R.id.wbrowserButtonTitle);
        tvx.setTextColor(textColor);
        setIconColorW(R.id.wcameraButton, iconColor);
        tvx = findViewById(R.id.wcameraButtonTitle);
        tvx.setTextColor(textColor);
        setIconColorW(R.id.wapplistButton, iconColor);
        tvx = findViewById(R.id.wapplistButtonTitle);
        tvx.setTextColor(textColor);
        setIconColorW(R.id.wfavlistButton, iconColor);
        tvx = findViewById(R.id.wfavlistButtonTitle);
        tvx.setTextColor(textColor);
        setIconColorW(R.id.waiButton, iconColor);
        tvx = findViewById(R.id.waiButtonTitle);
        tvx.setTextColor(textColor);
        setIconColorW(R.id.wdiscoveryButton, iconColor);
        tvx = findViewById(R.id.wdiscoveryButtonTitle);
        tvx.setTextColor(textColor);
        setIconColorW(R.id.wmapButton, iconColor);
        tvx = findViewById(R.id.wmapButtonTitle);
        tvx.setTextColor(textColor);

        tvx = findViewById(R.id.widgetNoteTitle);
        tvx.setTextColor(textColor);
    }


    //
    // Widget nézet indítása
    //
    @Override
    public void onStart(){
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.enter_from_left, R.anim.exit_to_right);
        overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.enter_from_right, R.anim.exit_to_left);

        super.onStart();

        String val = MainActivity.sharedPreferences.getString(SETTINGS_NOTE_TAG, "");
        if (!val.isEmpty()) {
            EditText tv = findViewById(R.id.widgetTextNote);
            tv.setText(val);
        }
        firstLoadAI = true;


        ScrollView sv = findViewById(R.id.widgetScrollFrame);
        sv.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (Math.abs(scrollY - oldScrollY) > 1) {
                hideKeyboard(this);
            }
        });

        syslog(getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
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
        settings.putString(SETTINGS_NOTE_TAG, text);
        settings.apply();
    }



    //
    //  Fő nézet: alapértelmezett gombok beállítása
    //
    public void setIconColorW(int id, int color) {
        ImageView btn;
        Drawable drw;
        btn = findViewById(id);
        drw =btn.getDrawable();
        if(drw !=null) {
            drw = DrawableCompat.wrap(drw).mutate();
            DrawableCompat.setTint(drw, color);
            btn.setImageDrawable(drw);
        }
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
        try {
            Intent brIn;
            if (privateAIUrl.contains("://")) {
                brIn = new Intent(Intent.ACTION_VIEW, Uri.parse(privateAIUrl));
                brIn.addCategory(Intent.CATEGORY_DEFAULT);
                brIn = new Intent(Intent.ACTION_VIEW);
                brIn.setData(Uri.parse(privateAIUrl));
                brIn.setPackage(MainActivity.packName.get(2));
                brIn.addCategory(Intent.CATEGORY_DEFAULT);
            } else {
                brIn = new Intent(Intent.ACTION_WEB_SEARCH);
                brIn.addCategory(Intent.CATEGORY_DEFAULT);
                brIn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                brIn.putExtra(SearchManager.QUERY, privateAIUrl + "\n");
                brIn.setComponent(new ComponentName("com.google.android.googlequicksearchbox", "com.google.android.googlequicksearchbox.SearchActivity"));
                //searchIntent.setPackage("com.google.android.googlequicksearchbox");
            }
            startActivity(brIn);
            this.finish();
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }


    //
    // App indítása
    //
    public void startButtonAppDial(View v) {
        // dial
        try {
            final PackageManager pm = getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(MainActivity.packName.get(0));
            assert launchIntent != null;
            startActivity(launchIntent);
            this.finish();
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }



    //
    // App indítása
    //
    public void startButtonAppMail(View v) {
        // mail
        try {
            final PackageManager pm = getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(MainActivity.packName.get(1));
            assert launchIntent != null;
            startActivity(launchIntent);
            this.finish();
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }



    //
    // App indítása
    //
    public void startButtonAppHelp(View v) {
        // help
        openHelp(v);
    }



    //
    // App indítása
    //
    public void startButtonAppBrowser(View v) {
        // browser
        try {
            String url = MainActivity.sharedPreferences.getString(SETTINGS_URL_SEARCH_TAG, "");
            if (url.isEmpty()) {
                url = MainActivity.privateSearchUrl;
            }
            Intent broIn = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            broIn.addCategory(Intent.CATEGORY_DEFAULT);
            // ! final PackageManager pm = getPackageManager();
            // ! Intent broIn = new Intent(Intent.ACTION_MAIN);
            // ! broIn.addCategory(Intent.CATEGORY_APP_BROWSER);
            startActivity(broIn);
            this.finish();
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }



    //
    // App indítása
    //
    public void startButtonAppCamera(View v) {
        // camera
        try {
            final PackageManager pm = getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(MainActivity.packName.get(3));
            assert launchIntent != null;
            startActivity(launchIntent);
            this.finish();
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }



    //
    // App indítása
    //
    public void startButtonAppList(View v) {
        // applist
        startActivity(new Intent(WidgetActivity.this, AppListActivity.class));
        this.finish();
    }



    //
    // App indítása
    //
    public void startButtonAppFav(View v) {
        // fav
        startActivity(new Intent(WidgetActivity.this, FavoritesActivity.class));
        this.finish();
    }



    //
    // App indítása
    //
    public void startButtonAppAI(View v) {
        // ai
        startAI(v);
    }



    //
    // App indítása
    //
    public void startButtonAppDiscovery(View v) {
        // discovery
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.googlequicksearchbox");
            startActivity(intent);
            this.finish();
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }



    //
    // App indítása
    //
    public void startButtonAppMap(View v) {
        // map
        openMap(v);
    }



    //
    // Térkép mutatása
    //
    public void openMap(View view) {
        try {
            //Uri gmmIntentUri = Uri.parse("geo:0,0?q=restaurants");
            Uri mapUri = Uri.parse("geo:0,0");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            startActivity(mapIntent);
            this.finish();
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }



    //
    //  Fő nézet: leírás
    //
    public void openHelp(View view) {
        startActivity(new Intent(WidgetActivity.this, HelpActivity.class));
        this.finish();
    }

}