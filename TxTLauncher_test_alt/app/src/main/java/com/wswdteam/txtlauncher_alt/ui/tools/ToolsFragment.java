package com.wswdteam.txtlauncher_alt.ui.tools;

import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_NOTE;
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
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.wswdteam.txtlauncher_alt.MainActivity;
import com.wswdteam.txtlauncher_alt.R;
import com.wswdteam.txtlauncher_alt.databinding.FragmentToolsBinding;


public class ToolsFragment extends Fragment {

    private FragmentToolsBinding binding;


    public static boolean firstLoadAI = true;


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

        @SuppressLint({"UseRequireInsteadOfGet", "CutPasteId"}) EditText et = requireActivity().findViewById(R.id.widgetTextNote);
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

        CalendarView cv = requireActivity().findViewById(R.id.widgetCalendar);
        cv.setOnDateChangeListener((view2, year, month, dayOfMonth) -> {
            try {
                Intent cal = new Intent(Intent.ACTION_MAIN);
                cal.addCategory(Intent.CATEGORY_APP_CALENDAR);
                startActivity(cal);
                requireActivity().finish();
            } catch (Exception e) {
                systemMessage(getString(R.string.error_startapp));
            }
        });

        Button b1 = requireActivity().findViewById(R.id.widgetOpenMap);
        b1.setOnClickListener(this::openWidgetMap);

        Button b2 = requireActivity().findViewById(R.id.startAI);
        b2.setOnClickListener(this::startAI);

        String val = MainActivity.sharedPreferences.getString(SETTINGS_NOTE, "");
        if (!val.isEmpty()) {
            assert getView() != null;
            EditText tv = getView().findViewById(R.id.widgetTextNote);
            tv.setText(val);
        }

        firstLoadAI = true;
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
        } catch (Exception e) {
            systemMessage(getString(R.string.error_startapp));
        }
    }


}