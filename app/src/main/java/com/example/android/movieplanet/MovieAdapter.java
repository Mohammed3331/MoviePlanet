package com.example.android.movieplanet;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by toshiba on 1/27/2016.
 */
public class MovieAdapter extends CursorAdapter {

    LayoutInflater mLayoutInflater;
    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, Cursor c,int flags) {
        super(context, c, flags);
       mLayoutInflater= LayoutInflater.from(context);
    }

    public static class ViewHolder{
        public final ImageView poster;
        public ViewHolder(View view){
            poster=(ImageView)view.findViewById(R.id.list_item_poster);
        }

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.list_item_poster, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;


    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String posterUrl = cursor.getString(MainActivityFragment.COL_POSTER_PATH);
        Picasso.with(context).load(posterUrl).placeholder(R.drawable.placeholder).error(R.drawable.error).into(viewHolder.poster);
    }
}
