package com.example.android.movieplanet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        Intent intent=getActivity().getIntent();
        View rootView= inflater.inflate(R.layout.fragment_movie_details, container, false);

        if (intent!=null && intent.hasExtra("poster")){
            String poster = intent.getStringExtra("poster");
            ImageView posterImage=(ImageView) rootView.findViewById(R.id.poster);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185"+poster).resize(400,600).into(posterImage);
        }
        if (intent!=null && intent.hasExtra("originalTitle")){
            String title = intent.getStringExtra("originalTitle");
            ((TextView)rootView.findViewById(R.id.title)).setText(title);
        }

        if (intent!=null && intent.hasExtra("releaseDate")){
            String releaseDate = intent.getStringExtra("releaseDate");
            ((TextView)rootView.findViewById(R.id.release)).setText(releaseDate);
        }

        if (intent!=null && intent.hasExtra("rating")){
            String rating = intent.getStringExtra("rating");
            ((TextView)rootView.findViewById(R.id.rating)).setText(rating);
        }

        if (intent!=null && intent.hasExtra("overview")){
            String overview = intent.getStringExtra("overview");
            ((TextView)rootView.findViewById(R.id.overview)).setText(overview);
        }
        return rootView;
    }
}
