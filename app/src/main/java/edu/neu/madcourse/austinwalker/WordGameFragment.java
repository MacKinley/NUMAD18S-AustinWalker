package edu.neu.madcourse.austinwalker;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WordGameFragment extends Fragment {

    final static String TAG = "WordGameFragment";

    static private int mLargeIds[] = {R.id.largeTile1, R.id.largeTile2, R.id.largeTile3, R.id.largeTile4, R.id.largeTile5, R.id.largeTile6, R.id.largeTile7, R.id.largeTile8, R.id.largeTile9};

    private GameBoard mGameBoards[] = new GameBoard[9];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: save state
        setRetainInstance(true);
        initGame();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_word_game, container, false);
        initViews(rootView);
        return rootView;
    }

    public void initGame() {
        String[] words = getRandomWords();

        // Set up the tiles
        for (int i = 0; i < 9; i++) {
            mGameBoards[i] = new GameBoard(this, words[i]);
        }
    }

    private void initViews(View rootView) {
        for (int i = 0; i < 9; i++) {
            View outer = rootView.findViewById(mLargeIds[i]);
            mGameBoards[i].setView(outer);
        }
    }

    public String[] getRandomWords() {
        String[] randomWords = new String[9];

        MyApplication myApp = (MyApplication) this.getActivity().getApplication();
        SQLiteDatabase wordDb = myApp.dictionaryDb;

        Cursor cursor = wordDb.rawQuery("SELECT word FROM words WHERE LENGTH(word) = 9 ORDER BY RANDOM() LIMIT 9 ", new String[]{});

        cursor.moveToFirst();
        for (int i = 0; i < 9; i++) {
            randomWords[i] = cursor.getString(0);
            Log.d(TAG, "getRandomWords: " + randomWords[i]);
            cursor.moveToNext();
        }

        return randomWords;
    }


}
