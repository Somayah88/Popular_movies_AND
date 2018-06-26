package com.somayahalharbi.popular_movies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.somayahalharbi.popular_movies.MainActivity.MOVIE_DB_QUERY_URL;


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
    Parcelable reviewList;
    Parcelable trailerList;
    private static final String TRAILER_SAVED_STATE = "trailers_state";
    private static final String REVIEWS_SAVED_STATE = "reviews_state";


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
        Cursor cursor = getContentResolver().query(MovieContract.FavoriteMoviesEntry.CONTENT_URI, null, MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = " + movie.getId(), null, null);
        if (cursor.getCount() > 0) {
            mFavoriteButton.setChecked(true);
        } else
            mFavoriteButton.setChecked(false);

        mFavoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Cursor cursor = getContentResolver().query(MovieContract.FavoriteMoviesEntry.CONTENT_URI, null, MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = " + movie.getId(), null, null);
                    if (cursor.getCount() < 1) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
                        contentValues.put(MovieContract.FavoriteMoviesEntry.COLUMN_NAME, movie.getTitle());
                        Uri uri = getContentResolver().insert(MovieContract.FavoriteMoviesEntry.CONTENT_URI, contentValues);
                    }
                } else {
                    Uri uri = MovieContract.FavoriteMoviesEntry.CONTENT_URI.buildUpon().appendPath(movie.getId()).build();
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
    protected void onResume() {
        super.onResume();

        if (reviewList != null) {
            reviewsRecyclerView.getLayoutManager().onRestoreInstanceState(reviewList);

        }
        if (trailerList != null) {
            videoRecyclerView.getLayoutManager().onRestoreInstanceState(trailerList);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        reviewList = reviewsRecyclerView.getLayoutManager().onSaveInstanceState();
        trailerList = videoRecyclerView.getLayoutManager().onSaveInstanceState();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        trailerList = videoRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(TRAILER_SAVED_STATE, trailerList);
        reviewList = reviewsRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(REVIEWS_SAVED_STATE, reviewList);


    }

    @Override
    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);

        if (inState != null) {
            reviewList = inState.getParcelable(REVIEWS_SAVED_STATE);
            trailerList = inState.getParcelable(TRAILER_SAVED_STATE);
        }
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == REVIEW_LOADER_ID || id == TRAILERS_LOADER_ID)
            return new MoviesAsyncTaskLoader(this, args, new AsyncTaskListener<String>() {
                @Override
                public void onTaskComplete(String result) {

                }
            });
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();

        if (id == REVIEW_LOADER_ID) {
            if (!data.equals("") && data != null) {
                ArrayList<Review> reviews = JSONUtils.parseReviewJson((String) data);
                reviewAdapter.setReviewsData(reviews);
                reviewsRecyclerView.setAdapter(reviewAdapter);
            }


        }
        if (id == TRAILERS_LOADER_ID) {
            if (!data.equals("") && data != null) {
                ArrayList<Trailer> trailers = JSONUtils.parseTrailerJson((String) data);
                trailerAdapter.setTrailersData(trailers);
                videoRecyclerView.setAdapter(trailerAdapter);
            }
        }


    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (loader.getId() == REVIEW_LOADER_ID)
            reviewAdapter.clear();
        if (loader.getId() == TRAILERS_LOADER_ID)
            trailerAdapter.clear();

    }


}
