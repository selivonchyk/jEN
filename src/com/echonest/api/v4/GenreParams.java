package com.echonest.api.v4;

public class GenreParams extends Params {
    public void setName(String name) {
        add(Genre.GENRE_NAME, name);
    }
}
