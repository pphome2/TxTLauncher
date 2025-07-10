package com.wswdteam.txtlauncher_alt.ui.allapps;

import static com.wswdteam.txtlauncher_alt.MainActivity.DEBUG_TAG;
import static com.wswdteam.txtlauncher_alt.MainActivity.systemMessage;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wswdteam.txtlauncher_alt.MainActivity;
import com.wswdteam.txtlauncher_alt.R;
import com.wswdteam.txtlauncher_alt.databinding.FragmentAllappsBinding;

import java.util.ArrayList;
import java.util.Objects;


public class AllappsFragment extends Fragment {

    private FragmentAllappsBinding binding;

    final ArrayList<ResolveInfo> allappList = new ArrayList<>();
    final ArrayList<String> appList = new ArrayList<>();
    final ArrayList<String> appPackList = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //AllappsViewModel allappsViewModel = new ViewModelProvider(this).get(AllappsViewModel.class);

        binding = FragmentAllappsBinding.inflate(inflater, container, false);
        return binding.getRoot();
        //return inflater.inflate(R.layout.fragment_allapps, container, false);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    //
    // Minden app  listája
    //
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        char first = '\0';
        final ListView appTable = view.findViewById(R.id.allAppListTable);
        ArrayAdapter<String> adapterallapp = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, appList){
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                String appN = getItem(position);
                //Log.d(DEBUG_TAG, "S" + appN);
                View row = super.getView(position, convertView, parent);
                TextView tvt = row.findViewById(android.R.id.text1);
                if (appN != null) {
                    if (appN.isEmpty()) {
                        tvt.setText("");
                        tvt.setCompoundDrawables(null, null, null, null);
                    } else {
                        if (appN.length() == 1) {
                            //tvt.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_secondary));
                            tvt.setText(appN);
                            tvt.setCompoundDrawables(null, null, null, null);
                        } else {
                            //tvt.setTextColor(getResources().getColor(com.google.android.material.R.color.design_default_color_on_primary));
                            tvt.setText(appN);
                            ResolveInfo thisApp = allappList.get(position);
                            Drawable appI = thisApp.loadIcon(MainActivity.packageMan);
                            int ts = (int) tvt.getTextSize() + 25;
                            appI.setBounds(0, 0, ts, ts);
                            tvt.setCompoundDrawables(appI, null, null, null);
                            tvt.setCompoundDrawablePadding(30);
                            tvt.setPadding(10, 10, 10, 10);
                        }
                    }
                }
                return row;
            }
        };

        appList.clear();
        appPackList.clear();
        allappList.clear();

        boolean firstapp = true;
        for (ResolveInfo app : MainActivity.allApplicationsList) {
            String appName = app.loadLabel(MainActivity.packageMan).toString();
            String pName = app.activityInfo.packageName;
            if (first != appName.charAt(0)) {
                first = appName.charAt(0);
                String s = String.valueOf(first);
                if (firstapp) {
                    firstapp = false;
                }
                appList.add(s);
                appPackList.add("");
                allappList.add(null);
            }
            allappList.add(app);
            appList.add(appName + " ");
            appPackList.add(pName);
        }

        appTable.setAdapter(adapterallapp);
        adapterallapp.notifyDataSetChanged();

        appTable.setOnItemClickListener((parent, lview, position, id) -> {
            //view.setBackgroundColor(getColor(com.google.android.material.R.color.design_default_color_primary));
            String selectedP = (String) (appTable.getItemAtPosition(position));
            //Log.d(DEBUG_TAG, selectedP);
            for (int i = 0; i < appList.size(); i++) {
                String appp = appList.get(i);
                if (Objects.equals(appp, selectedP)) {
                    try {
                        appp = appPackList.get(i);
                        Intent launchIntent = MainActivity.packageMan.getLaunchIntentForPackage(appp);
                        if (launchIntent != null) {
                            startActivity(launchIntent);
                            getActivity().findViewById(R.id.navigation_home).callOnClick();
                        }
                    } catch (Exception e) {
                        systemMessage(getString(R.string.error_startapp));
                    }
                    //Log.d(DEBUG_TAG, getString(R.string.starting_other_app) + ": " + appp);
                }
            }
        });
    }


}