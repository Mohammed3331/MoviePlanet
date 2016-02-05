package com.example.android.movieplanet;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.example.android.movieplanet.AsyncTasks.FetchMovieTask;
import com.example.android.movieplanet.data.MovieContract;

import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    MovieAdapter movieAdapter;
    private List<Movie> movieList;
    GridView gridView;

    private int mPosition= ListView.INVALID_POSITION;
    private static final String SELECTED_KEY="selected_position";

    private static final int Movie_loader=0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_ID,
    };
    static final int ID = 0 ;
    static final int COL_POSTER_PATH= 1;
    static final int COL_ID=2;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //first time view created
        if(savedInstanceState == null){
            updateMovies();
        }

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public interface Callback{
        void onItemSelected(int movieId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        getLoaderManager().initLoader(Movie_loader, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.fragment_main, container, false);
        /*movieList = new ArrayList<>();
        if(savedInstanceState !=null)
        {
            movieList=(List<Movie> )savedInstanceState.get("moviesKey");
        }
        else{
            updateMovies();
        }*/


        movieAdapter =new MovieAdapter(getActivity(),null,0);
        gridView=(GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity()).onItemSelected(COL_ID);
                }
                mPosition = position;
                /*Movie movies = (Movie) movieAdapter.getItem(position);
                Log.v("CHECKING I.P","I.P="+movies);

                Intent intent = new Intent(getActivity(), MovieDetails.class).putExtra("movies", movies);
                Log.v("SEND I","intent="+intent);
                startActivity(intent);*/
            }
        });
        if(savedInstanceState !=null &&savedInstanceState.containsKey(SELECTED_KEY))
        {
            mPosition=savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;

    }
    @Override
    public void onSaveInstanceState(Bundle outState){

      //  outState.putParcelableArrayList("moviesKey", (ArrayList<? extends Parcelable>) movieList);
        if (mPosition != ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY,mPosition);
        }
        super.onSaveInstanceState(outState);

    }
    private void updateMovies(){
        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
        String sortBy = Utility.getSortPreference(getActivity());
        movieTask.execute(sortBy);
        getLoaderManager().restartLoader(Movie_loader, null, this);

    }
   /* @Override
    public void onStart(){
        super.onStart();
        updateMovies();
    }*/

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortPref= Utility.getSortPreference(getActivity());
        switch (sortPref){
            case "popularity":
                return  new CursorLoader(getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        ""+ MovieContract.MovieEntry.COLUMN_POPULARITY +"DESC"
                        );
            case "vote_average":
                return new CursorLoader(getActivity(),
                        MovieContract.MovieEntry.CONTENT_URI,
                        MOVIE_COLUMNS,
                        null,
                        null,
                        ""+ MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + "DESC");
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
        if (mPosition!=ListView.INVALID_POSITION)
            gridView.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }


}
