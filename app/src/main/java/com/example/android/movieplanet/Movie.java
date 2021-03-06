package com.example.android.movieplanet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by toshiba on 12/24/2015.

 */
public class Movie implements Parcelable{

    public int id;
    public String posterPath;
    public String overview;
    public String releaseDate;
    public String title;
    public String backdropPath;
    public double voteAverage;

    public static final String _ID= "id";
    public static final String POSTER_PATH="poster_path";
    public static final String OVERVIEW = "overview";
    public static final String RELEASE_DATE = "release_date";
    public static final String TITLE = "title";
    public static final String BACKDROP_PATH = "backdrop_path";
    public static final String VOTE_AVERAGE = "vote_average";



    public  Movie(){
    }


    public Movie(Parcel in) {

        this.posterPath=in.readString();
        this.overview=in.readString();
        this.releaseDate=in.readString();
        this.id=in.readInt();
        this.title=in.readString();
        this.voteAverage=in.readDouble();
    }


    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.posterPath);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDate);
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeDouble(this.voteAverage);
    }
}
