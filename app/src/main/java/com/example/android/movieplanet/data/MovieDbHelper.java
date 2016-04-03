package com.example.android.movieplanet.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by toshiba on 1/25/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=5;
    static final String DATABASE_NAME="movies.db";
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE="CREATE TABLE "+MovieContract.MovieEntry.TABLE_NAME + "("+
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                MovieContract.MovieEntry.COLUMN_ID + " INTEGER NOT NULL,"+
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT ,"+
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT,"+
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE +" REAL NOT NULL,"+
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL,"+
                MovieContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT,"+
                MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL ,"+
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL,"+
                MovieContract.MovieEntry.COLUMN_FAVORED + " INTEGER NOT NULL DEFAULT 0,"+
                " UNIQUE ("+ MovieContract.MovieEntry.COLUMN_ID + ") ON CONFLICT IGNORE);";

        final String SQL_CREATE_TRAILER_TABLE="CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME +"("+
                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY + " TEXT NOT NULL, "+
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "+
                " FOREIGN KEY ("+ MovieContract.TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES "+
                MovieContract.MovieEntry.TABLE_NAME+" ("+ MovieContract.MovieEntry.COLUMN_ID+"), "+
                " UNIQUE ("+ MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY +") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE "+ MovieContract.ReviewEntry.TABLE_NAME +"("+
                MovieContract.ReviewEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT,  "+
                MovieContract.ReviewEntry.COLUMN_AUTHOR +" TEXT NOT NULL , "+
                MovieContract.ReviewEntry.COLUMN_CONTENT +" TEXT NOT NULL , "+
                MovieContract.ReviewEntry.COLUMN_REVIEW_URL+" TEXT NOT NULL , "+
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL , "+
                "FOREIGN KEY ("+ MovieContract.ReviewEntry.COLUMN_MOVIE_ID+") REFERENCES "+
                MovieContract.ReviewEntry.TABLE_NAME + "("+ MovieContract.MovieEntry.COLUMN_ID+"),"+
                "UNIQUE ("+ MovieContract.ReviewEntry.COLUMN_CONTENT +")ON CONFLICT REPLACE) ;";


        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }





    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        onCreate(db);

    }
}
