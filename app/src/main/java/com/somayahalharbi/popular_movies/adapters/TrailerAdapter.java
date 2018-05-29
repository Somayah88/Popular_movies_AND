package com.somayahalharbi.popular_movies.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.somayahalharbi.popular_movies.R;
import com.somayahalharbi.popular_movies.models.Trailer;

import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {

    private ArrayList<Trailer> trailers=new ArrayList<>();
    private final TrailerAdapterOnClickHandler mClickHandler;

    public TrailerAdapter(TrailerAdapterOnClickHandler clickHandler)
    {
        mClickHandler=clickHandler;
    }



    @NonNull
    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new TrailerAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterViewHolder holder, int position) {
        String title=trailers.get(position).getName();
        holder.videoTitle.setText(title);

    }


    @Override
    public int getItemCount() {
        if(trailers.isEmpty())
        return 0;
        else
            return trailers.size();
    }
    public void setTrailersData(ArrayList<Trailer> trailersData) {
        clear();
        trailers = trailersData;
        notifyDataSetChanged();
    }
    public void clear() {
        final int size = trailers.size();
        trailers.clear();
        notifyItemRangeRemoved(0, size);
    }



    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.video_title)
        TextView videoTitle ;


        public TrailerAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            //open new intent with youtube video
            String key=trailers.get(position).getKey();
            mClickHandler.onClick(key);

        }
    }

    public interface TrailerAdapterOnClickHandler {
        void onClick(String key);

    }
}
