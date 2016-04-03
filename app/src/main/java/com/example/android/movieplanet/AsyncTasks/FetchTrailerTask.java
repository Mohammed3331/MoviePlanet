package com.example.android.movieplanet.AsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.movieplanet.BuildConfig;
import com.example.android.movieplanet.ImageAdapter;
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
 * Created by toshiba on 2/20/2016.
 */
public class FetchTrailerTask extends AsyncTask<Integer, Void, Void> {
private ImageAdapter movieAdapter;

private Context mContext;
private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

public FetchTrailerTask(Context context) {
        mContext = context;

        }

private void getTrailerInfoFromJson(String TrailerJsonStr) throws JSONException {
        int movieId;
        String YouTubeKey;

        JSONObject JSONString = new JSONObject(TrailerJsonStr);
        JSONArray TrailerArray = JSONString.getJSONArray("results");
        movieId = JSONString.getInt("id");

        Vector<ContentValues> cVVector = new Vector<ContentValues>(TrailerArray.length());
        Log.v("Trailer Adapter", "num trailers fetched json: " + TrailerArray.length());

    for (int i = 0; i < TrailerArray.length(); i++) {
        JSONObject Trailer = TrailerArray.getJSONObject(i);
        YouTubeKey= Trailer.getString("key");

        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, YouTubeKey);
        cVVector.add(trailerValues);
        }

        //int inserted = 0;
        //add to database
        if (cVVector.size() > 0) {
        ContentValues[] cvArray = new ContentValues[cVVector.size()];
        cVVector.toArray(cvArray);
        mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "Fetch Trailer Task Complete. " + cVVector.size() + " Inserted");

        }




@Override
protected Void doInBackground(Integer... params) {

    int movieId = params[0];
    // These two need to be declared outside the try/catch
    // so that they can be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;

    // Will contain the raw JSON response as a string.
    String TrailerJsonStr = null;


    try {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath("" + movieId)
                .appendPath("videos")
                .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY).build();

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
        TrailerJsonStr = buffer.toString();
        getTrailerInfoFromJson(TrailerJsonStr);

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
}
