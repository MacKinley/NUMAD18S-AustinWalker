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

import edu.neu.madcourse.austinwalker.AboutActivity;
import edu.neu.madcourse.austinwalker.DictionaryActivity;
import edu.neu.madcourse.austinwalker.R;

public class DictionaryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dictionary, container, false);

        TextView wordList = (TextView)rootView.findViewById(R.id.dictionary_word_list);
        wordList.setText("Hello world");

        return rootView;
    }

    public void onResume() {
        super.onResume();

    }
}
