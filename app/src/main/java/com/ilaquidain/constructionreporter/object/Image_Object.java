package com.ilaquidain.constructionreporter.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class Image_Object implements Parcelable, Serializable {

    private String PathDevice;
    private String PathFirebase;
    private String PhotoDate;
    private String PhotoCoordinates;
    private String PhotoDescription;
    private String PhotoBearing;
    private String PhotoActivity;

    public void setPathDevice(String s0){PathDevice = s0;}
    public String getPathDevice(){return PathDevice;}

    public void setPathFirebase(String s1){PathFirebase = s1;}
    public String getPathFirebase(){return PathFirebase;}

    public void setPhotoDate(String s2){PhotoDate = s2;}
    public String getPhotoDate(){return PhotoDate;}

    public void setPhotoCoordinates(String s3){PhotoCoordinates = s3;}
    public String getPhotoCoordinates(){return PhotoCoordinates;}

    public void setPhotoDescription(String s4){PhotoDescription = s4;}
    public String getPhotoDescription(){return PhotoDescription;}

    public void setPhotoBearing(String s5){PhotoBearing = s5;}
    public String getPhotoBearing(){return PhotoBearing;}

    public void setPhotoActivity(String s6){PhotoActivity = s6;}
    public String getPhotoActivity(){return PhotoActivity;}

    public Image_Object(){
        this.PathDevice = null;
        this.PathFirebase = null;
        this.PhotoDate = null;
        this.PhotoCoordinates = null;
        this.PhotoDescription = null;
        this.PhotoBearing = null;
        this.PhotoActivity = null;
    }

    public Image_Object(Parcel in){
        PathDevice = in.readString();
        PathFirebase = in.readString();
        PhotoDate = in.readString();
        PhotoCoordinates = in.readString();
        PhotoDescription = in.readString();
        PhotoBearing = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(PathDevice);
        dest.writeString(PathFirebase);
        dest.writeString(PhotoDate);
        dest.writeString(PhotoCoordinates);
        dest.writeString(PhotoDescription);
        dest.writeString(PhotoBearing);
        dest.writeString(PhotoActivity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator CREATOR = new Creator(){
        @Override
        public Object createFromParcel(Parcel source) {
            return new Image_Object(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Image_Object[size];
        }
    };
}
