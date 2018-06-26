package com.somayahalharbi.popular_movies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.somayahalharbi.popular_movies.adapters.ReviewAdapter;
import com.somayahalharbi.popular_movies.adapters.TrailerAdapter;
import com.somayahalharbi.popular_movies.data.MovieContract;
import com.somayahalharbi.popular_movies.interfaces.AsyncTaskListener;
import com.somayahalharbi.popular_movies.models.Movie;
import com.somayahalharbi.popular_movies.models.Review;
import com.somayahalharbi.popular_movies.models.Trailer;
import com.somayahalharbi.popular_movies.utilities.JSONUtils;
import com.somayahalharbi.popular_movies.utilities.MoviesAsyncTaskLoader;
import com.somayahalharbi.popular_movies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.somayahalharbi.popular_movies.MainActivity.MOVIE_DB_QUERY_URL;

//TODO: save the app state and change the AsycTask to loaders

public class DetailsActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler, LoaderManager.LoaderCallbacks {

    @BindView(R.id.overview_tv)
    TextView mOverviewTextView;
    @BindView(R.id.release_date_tv)
    TextView mReleaseDateTextView;
    @BindView(R.id.rating_tv)
    TextView mRatingTextView;
    @BindView(R.id.movie_image)
    ImageView mMovieImageView;
    @BindView(R.id.movie_poster)
    ImageView mMoviePosterImageView;
    @BindView(R.id.video_recycler_view)
    RecyclerView videoRecyclerView;
    @BindView(R.id.reviews_recycler_view)
    RecyclerView reviewsRecyclerView;
    @BindView(R.id.button_favorite)
    ToggleButton mFavoriteButton;
    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;
    private static final int REVIEW_LOADER_ID = 40;
    private static final int TRAILERS_LOADER_ID = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        GridLayoutManager videoLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        videoRecyclerView.setLayoutManager(videoLayoutManager);
        trailerAdapter = new TrailerAdapter(this);
        videoRecyclerView.setAdapter(trailerAdapter);
        GridLayoutManager reviewLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
        reviewsRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewAdapter = new ReviewAdapter();
        reviewsRecyclerView.setAdapter(reviewAdapter);
        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);
        getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, null, this);
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        final Bundle movieData = getIntent().getExtras();
        if (movieData != null) {
            Movie movie = movieData.getParcelable("movie");
            if (movie != null) {
                this.setTitle(movie.getTitle());
                showDetails(movie);
            } else {
                closeOnError();
            }

        } else {
            closeOnError();
        }



    }

    private void showDetails(final Movie movie) {

        mOverviewTextView.setText(movie.getOverview());
        mRatingTextView.setText(String.format(Locale.US, "%.1f/10", movie.getRating()));
        mReleaseDateTextView.setText(movie.getRelease_date());
        Picasso.with(this).load("http://image.tmdb.org/t/p/w500/" + movie.getBackDropImg()).into(mMovieImageView);
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + movie.getImage()).into(mMoviePosterImageView);

        Cursor cursor = getContentResolver().query(MovieContract.FavoritMoviesEntry.CONTENT_URI, null, MovieContract.FavoritMoviesEntry.COLUMN_MOVIE_ID + " = " + movie.getId(), null, null);

        if (cursor.getCount() > 0) {
            mFavoriteButton.setChecked(true);
        } else
            mFavoriteButton.setChecked(false);

        mFavoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Cursor cursor = getContentResolver().query(MovieContract.FavoritMoviesEntry.CONTENT_URI, null, MovieContract.FavoritMoviesEntry.COLUMN_MOVIE_ID + " = " + movie.getId(), null, null);
                    if (cursor.getCount() < 1) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MovieContract.FavoritMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
                        contentValues.put(MovieContract.FavoritMoviesEntry.COLUMN_NAME, movie.getTitle());
                        Uri uri = getContentResolver().insert(MovieContract.FavoritMoviesEntry.CONTENT_URI, contentValues);
                    }
                } else {
                    Uri uri = MovieContract.FavoritMoviesEntry.CONTENT_URI.buildUpon().appendPath(movie.getId()).build();
                    getContentResolver().delete(uri, null, null);
                }
            }
        });
        showTrailers(movie.getId());
        showReviews(movie.getId());


    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error, Toast.LENGTH_SHORT).show();
    }

    private void showTrailers(String id) {
        String apiKey = this.getResources().getString(R.string.api_key);
        URL url = NetworkUtils.buildQueryURL(id, "videos", apiKey);
        LoaderManager loaderManager = getSupportLoaderManager();

        Bundle bundle = new Bundle();
        bundle.putString(MOVIE_DB_QUERY_URL, url.toString());
        Loader loader = loaderManager.getLoader(TRAILERS_LOADER_ID);
        if (loader == null) {
            loaderManager.initLoader(TRAILERS_LOADER_ID, bundle, this);
        } else {
            loaderManager.restartLoader(TRAILERS_LOADER_ID, bundle, this);
        }

    }

    private void showReviews(String id) {
        String apiKey = this.getResources().getString(R.string.api_key);
        URL url = NetworkUtils.buildQueryURL(id, "reviews", apiKey);
        LoaderManager loaderManager = getSupportLoaderManager();

        Bundle bundle = new Bundle();
        bundle.putString(MOVIE_DB_QUERY_URL, url.toString());
        Loader loader = loaderManager.getLoader(REVIEW_LOADER_ID);
        if (loader == null) {
            loaderManager.initLoader(REVIEW_LOADER_ID, bundle, this);
        } else {
            loaderManager.restartLoader(REVIEW_LOADER_ID, bundle, this);
        }
    }

    @Override
    public void onClick(String key) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + key)));


    }


    @Override
    public Loader onCreateLoader(int id,  Bundle args) {
        if (id == REVIEW_LOADER_ID || id == TRAILERS_LOADER_ID)
            return new MoviesAsyncTaskLoader(this, args, new AsyncTaskListener<String>() {
                @Override
                public void onTaskComplete(String result) {

                }
            });
        return null;
    }

    @Override
    public void onLoadFinished( Loader loader, Object data) {
        int id = loader.getId();

        if (id == REVIEW_LOADER_ID) {
            if(!data.equals("") && data!=null ) {
                ArrayList<Review> reviews = JSONUtils.parseReviewJson((String) data);
                reviewAdapter.setReviewsData(reviews);
                reviewsRecyclerView.setAdapter(reviewAdapter);
            }


        }
        if (id == TRAILERS_LOADER_ID) {
            if(!data.equals("") && data!=null ) {
                ArrayList<Trailer> trailers = JSONUtils.parseTrailerJson((String) data);
                trailerAdapter.setTrailersData(trailers);
                videoRecyclerView.setAdapter(trailerAdapter);
            }
        }


    }

    @Override
    public void onLoaderReset( Loader loader) {
        if(loader.getId()==REVIEW_LOADER_ID)
            reviewAdapter.clear();
        if(loader.getId()==TRAILERS_LOADER_ID)
            trailerAdapter.clear();

    }
    /*private static class TrailersApiQuery extends AsyncTask<URL, Void, String> {
        private final WeakReference<DetailsActivity> detailActivityReference;

        TrailersApiQuery(DetailsActivity context) {
            detailActivityReference = new WeakReference<>(context);
        }


        @Override
        protected String doInBackground(URL... urls) {
            if (urls.length == 0)
                return null;
            URL queryURL = urls[0];
            String trailersResults = null;
            try {
                trailersResults = NetworkUtils.getResponseFromHttpUrl(queryURL);
                return trailersResults;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DetailsActivity activity = detailActivityReference.get();
            if (activity == null || activity.isFinishing()) return;
        }

        @Override
        protected void onPostExecute(String trailerResult) {
            super.onPostExecute(trailerResult);
           DetailsActivity activity = detailActivityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (!trailerResult.equals("") && !trailerResult.isEmpty()) {
                ArrayList<Trailer> trailers = JSONUtils.parseTrailerJson(trailerResult);
                activity.trailerAdapter.setTrailersData(trailers);
                activity.videoRecyclerView.setAdapter(activity.trailerAdapter);
            }


        }
    }*/
    /*private static class ReviewsApiQuery extends AsyncTask<URL, Void, String> {
        private final WeakReference<DetailsActivity> detailActivityReference;

        ReviewsApiQuery(DetailsActivity context) {
            detailActivityReference = new WeakReference<>(context);
        }


        @Override
        protected String doInBackground(URL... urls) {
            if (urls.length == 0)
                return null;
            URL queryURL = urls[0];
            String reviewsResults = null;
            try {
                reviewsResults = NetworkUtils.getResponseFromHttpUrl(queryURL);
                return reviewsResults;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DetailsActivity activity = detailActivityReference.get();
            if (activity == null || activity.isFinishing()) return;
        }

        @Override
        protected void onPostExecute(String reviewResult) {
            super.onPostExecute(reviewResult);
            DetailsActivity activity = detailActivityReference.get();
            if (activity == null || activity.isFinishing()) return;
            if (!reviewResult.equals("") && !reviewResult.isEmpty()) {
                ArrayList<Review> reviews = JSONUtils.parseReviewJson(reviewResult);
                activity.reviewAdapter.setReviewsData(reviews);
                activity.reviewsRecyclerView.setAdapter(activity.reviewAdapter);
            }


        }*/
    // }

}
