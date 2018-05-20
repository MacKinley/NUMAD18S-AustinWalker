package edu.neu.madcourse.austinwalker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainFragment extends Fragment {

    final static String tag = "MainFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // About button listener
        View aboutButton = rootView.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                getActivity().startActivity(intent);
            }
        });

        // Crash button listener
        View crashButton = rootView.findViewById(R.id.crash_button);
        crashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String testLabSetting = Settings.System.getString(getActivity().getContentResolver(), "firebase.test.lab");
                if (testLabSetting != null && "true".equals(testLabSetting)) {
                    // Do nothing
                } else {
                    throw new NullPointerException("You sunk my battleship!");
                }
            }
        });

        return rootView;
    }

    public void onResume() {
        super.onResume();

        // Show the version info
        try {
            String packageName = getActivity().getPackageName();
            String versionName = getActivity().getPackageManager().getPackageInfo(packageName, 0).versionName;
            int versionCode = getActivity().getPackageManager().getPackageInfo(packageName, 0).versionCode;
            String versionCodeStr = Integer.toString(versionCode);

            TextView versionText = (TextView) getActivity().findViewById(R.id.version_text);
            versionText.setText("Version " + versionName + " (# " + versionCodeStr + ")");


        } catch (PackageManager.NameNotFoundException e) {
            Log.e(tag, "No such package name");
        }
    }
}
