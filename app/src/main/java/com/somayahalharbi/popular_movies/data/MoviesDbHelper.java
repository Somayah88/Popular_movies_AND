package com.somayahalharbi.popular_movies.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.somayahalharbi.popular_movies.data.MovieContract.FavoriteMoviesEntry;


public class MoviesDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME="MOVIES.db" ;
   private static final int DATABASE_VERSION=1;

    public MoviesDbHelper(Context context) {
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE="create table "+ FavoriteMoviesEntry.TABLE_NAME+" ( "+
                FavoriteMoviesEntry._ID+" INTEGER PRIMARY KEY, "+
                FavoriteMoviesEntry.COLUMN_MOVIE_ID+  " TEXT NOT NULL, "+
                FavoriteMoviesEntry.COLUMN_NAME+" TEXT NOT NULL );";
        db.execSQL(CREATE_TABLE);




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesEntry.TABLE_NAME);
        onCreate(db);

    }
}
