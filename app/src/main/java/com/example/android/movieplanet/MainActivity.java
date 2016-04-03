package com.example.android.movieplanet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {
    private final String LOG_TAG=MainActivity.class.getSimpleName();
    private static final String MovieDetailFragment_TAG = "MDFTAG";
    private boolean mTwoPane;
    private String mSortPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSortPrefs=Utility.getSortPreference(this);

        if(findViewById(R.id.fragment)!=null){
            mTwoPane =true;
            if (savedInstanceState== null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new MovieDetailsFragment(),MovieDetailFragment_TAG)
                        .commit();
            }
        }else{
            mTwoPane=false;
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }
    @Override
    protected void onStart(){
        Log.v(LOG_TAG, "in onStart");
        super.onStart();
        //the activity is about to become visible
    }
    @Override
    protected void onResume(){
        Log.v(LOG_TAG,"in onResume");
        super.onResume();
        // the activity has become visible ("it is now resumed")

        String sortPrefs=Utility.getSortPreference(this);

        if (sortPrefs != null && !sortPrefs.equals(mSortPrefs)){
            MainActivityFragment maf = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.container);
            if (null != maf){
                if(sortPrefs.equals("favorites"))
                    maf.updateFavoritesLoader();
                else
                maf.updateMovies();
            }

            mSortPrefs=sortPrefs;
        }

    }

    @Override
    protected void onPause(){
        Log.v(LOG_TAG,"in onPause");
        super.onPause();
        // Another activity is taking focus ("this activity is about to be paused")
    }
    @Override
    protected void onStop(){
        Log.v(LOG_TAG,"in onStop");
        super.onStop();
        //the activity is no longer visible(it's now stopped )
    }
    @Override
    protected void onDestroy(){
        Log.v(LOG_TAG,"in onDestroy");
        super.onDestroy();
        //the activity is about to be destroyed.
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(int movieId) {
        if (mTwoPane){
            Bundle arguments = new Bundle();
            arguments.putInt("MovieDetails", movieId);
          /*  Log.v("OnItmeSelected", "TwoPaneMode");
            Log.v("OnItemSelected","MovieId="+movieId);*/

            //pass movie id to detail fragment
            MovieDetailsFragment MDF = new MovieDetailsFragment();
            MDF.setArguments(arguments);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment,MDF,MovieDetailFragment_TAG)
                    .commit();

        }else {
            //start detail activity via intent
            Intent intent = new Intent(this, MovieDetails.class).putExtra("movie_intent", movieId);
            startActivity(intent);
        }
    }
}
