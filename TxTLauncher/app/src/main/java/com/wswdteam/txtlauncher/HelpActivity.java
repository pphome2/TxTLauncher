package com.wswdteam.txtlauncher;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
import static com.wswdteam.txtlauncher.TxTLauncherApp.TXT_APP_NAME;
import static com.wswdteam.txtlauncher.TxTLauncherApp.TXT_VERSION;
import static com.wswdteam.txtlauncher.TxTLauncherApp.TXT_WEB_PAGE;
import static com.wswdteam.txtlauncher.TxTLauncherApp.defaultBackGroundColor;
import static com.wswdteam.txtlauncher.TxTLauncherApp.defaultFontSize;
import static com.wswdteam.txtlauncher.TxTLauncherApp.defaultPlusFontSizeTitle;
import static com.wswdteam.txtlauncher.TxTLauncherApp.defaultTextColor;
import static com.wswdteam.txtlauncher.TxTLauncherApp.syslog;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


//
// Beállítások nézet
//
public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.helpLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);

        // teljes háttér színe
        getWindow().getDecorView().setBackgroundColor(defaultBackGroundColor);

        TextView tv = findViewById(R.id.helpViewTitle);
        tv.setTextColor(defaultTextColor);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultFontSize + defaultPlusFontSizeTitle);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable appI = getDrawable(R.drawable.arrow_back);
        if (appI != null) {
            int ts = (int) defaultFontSize + (int) defaultPlusFontSizeTitle;
            appI.setBounds(0, 0, ts, ts);
            appI.setTint(defaultTextColor);
            tv.setCompoundDrawables(appI, null, null, null);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setCompoundDrawables(appI, null, null, null);
        }
        tv = findViewById(R.id.helpVersion);
        tv.setTextColor(defaultTextColor);
        tv = findViewById(R.id.helpWeb);
        tv.setTextColor(defaultTextColor);
        tv = findViewById(R.id.helpText1);
        tv.setTextColor(defaultTextColor);
        tv = findViewById(R.id.helpText2);
        tv.setTextColor(defaultTextColor);
        tv = findViewById(R.id.helpText3);
        tv.setTextColor(defaultTextColor);
        tv = findViewById(R.id.helpText4);
        tv.setTextColor(defaultTextColor);
    }


    //
    // Beállítások indítása
    //
    @SuppressLint("SetTextI18n")
    public void onStart() {
        overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.enter_from_top, R.anim.exit_to_bottom);
        // - overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.enter_from_left, R.anim.exit_to_right);
        super.onStart();

        TextView ver = findViewById(R.id.helpVersion);
        ver.setText(String.format("%s: %s %s", TXT_APP_NAME, TXT_VERSION, getString(R.string.licence)));
        TextView web = findViewById(R.id.helpWeb);
        web.setText(String.format("%s", TXT_WEB_PAGE));

        TextView help = findViewById(R.id.helpText1);
        help.setText(getString(R.string.help_text_1));
        help = findViewById(R.id.helpText2);
        help.setText(getString(R.string.help_text_2));
        help = findViewById(R.id.helpText3);
        help.setText(getString(R.string.help_text_3));
        help = findViewById(R.id.helpText4);
        help.setText(getString(R.string.help_text_4));

        syslog(getString(R.string.started_activity) + ": "+ this.getClass().getSimpleName());
    }


    //
    // Beállítások leállítása
    //
    public void onStop() {
        super.onStop();
    }


    //
    // Beállítások nézet bezárása
    //
    public void closeHelpButton(View v) {
        this.finish();
    }

}


