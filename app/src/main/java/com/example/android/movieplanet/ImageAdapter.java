package com.example.android.movieplanet;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by toshiba on 12/24/2015.
 */
public class ImageAdapter extends ArrayAdapter<Movie> {
    ImageView imageView;
    private Context mContext;
    private List<Movie> array;

    public ImageAdapter(Context c, List<Movie> paths) {
        super(c,0,paths);
        mContext = c;
        array = paths;
    }

    public int getCount() {
        return array.size();
    }

    //public Movie getItem(int position) {
    //  return null;
    //  }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie=getItem(position);
        String url = "http://image.tmdb.org/t/p/w185";

        if (convertView == null) {
            //if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            imageView = (ImageView) convertView;
        }
        // String url=array.get(position).posterPath;
        Picasso.with(mContext).load(url+array.get(position).posterPath).resize(300, 750).placeholder(R.drawable.placeholder).error(R.drawable.error).into(imageView);
        return imageView;
    }
}