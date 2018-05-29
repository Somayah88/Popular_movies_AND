package com.somayahalharbi.popular_movies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.somayahalharbi.popular_movies.R;
import com.somayahalharbi.popular_movies.models.Movie;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<Movie> moviesList = new ArrayList<>();
    private final MovieAdapterOnClickHandler mClickHandler;

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }


    @NonNull
    @Override
    public MovieAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);


        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MovieAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapterViewHolder holder, int position) {

        String img = moviesList.get(position).getImage();
        Picasso.with(holder.mMovieImage.getContext()).load("http://image.tmdb.org/t/p/w185/" + img).into(holder.mMovieImage);


    }

    @Override
    public int getItemCount() {
        if (moviesList.isEmpty())
            return 0;
        return moviesList.size();
    }

    public void setMovieData(ArrayList<Movie> movies) {
        clear();
        moviesList = movies;
        notifyDataSetChanged();
    }

    //From StackOverflow: https://stackoverflow.com/questions/29978695/remove-all-items-from-recyclerview
    public void clear() {
        final int size = moviesList.size();
        moviesList.clear();
        notifyItemRangeRemoved(0, size);
    }


    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.movie_image)
        ImageView mMovieImage;


        public MovieAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Movie movieData = moviesList.get(position);
            mClickHandler.onClick(movieData);
        }
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);

    }


}
