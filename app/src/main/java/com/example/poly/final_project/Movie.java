package com.example.poly.final_project;

import android.graphics.Bitmap;

/**
 * Created by Poly on 2018-04-16.
 */

public class Movie {
    private String title;
    private String actors;
    private String length;
    private String description;
    private String rating; //rating (out of 4 starts)
    private String genre; //(comedy, horror, action…)
    private String url; //of the movie poster
    private long   id;
    private Bitmap pic;

    // constructor
    public Movie(String title, String actors, String length, String description, String rating, String genre, String url, long id) {
        this.title = title;
        this.actors = actors;
        this.length = length;
        this.description = description;
        this.rating = rating;
        this.genre = genre;
        this.url = url;
        this.id = id;
    }

    public String getTitle() {
        return title;
   }

    public String getActors() {
        return actors;
    }

    public String getLength() {
        return length;
    }

    public String getDescription() {
        return description;
    }

    public String getRating() {
        return rating;
    }

    public String getGenre() {
        return genre;
    }

    public String getUrl() {
        return url;
    }

    public long getId() {
        return id;
    }

    public Bitmap getPic() {
        return pic;
    }
}

