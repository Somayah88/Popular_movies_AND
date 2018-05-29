package com.somayahalharbi.popular_movies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.somayahalharbi.popular_movies.adapters.ReviewAdapter;
import com.somayahalharbi.popular_movies.adapters.TrailerAdapter;
import com.somayahalharbi.popular_movies.models.Movie;
import com.somayahalharbi.popular_movies.models.Review;
import com.somayahalharbi.popular_movies.models.Trailer;
import com.somayahalharbi.popular_movies.utilities.JSONUtils;
import com.somayahalharbi.popular_movies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler{

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
    TrailerAdapter trailerAdapter;
    ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        GridLayoutManager videoLayoutManager= new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        videoRecyclerView.setLayoutManager(videoLayoutManager);
        trailerAdapter=new TrailerAdapter(this);
        videoRecyclerView.setAdapter(trailerAdapter);
        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }
        Bundle movieData = getIntent().getExtras();
        if (movieData != null) {
            Movie movie = movieData.getParcelable("movie");
            if (movie != null) {
                this.setTitle(movie.getTitle());
                showDetails(movie);
            } else {
                closeOnError();
            }
        }
        else
        {
            closeOnError();
        }
    }

    private void showDetails(Movie movie) {

        mOverviewTextView.setText(movie.getOverview());
        mRatingTextView.setText(String.format(Locale.US,"%.1f/10",movie.getRating()));
        mReleaseDateTextView.setText(movie.getRelease_date());
        Picasso.with(this).load("http://image.tmdb.org/t/p/w500/" + movie.getBackDropImg()).into(mMovieImageView);
        Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + movie.getImage()).into(mMoviePosterImageView);


    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error, Toast.LENGTH_SHORT).show();
    }
    private void showTrailers(String id){
        String apiKey = this.getResources().getString(R.string.api_key);
        URL url = NetworkUtils.buildQueryURL(id,"videos", apiKey);
        new ReviewsApiQuery(this).execute(url);

    }
    private void showReviews(String id){
        String apiKey = this.getResources().getString(R.string.api_key);
        URL url = NetworkUtils.buildQueryURL(id,"reviews", apiKey);

    }

    @Override
    public void onClick(String key) {

    }
    private static class TrailersApiQuery extends AsyncTask<URL, Void, String> {
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
    }
    private static class ReviewsApiQuery extends AsyncTask<URL, Void, String> {
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


        }
    }

}
