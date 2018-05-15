package com.somayahalharbi.popular_movies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.somayahalharbi.popular_movies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
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
}
