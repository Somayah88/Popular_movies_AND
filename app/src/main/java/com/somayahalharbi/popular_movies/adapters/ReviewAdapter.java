package com.somayahalharbi.popular_movies.adapters;



import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.somayahalharbi.popular_movies.R;
import com.somayahalharbi.popular_movies.models.Review;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder>{
    private ArrayList<Review> reviews=new ArrayList<>();


    @NonNull
    @Override
    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ReviewAdapter.ReviewAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapterViewHolder holder, int position) {
        String author=reviews.get(position).getAuthor();
        String content=reviews.get(position).getContent();
        holder.authorTextView.setText(author);
        holder.contentTextView.setText(content);

    }


    @Override
    public int getItemCount() {
        if(reviews.isEmpty())
            return 0;
        else
            return reviews.size();
    }



    public void setReviewsData(ArrayList<Review> reviewsData) {
        clear();
        reviews=reviewsData;
        notifyDataSetChanged();

    }
    public void clear() {
        final int size = reviews.size();
        reviews.clear();

       notifyItemRangeRemoved(0, size);
    }



    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder  {

        @BindView(R.id.author_tv)
        TextView authorTextView ;
        @BindView(R.id.content_tv)
        TextView contentTextView;


        public ReviewAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);


        }

    }
}
