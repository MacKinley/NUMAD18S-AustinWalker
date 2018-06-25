package edu.neu.madcourse.austinwalker;

import android.view.View;
import android.widget.Button;

public class Tile {

    private boolean mSelected;
    private boolean mValid;
    private char mLetter;
    private View mView;

    private int mBoardId;
    private int mTileIndex;

    private WordGameFragment mGame;

    public Tile(WordGameFragment game, int boardId, int tileIndex, char letter) {
        mGame = game;
        mSelected = false;
        mValid = false;
        mBoardId = boardId;
        mTileIndex = tileIndex;
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

        if (mLetter != ' ')
            setUnselected();
        else
            setHidden();
    }

    public void removeLetter() {
        setLetter(' ');
       setHidden();
    }

    public int getIndex() {
        return mTileIndex;
    }

    public int getBoard() {
        return mBoardId;
    }

    public char getLetter() {
        return mLetter;
    }

    public int getPoints() {
        switch (mLetter) {
            case 'e':
            case 'a':
            case 'i':
            case 'o':
            case 'n':
            case 'r':
            case 't':
            case 'l':
            case 's':
            case 'u':
                return 1;

            case 'd':
            case 'g':
                return 2;

            case 'b':
            case 'c':
            case 'm':
            case 'p':
                return 3;

            case 'f':
            case 'h':
            case 'v':
            case 'w':
            case 'y':
                return 4;

            case 'k':
                return 5;

            case 'j':
            case 'x':
                return 8;

            case 'q':
            case 'z':
                return 10;

            default:
                return 0;
        }
    }

    public boolean hasLetter() {
        return mLetter != ' ';
    }

    public boolean selected() {
        return mSelected;
    }

    public void setHidden() {
        mView.getBackground().setLevel(0);
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
