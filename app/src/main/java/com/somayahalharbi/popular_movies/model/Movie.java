package com.somayahalharbi.popular_movies.model;

import android.os.Parcel;
import android.os.Parcelable;


public class Movie implements Parcelable {

    private String title;
    private String image;
    private String overview;
    private String release_date;
    private float rating;
    private String backDropImg;



   public Movie(){

    }

  public Movie(String title, String img, String overview, String releaseDate, float rating ,String backDrop){

     this.title=title;
     this.image=img;
     this.overview=overview;
     this.release_date=releaseDate;
     this.rating=rating;
     this.backDropImg=backDrop;

 }
 private Movie(Parcel in)
 {
     this.title=in.readString();
     this.image=in.readString();
     this.overview=in.readString();
     this.release_date=in.readString();
     this.rating=in.readFloat();
     this.backDropImg=in.readString();

 }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
    public void setBackDropImg(String backImg)
    {
        this.backDropImg=backImg;
    }
    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public float getRating() {
        return rating;
    }
    public String getBackDropImg() {
        return backDropImg;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      dest.writeString(title);
      dest.writeString(image);
      dest.writeString(overview);
      dest.writeString(release_date);
      dest.writeFloat(rating);
      dest.writeString(backDropImg);


    }
    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
