package com.example.android.movieplanet.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by toshiba on 1/25/2016.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int TRAILER = 200;
    static final int TRAILER_WITH_MOVIE_ID=201;
    static final int REVIEW = 300;
    static final int REVIEW_WITH_MOVIE_ID= 301;

    private static final SQLiteQueryBuilder sMovieByIdQueryBuilder;
    private static final SQLiteQueryBuilder sMovieWithTrailersQueryBuilder;
    private static final SQLiteQueryBuilder sMovieWithReviewsQueryBuilder;

    static {

        sMovieByIdQueryBuilder = new SQLiteQueryBuilder();
        sMovieWithTrailersQueryBuilder = new SQLiteQueryBuilder();
        sMovieWithReviewsQueryBuilder = new SQLiteQueryBuilder();

        sMovieByIdQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);
        sMovieWithTrailersQueryBuilder.setTables(MovieContract.TrailerEntry.TABLE_NAME);
        sMovieWithReviewsQueryBuilder.setTables(MovieContract.ReviewEntry.TABLE_NAME);
    }

    //Movies.movieID=?
    private static final String sMovieIdSelection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_ID + " =?";
    //Trailer.movieID=?
    private static final String sTrailerMovieIdSelection = MovieContract.TrailerEntry.TABLE_NAME+"."+ MovieContract.TrailerEntry.COLUMN_MOVIE_ID+"=?";
    //Review.movieID=?
    private static final String sReviewMovieidSelection = MovieContract.ReviewEntry.TABLE_NAME+"."+ MovieContract.ReviewEntry.COLUMN_MOVIE_ID+"=?";

    private Cursor getMovieByMovieId(Uri uri, String[] projection, String sortOrder) {
        int id = MovieContract.MovieEntry.getMovieIdFromUri(uri);
        String[] selectionArgs;
        selectionArgs = new String[]{"" + id};
        return sMovieByIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieIdSelection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }
    private Cursor getTrailerByMovieId(Uri uri , String[] projection, String sortOrder){
        int id = MovieContract.TrailerEntry.getMovieIdFromUri(uri);
        String[] selectionArgs ;
        selectionArgs=new String[]{Integer.toString(id)};
        return sMovieWithTrailersQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sTrailerMovieIdSelection,
                selectionArgs,
                null,
                null,
                sortOrder);

    }

    private Cursor getReviewByMovieId(Uri uri, String[] projection, String sortOrder){
        int id = MovieContract.ReviewEntry.getMovieFromUri(uri);
        String[] selectionArgs;
        selectionArgs = new String[]{Integer.toString(id)};
        return sMovieWithReviewsQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sReviewMovieidSelection,
                selectionArgs,
                null,
                null,
                sortOrder);


    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.MOVIE_PATH, MOVIES);
        matcher.addURI(authority, MovieContract.MOVIE_PATH + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority,MovieContract.TRAILER_PATH, TRAILER);
        matcher.addURI(authority,MovieContract.TRAILER_PATH+"/#",TRAILER_WITH_MOVIE_ID);
        matcher.addURI(authority,MovieContract.REVIEW_PATH, REVIEW);
        matcher.addURI(authority, MovieContract.REVIEW_PATH +"/#",REVIEW_WITH_MOVIE_ID);
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_WITH_ID: {
                retCursor = getMovieByMovieId(uri, projection, sortOrder);
                break;
            }
            case MOVIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TRAILER_WITH_MOVIE_ID:{
                retCursor=getTrailerByMovieId(uri,projection,sortOrder);
                break;
            }
            case TRAILER:{
                retCursor=mOpenHelper.getReadableDatabase().query(MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REVIEW_WITH_MOVIE_ID:{
                retCursor= getReviewByMovieId(uri,projection,sortOrder);
                break;
            }
            case REVIEW:{
                retCursor= mOpenHelper.getReadableDatabase().query(MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case TRAILER_WITH_MOVIE_ID:
                return MovieContract.TrailerEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case REVIEW_WITH_MOVIE_ID:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER:{
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME,null,values);
                if (_id>0)
                    returnUri= MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into "+uri);
                break;
            }
            case REVIEW:{
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME,null,values);
                if (_id>0)
                    returnUri= MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into "+uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (null == selection)
            selection = "1";
        switch (match) {
            case MOVIES: {
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TRAILER:{
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            case REVIEW:{
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME,selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowUpdated;
        switch (match) {
            case MOVIES: {
                rowUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case TRAILER:{
                rowUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            case REVIEW:{
                rowUpdated= db.update(MovieContract.ReviewEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        if (rowUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TRAILER:
                db.beginTransaction();
                 returnCount= 0;
                try{
                    for (ContentValues value: values){
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME,null,value);
                        if (_id != -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);
                return returnCount;

            case REVIEW :
                db.beginTransaction();
                returnCount=0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != 1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri,null);

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}

