package edu.neu.madcourse.austinwalker;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class WordGameFragment extends Fragment {

    final static String TAG = "WordGameFragment";

    static private int mLargeIds[] = {R.id.largeTile1, R.id.largeTile2, R.id.largeTile3, R.id.largeTile4, R.id.largeTile5, R.id.largeTile6, R.id.largeTile7, R.id.largeTile8, R.id.largeTile9};

    private GameBoard mGameBoards[] = new GameBoard[9];
    private int gameMode = 1;
    private int mWordsLeft = 9;
    private int mScore = 0;

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
            mGameBoards[i] = new GameBoard(this, i, words[i]);
        }
    }

    private void initViews(final View rootView) {
        for (int i = 0; i < 9; i++) {
            View outer = rootView.findViewById(mLargeIds[i]);
            mGameBoards[i].setView(rootView, outer);
        }

        Button finishButton = (Button) rootView.findViewById(R.id.scroggle_finish_word_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedBoard = GameBoard.lastBoardSelected;
                if (mGameBoards[selectedBoard].finishWord()) {
                    mWordsLeft--;

                    TextView wordsLeftView = (TextView) rootView.findViewById(R.id.scroggle_display_word);
                    wordsLeftView.setText("Find " + mWordsLeft + " words");

                    // TODO extract this
                    mScore += mGameBoards[selectedBoard].getBoardScore();
                    TextView scoreView = (TextView) rootView.findViewById(R.id.scroggle_display_score);
                    String scoreLabel = getResources().getString(R.string.scroggle_score_label, mScore);
                    scoreView.setText(scoreLabel);
                }
            }
        });
    }

    private String[] getRandomWords() {
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
