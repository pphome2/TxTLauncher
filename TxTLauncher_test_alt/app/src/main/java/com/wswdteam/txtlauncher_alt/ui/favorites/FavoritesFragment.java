package com.wswdteam.txtlauncher_alt.ui.favorites;

import static com.wswdteam.txtlauncher_alt.MainActivity.SETTINGS_FAV_APP_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.allApplicationsList;
import static com.wswdteam.txtlauncher_alt.MainActivity.defaultFontSize;
import static com.wswdteam.txtlauncher_alt.MainActivity.defaultPlusFontSize;
import static com.wswdteam.txtlauncher_alt.MainActivity.favAppNum;
import static com.wswdteam.txtlauncher_alt.MainActivity.systemMessage;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.wswdteam.txtlauncher_alt.MainActivity;
import com.wswdteam.txtlauncher_alt.R;
import com.wswdteam.txtlauncher_alt.databinding.FragmentFavoritesBinding;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    public static List<String> favAppName = new ArrayList<>();
    public static boolean favAppIcon = false;

    private FragmentFavoritesBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //FavoritesViewModel favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    //
    // Kedvenc app lista előkészítés
    //
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity.generateAppList();
        favAppName.clear();
        String tag;
        String val;
        String appName;
        for (var i=0; i<favAppNum; i++) {
            tag = SETTINGS_FAV_APP_TAG + i;
            val = MainActivity.sharedPreferences.getString(tag, "");
            if (!val.isEmpty()) {
                for (ResolveInfo app : allApplicationsList) {
                    appName = app.loadLabel(MainActivity.packageMan).toString();
                    if (appName.equals(val)) {
                        favAppName.add(appName);
                    }
                }
            }
        }

        val = MainActivity.sharedPreferences.getString("AppIcon", "");
        if (!val.isEmpty()) {
            favAppIcon = !val.equals("0");
        }

        setFavApp(view);
    }


    //
    //  Fő nézet: alapértelmezett gombok beállítása
    //
    public void setFavApp(View view){
        final ArrayList<String> appFList1 = new ArrayList<>();
        final ArrayList<String> appFList2 = new ArrayList<>();
        final ListView favTable1 = requireActivity().findViewById(R.id.favAppList1);
        final ListView favTable2 = requireActivity().findViewById(R.id.favAppList2);
        ArrayAdapter<String> adapterf1 = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, appFList1) {
            @Override
            public String getItem(int position) {
                return super.getItem(position);
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                String appN = getItem(position);
                //Log.d(DEBUG_TAG, appN);
                View row = super.getView(position, convertView, parent);
                if (appN != null) {
                    TextView tvt = row.findViewById(android.R.id.text1);
                    if (favAppIcon) {
                        for (ResolveInfo app : allApplicationsList) {
                            String appName = app.loadLabel(MainActivity.packageMan).toString();
                            if (appName.equals(appN)) {
                                tvt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                                Drawable appI = app.loadIcon(MainActivity.packageMan);
                                int ts = (int) tvt.getTextSize() + 40;
                                appI.setBounds(0, 0, ts, ts);
                                tvt.setCompoundDrawables(appI, null, null, null);
                            }
                        }
                    } else {
                        tvt.setTextSize(defaultFontSize + defaultPlusFontSize);
                        tvt.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    tvt.setText(appN);
                    tvt.setCompoundDrawablePadding(30);
                    tvt.setPadding(10,10,10,10);
                    tvt.setEllipsize(TextUtils.TruncateAt.END);
                    tvt.setMaxLines(1);
                }
                return row;
            }
        };
        ArrayAdapter<String> adapterf2 = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, appFList2) {
            @Override
            public String getItem(int position) {
                return super.getItem(position);
            }

            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                String appN = getItem(position);
                //Log.d(DEBUG_TAG, appN);
                View row = super.getView(position, convertView, parent);
                if (appN != null) {
                    TextView tvt = row.findViewById(android.R.id.text1);
                    if (favAppIcon) {
                        for (ResolveInfo app : allApplicationsList) {
                            String appName = app.loadLabel(MainActivity.packageMan).toString();
                            if (appName.equals(appN)) {
                                tvt.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                                Drawable appI = app.loadIcon(MainActivity.packageMan);
                                int ts = (int) tvt.getTextSize() + 40;
                                appI.setBounds(0, 0, ts, ts);
                                tvt.setCompoundDrawables(appI, null, null, null);
                            }
                        }
                    } else {
                        tvt.setTextSize(defaultFontSize + defaultPlusFontSize);
                        tvt.setGravity(Gravity.CENTER_HORIZONTAL);
                    }
                    tvt.setText(appN);
                    tvt.setCompoundDrawablePadding(30);
                    tvt.setPadding(10,10,10,10);
                    tvt.setEllipsize(TextUtils.TruncateAt.END);
                    tvt.setMaxLines(1);
                }
                return row;
            }
        };

        double anum = Math.min(favAppName.size(), favAppNum);
        double an = anum / 2;
        long halfA = Math.round(an);
        int halfApp = (int) halfA;
        for (var i=0; i < halfApp; i++){
            if (favAppName.size() > i) {
                if (!favAppName.get(i).isEmpty()) {
                    appFList1.add(favAppName.get(i));
                }
            }
        }
        for (var i=halfApp; i < favAppNum; i++){
            if (favAppName.size() > i) {
                if (!favAppName.get(i).isEmpty()) {
                    appFList2.add(favAppName.get(i));
                }
            }
        }
        if ((halfApp * 2) > favAppName.size()) {
            appFList2.add("");
        }

        favTable1.setAdapter(adapterf1);
        adapterf1.notifyDataSetChanged();
        favTable2.setAdapter(adapterf2);
        adapterf2.notifyDataSetChanged();

        favTable1.setOnItemClickListener((parent, view3, position, id) -> {
            String selectedP = (String) (favTable1.getItemAtPosition(position));
            for (ResolveInfo app : allApplicationsList) {
                String appName = app.loadLabel(MainActivity.packageMan).toString();
                String pName = app.activityInfo.packageName;
                if (appName.equals(selectedP)) {
                    try {
                        Intent launchIntent = MainActivity.packageMan.getLaunchIntentForPackage(pName);
                        if (launchIntent != null) {
                            startActivity(launchIntent);
                            requireActivity().findViewById(R.id.navigation_home).callOnClick();
                        }
                    } catch (Exception e) {
                        systemMessage(getString(R.string.error_startapp));
                    }
                }
            }
            //Log.d(DEBUG_TAG, selectedP);
        });
        favTable2.setOnItemClickListener((parent, view3, position, id) -> {
            String selectedP = (String) (favTable2.getItemAtPosition(position));
            for (ResolveInfo app : allApplicationsList) {
                String appName = app.loadLabel(MainActivity.packageMan).toString();
                String pName = app.activityInfo.packageName;
                if (appName.equals(selectedP)) {
                    try {
                        Intent launchIntent = MainActivity.packageMan.getLaunchIntentForPackage(pName);
                        if (launchIntent != null) {
                            startActivity(launchIntent);
                            requireActivity().findViewById(R.id.navigation_home).callOnClick();
                        }
                    } catch (Exception e) {
                        systemMessage(getString(R.string.error_startapp));
                    }
                }
            }
            //Log.d(DEBUG_TAG, selectedP);
        });
    }



}