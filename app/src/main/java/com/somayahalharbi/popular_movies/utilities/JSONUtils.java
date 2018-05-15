package com.somayahalharbi.popular_movies.utilities;

import android.util.Log;

import com.somayahalharbi.popular_movies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class JSONUtils {

   private  static ArrayList<Movie> moviesList;

    public static ArrayList<Movie> parseMovieJson(String response)
    {

        try{
            moviesList=new ArrayList<>();
            JSONObject movieResponse=new JSONObject(response);
            JSONArray moviesJsonArray= movieResponse.optJSONArray("results");
            Log.v("JSONUtils", "Read the movie JSON array "+ moviesJsonArray.length());

            for(int i=0; i<moviesJsonArray.length();i++)
            {
                JSONObject movie=moviesJsonArray.optJSONObject(i);
                String avgRating=movie.optString("vote_average");
                String img=movie.optString("poster_path");
                String title=movie.optString("original_title");
                String overview=movie.optString("overview");
                String release_date=movie.optString("release_date");
                String backDropImage=movie.optString("backdrop_path");

                moviesList.add(new Movie(title,img,overview,release_date, Float.parseFloat(avgRating), backDropImage));

            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        Log.v("JSONUtils", "Read the movie JSON array "+ moviesList.size());

        return moviesList;
    }


}
