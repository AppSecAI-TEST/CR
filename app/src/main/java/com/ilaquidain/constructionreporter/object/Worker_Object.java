package com.ilaquidain.constructionreporter.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.UUID;

public class Worker_Object implements Parcelable, Serializable {

    private String IdNumber;
    private String Name;
    private String Trade;
    private String Company;
    private String Hours;
    private String Activity;

    public void setIdNumber(String s0){IdNumber = s0;}
    public String getIdNumber(){return IdNumber;}

    public void setName(String s1){Name = s1;}
    public String getName(){return Name;}

    public void setTrade(String s2){Trade = s2;}
    public String getTrade(){return Trade;}

    public void setCompany(String s3){Company = s3;}
    public String getCompany(){return Company;}

    public void setHours(String s4){Hours = s4;}
    public String getHours(){return Hours;}

    public void setActivity(String s5){Activity = s5;}
    public String getActivity(){return Activity;}

    public Worker_Object(){
        IdNumber = UUID.randomUUID().toString();
        Name = "";
        Trade = "";
        Company = "";
        Hours = "8";
        Activity = "";
    }

    public Worker_Object(Parcel in){
        IdNumber = in.readString();
        Name = in.readString();
        Trade = in.readString();
        Company = in.readString();
        Hours = in.readString();
        Activity = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(IdNumber);
        dest.writeString(Name);
        dest.writeString(Trade);
        dest.writeString(Company);
        dest.writeString(Hours);
        dest.writeString(Activity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Object createFromParcel(Parcel source) {
            return new Worker_Object(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Worker_Object[size];
        }
    };
}
