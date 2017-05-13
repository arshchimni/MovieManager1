package com.mytechwall.android.moviemanager.model;

import java.io.Serializable;

/**
 * Created by arshdeep chimni on 16-04-2017.
 */

public class MovieData implements Serializable{
    String id;
    String movieTitle;
    String overview;
    float voteAverage;
    float coteCount;
    String posterPath;
    String backdropPath;


    public MovieData(String id, String movieTitle, String overview,
                     float voteAverage, float coteCount,
                     String posterPath, String backdropPath) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.coteCount = coteCount;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public float getCoteCount() {
        return coteCount;
    }

    public void setCoteCount(float coteCount) {
        this.coteCount = coteCount;
    }

    public String getPosterPath() {
        return "https://image.tmdb.org/t/p/w342"+posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return "https://image.tmdb.org/t/p/w780"+backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }


}
