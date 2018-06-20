package edu.neu.madcourse.austinwalker;

import android.view.View;

public class Tile {

    private boolean mSelected;
    private View mView;

    private WordGameFragment mGame;

    public Tile(WordGameFragment game) {
        mGame = game;
        mSelected = false;
    }

    public void setView(View view) {
        mView = view;
    }

    public boolean selected() {
        return mSelected;
    }

    public void setSelected() {
        mSelected = true;
        mView.getBackground().setLevel(1);
    }

    public void setUnselected() {
        mSelected = false;
        mView.getBackground().setLevel(0);
    }

    public void setIncomplete() {
        mView.getBackground().setLevel(2);
    }

    public void setComplete() {
        mView.getBackground().setLevel(3);
    }

}
