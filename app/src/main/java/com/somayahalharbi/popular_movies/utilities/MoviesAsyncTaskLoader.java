package com.somayahalharbi.popular_movies.utilities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.somayahalharbi.popular_movies.interfaces.AsyncTaskListener;

import java.io.IOException;
import java.net.URL;

import static com.somayahalharbi.popular_movies.MainActivity.MOVIE_DB_QUERY_URL;


public class MoviesAsyncTaskLoader extends AsyncTaskLoader<String> {

    private final AsyncTaskListener<String> listener;
    private String resultFromHttp = null;

    Bundle args;
    public MoviesAsyncTaskLoader(@NonNull Context context, Bundle args, AsyncTaskListener<String> listener) {
        super(context);
        this.listener = listener;
        this.args=args;
    }



    @Override
    protected void onStartLoading() {
        if (args != null) {

            if (resultFromHttp != null) {
                deliverResult(resultFromHttp);
            } else {
                forceLoad();
            }
        }

    }

    @Override
    public String loadInBackground() {
        String moviesResults = null;
        String url = args.getString(MOVIE_DB_QUERY_URL);
        if (url.isEmpty())
            return null;

        else {
            try {

                moviesResults = NetworkUtils.getResponseFromHttpUrl(new URL(url));
                return moviesResults;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void deliverResult(String data) {
        resultFromHttp = data;
        super.deliverResult(data);
    }
}

