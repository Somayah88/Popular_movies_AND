package com.somayahalharbi.popular_movies.utilities;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;



public class NetworkUtils {
    private static final String BASE_URL="http://api.themoviedb.org/3/movie";
   private  static final String IMAGE_BASE_URL="http://image.tmdb.org/t/p/";
   private static final String IMG_SIZE="w185";
   private  static final String PARAM_API_KEY ="api_key";



    public static URL buildURL(String sortOption, String apiKey){

        Uri builtUri=Uri.parse(BASE_URL).buildUpon().appendPath(sortOption).appendQueryParameter(PARAM_API_KEY, apiKey ).build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
