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
        mView.setBackgroundColor(mGame.getResources().getColor(R.color.tile_selected_color));
    }

    public void setUnselected() {
        mSelected = false;
        mView.setBackgroundColor(mGame.getResources().getColor(R.color.gray_color));
    }

}
