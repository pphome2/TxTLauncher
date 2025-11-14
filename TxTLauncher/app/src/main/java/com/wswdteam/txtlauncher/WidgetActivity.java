package com.wswdteam.txtlauncher;

import static com.wswdteam.txtlauncher.MainActivity.SETTINGS_NOTE;
import static com.wswdteam.txtlauncher.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.MainActivity.privateAIUrl;
import static com.wswdteam.txtlauncher.MainActivity.systemMessage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
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
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, MainActivity.defaultFontSize + defaultPlusFontSizeTitle);
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

        if (MainActivity.homeStartAppIcon) {
            GradientDrawable border = new GradientDrawable();
            border.setColor(Color.TRANSPARENT);
            border.setStroke(2, Color.WHITE);
            border.setCornerRadius(10);
            LinearLayout lv = findViewById(R.id.buttonsFrame1);
            var db = lv.getChildCount();
            for (var p = 0; p < db; p++) {
                View v = lv.getChildAt(p);
                v.setBackground(border);
                v.setPadding(15,15,15,15);
            }
            lv = findViewById(R.id.buttonsFrame2);
            db = lv.getChildCount();
            for (var p = 0; p < db; p++) {
                View v = lv.getChildAt(p);
                v.setBackground(border);
                v.setPadding(15,15,15,15);
            }

        }


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

        firstLoadAI = true;
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
        try {
            Intent broIn = new Intent(Intent.ACTION_VIEW, Uri.parse(privateAIUrl));
            broIn.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(broIn);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }

    }


    //
    // App indítása
    //
    public void startButtonApp1(View v) {
        // dial
        final PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(MainActivity.packName.get(0));
        assert launchIntent != null;
        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }

    //
    // App indítása
    //
    public void startButtonApp2(View v) {
        // mail
        final PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(MainActivity.packName.get(1));
        assert launchIntent != null;
        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }

    //
    // App indítása
    //
    public void startButtonApp3(View v) {
        // help
        openHelp(v);
    }

    //
    // App indítása
    //
    public void startButtonApp4(View v) {
        // browser
        final PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(MainActivity.packName.get(2));
        assert launchIntent != null;
        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }

    //
    // App indítása
    //
    public void startButtonApp5(View v) {
        // camera
        final PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(MainActivity.packName.get(3));
        assert launchIntent != null;
        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }

    //
    // App indítása
    //
    public void startButtonApp6(View v) {
        // applist
        startActivity(new Intent(WidgetActivity.this, AppListActivity.class));
    }

    //
    // App indítása
    //
    public void startButtonApp7(View v) {
        // fav
        startActivity(new Intent(WidgetActivity.this, FavoritesActivity.class));
    }

    //
    // App indítása
    //
    public void startButtonApp8(View v) {
        // ai
        startAI(v);
    }

    //
    // App indítása
    //
    public void startButtonApp9(View v) {
        // discovery
        try {
            Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.googlequicksearchbox");
            startActivity(intent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }

    //
    // App indítása
    //
    public void startButtonApp10(View v) {
        // map
        try {
            Uri mapUri = Uri.parse("geo:0,0");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            startActivity(mapIntent);
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }

    //
    //  Fő nézet: leírás
    //
    public void openHelp(View view) {
        startActivity(new Intent(WidgetActivity.this, HelpActivity.class));
    }


}