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

    private View mRootView; // Hacky, but I need to update the word display
    private View mView;
    private int mBoardId;
    private boolean mBoardFinished = false;
    private boolean mBoardValid = false;
    private StringBuilder mCurrentWord = new StringBuilder(9);
    private Tile[] gameTiles = new Tile[9];

    private Stack<Integer> selectedTiles = new Stack<>();

    private WordGameFragment mGame;

    public GameBoard(WordGameFragment game, int boardID, String startingWord) {
        mGame = game;
        mBoardId = boardID;

        char[] startState = shuffleWord(startingWord);

        for (int i = 0; i < 9; i++) {
            gameTiles[i] = new Tile(game, startState[i]);
        }
    }

    // TODO: make this better
    private char[] shuffleWord(String word) {
        char[] boardState = new char[9];
        boardState[0] = word.charAt(0);
        boardState[1] = word.charAt(1);
        boardState[2] = word.charAt(2);
        boardState[3] = word.charAt(5);
        boardState[4] = word.charAt(4);
        boardState[5] = word.charAt(3);
        boardState[6] = word.charAt(6);
        boardState[7] = word.charAt(7);
        boardState[8] = word.charAt(8);

        return boardState;
    }

    public void setView(View rootView, View view) {
        mRootView = rootView;
        mView = view;

        for (int i = 0; i < 9; i++) {
            final Button inner = (Button) mView.findViewById(tileIds[i]);
            gameTiles[i].setView(inner);

            final int tileIndex = i;
            inner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canSelectOrUnselect(tileIndex)) {
                        updateSelection(tileIndex);
                        lastBoardSelected = mBoardId;
                    }
                }
            });
        }
    }

    // Returns true if tile is:
    // 1. first to be selected
    // 2. is adjacent to last selected and is NOT selected
    // 3. is already in the word
    private boolean canSelectOrUnselect(int index) {
        Tile tile = gameTiles[index];

        if (mBoardFinished || !tile.hasLetter())
            return false;
        if (selectedTiles.empty())
            return true;

        int lastSelected = selectedTiles.peek();
        return (isAdjacent(lastSelected, index) && !tile.selected())
                || tile.selected();
    }

    // Add the letter to mCurrentWord
    // OR pop back to the current tile
    private void updateSelection(int index) {
        Tile tile = gameTiles[index];


        if (!tile.selected()) {
            mCurrentWord.append(tile.getLetter());
            tile.setSelected();
            selectedTiles.push(index);
        } else {
            // Unselect the last tile
            if (selectedTiles.peek() == index) {
                selectedTiles.pop();
                tile.setUnselected();
                mCurrentWord.deleteCharAt(mCurrentWord.length() - 1);
            } else {
                int deleteLen = 0;

                // Pop back to the selected
                while (!selectedTiles.empty() && selectedTiles.peek() != index) {
                    int lastIndex = selectedTiles.pop();
                    gameTiles[lastIndex].setUnselected();
                    deleteLen++;
                }

                int currentLen = mCurrentWord.length();
                mCurrentWord.delete(currentLen - deleteLen, currentLen);
            }
        }

        TextView currentWordText = (TextView) mRootView.findViewById(R.id.scroggle_display_word);
        currentWordText.setText(mCurrentWord.toString().toUpperCase());
        Log.d(TAG, "Current word: " + mCurrentWord.toString());
    }

    // Return a negative score if this wasn't a valid word
    public int getBoardScore() {
        if (mBoardValid) {
            return mCurrentWord.length();
        } else {
            return 0 - mCurrentWord.length();
        }
    }

    // returns true if final selection is a word
    public boolean finishWord() {
        if (mBoardFinished || mCurrentWord.length() < 3) {
            return false;
        }

        if (checkDictionaryWord(mCurrentWord.toString())) {
            beep();
            finishTiles(true);
        } else {
            finishTiles(false);
        }

        mBoardFinished = true;
        return true;
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

    // Color the current word green or red
    private void finishTiles(boolean valid) {
        mBoardValid = valid;
        Stack<Integer> tilesCopy = selectedTiles;

        while (!tilesCopy.empty()) {
            int tileId = tilesCopy.pop();

            if (valid) {
                gameTiles[tileId].setValid();
            } else {
                gameTiles[tileId].setInvalid();
            }
        }
    }

    // See if the text is in our dictionary db
    private boolean checkDictionaryWord(String word) {
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
