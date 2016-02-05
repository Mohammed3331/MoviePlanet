package com.example.android.movieplanet.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by toshiba on 1/25/2016.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.movieplanet";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String MOVIE_PATH = "movies";

    public static final class MovieEntry implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "movies";
        //poster
        public static final String COLUMN_POSTER_PATH = "poster_path";
        //overview
        public static final String COLUMN_OVERVIEW = "overview";
        //release date
        public static final String COLUMN_RELEASE_DATE = "release_date";
        //ID
        public static final String COLUMN_ID = "id";
        // title
        public static final String COLUMN_TITLE = "title";
        //backdrop path
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        //popularity
        public static final String COLUMN_POPULARITY="popularity";
        //vote average
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        //favoured
        public static final String COLUMN_FAVOURED = "favorites";


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIE_PATH).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;
        public static final String CONTENT_ITEM_TYPE=ContentResolver.CURSOR_ITEM_BASE_TYPE +"/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static int getMovieIdFromUri(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

        public  static final Uri buildMoviesUri(){
            return CONTENT_URI.buildUpon().build();
        }

        public  static final Uri buildMovieWithMovieId(int MovieId){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(MovieId)).build();
        }
    }
}