package com.example.android.movieplanet.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by toshiba on 1/25/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=2;
    static final String DATABASE_NAME="movies.db";
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE="CREATE TABLE "+MovieContract.MovieEntry.TABLE_NAME + "("+
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MovieContract.MovieEntry.COLUMN_ID + " INTEGER NOt NULL,"+
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT ,"+
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT,"+
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE +" REAL NOT NULL,"+
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL,"+
                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT,"+
                MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL ,"+
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL,"+
                MovieContract.MovieEntry.COLUMN_FAVOURED + " INTEGER NOT NULL DEFAULT 0,"+
                " UNIQUE ("+ MovieContract.MovieEntry.COLUMN_ID + ") ON CONFLICT IGNORE);";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);

    }
}
