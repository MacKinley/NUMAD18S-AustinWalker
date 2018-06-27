package edu.neu.madcourse.austinwalker;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Scanner;
import java.util.Stack;

public class WordGameFragment extends Fragment {

    final static String TAG = "WordGameFragment";

    final private int ROUND_SECONDS = 90;

    static private int mLargeIds[] = {R.id.largeTile1, R.id.largeTile2, R.id.largeTile3, R.id.largeTile4, R.id.largeTile5, R.id.largeTile6, R.id.largeTile7, R.id.largeTile8, R.id.largeTile9};

    public enum GAME_STATE {ROUND_ONE, CHANGE_ROUND, ROUND_TWO, GAME_OVER}

    public static GAME_STATE CURRENT_STATE;

    private View mRootView;
    private TextView mDisplayTextView;
    private TextView mScoreTextView;
    private Button mMakeMoveButton;
    private GameBoard mGameBoards[] = new GameBoard[9];
    private long mSecondsLeft;
    private CountDownTimer mTimer;

    private int mWordsLeft;
    private int mScore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        initGame();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_word_game, container, false);
        initViews(rootView);
        return rootView;
    }

    public void onResume() {
        super.onResume();

        startTimer();
    }

    public void onPause() {
        super.onPause();

        mTimer.cancel();
    }

    private void startTimer() {
        mTimer = new CountDownTimer(mSecondsLeft * 1000, 1000) {

            TextView timerView = mRootView.findViewById(R.id.scroggle_timer_view);

            public void onTick(long millisUntilFinished) {
                mSecondsLeft = millisUntilFinished / 1000;
                timerView.setText(Long.toString(mSecondsLeft));
            }

            public void onFinish() {
                timerView.setText("0");

                if (CURRENT_STATE == GAME_STATE.ROUND_ONE) {
                    mWordsLeft = 0;
                    redisplayText();
                    CURRENT_STATE = GAME_STATE.CHANGE_ROUND;
                } else {
                    CURRENT_STATE = GAME_STATE.GAME_OVER;
                    redisplayText();
                }

            }
        }.start();
    }

    public void initGame() {
        CURRENT_STATE = GAME_STATE.ROUND_ONE;
        String[] words = getRandomWords(); // TODO: Technically it's doing this on resume as well...

        // Set up the tiles
        for (int i = 0; i < 9; i++) {
            mGameBoards[i] = new GameBoard(this, i, words[i]);
        }

        mSecondsLeft = ROUND_SECONDS;
        mScore = 0;
        mWordsLeft = 9;
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
                } else if (CURRENT_STATE == GAME_STATE.ROUND_TWO){
                    doRoundTwoTurn();
                } else {
                    restartGame();
                }
            }
        });
    }

    private void doRoundOneTurn() {
        int selectedBoard = GameBoard.lastBoardSelected;

        if (selectedBoard != -1 && mGameBoards[selectedBoard].finishWord()) {
            GameBoard.vibrate(300);
            mWordsLeft--;

            mScore += mGameBoards[selectedBoard].getBoardScore();
            redisplayScore();
            redisplayText();

            // Time to do round 2
            if (mWordsLeft == 0) {
                CURRENT_STATE = GAME_STATE.CHANGE_ROUND;
            }
        }
    }

    private void changeRound() {
        // pop instruction toast

        for (int i = 0; i < 9; i++) {
            mGameBoards[i].startRoundTwo();
        }

        mSecondsLeft = ROUND_SECONDS;
        startTimer();

        redisplayText();

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

            redisplayText();
        } else {
            GameBoard.resetRoundTwo();
            mDisplayTextView.setText("Not a word!");
        }

        GameBoard.vibrate(300);
    }

    private void restartGame() {
        initGame();

        for (int i = 0; i < 9; i++) {
            View outer = mRootView.findViewById(mLargeIds[i]);
            mGameBoards[i].setView(mRootView, outer);
        }

        redisplayText();
        redisplayScore();

        startTimer();
    }

    private void redisplayScore() {
        String scoreLabel = getResources().getString(R.string.scroggle_score_label, mScore);
        mScoreTextView.setText(scoreLabel);
    }

    private void redisplayText() {
        switch (CURRENT_STATE) {
            case ROUND_ONE:
                if (mWordsLeft > 0) {
                    mDisplayTextView.setText("Find " + mWordsLeft + " words");
                } else {
                    mDisplayTextView.setText("Round 2!");
                    mMakeMoveButton.setText("Go");
                }
                break;

            case ROUND_TWO:
                mDisplayTextView.setText("Spell a word");
                break;

            case CHANGE_ROUND:
                mMakeMoveButton.setText("Select");
                mDisplayTextView.setText("Spell a word");
                break;

            case GAME_OVER:
                mDisplayTextView.setText("Game over!");
                mMakeMoveButton.setText("Restart");
        }
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

    public String getGameState() {
        StringBuilder state = new StringBuilder();
        char delim = '<';

        state.append(CURRENT_STATE == GAME_STATE.ROUND_ONE ? "1" : "2");
        state.append(delim);

        state.append(mScore);
        state.append(delim);

        state.append(mSecondsLeft);
        state.append(delim);

        state.append(mWordsLeft);
        state.append(delim);

        for (int i = 0; i < 9; i++) {
            state.append(mGameBoards[i].getGameState());
            state.append(delim);
        }

        if (CURRENT_STATE == GAME_STATE.ROUND_TWO) {
            state.append(GameBoard.getRoundTwoState());
            state.append(delim);
        }

        Log.d(TAG, "getGameState: " + state);
        return state.toString();
    }

    public void resumeGame(String state) {
        Scanner scanner = new Scanner(state);
        scanner.useDelimiter("<");

        if (scanner.nextInt() == 1)
            CURRENT_STATE = GAME_STATE.ROUND_ONE;
        else
            CURRENT_STATE = GAME_STATE.ROUND_TWO;

        mScore = scanner.nextInt();
        mSecondsLeft = scanner.nextInt();
        mWordsLeft = scanner.nextInt();

        boolean isRoundTwo = CURRENT_STATE == GAME_STATE.ROUND_TWO;

        for (int i = 0; i < 9; i++) {
            mGameBoards[i].resumeGame(scanner.next(), isRoundTwo);
        }

        // We just switched to round 2
        if (isRoundTwo && !scanner.hasNext()) {
            for (GameBoard gb : mGameBoards) {
                gb.startRoundTwo();
            }
        } else if (scanner.hasNext()) {
            for (GameBoard gb : mGameBoards) {
                gb.resumeRoundTwo();
            }

            String roundTwoState = scanner.next();

            GameBoard.mSelectedTilesRoundTwo = new Stack<>();
            GameBoard.mRoundTwoWord = new StringBuilder();

            Scanner s2 = new Scanner(roundTwoState);
            s2.useDelimiter(Character.toString(GameBoard.STATE_DELIMITER));

            while (s2.hasNext()) {
                int boardIndex = s2.nextInt();
                int tileIndex = s2.nextInt();
                mGameBoards[boardIndex].pushRoundTwoState(tileIndex);
            }
        }

        redisplayScore();
        redisplayText();
    }
}
