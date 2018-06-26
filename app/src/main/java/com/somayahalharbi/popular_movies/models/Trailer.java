package com.somayahalharbi.popular_movies.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable{

    private String key;
    private String name;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

public Trailer(String key, String name)
{
    this.key=key;
    this.name=name;
}
private Trailer (Parcel in)
{
    this.key=in.readString();
    this.name=in.readString();

}
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);


    }
    public static final Parcelable.Creator<Trailer> CREATOR
            = new Parcelable.Creator<Trailer>() {
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

}
