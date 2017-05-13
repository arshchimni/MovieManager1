package com.mytechwall.android.moviemanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mytechwall.android.moviemanager.R;
import com.mytechwall.android.moviemanager.activities.MovieDetailActivity;
import com.mytechwall.android.moviemanager.model.MovieData;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by arshdeep chimni on 16-04-2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    List<MovieData>movies;
    Context context;

    public MovieAdapter(List<MovieData> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    public Context getContextAdapter(){
        return context;
    }


    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieViewHolder holder, int position) {
        MovieData movieData=movies.get(position);
        holder.movieTitile.setText(movieData.getMovieTitle());
        holder.movieOverview.setText(movieData.getOverview());
        //System.out.println(movieData.getPosterPath());

        Picasso.with(getContextAdapter())
                .load(movieData.getPosterPath())
                .into(holder.ivMoviePoster);

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public  class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.ivMoviePoster)
        ImageView ivMoviePoster;
        @BindView(R.id.movieTitile)
        TextView movieTitile;
        @BindView(R.id.movieOverview)
        TextView movieOverview;
        @BindView(R.id.rlMovieInfo)
        RelativeLayout rlMovieInfo;
        @BindView(R.id.cvMoview)
        CardView cvMoview;

        MovieViewHolder(View view) {
            super( view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            MovieData movieData=movies.get(getAdapterPosition());
            Intent intent=new Intent(getContextAdapter(), MovieDetailActivity.class)
                    .putExtra("MOVIE",movieData);
            getContextAdapter().startActivity(intent);


        }
    }
}
