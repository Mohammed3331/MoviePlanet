package com.example.android.movieplanet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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
import java.util.Arrays;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
     ImageAdapter adapter;
    private ArrayList<String> poster;
    ImageView imageView;
    GridView gridView;

    private  ArrayList<String> originalTitle;
    private  ArrayList<String> overview;
    private  ArrayList<String> rating;
    private  ArrayList<String> releaseDate;


    public MainActivityFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
           }


    public  class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private ArrayList<String> array;
        public ImageAdapter(Context c, ArrayList<String> paths){
            mContext=c;
            array=paths;
        }
        public int getCount(){
            return array.size();
        }
        public Object getItem(int position){
            return null;
        }
        public long getItemId(int position){
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView,ViewGroup parent){

            if(convertView == null)
            {
                //if it's not recycled, initialize some attributes
                imageView= new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            else
            {
                imageView=(ImageView) convertView;
            }
            Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/"+array.get(position)).resize(300,550).into(imageView);
            return imageView;
        }


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.fragment_main, container, false);
        final ArrayList<String>array = new ArrayList<String>();
        adapter =new ImageAdapter(getActivity(),array);
         gridView=(GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(adapter);

       gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
               //String movies = (String) adapter.getItem(position);

               Intent intent = new Intent(getActivity(), MovieDetails.class).putExtra("poster",poster.get(position))
                       .putExtra("overview",overview.get(position))
                       .putExtra("originalTitle",originalTitle.get(position))
                       .putExtra("releaseDate",releaseDate.get(position))
                       .putExtra("rating",rating.get(position));
               startActivity(intent);
           }
       });
            return rootView;
           }
        private void updateMovies(){
            FetchMovieTask movieTask = new FetchMovieTask();
            SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy= prefs.getString(getString(R.string.sort_key),getString(R.string.sort_type_popularity));
            movieTask.execute(sortBy);

        }
        @Override
        public void onStart(){
            super.onStart();
            updateMovies();
        }


           public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<String>> {
               private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

               private String[] getMoviesInfoFromJson(String movieJsonStr) throws JSONException {
                   JSONObject JSONString = new JSONObject(movieJsonStr);
                   JSONArray moviesArray = JSONString.getJSONArray("results");
                   String[] result = new String[moviesArray.length()];

                   for (int i = 0; i < moviesArray.length(); i++) {
                       JSONObject movie = moviesArray.getJSONObject(i);
                       String posterPath = movie.getString("poster_path");
                       result[i] = posterPath;
                   }
                   return result;

               }
               private String[] getAllMoviesInfoFromJSON(String JSONStringParam, String param)  throws JSONException
               {
                   JSONObject JSONString = new JSONObject(JSONStringParam);
                   JSONArray moviesArray = JSONString.getJSONArray("results");
                   String[] result = new String[moviesArray.length()];

                   for(int i = 0; i<moviesArray.length();i++)
                   {
                       JSONObject movie = moviesArray.getJSONObject(i);
                       if(param.equals("vote_average"))
                       {
                           Double number = movie.getDouble("vote_average");
                           String rating =Double.toString(number)+"/10";
                           result[i]=rating;
                       }
                       else {
                           String data = movie.getString(param);
                           result[i] = data;
                       }
                   }
                   return result;
               }

               @Override
               protected ArrayList<String> doInBackground(String... params) {

                   // These two need to be declared outside the try/catch
                   // so that they can be closed in the finally block.
                   HttpURLConnection urlConnection = null;
                   BufferedReader reader = null;

                   // Will contain the raw JSON response as a string.
                   String movieJsonStr = null;

                   try {

                       String baseUrl="http://api.themoviedb.org/3/discover/movie?";
                       if(params[0].equals("vote_average.desc")) {
                           baseUrl = "http://api.themoviedb.org/3/discover/movie?certification_country=US&certification=R&";

                       }
                       if (params[0].equals("popularity.desc")){
                           baseUrl="http://api.themoviedb.org/3/discover/movie?";
                       }


                       final String sort= "sort_by";
                       String apiKey = "api_key";

                       Uri builtUri = Uri.parse(baseUrl).buildUpon().appendQueryParameter(sort,params[0]).appendQueryParameter(apiKey, BuildConfig.MOVIE_DB_API_KEY).build();
                       URL url = new URL(builtUri.toString());
                      // Log.v(LOG_TAG, "Built URI " + builtUri.toString());


                       // Create the request to OpenWeatherMap, and open the connection
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
                      // Log.v(LOG_TAG, "getting movie JSON string" + movieJsonStr);
                   } catch (IOException e) {
                       Log.e(LOG_TAG, "Error ", e);
                       // If the code didn't successfully get the movie data, there's no point in attemping
                       // to parse it.
                       return null;
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
                   try {
                       originalTitle=new ArrayList(Arrays.asList(getAllMoviesInfoFromJSON(movieJsonStr,"original_title")));
                       overview=new ArrayList(Arrays.asList(getAllMoviesInfoFromJSON(movieJsonStr,"overview")));
                       rating=new ArrayList(Arrays.asList(getAllMoviesInfoFromJSON(movieJsonStr,"vote_average")));
                       releaseDate=new ArrayList(Arrays.asList(getAllMoviesInfoFromJSON(movieJsonStr,"release_date")));
                       poster = new ArrayList(Arrays.asList(getMoviesInfoFromJson(movieJsonStr)));
                       return poster;
                   } catch (JSONException e) {
                       Log.e(LOG_TAG, e.getMessage(), e);
                       e.printStackTrace();
                   }
                   // This will only happen if there was an error getting or parsing the forecast.
                   return null;
               }

               @Override
               protected void onPostExecute(ArrayList<String> result) {
                   if (result != null) {
                       ImageAdapter adapter = new ImageAdapter(getActivity(), result);
                       gridView.setAdapter(adapter);
                   }
               }
           }
       }


