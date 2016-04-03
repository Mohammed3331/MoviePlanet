package com.example.android.movieplanet;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.movieplanet.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    private static final String MOVIE_SHARE_HASHTAG = " #PopularMoviesApp";
    private LayoutInflater mLayoutInflater;
    private ShareActionProvider mShareActionProvider;

    private static final int DETAIL_LOADER=0;
    private static final int TRAILER_LOADER=1;
    private static final int REVIEW_LOADER=2;

    private static final String[] MOVIES_COLUMNS={
            MovieContract.MovieEntry.TABLE_NAME+"."+ MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE

    };
    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY,
//            MovieContract.TrailerEntry.COLUMN_MOVIE_ID
    };

    private static final String[] REVIEW_COLUMNS= {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT,
            MovieContract.ReviewEntry.COLUMN_REVIEW_URL
    };

    public static final int COL_DETAIL_ID = 0;
    public static final int COL_DETAIL_POSTER = 1;
    public static final int COL_DETAIL_OVERVIEW = 2;
    public  static final int COL_DETAIL_RELEASE = 3;
    public static final int COL_DETAIL_TITLE = 4;
    public static final int COL_DETAIL_VOTE_AVERAGE = 5;

    public static final int COL_TRAILER_ID = 0;
    public static final int COL_TRAILER_KEY = 1;

    public static final int COL_REVIEW_ID=0;
    public static final int COL_REVIEW_AUTHOR=1;
    public static final int COL_REVIEW_CONTENT=2;
    public static final int COL_REVIEW_URL=3;


    private int mMovieId;
    private ImageView mPosterView;
    private TextView mOverView;
    private TextView mReleaseDateView;
    private TextView mTitleView;
    private TextView mVoteAverageView;

    public LinearLayout mTrailerLayout;
    public LinearLayout mReviewLayout;

    public CheckBox mFavoriteButton;

    public boolean mTrailerDataAlreadyLoaded;
    public boolean mReviewDataAlreadyLoaded;

    private String mFirstTrailerUrl;




    public MovieDetailsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if(arguments != null){
        mMovieId=arguments.getInt("MovieDetails");
            Log.v("Arguments Data","Movie ID="+mMovieId);
        }

        mLayoutInflater= inflater;

        View rootView= inflater.inflate(R.layout.fragment_movie_details, container, false);


        mPosterView = (ImageView) rootView.findViewById(R.id.poster);
        mOverView = (TextView) rootView.findViewById(R.id.overview);
        mReleaseDateView= (TextView) rootView.findViewById(R.id.release);
        mTitleView= (TextView)rootView.findViewById(R.id.title);
        mVoteAverageView=(TextView) rootView.findViewById(R.id.rating);

        mTrailerLayout = (LinearLayout) rootView.findViewById(R.id.trailer_layout);
        mReviewLayout = (LinearLayout) rootView.findViewById(R.id.review_layout);
        mFavoriteButton =(CheckBox)rootView.findViewById(R.id.favorites_button);
        mFavoriteButton.setChecked(getFavoritesPreference());
        mFavoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setFavoritesPreference(isChecked);
            }
        });

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        mTrailerDataAlreadyLoaded = false;
        mReviewDataAlreadyLoaded = false;
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public void onResume() {
        //initialize loaders
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);

        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri trailerUri = MovieContract.TrailerEntry.buildTrailerWithMovie(mMovieId);
        Uri detailUri = MovieContract.MovieEntry.buildMovieWithMovieId(mMovieId);
        Uri reviewUri = MovieContract.ReviewEntry.buildReviewWithMovie(mMovieId);
        switch (id) {
            case DETAIL_LOADER:
           return new CursorLoader(getActivity(), detailUri, MOVIES_COLUMNS, null, null, null);



            case TRAILER_LOADER:
                return new CursorLoader(getActivity(),trailerUri,TRAILER_COLUMNS,null,null,null);
            case REVIEW_LOADER:
                return new CursorLoader(getActivity(),reviewUri,REVIEW_COLUMNS,null,null,null);

            default:return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        switch (loader.getId()) {
            case DETAIL_LOADER:
            if (data != null && data.moveToFirst()) {
                int movieId = data.getInt(COL_DETAIL_ID);
                Picasso.with(getActivity()).load(data.getString(MovieDetailsFragment.COL_DETAIL_POSTER)).into(mPosterView);

                String title = data.getString(MovieDetailsFragment.COL_DETAIL_TITLE);
                mTitleView.setText(title);

                String overview = data.getString(MovieDetailsFragment.COL_DETAIL_OVERVIEW);
                mOverView.setText(overview);

                String releaseDate = data.getString(MovieDetailsFragment.COL_DETAIL_RELEASE);
                mReleaseDateView.setText(releaseDate);

                double voteAverage = data.getDouble(MovieDetailsFragment.COL_DETAIL_VOTE_AVERAGE);
                mVoteAverageView.setText(voteAverage + "/10");



            }
                break;
            case TRAILER_LOADER:
                if (!mTrailerDataAlreadyLoaded){
                    for (data.moveToFirst();!data.isAfterLast();data.moveToNext()){
                        View trailerView = mLayoutInflater.inflate(R.layout.list_item_trailer,null);

                        if (data.isFirst()){
                            mFirstTrailerUrl = data.getString(COL_DETAIL_POSTER);
                        }
                        LinearLayout linearLayout = (LinearLayout) trailerView.findViewById(R.id.trailer1);
                       linearLayout.setId(data.getPosition());
                       TextView textView = (TextView)linearLayout.findViewById(R.id.list_item_trailer_textBox);
                       textView.setText("Watch Trailer" +(data.getPosition()+1) );

                        final String trailerUrl = data.getString(COL_DETAIL_POSTER);
                        linearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("vnd.youtube:"+ trailerUrl));
                                startActivity(intent);
                            }
                        });
                        mTrailerLayout.addView(trailerView);
                    }
                mTrailerDataAlreadyLoaded=true;
                }
                break;
            case REVIEW_LOADER:
                if (!mReviewDataAlreadyLoaded){
                    for (data.moveToFirst();!data.isAfterLast();data.moveToNext()){
                        View reviewView = mLayoutInflater.inflate(R.layout.list_item_review,null);
                        if (data.isFirst()){
                            TextView review = (TextView) reviewView.findViewById(R.id.list_item_review_title);
                            review.setText("Reviews");
                        }
                        TextView authorView = (TextView) reviewView.findViewById(R.id.list_item_review_author);
                        String author = data.getString(MovieDetailsFragment.COL_REVIEW_AUTHOR);
                        authorView.setText(author+":");

                        TextView contentView = (TextView) reviewView.findViewById(R.id.list_item_review_content);
                        String content = data.getString(MovieDetailsFragment.COL_REVIEW_CONTENT);
                        contentView.setText(content);
                        mReviewLayout.addView(reviewView);
                    }
                    mReviewDataAlreadyLoaded=true;
                }
                break;
            default:
                break;
        }
        if (mShareActionProvider != null){
            mShareActionProvider.setShareIntent(ShareTrailer());
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private Intent ShareTrailer(){
        //share youtube link
        Intent shareTrailer = new Intent(Intent.ACTION_SEND);
        shareTrailer.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareTrailer.setType("text/plain");
        if (mFirstTrailerUrl==null){
            shareTrailer.putExtra(Intent.EXTRA_TEXT,"No Trailers are available ");

        }
        else{
            shareTrailer.putExtra(Intent.EXTRA_TEXT,"https://www.youtube.com/watch?v="+mFirstTrailerUrl);
        }
        return shareTrailer;
    }

    public boolean getFavoritesPreference(){
        Cursor cursor = getActivity().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_FAVORED},
                MovieContract.MovieEntry.COLUMN_ID+" = ?",
                new String[]{""+mMovieId},
                null);
        boolean isFavorite = false;
        if (cursor!=null &&cursor.moveToFirst()){
            if (cursor.getInt(1)==1)
                isFavorite=true;
        }
        return isFavorite;
    }
    public void setFavoritesPreference(boolean favorite){
        ContentValues favoriteValues = new ContentValues();
        favoriteValues.put(MovieContract.MovieEntry.COLUMN_FAVORED, favorite);

        getActivity().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                favoriteValues,
                MovieContract.MovieEntry.COLUMN_ID + "= ?",
                new String[]{""+mMovieId});
    }
}
