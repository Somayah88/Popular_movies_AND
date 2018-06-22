package com.somayahalharbi.popular_movies;


import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.somayahalharbi.popular_movies.adapters.MovieAdapter;
import com.somayahalharbi.popular_movies.data.MovieContract;
import com.somayahalharbi.popular_movies.models.Movie;
import com.somayahalharbi.popular_movies.utilities.JSONUtils;
import com.somayahalharbi.popular_movies.utilities.NetworkUtils;
import java.io.IOException;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, LoaderManager.LoaderCallbacks{


    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.loader_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.error_message_tv)
    TextView mErrorMessage;
    private MovieAdapter mMovieAdapter;
    private String sortType = "popular";
    public static final int MOVIE_DB_LOADER=50;
    public static final String MOVIE_DB_QUERY_URL="URL_FOR_MOVIE_DB_QUERY";
    public static final int FAVORITE_MOVIES_LOADER=70;
    private URL queryURl;
    String   apiKey;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        GridLayoutManager gridLayoutManager;
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        } else {
            gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);

        }
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        getSupportLoaderManager().initLoader(MOVIE_DB_LOADER, null, this);

        loadMovieData(sortType);


    }


    private void loadMovieData(String sortBy) {
         apiKey = this.getResources().getString(R.string.api_key);
        LoaderManager loaderManager = getSupportLoaderManager();
        if (isOnline()) {
            if(sortBy.equals("favorite")){
                Loader loader = loaderManager.getLoader(FAVORITE_MOVIES_LOADER);
                if(loader==null){
                    loaderManager.initLoader(FAVORITE_MOVIES_LOADER, null, this);
                }else{
                    loaderManager.restartLoader(FAVORITE_MOVIES_LOADER, null, this);
                }

            }
            else {
                queryURl = NetworkUtils.buildURL(sortBy, apiKey);
                Bundle bundle=new Bundle();
                bundle.putString (MOVIE_DB_QUERY_URL, queryURl.toString());
                Loader loader = loaderManager.getLoader(MOVIE_DB_LOADER);
                if(loader==null){
                    loaderManager.initLoader(MOVIE_DB_LOADER, bundle, this);
                }else{
                    loaderManager.restartLoader(MOVIE_DB_LOADER, bundle, this);
                }
            }



        } else {
            showErrorMessages();
        }


    }

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailsActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("movie", movie);
        startActivity(intentToStartDetailActivity);

    }
    //TODO: save the app state
    //TODO: separate loaders in outside classes
    




    @Override
    public  Loader onCreateLoader(int id, final Bundle args) {
        if(id==MOVIE_DB_LOADER) {
            return new AsyncTaskLoader<String>(this) {


                private String resultFromHttp = null;


                @Override
                protected void onStartLoading() {
                    if (args != null) {

                        if (resultFromHttp != null) {
                            deliverResult(resultFromHttp);
                        } else {

                            mProgressBar.setVisibility(View.VISIBLE);
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
               /* else {
                    if (url.equals("favorite")) {

                    }*/
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
                // }

                @Override
                public void deliverResult(String data) {
                    resultFromHttp = data;
                    super.deliverResult(data);
                }
            };
        }
       else if(id==FAVORITE_MOVIES_LOADER){
            return new AsyncTaskLoader<ArrayList<String>>(this) {



                private ArrayList<String> resultFromHttp = new ArrayList<>();


                @Override
                protected void onStartLoading() {
                    Log.v("MainActivity", "Starting the favorite loader ");


                    if (resultFromHttp.size()>0) {
                        Log.v("MainActivity", "result is not null ");
                            deliverResult(resultFromHttp);
                        } else {
                        Log.v("MainActivity", "result is null ");

                            mProgressBar.setVisibility(View.VISIBLE);
                            forceLoad();
                        }


                }

                @Override
                public ArrayList<String> loadInBackground() {
                    ArrayList<String> moviesResults = new ArrayList();
                    Log.v("MainActivity", "Starting do in the background");
                    Cursor cursor=getContentResolver().query(MovieContract.FavoritMoviesEntry.CONTENT_URI,null,null,null,null);
                    if(cursor.getCount()<1){
                        //TODO:show error msg
                        return null;
                    }else {
                        try {
                            cursor.moveToFirst();
                            do{
                                URL url=NetworkUtils.buildURL(cursor.getString(cursor.getColumnIndex(MovieContract.FavoritMoviesEntry.COLUMN_MOVIE_ID)),apiKey );
                                moviesResults.add(NetworkUtils.getResponseFromHttpUrl(url));
                            }
                            while(cursor.moveToNext());
                            cursor.close();

                            Log.v("MainActivity", "Finished Reading the movie Results "+moviesResults.size());

                            return moviesResults;

                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }


                }

                // }

                @Override
                public void deliverResult(ArrayList<String> data) {
                    resultFromHttp = data;
                    super.deliverResult(data);
                }
            };

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object moviesResult) {
        int id=loader.getId();
        ArrayList<Movie> movies=new ArrayList<>();
        showData();
        if(id==MOVIE_DB_LOADER){
           movies= parseOneString((String)moviesResult);

        }
        if(id==FAVORITE_MOVIES_LOADER){
           movies=parseMultipleStrings((ArrayList<String>) moviesResult);
        }

            mMovieAdapter.setMovieData(movies);
            mRecyclerView.setAdapter(mMovieAdapter);

        }
    @Override
    public void onLoaderReset(Loader loader) {
        mMovieAdapter.clear();

    }
    public ArrayList<Movie> parseOneString(String data)
    { ArrayList<Movie> movies=new ArrayList<>();
        if ( !data.equals("") && !data.isEmpty()) {
            movies = JSONUtils.parseMovieJson(data.toString());
        }
       return movies;
    }
    public ArrayList<Movie> parseMultipleStrings(ArrayList<String> data) {
        ArrayList<Movie> movies = new ArrayList<>();
        if (data.size() != 0 && data != null) {
            for (int i = 0; i < data.size(); i++) {
                Movie temp=JSONUtils.parseOneMovie(data.get(i));
               movies.add(temp);

            }
        }
        return movies;
    }



    // Reference: https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur
    /*private static class MovieApiQuery extends AsyncTask<URL, Void, String> {
        private final WeakReference<MainActivity> mainActivityReference;

        MovieApiQuery(MainActivity context) {
            mainActivityReference = new WeakReference<>(context);
        }


        @Override
        protected String doInBackground(URL... urls) {
            if (urls.length == 0)
                return null;
            URL queryURL = urls[0];
            String moviesResults = null;
            try {
                moviesResults = NetworkUtils.getResponseFromHttpUrl(queryURL);
                return moviesResults;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = mainActivityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String moviesResult) {
            super.onPostExecute(moviesResult);
            MainActivity activity = mainActivityReference.get();
            if (activity == null || activity.isFinishing()) return;
            activity.showData();
            if (!moviesResult.equals("") && !moviesResult.isEmpty()) {
                ArrayList<Movie> movies = JSONUtils.parseMovieJson(moviesResult);
                activity.mMovieAdapter.setMovieData(movies);
                activity.mRecyclerView.setAdapter(activity.mMovieAdapter);
            }


        }
    }*/
    /*private class MovieApiQuery extends AsyncTaskLoader<String> {


        private String resultFromHttp;

        public MovieApiQuery(Context context) {
            super(context);


        }

        @Override
        protected void onStartLoading() {

            if (resultFromHttp != null) {
                deliverResult(resultFromHttp);
            } else {

              mProgressBar.setVisibility(View.VISIBLE);
                forceLoad();
            }

        }

        @Override
        public String loadInBackground() {
            String moviesResults = null;
            String url=args.getString();
            try {

                moviesResults = NetworkUtils.getResponseFromHttpUrl();
                return moviesResults;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void deliverResult(String data) {
            resultFromHttp = data;
            super.deliverResult(data);
        }
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.popular) {
            sortType = "popular";
            mMovieAdapter.clear();
            loadMovieData(sortType);
            return true;
        }
        if (id == R.id.top_rated) {
            sortType = "top_rated";
            mMovieAdapter.clear();
            loadMovieData(sortType);
            return true;
        }
        if (id== R.id.favorite)
        {
            sortType="favorite";
            mMovieAdapter.clear();
            loadMovieData(sortType);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

// The following code is from https://dzone.com/articles/android-snippet-check-if

    private Boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        try {
            networkInfo = cm.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            networkInfo = null;
        }
        if (networkInfo != null && networkInfo.isConnected())
            return true;

        return false;
    }

    private void showErrorMessages() {
        mRecyclerView.setVisibility(View.GONE);
        Toast.makeText(this, this.getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
        mErrorMessage.setVisibility(View.VISIBLE);


    }

    private void showData() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

}
