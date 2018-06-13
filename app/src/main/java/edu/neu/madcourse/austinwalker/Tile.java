package edu.neu.madcourse.austinwalker;

import android.view.View;

public class Tile {

    private boolean mIsLarge;
    private int mId;
    private View mView;

    private WordGameFragment mGame;

    public Tile(WordGameFragment game) {
        mGame = game;
    }

    public void setView(View view) {
        mView = view;
    }
}
