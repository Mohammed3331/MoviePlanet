package com.example.android.movieplanet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {


    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.fragment_movie_details, container, false);

        Intent intent=getActivity().getIntent();

        if (intent!=null && intent.hasExtra("movies"))
        {
                Log.v("CHECKING INTENT", "intent data =" + intent);
                Movie movie = intent.getParcelableExtra("movies");
                ImageView posterImage=(ImageView) rootView.findViewById(R.id.poster);
                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185"+movie.posterPath).resize(400,600).placeholder(R.drawable.placeholder).error(R.drawable.error).into(posterImage);

                ((TextView)rootView.findViewById(R.id.title)).setText(movie.title);
                ((TextView)rootView.findViewById(R.id.overview)).setText(movie.overview);
                ((TextView)rootView.findViewById(R.id.release)).setText((movie.releaseDate));
               //this retrieves the ID
               // ((TextView)rootView.findViewById(R.id.release)).setText(Integer.toString(movie.id));
                ((TextView)rootView.findViewById(R.id.rating)).setText(Double.toString(movie.voteAverage)+" / 10");


        }


        return rootView;

    }
}
