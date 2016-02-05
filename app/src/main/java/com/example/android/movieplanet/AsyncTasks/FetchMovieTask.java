package com.example.android.movieplanet.AsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.movieplanet.BuildConfig;
import com.example.android.movieplanet.ImageAdapter;
import com.example.android.movieplanet.Movie;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by toshiba on 1/23/2016.
 */
public class FetchMovieTask extends AsyncTask<String, Void, Void> {
    private ImageAdapter movieAdapter;
    private List<Movie> movieList;
    private Context mContext;
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    public FetchMovieTask(Context context) {
        mContext = context;
        this.movieAdapter = movieAdapter;
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
        List<Movie> movieList = new ArrayList<>();

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
            cVVector.add(movieValues);

         /*   Movie movie1=new Movie();

            movie1.posterPath=movie.getString("poster_path");
            movie1.overview=movie.getString("overview");
            movie1.releaseDate = movie.getString("release_date");
            movie1.id=movie.getInt("id");
            movie1.title = movie.getString("title");
            movie1.voteAverage = movie.getDouble("vote_average");


            movieList.add(movie1);
            Log.v(LOG_TAG, "movieList" + movieList);*/
        }

        //int inserted = 0;
        //add to database
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
             mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "FetchPopularMovie Task Complete. " +  cVVector.size() + " Inserted");

       /* Cursor cur = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,null,null,null,null);
        cVVector = new Vector<ContentValues>(cur.getCount());
        if (cur.moveToFirst()){
            do{
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(cur, cv);
                cVVector.add(cv);
            }while (cur.moveToNext());
        }*/

        //  return movieList;


      //  return null;
    }

        /*private String[] getYouTubeFromJSON(String movieJsonStr)throws JSONException{
            JSONObject JSONString = new JSONObject(movieJsonStr);
            JSONArray moviesArray = JSONString.getJSONArray("results");


        }*/


    @Override
    protected Void doInBackground(String... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;


        try {

            String baseUrl = "http://api.themoviedb.org/3/discover/movie?";
            if (params[0].equals("vote_average.desc")) {
                baseUrl = "http://api.themoviedb.org/3/discover/movie?certification_country=US&certification=R&";

            }
            if (params[0].equals("popularity.desc")) {
                baseUrl = "http://api.themoviedb.org/3/discover/movie?";
            }


            final String sort = "sort_by";
            String apiKey = "api_key";

            Uri builtUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(sort, params[0]).appendQueryParameter(apiKey, BuildConfig.MOVIE_DB_API_KEY).build();
            URL url = new URL(builtUri.toString());
            // Log.v(LOG_TAG, "Built URI " + builtUri.toString());


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
       /* try {
           movieList= getMoviesInfoFromJson(movieJsonStr);
            return movieList;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }*/


    /*@Override
    protected void onPostExecute(List<Movie> result) {
        if (result != null) {
            if (movieAdapter != null){
                movieAdapter.clear();
            for (Movie movie:result)
                movieAdapter.add(movie);

        }
        }
    }*/

        return null;
    }


}
