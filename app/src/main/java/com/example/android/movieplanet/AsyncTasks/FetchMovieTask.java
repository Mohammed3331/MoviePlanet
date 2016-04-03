package com.example.android.movieplanet.AsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.movieplanet.ImageAdapter;
import com.example.android.movieplanet.Utility;
import com.example.android.movieplanet.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by toshiba on 1/23/2016.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Void> {
    private ImageAdapter movieAdapter;

    private Context mContext;
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    public FetchMovieTask(Context context) {
        mContext = context;

    }

    private void getMoviesInfoFromJson(String movieJsonStr) throws JSONException {
        int id;
        String posterPath;
        String overview;
        String releaseDate;
        String title;
        String backdropPath;
        double popularity;
        double voteAverage;
        JSONObject JSONString = new JSONObject(movieJsonStr);
        JSONArray moviesArray = JSONString.getJSONArray("results");

        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movie = moviesArray.getJSONObject(i);
            id = movie.getInt("id");
            posterPath = "http://image.tmdb.org/t/p/w185"+ movie.getString("poster_path");
            overview = movie.getString("overview");
            releaseDate = movie.getString("release_date");
            title = movie.getString("title");
            backdropPath = "http://image.tmdb.org/t/p/w500"+ movie.getString("backdrop_path");
            popularity=movie.getDouble("popularity");
            voteAverage = movie.getDouble("vote_average");

            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.COLUMN_ID, id);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropPath);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY,popularity);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);

            // update trailer and review
            updateTrailer(id);
            updateReview(id);
            cVVector.add(movieValues);

        }

        //int inserted = 0;
        //add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
             mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "FetchPopularMovie Task Complete. " +  cVVector.size() + " Inserted");

    }




    @Override
    protected Void doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        String sort = Utility.getSortPreference(mContext);
        String order = "desc";
        int minVotes = 50;

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("sort_by",sort)

                    .appendQueryParameter("api_key", "4ba99f43c07d44f2bbabe2be587a3344");

            URL url = new URL(builder.toString());
            // Log.v(LOG_TAG, "Built URI " + builder.toString());


            // Create the request to themoviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                //do nothing
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                //Stream was empty. no point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();
            getMoviesInfoFromJson(movieJsonStr);

           // Log.v(LOG_TAG, "getting movie JSON string" + movieJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
            // to parse it.
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection == null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }
    private void updateTrailer(int movieId){
        FetchTrailerTask fitchTrailerTask = new FetchTrailerTask(mContext);
        fitchTrailerTask.execute(movieId);
    }
    private void updateReview(int movieId){
        FetchReviewTask fitchReviewTask = new FetchReviewTask(mContext);
        fitchReviewTask.execute(movieId);

    }
}
