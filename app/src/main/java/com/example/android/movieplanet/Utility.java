package com.example.android.movieplanet;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

/**
 * Created by toshiba on 2/5/2016.
 */
public class Utility {
    public static String getSortPreference(Context context){

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        String sortBy= prefs.getString(context.getString(R.string.sort_key),context.getString(R.string.sort_type_popularity));
        return sortBy;
    }

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo =connectivityManager.getActiveNetworkInfo();
        return activeNetInfo!= null && activeNetInfo.isConnectedOrConnecting();

    }
}
