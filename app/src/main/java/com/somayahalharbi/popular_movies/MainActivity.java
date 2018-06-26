package com.somayahalharbi.popular_movies;


import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.content.Loader;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
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

import com.somayahalharbi.popular_movies.adapters.MovieAdapter;
import com.somayahalharbi.popular_movies.interfaces.AsyncTaskListener;
import com.somayahalharbi.popular_movies.models.Movie;
import com.somayahalharbi.popular_movies.utilities.FavoriteMoviesAsyncTaskLoader;
import com.somayahalharbi.popular_movies.utilities.JSONUtils;
import com.somayahalharbi.popular_movies.utilities.MoviesAsyncTaskLoader;
import com.somayahalharbi.popular_movies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, LoaderManager.LoaderCallbacks {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.loader_indicator)
    ProgressBar mProgressBar;
    @BindView(R.id.error_message_tv)
    TextView mErrorMessage;
    private MovieAdapter mMovieAdapter;
    private String sortType = "popular";
    public static final int MOVIE_DB_LOADER = 50;
    public static final String MOVIE_DB_QUERY_URL = "URL_FOR_MOVIE_DB_QUERY";
    public static final int FAVORITE_MOVIES_LOADER = 70;
    private URL queryURl;
    String apiKey;
    private static final String MOVIES_STATE = "movies_state";
    private static final String SAVED_RECYCLERVIEW_STATUS = "saved_recyclerview_statues";
    private ArrayList<Movie> savedMovies;
    private Parcelable movieList;


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
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_RECYCLERVIEW_STATUS)) {
            savedMovies = savedInstanceState.getParcelableArrayList(MOVIES_STATE);
            mMovieAdapter.setMovieData(savedMovies);
            mRecyclerView.setAdapter(mMovieAdapter);
        } else
            loadMovieData(sortType);


    }


    private void loadMovieData(String sortBy) {
        apiKey = this.getResources().getString(R.string.api_key);
        LoaderManager loaderManager = getSupportLoaderManager();
        if (isOnline()) {
            if (sortBy.equals("favorite")) {
                Loader loader = loaderManager.getLoader(FAVORITE_MOVIES_LOADER);
                if (loader == null) {
                    loaderManager.initLoader(FAVORITE_MOVIES_LOADER, null, this);
                } else {
                    loaderManager.restartLoader(FAVORITE_MOVIES_LOADER, null, this);
                }

            } else {
                queryURl = NetworkUtils.buildURL(sortBy, apiKey);
                Bundle bundle = new Bundle();
                bundle.putString(MOVIE_DB_QUERY_URL, queryURl.toString());
                Loader loader = loaderManager.getLoader(MOVIE_DB_LOADER);
                if (loader == null) {
                    loaderManager.initLoader(MOVIE_DB_LOADER, bundle, this);
                } else {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        movieList = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(SAVED_RECYCLERVIEW_STATUS, movieList);
        outState.putParcelableArrayList(MOVIES_STATE, savedMovies);


    }

    @Override
    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);

        if (inState != null) {
            movieList = inState.getParcelable(SAVED_RECYCLERVIEW_STATUS);
            savedMovies = inState.getParcelableArrayList(MOVIES_STATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (movieList != null) {
            mRecyclerView.getLayoutManager().onRestoreInstanceState(movieList);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        movieList = mRecyclerView.getLayoutManager().onSaveInstanceState();

    }


    @Override
    public Loader onCreateLoader(int id, final Bundle args) {
        if (id == MOVIE_DB_LOADER) {
            return new MoviesAsyncTaskLoader(this, args, new AsyncTaskListener<String>() {
                @Override
                public void onTaskComplete(String result) {

                }
            });
        } else if (id == FAVORITE_MOVIES_LOADER) {
            return new FavoriteMoviesAsyncTaskLoader(this, new AsyncTaskListener<ArrayList<String>>() {
                @Override
                public void onTaskComplete(ArrayList<String> result) {

                }
            });

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object moviesResult) {
        int id = loader.getId();
        ArrayList<Movie> movies = new ArrayList<>();
        showData();

        if (id == MOVIE_DB_LOADER) {
            if (moviesResult != null) {
                movies = parseOneString((String) moviesResult);
                //savedMovies = parseOneString((String) moviesResult);
            } else
                showErrorMessages();


        }
        if (id == FAVORITE_MOVIES_LOADER) {
            if (moviesResult != null) {

                movies = parseMultipleStrings((ArrayList<String>) moviesResult);
            } else {
                showNoFavoriteMovies();
            }

        }
        savedMovies = movies;
        mMovieAdapter.setMovieData(movies);
        mRecyclerView.setAdapter(mMovieAdapter);
    }


    @Override
    public void onLoaderReset(Loader loader) {
        mMovieAdapter.clear();

    }

    public ArrayList<Movie> parseOneString(String data) {
        ArrayList<Movie> movies = new ArrayList<>();
        if (!data.equals("") && !data.isEmpty()) {
            movies = JSONUtils.parseMovieJson(data.toString());
        }
        return movies;
    }

    public ArrayList<Movie> parseMultipleStrings(ArrayList<String> data) {
        ArrayList<Movie> movies = new ArrayList<>();
        if (data.size() != 0 && data != null) {
            for (int i = 0; i < data.size(); i++) {
                Movie temp = JSONUtils.parseOneMovie(data.get(i));
                movies.add(temp);

            }
        }
        return movies;
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
        if (id == R.id.favorite) {
            sortType = "favorite";
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

    private void showNoFavoriteMovies() {
        mRecyclerView.setVisibility(View.GONE);
        Toast.makeText(this, this.getResources().getString(R.string.empty_favorite), Toast.LENGTH_LONG).show();
        mErrorMessage.setVisibility(View.VISIBLE);
        mErrorMessage.setText(R.string.empty_favorite);

    }


    private void showData() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

}
