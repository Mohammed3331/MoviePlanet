package com.example.android.movieplanet.AsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.movieplanet.BuildConfig;
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
 * Created by toshiba on 2/21/2016.
 */
public class FetchReviewTask extends AsyncTask<Integer,Void,Void> {

    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();
    private final Context mContext;

    public FetchReviewTask(Context context) {
        mContext = context;
    }

    private void getReviewFromJson(String reviewJsonStr) throws JSONException {
        int id ;
        String content;
        String author;
        String url;

        JSONObject reviewJsonString = new JSONObject(  reviewJsonStr);
        JSONArray reviewArray = reviewJsonString.getJSONArray("results");
        id= reviewJsonString.getInt("id");
        //insert the new review info into the database
        Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewArray.length());
        for (int i = 0 ; i<reviewArray.length();i++){
            JSONObject reviewData = reviewArray.getJSONObject(i);

            content = reviewData.getString("content");
            author = reviewData.getString("author");
            url = reviewData.getString("url");

            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID,id);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR,author);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT,content);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL,url);
            cVVector.add(reviewValues);
        }
        // add to database
        if (cVVector.size()>0){
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI,cvArray);

        }
        Log.v(LOG_TAG, "Fetch review task Complete. " + cVVector.size() + " Inserted.  Movie Id: " + id);
    }
    @Override
    protected Void doInBackground(Integer... params) {
        Log.v(LOG_TAG, "in do in background");

        if (params.length == 0) {
            return null;
        }
        int movieId = params[0];

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String reviewJsonStr = null;

        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http")
                    .authority("api.themoviedb.org")
                    .appendPath("3")
                    .appendPath("movie")
                    .appendPath("" + movieId)
                    .appendPath("reviews")
                    .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY);

            URL url = new URL(builder.build().toString());

            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
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
                // Stream was empty.  No point in parsing.
                return null;
            }
            reviewJsonStr = buffer.toString();
            getReviewFromJson(reviewJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } catch(JSONException e)
        {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
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
