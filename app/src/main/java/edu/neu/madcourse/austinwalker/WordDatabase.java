package edu.neu.madcourse.austinwalker;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class WordDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "dictionary.db";
    private static final int DATABASE_VERSION = 1;

    public WordDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}