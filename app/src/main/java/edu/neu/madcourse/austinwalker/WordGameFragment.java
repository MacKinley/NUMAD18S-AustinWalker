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

    public enum GAME_STATE {ROUND_ONE, CHANGE_ROUND, ROUND_TWO}

    public static GAME_STATE CURRENT_STATE = GAME_STATE.ROUND_ONE;

    private View mRootView;
    private TextView mDisplayTextView;
    private TextView mScoreTextView;
    private Button mMakeMoveButton;
    private GameBoard mGameBoards[] = new GameBoard[9];

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
        mRootView = rootView;

        mDisplayTextView = (TextView) mRootView.findViewById(R.id.scroggle_display_word);
        mScoreTextView = (TextView) mRootView.findViewById(R.id.scroggle_display_score);
        mMakeMoveButton = (Button) rootView.findViewById(R.id.scroggle_finish_word_button);

        for (int i = 0; i < 9; i++) {
            View outer = rootView.findViewById(mLargeIds[i]);
            mGameBoards[i].setView(rootView, outer);
        }

        mMakeMoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CURRENT_STATE == GAME_STATE.ROUND_ONE) {
                    doRoundOneTurn();
                } else if (CURRENT_STATE == GAME_STATE.CHANGE_ROUND) {
                    changeRound();
                } else {
                    doRoundTwoTurn();
                }
            }
        });
    }

    private void doRoundOneTurn() {
        int selectedBoard = GameBoard.lastBoardSelected;
        if (mGameBoards[selectedBoard].finishWord()) {
            mWordsLeft--;

            mDisplayTextView.setText("Find " + mWordsLeft + " words");

            mScore += mGameBoards[selectedBoard].getBoardScore();
            redisplayScore();

            // Time to do round 2
            if (mWordsLeft == 0) {
                mDisplayTextView.setText("Round 2!");
                mMakeMoveButton.setText("Go");
                CURRENT_STATE = GAME_STATE.CHANGE_ROUND;
            }
        }
    }

    private void changeRound() {
        // pop instruction toast

        for (int i = 0; i < 9; i++) {
            mGameBoards[i].startRoundTwo();
        }

        mMakeMoveButton.setText("Select");
        mDisplayTextView.setText("Spell a word");
        CURRENT_STATE = GAME_STATE.ROUND_TWO;
    }

    private void doRoundTwoTurn() {
        int score = GameBoard.finishWordRoundTwo();

        if (score > 0) {
            mScore += score;
            redisplayScore();

            // I don't like this
            for (int i = 0; i < 9; i++) {
                mGameBoards[i].checkIfEmpty();
            }

            mDisplayTextView.setText("Spell a word");
        }


    }

    private void redisplayScore() {
        String scoreLabel = getResources().getString(R.string.scroggle_score_label, mScore);
        mScoreTextView.setText(scoreLabel);
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
