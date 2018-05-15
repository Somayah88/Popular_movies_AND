package com.somayahalharbi.popular_movies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.somayahalharbi.popular_movies.model.Movie;
import com.somayahalharbi.popular_movies.utilities.JSONUtils;
import com.somayahalharbi.popular_movies.utilities.NetworkUtils;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {


    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.loader_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.error_message_tv)
    TextView mErrorMessage;
    private MovieAdapter mMovieAdapter;
    private String sortType = "popular";


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


        loadMovieData(sortType);


    }


    private void loadMovieData(String sortBy) {
        String apiKey = this.getResources().getString(R.string.api_key);
        if (isOnline()) {
            URL url = NetworkUtils.buildURL(sortBy, apiKey);
            new MovieApiQuery(this).execute(url);
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

    // Reference: https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur
    private static class MovieApiQuery extends AsyncTask<URL, Void, String> {
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
    }

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
