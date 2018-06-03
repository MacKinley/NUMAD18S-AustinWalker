package edu.neu.madcourse.austinwalker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import edu.neu.madcourse.austinwalker.AboutActivity;
import edu.neu.madcourse.austinwalker.DictionaryActivity;
import edu.neu.madcourse.austinwalker.R;

public class DictionaryFragment extends Fragment {

    static ArrayList<String> wordList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_dictionary, container, false);

        // Clear button listener
        View clearButton = rootView.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearWordList();
            }
        });

        final EditText textInput = rootView.findViewById(R.id.dictionary_edit_text);
        textInput.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();

                if (checkDictionaryWord(text)) {
                    wordList.add(0, text);
                    beep();
                    textInput.setText("");
                    printWordList();
                }
            }
        });

        return rootView;
    }

    public void onResume() {
        super.onResume();

        printWordList();
    }

    private void printWordList() {
        StringBuilder words = new StringBuilder();

        TextView wordListView = (TextView) getView().findViewById(R.id.dictionary_word_list);

        for (String word : wordList)
            words.append(word + "\n");

        wordListView.setText(words.toString());
    }

    // Empty our wordlist and clear the views
    private void clearWordList() {
        TextView wordListView = (TextView) getView().findViewById(R.id.dictionary_word_list);
        wordListView.setText("");

        EditText textInput = getView().findViewById(R.id.dictionary_edit_text);
        textInput.setText("");

        wordList.clear();
    }

    // Placeholder
    private boolean checkDictionaryWord(String word) {
        return word.equals("hello") || word.equals("goodbye");
    }

    private void beep() {
        ToneGenerator beep = new ToneGenerator(AudioManager.STREAM_ALARM, 75);
        beep.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);
    }
}
