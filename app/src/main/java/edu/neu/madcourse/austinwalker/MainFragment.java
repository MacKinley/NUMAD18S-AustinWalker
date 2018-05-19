package edu.neu.madcourse.austinwalker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {
    private AlertDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Handle buttons
        View aboutButton = rootView.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        // Don't keep the about dialog up
        if (mDialog != null)
            mDialog.dismiss();
    }
}
