package com.wswdteam.txtlauncher_alt;

import static com.wswdteam.txtlauncher_alt.MainActivity.TXT_APP_NAME;
import static com.wswdteam.txtlauncher_alt.MainActivity.TXT_VERSION;
import static com.wswdteam.txtlauncher_alt.MainActivity.TXT_WEB_PAGE;
import static com.wswdteam.txtlauncher_alt.MainActivity.lineSeparator;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tv = findViewById(R.id.helpViewTitle);
        tv.setTextSize(tv.getTextSize() + MainActivity.defaultPlusFontSizeTitle);
        int ts = (int) tv.getTextSize();
        @SuppressLint("UseCompatLoadingForDrawables") Drawable appI = getDrawable(R.drawable.arrow_back);
        if (appI != null) {
            appI.setBounds(0, 0, ts, ts);
            tv.setCompoundDrawables(appI, null, null, null);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setCompoundDrawables(appI, null, null, null);
        }
        TextView ver = findViewById(R.id.helpVersion);
        ver.setText(String.format("%s: %s %s", TXT_APP_NAME, TXT_VERSION, getString(R.string.licence)));
        TextView web = findViewById(R.id.helpWeb);
        web.setText(String.format("%s", TXT_WEB_PAGE));

        TextView help = findViewById(R.id.helpText1);
        help.setText(getString(R.string.help_text_1).replaceAll(lineSeparator, "\n"));
        help = findViewById(R.id.helpText2);
        help.setText(getString(R.string.help_text_2).replaceAll(lineSeparator, "\n"));
        help = findViewById(R.id.helpText3);
        help.setText(getString(R.string.help_text_3).replaceAll(lineSeparator, "\n"));
        help = findViewById(R.id.helpText4);
        help.setText(getString(R.string.help_text_4).replaceAll(lineSeparator, "\n"));
    }



    //
    // Beállítások indítása
    //
    public void onStart() {
        super.onStart();

        TextView ver = findViewById(R.id.helpVersion);
        ver.setText(String.format("%s: %s %s", TXT_APP_NAME, TXT_VERSION, getString(R.string.licence)));
        TextView web = findViewById(R.id.helpWeb);
        web.setText(String.format("%s", TXT_WEB_PAGE));

        TextView help = findViewById(R.id.helpText1);
        help.setText(getString(R.string.help_text_1).replaceAll(lineSeparator, "\n"));
        help = findViewById(R.id.helpText2);
        help.setText(getString(R.string.help_text_2).replaceAll(lineSeparator, "\n"));
        help = findViewById(R.id.helpText3);
        help.setText(getString(R.string.help_text_3).replaceAll(lineSeparator, "\n"));
        help = findViewById(R.id.helpText4);
        help.setText(getString(R.string.help_text_4).replaceAll(lineSeparator, "\n"));
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