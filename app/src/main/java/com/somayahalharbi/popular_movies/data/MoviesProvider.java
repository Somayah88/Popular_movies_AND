package com.somayahalharbi.popular_movies.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.somayahalharbi.popular_movies.data.MovieContract.FavoriteMoviesEntry;

import static com.somayahalharbi.popular_movies.data.MovieContract.FavoriteMoviesEntry.TABLE_NAME;


public class MoviesProvider extends ContentProvider{
    public static final int FAVORITE=100;
    public static final int FAVORITE_WITH_ID=101;

    private static final UriMatcher mUriMatcher=buildUriMatcher();

    private MoviesDbHelper mMoviesHelper;

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITES, FAVORITE);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITES+"/#",FAVORITE_WITH_ID);
        return uriMatcher;

    }

    @Override
    public boolean onCreate() {
        Context context=getContext();
        mMoviesHelper=new MoviesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db=mMoviesHelper.getReadableDatabase();
        int match =mUriMatcher.match(uri);
        Cursor returnCursor;
        switch (match){
            case FAVORITE:
                returnCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return returnCursor;


    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db=mMoviesHelper.getWritableDatabase();
        int match=mUriMatcher.match(uri);
        Uri returnUri;

        switch(match) {
            case FAVORITE:
                long id = db.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMoviesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
                getContext().getContentResolver().notifyChange(uri, null);
         return returnUri;



    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mMoviesHelper.getWritableDatabase();

        int match = mUriMatcher.match(uri);

        int movieDeleted;


        switch (match) {
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                movieDeleted = db.delete(TABLE_NAME, FavoriteMoviesEntry.COLUMN_MOVIE_ID+ "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (movieDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return movieDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
