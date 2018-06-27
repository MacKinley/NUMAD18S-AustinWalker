package edu.neu.madcourse.austinwalker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class GameBoard {

    static final String TAG = "GameBoard";

    public static char STATE_DELIMITER = '/';

    private static int tileIds[] = {R.id.smallTile1, R.id.smallTile2, R.id.smallTile3, R.id.smallTile4, R.id.smallTile5, R.id.smallTile6, R.id.smallTile7, R.id.smallTile8, R.id.smallTile9};
    private static final int[][] ADJACENT_TILES = {{1, 4, 3}, {2, 3, 4, 5, 0}, {1, 4, 5}, {0, 1, 4, 7, 6}, {0, 3, 2, 5, 8, 7, 6, 1}, {2, 1, 4, 7, 8}, {3, 4, 7}, {6, 3, 8, 5, 4}, {7, 4, 5}};
    private static ToneGenerator successBeep = new ToneGenerator(AudioManager.STREAM_ALARM, 75);
    private static WordGameFragment mGame;

    private View mRootView; // Hacky, but I need to update the word display
    private View mView;
    private int mBoardId;

    private Tile[] mGameTiles = new Tile[9];

    // Round 1 fields
    public static int lastBoardSelected = -1;

    private Stack<Tile> mSelectedTiles = new Stack<>();
    private StringBuilder mCurrentWord = new StringBuilder(9);
    private boolean mBoardFinished = false;
    private boolean mBoardValid = false;

    // Round 2 fields
    public static StringBuilder mRoundTwoWord = new StringBuilder();
    public static Stack<Tile> mSelectedTilesRoundTwo = new Stack<>();


    public GameBoard(WordGameFragment game, int boardID, String startingWord) {
        mGame = game;
        mBoardId = boardID;

        char[] startState = shuffleWord(startingWord);

        for (int i = 0; i < 9; i++) {
            mGameTiles[i] = new Tile(game, boardID, i, startState[i]);
        }
    }

    public String getGameState() {
        StringBuilder state = new StringBuilder();

        state.append(mBoardFinished ? "1" : "0");
        state.append(STATE_DELIMITER);
        state.append(mBoardValid ? "1" : "0");
        state.append(STATE_DELIMITER);

        for (Tile tile : mGameTiles) {
            state.append(tile.getLetter());
        }

        state.append(STATE_DELIMITER);

        // Reverse the selected tiles stack
        Stack<Tile> copyStack = (Stack<Tile>) mSelectedTiles.clone();
        Stack<Tile> reverseStack = new Stack<>();

        while (!copyStack.empty()) {
            reverseStack.push(copyStack.pop());
        }

        while (!reverseStack.empty()) {
            Tile tile = reverseStack.pop();
            state.append(Integer.toString(tile.getIndex()));
            state.append(STATE_DELIMITER);
        }

        return state.toString();
    }

    // Append the static selectedWords stack
    // UGGGGLLLLYYYY
    public static String getRoundTwoState() {
        StringBuilder state = new StringBuilder();

        // Reverse the selected tiles stack
        Stack<Tile> copyStack = (Stack<Tile>) mSelectedTilesRoundTwo.clone();
        Stack<Tile> reverseStack = new Stack<>();

        while (!copyStack.empty()) {
            reverseStack.push(copyStack.pop());
        }

        while (!reverseStack.empty()) {
            Tile tile = reverseStack.pop();
            state.append(tile.getBoard());
            state.append(STATE_DELIMITER);
            state.append(tile.getIndex());
            state.append(STATE_DELIMITER);
        }

        return state.toString();
    }

    public void resumeGame(String state, boolean isRoundTwo) {
        Scanner scanner = new Scanner(state);

        scanner.useDelimiter(Character.toString(STATE_DELIMITER));
        mBoardFinished = scanner.nextInt() == 1;
        mBoardValid = scanner.nextInt() == 1;

        String tiles = scanner.next();
        Log.d(TAG, "resumeGame: " + tiles);

        for (int i = 0; i < 9; i++) {
            mGameTiles[i].setLetter(tiles.charAt(i));
        }

        mSelectedTiles = new Stack<>();

        while (scanner.hasNextInt()) {
            int index = scanner.nextInt();
            Tile nextTile = mGameTiles[index];

            if (mBoardFinished && mBoardValid)
                nextTile.setValid();
            else if (mBoardFinished && !mBoardValid)
                nextTile.setInvalid();
            else
                nextTile.setSelected();

            mCurrentWord.append(nextTile.getLetter());
            mSelectedTiles.push(nextTile);
        }
    }

    public void resumeRoundTwo() {
        for (Tile tile : mGameTiles) {
            if (tile.hasLetter())
                tile.setUnselected();
            else
                tile.setHidden();
        }
    }

    public static void resetRoundTwo() {
        while (!mSelectedTilesRoundTwo.empty()) {
            Tile tile = mSelectedTilesRoundTwo.pop();
            tile.setUnselected();
        }

        mRoundTwoWord.delete(0, mRoundTwoWord.length());
    }

    public void pushRoundTwoState(int index) {
        Tile tile = mGameTiles[index];

        mSelectedTilesRoundTwo.push(tile);
        mRoundTwoWord.append(tile.getLetter());
        tile.setSelected();
    }

    private char[] shuffleWord(String word) {
        int[] tileOrder = findRandomPath();
        char[] boardState = new char[9];

        // Hack: sometimes findRandomPath gives up early
        while (tileOrder.length != 9) {
            tileOrder = findRandomPath();
        }

        for (int i = 0; i < 9; i++) {
            int next = tileOrder[i];
            boardState[i] = word.charAt(next);
        }

        return boardState;
    }

    public void setView(View rootView, View view) {
        mRootView = rootView;
        mView = view;

        for (int i = 0; i < 9; i++) {
            final Button inner = (Button) mView.findViewById(tileIds[i]);
            mGameTiles[i].setView(inner);

            final int tileIndex = i;
            inner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (WordGameFragment.CURRENT_STATE == WordGameFragment.GAME_STATE.ROUND_ONE) {
                        doRoundOneTurn(tileIndex);
                    } else if (WordGameFragment.CURRENT_STATE == WordGameFragment.GAME_STATE.ROUND_TWO) {
                        doRoundTwoTurn(tileIndex);
                    }
                }
            });
        }
    }

    private void doRoundOneTurn(int tileIndex) {
        if (canSelectOrUnselect(tileIndex)) {
            updateSelection(tileIndex, mCurrentWord, mSelectedTiles);
            vibrate(50);
            lastBoardSelected = mBoardId;
        }
    }

    private void doRoundTwoTurn(int tileIndex) {
        if (canSelectOrUnselectRoundTwo(tileIndex)) {
            // only one at a time
            updateSelection(tileIndex, mRoundTwoWord, mSelectedTilesRoundTwo);
            vibrate(50);
        }
    }

    // Returns true if tile is:
    // 1. first to be selected
    // 2. is adjacent to last selected and is NOT selected
    // 3. is already in the word
    private boolean canSelectOrUnselect(int index) {
        Tile tile = mGameTiles[index];

        if (mBoardFinished || !tile.hasLetter())
            return false;
        if (mSelectedTiles.empty())
            return true;

        int lastSelected = mSelectedTiles.peek().getIndex();
        return (isAdjacent(lastSelected, index) && !tile.selected())
                || tile.selected();
    }

    // Returns true if tile is:
    // 1. first to be selected
    // 2. is adjacent to last selected and is NOT selected
    // 3. is already in the word
    private boolean canSelectOrUnselectRoundTwo(int index) {
        Tile tile = mGameTiles[index];

        if (!tile.hasLetter())
            return false;
        if (mSelectedTilesRoundTwo.empty())
            return true;

        int lastSelectedBoard = mSelectedTilesRoundTwo.peek().getBoard();
        return (isAdjacent(mBoardId, lastSelectedBoard) && !tile.selected())
                || tile.selected();
    }

    // Add the letter to mCurrentWord
    // OR pop back to the current tile
    private void updateSelection(int index, StringBuilder
            currentWord, Stack<Tile> letterStack) {
        Tile tile = mGameTiles[index];

        if (!tile.selected()) {
            currentWord.append(tile.getLetter());
            tile.setSelected();
            letterStack.push(tile);
        } else {
            // Unselect the last tile
            if (!letterStack.empty() && letterStack.peek() == tile) {
                letterStack.pop();
                tile.setUnselected();
                currentWord.deleteCharAt(currentWord.length() - 1);
            } else {
                int deleteLen = 0;

                // Pop back to the selected
                while (!letterStack.empty() && letterStack.peek() != tile) {
                    Tile lastTile = letterStack.pop();
                    lastTile.setUnselected();
                    deleteLen++;
                }

                int currentLen = currentWord.length();
                currentWord.delete(currentLen - deleteLen, currentLen);
            }
        }

        TextView currentWordText = (TextView) mRootView.findViewById(R.id.scroggle_display_word);
        currentWordText.setText(currentWord.toString().toUpperCase());
        Log.d(TAG, "Current word: " + currentWord.toString());
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
            beepSucess();
            finishTiles(true);
        } else {
            finishTiles(false);
        }

        mBoardFinished = true;
        return true;
    }

    // returns true if final selection is a word
    public static int finishWordRoundTwo() {
        int wordScore = 0;

        if (mRoundTwoWord.length() >= 3 && checkDictionaryWord(mRoundTwoWord.toString())) {
            beepSucess();
            wordScore = finishTilesRoundTwo();
        }

        return wordScore;
    }

    private static int finishTilesRoundTwo() {
        int wordScore = 0;

        while (!mSelectedTilesRoundTwo.empty()) {
            Tile tile = mSelectedTilesRoundTwo.pop();
            wordScore += tile.getPoints();
            tile.removeLetter();
        }

        // Longer words are better
        wordScore += mRoundTwoWord.length();

        mRoundTwoWord.delete(0, mRoundTwoWord.length());

        return wordScore;
    }

    // Simple check for tiles that are touching each other
    public static boolean isAdjacent(int i, int j) {
        int[] neighbors = ADJACENT_TILES[i];

        for (int n : neighbors) {
            if (n == j)
                return true;
        }

        return false;
    }

    private int[] findRandomPath() {
        Random rand = new Random();
        Stack<Integer> stack = new Stack<>();
        Stack<Integer> path = new Stack<>();
        boolean[] visited = {false, false, false, false, false, false, false, false, false};

        stack.push(rand.nextInt(9));

            while (!stack.empty()) {
                int current = stack.pop();
                if (visited[current])
                    continue;

                // We're stuck! Go back to where we can try this node
                if (!path.empty() && !isAdjacent(current, path.peek())) {
                    visited[path.pop()] = false;

                    // Once more to make sure we don't get stuck again
                    if (!path.empty())
                        visited[path.pop()] = false;

                    if (!path.empty())
                        Log.d(TAG, "findRandomPath: popped back to=" + path.peek());

                    continue;
                }

                visited[current] = true;
                path.push(current);
                Log.d(TAG, "findRandomPath: current=" + current);

                for (int neighbor : shuffleNeighors(current)) {
                    if (!visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }

        int[] arr = new int[path.size()];
        for (int i = 0; i < path.size(); i++) {
            Log.d(TAG, "findRandomPath: finalPath=" + path.elementAt(i));
            arr[i] = path.elementAt(i);
        }

        return arr;
    }

    private ArrayList<Integer> shuffleNeighors(int tileIndex) {
        int[] neighbors = ADJACENT_TILES[tileIndex];
        ArrayList<Integer> list = new ArrayList<>(neighbors.length);

        for (int i : neighbors) {
            list.add(i);
        }

        Collections.shuffle(list);

        return list;
    }

    // Color the current word green or red
    private void finishTiles(boolean valid) {
        mBoardValid = valid;
        Stack<Tile> tilesCopy = (Stack<Tile>) mSelectedTiles.clone();
        while (!tilesCopy.empty()) {
            Tile tile = tilesCopy.pop();

            if (valid) {
                tile.setValid();
            } else {
                tile.setInvalid();
            }
        }
    }

    // Unselect tiles
    public void startRoundTwo() {
        mBoardFinished = false;
        mRoundTwoWord.delete(0, mRoundTwoWord.length());
        mSelectedTilesRoundTwo = new Stack<>();

        for (int i = 0; i < 9; i++) {
            Tile tile = mGameTiles[i];

            tile.setUnselected();

            if (!mBoardValid || !tile.isValid()) {
                tile.removeLetter();
            }
        }

        // If we removed everything, place a random letter
        if (!mBoardValid) {
            mGameTiles[4].setLetter(getRandomLetter());
        }
    }

    // See if the text is in our dictionary db
    private static boolean checkDictionaryWord(String word) {
        MyApplication myApp = (MyApplication) mGame.getActivity().getApplication();
        SQLiteDatabase wordDb = myApp.dictionaryDb;

        Cursor cursor = wordDb.rawQuery("SELECT word FROM words WHERE word = ? COLLATE NOCASE", new String[]{word});

        boolean found = (cursor.getCount() == 1);
        cursor.close();

        return found;
    }

    private static void beepSucess() {
        try {
            successBeep.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300);
        } catch (RuntimeException e) {
            Log.d(TAG, "Can't generate tone!");
        }
    }

    private char getRandomLetter() {
        Random r = new Random();

        return (char) (r.nextInt(26) + 'a');
    }

    public void checkIfEmpty() {
        boolean empty = true;

        for (int i = 0; i < 9; i++) {
            if (mGameTiles[i].hasLetter())
                empty = false;
        }

        // Replace with a random letter
        if (empty) {
            mGameTiles[4].setLetter(getRandomLetter());
        }
    }

    public static void vibrate(int duration) {
        Vibrator v = (Vibrator) mGame.getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(duration);
        }
    }
}
