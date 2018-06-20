package edu.neu.madcourse.austinwalker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Stack;

public class GameBoard {

    static final String TAG = "GameBoard";

    static public int lastBoardSelected = -1;

    static private int tileIds[] = {R.id.smallTile1, R.id.smallTile2, R.id.smallTile3, R.id.smallTile4, R.id.smallTile5, R.id.smallTile6, R.id.smallTile7, R.id.smallTile8, R.id.smallTile9};

    private View mRootView;
    private View mView;
    private int mBoardId;
    private boolean mBoardFinished = false;
    private char[] mBoardState = new char[9];
    private StringBuilder mCurrentWord = new StringBuilder(9);
    private Tile[] gameTiles = new Tile[9];

    private Stack<Integer> selectedTiles = new Stack<>();

    private WordGameFragment mGame;

    public GameBoard(WordGameFragment game, int boardID, String startingWord) {
        mGame = game;
        mBoardId = boardID;

        initBoardState(startingWord);

        for (int i = 0; i < 9; i++) {
            gameTiles[i] = new Tile(game);
        }
    }

    // TODO: make this better
    private void initBoardState(String word) {
        mBoardState[0] = word.charAt(0);
        mBoardState[1] = word.charAt(1);
        mBoardState[2] = word.charAt(2);
        mBoardState[3] = word.charAt(5);
        mBoardState[4] = word.charAt(4);
        mBoardState[5] = word.charAt(3);
        mBoardState[6] = word.charAt(6);
        mBoardState[7] = word.charAt(7);
        mBoardState[8] = word.charAt(8);
    }

    public void setView(View rootView, View view) {
        mRootView = rootView;
        mView = view;

        for (int i = 0; i < 9; i++) {
            final Button inner = (Button) mView.findViewById(tileIds[i]);
            gameTiles[i].setView(inner);

            inner.setText(mBoardState, i, 1);

            final int tileIndex = i;
            inner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canSelectOrUnselect(tileIndex)) {
                        toggleSelected(tileIndex);
                        lastBoardSelected = mBoardId;
                    }
                }
            });
        }
    }

    // Returns true if tile is:
    // 1. first to be selected
    // 2. is adjacent to selected and is NOT selected
    // 3. was most recently selected
    private boolean canSelectOrUnselect(int index) {
        if (mBoardFinished)
            return false;
        if (selectedTiles.empty())
            return true;

        int lastSelected = selectedTiles.peek();
        return (isAdjacent(lastSelected, index) && !gameTiles[index].selected())
                || lastSelected == index;
    }

    // Color the tile and add the letter to mCurrentWord
    // OR uncolor the tile and take away a letter
    private void toggleSelected(int index) {
        Tile tile = gameTiles[index];

        if (!tile.selected()) {
            mCurrentWord.append(mBoardState[index]);
            tile.setSelected();
            selectedTiles.push(index);
        } else {
            int len = mCurrentWord.length();
            mCurrentWord.deleteCharAt(len - 1);
            tile.setUnselected();
            selectedTiles.pop();
        }

        TextView currentWordText = (TextView) mRootView.findViewById(R.id.scroggle_display_word);
        currentWordText.setText(mCurrentWord.toString());
        Log.d(TAG, "Current word: " + mCurrentWord.toString());
    }

    public String getCurrentWord() {
        return mCurrentWord.toString();
    }

    // returns true if final selection is a word
    // TODO: bail if less thann 3 letters
    public void finishWord() {
        if (checkDictionaryWord(mCurrentWord.toString())) {
            beep();

            while (!selectedTiles.empty()) {
                int tileId = selectedTiles.pop();
                gameTiles[tileId].setComplete();
            }
        } else {
            while (!selectedTiles.empty()) {
                int tileId = selectedTiles.pop();
                gameTiles[tileId].setIncomplete();
            }
        }

        mBoardFinished = true;
    }

    // Simple check for tiles that are touching each other
    private boolean isAdjacent(int i, int j) {
        switch (i) {
            case 0:
                return j == 1 || j == 3 || j == 4;

            case 1:
                return j == 0 || j == 3 || j == 4 || j == 5 || j == 2;

            case 2:
                return j == 1 || j == 4 || j == 5;

            case 3:
                return j == 0 || j == 1 || j == 4 || j == 6 || j == 7;

            case 4:
                return j == 0 || j == 1 || j == 2 || j == 3 || j == 5 || j == 6 || j == 7 || j == 8;

            case 5:
                return j == 1 || j == 2 || j == 4 || j == 7 || j == 8;

            case 6:
                return j == 3 || j == 4 || j == 7;

            case 7:
                return j == 6 || j == 3 || j == 4 || j == 5 || j == 8;

            case 8:
                return j == 5 || j == 4 || j == 7;

            default:
                return false;
        }
    }

    // See if the text is in our dictionary db
    private boolean checkDictionaryWord(String word) {
        if (word.length() < 3) {
            return false;
        }

        MyApplication myApp = (MyApplication) mGame.getActivity().getApplication();
        SQLiteDatabase wordDb = myApp.dictionaryDb;

        Cursor cursor = wordDb.rawQuery("SELECT word FROM words WHERE word = ? COLLATE NOCASE", new String[]{word});

        boolean found = (cursor.getCount() == 1);
        cursor.close();

        return found;
    }

    private void beep() {
        try {
            ToneGenerator beep = new ToneGenerator(AudioManager.STREAM_ALARM, 75);
            beep.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);
        } catch (RuntimeException e) {
            Log.d(TAG, "Can't generate tone!");
        }
    }
}
