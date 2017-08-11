package com.ilaquidain.constructionreporter.object;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;


public class Task_Object implements Parcelable, Serializable {

    private String TaskName;
    private String TaskDescription;

    public void setTaskName(String s0){
        TaskName = s0;}
    public String getTaskName(){return TaskName;}

    public void setTaskDescription(String s1){
        TaskDescription = s1;}
    public String getTaskDescription(){return TaskDescription;}

    public Task_Object(){
        TaskName = "";
        TaskDescription = "";
    }

    public Task_Object(Parcel in){
        TaskName = in.readString();
        TaskDescription = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(TaskName);
        dest.writeString(TaskDescription);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator CREATOR = new Creator(){
        @Override
        public Object createFromParcel(Parcel source) {
            return new Task_Object(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new Task_Object[size];
        }
    };
}
