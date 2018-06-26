package com.somayahalharbi.popular_movies.interfaces;


public interface AsyncTaskListener<T> {

    void onTaskComplete(T result);
}
