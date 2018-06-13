package edu.neu.madcourse.austinwalker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class WordGameActivity extends AppCompatActivity {

    private static final String KEY_RESTORE = "key_restore";
    private static final String PREF_RESTORE = "pref_restore";
    private WordGameFragment mGameFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_game);

        mGameFragment = (WordGameFragment) getFragmentManager().findFragmentById(R.id.fragment_word_game);
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
//                mGameFragment.putState(gameData);
            }
        }


        // Restore game
    }
}
