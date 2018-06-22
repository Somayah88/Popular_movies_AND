package com.somayahalharbi.popular_movies.utilities;

import android.util.Log;

import com.somayahalharbi.popular_movies.models.Movie;
import com.somayahalharbi.popular_movies.models.Review;
import com.somayahalharbi.popular_movies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class JSONUtils {

   private  static ArrayList<Movie> moviesList;
   private static ArrayList<Trailer> movieTrailers;
   private static ArrayList<Review> movieReviews;

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
                String id=movie.optString("id");
                String img=movie.optString("poster_path");
                String title=movie.optString("original_title");
                String overview=movie.optString("overview");
                String release_date=movie.optString("release_date");
                String backDropImage=movie.optString("backdrop_path");

                moviesList.add(new Movie(title,img,overview,release_date, Float.parseFloat(avgRating), backDropImage, id));

            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        Log.v("JSONUtils", "Read the movie JSON array "+ moviesList.size());

        return moviesList;
    }
    public static Movie parseOneMovie(String response)
    {
        Movie movie=null;
        try{
            JSONObject movieResponse=new JSONObject(response);
            String avgRating=movieResponse.optString("vote_average");
            String id=movieResponse.optString("id");
            String img=movieResponse.optString("poster_path");
            String title=movieResponse.optString("original_title");
            String overview=movieResponse.optString("overview");
            String release_date=movieResponse.optString("release_date");
            String backDropImage=movieResponse.optString("backdrop_path");
       movie= new Movie(title,img,overview,release_date, Float.parseFloat(avgRating), backDropImage, id);
            Log.v("JSONUtils", "Read one movie JSON array ");


        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    return movie;
    }
    public static ArrayList<Trailer> parseTrailerJson(String response){
        try{
            movieTrailers=new ArrayList<>();
            JSONObject trailers=new JSONObject(response);
            JSONArray trailersArray=trailers.optJSONArray("results");
            for(int i=0;i<trailersArray.length();i++)
            {
                JSONObject video=trailersArray.optJSONObject(i);
                String key=video.optString("key");
                String name=video.optString("name");
                movieTrailers.add(new Trailer(key,name));
            }


        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        return movieTrailers;

    }


    public static ArrayList<Review> parseReviewJson(String response){
        try{
            movieReviews=new ArrayList<>();
            JSONObject trailers=new JSONObject(response);
            JSONArray trailersArray=trailers.optJSONArray("results");
            for(int i=0;i<trailersArray.length();i++)
            {
                JSONObject video=trailersArray.optJSONObject(i);
                String author=video.optString("author");
                String content=video.optString("content");
                movieReviews.add(new Review(author,content));
            }


        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        return movieReviews;

    }

}
