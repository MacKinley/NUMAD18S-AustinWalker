package edu.neu.madcourse.austinwalker;

import android.view.View;
import android.widget.Button;

public class Tile {

    private boolean mSelected;
    private char mLetter;
    private View mView;

    private WordGameFragment mGame;

    public Tile(WordGameFragment game, char letter) {
        mGame = game;
        mLetter = letter;
        mSelected = false;
    }

    public void setView(View view) {
        mView = view;

        Button tile = (Button) mView;
        tile.setText(new char[]{mLetter}, 0, 1);
    }

    public void setLetter(char letter) {
        mLetter = letter;
        Button tile = (Button) mView;
        tile.setText(new char[]{mLetter}, 0, 1);
    }

    public void removeLetter() {
        setLetter(' ');
    }

    public char getLetter() {
        return mLetter;
    }

    public boolean hasLetter() {
        return mLetter != ' ';
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

    public void setInvalid() {
        mView.getBackground().setLevel(2);
    }

    public void setValid() {
        mView.getBackground().setLevel(3);
    }

}
