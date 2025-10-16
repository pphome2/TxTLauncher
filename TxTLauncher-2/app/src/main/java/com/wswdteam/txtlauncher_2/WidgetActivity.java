package com.wswdteam.txtlauncher_2;

import static com.wswdteam.txtlauncher_2.MainActivity.SETTINGS_NOTE;
import static com.wswdteam.txtlauncher_2.MainActivity.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher_2.MainActivity.privateAIUrl;
import static com.wswdteam.txtlauncher_2.MainActivity.systemMessage;

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
import android.widget.CalendarView;
import android.widget.EditText;
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
    // Widget térkép mutatása
    //
    public void openWidgetMap(View view) {
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



}