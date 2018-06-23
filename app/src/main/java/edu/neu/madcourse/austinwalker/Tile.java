package edu.neu.madcourse.austinwalker;

import android.view.View;
import android.widget.Button;

public class Tile {

    private boolean mSelected;
    private boolean mValid;
    private char mLetter;
    private View mView;

    private WordGameFragment mGame;

    public Tile(WordGameFragment game, char letter) {
        mGame = game;
        mSelected = false;
        mValid = false;
        mLetter = letter;
    }

    public void setView(View view) {
        mView = view;
        setLetter(mLetter);
    }

    public void setLetter(char letter) {
        mLetter = letter;
        Button tile = (Button) mView;
        tile.setText(new char[]{mLetter}, 0, 1);
        tile.getBackground().setLevel(1);
    }

    public void removeLetter() {
        setLetter(' ');
        mView.getBackground().setLevel(0);
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
        mView.getBackground().setLevel(2);
    }

    public void setUnselected() {
        mSelected = false;
        mView.getBackground().setLevel(1);
    }

    public void setInvalid() {
        mValid = false;
        mView.getBackground().setLevel(3);
    }

    public void setValid() {
        mValid = true;
        mView.getBackground().setLevel(4);
    }

    public boolean isValid() {
        return mValid;
    }

}
