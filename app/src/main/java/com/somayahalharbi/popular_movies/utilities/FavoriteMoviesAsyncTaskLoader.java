package com.somayahalharbi.popular_movies.utilities;


import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.somayahalharbi.popular_movies.R;
import com.somayahalharbi.popular_movies.data.MovieContract;
import com.somayahalharbi.popular_movies.interfaces.AsyncTaskListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class FavoriteMoviesAsyncTaskLoader extends AsyncTaskLoader<ArrayList<String>> {
    private final AsyncTaskListener<ArrayList<String>> listener;
    private ArrayList<String> resultFromHttp = new ArrayList<>();

    public FavoriteMoviesAsyncTaskLoader(@NonNull Context context, AsyncTaskListener<ArrayList<String>> listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onStartLoading() {

        if (resultFromHttp.size()>0) {
            deliverResult(resultFromHttp);
        } else {
            forceLoad();
        }


    }

    @Override
    public ArrayList<String> loadInBackground() {
        ArrayList<String> moviesResults = new ArrayList();
        Context context=getContext();
        Cursor cursor=context.getContentResolver().query(MovieContract.FavoriteMoviesEntry.CONTENT_URI,null,null,null,null);
        if(cursor.getCount()<1){
            return null;
        }else {
            try {
                cursor.moveToFirst();
                do{
                    URL url=NetworkUtils.buildURL(cursor.getString(cursor.getColumnIndex(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID)),

                            context.getResources().getString(R.string.api_key));
                    moviesResults.add(NetworkUtils.getResponseFromHttpUrl(url));
                }
                while(cursor.moveToNext());
                cursor.close();

                return moviesResults;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


    }


    @Override
    public void deliverResult(ArrayList<String> data) {
        resultFromHttp = data;
        super.deliverResult(data);
    }
}
