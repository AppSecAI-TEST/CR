package com.ilaquidain.constructionreporter.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Project_Object implements Parcelable,Serializable {

    private String ProjectId;
    private String ProjectName;
    private String ProjectRefNo;
    private String ProjectAddress;
    private String BitmapFileAddress;
    private ArrayList<Report_Object> ProjectReports;

    public void setProjectId(String s1){ProjectId =s1;}
    public String getProjectId(){return ProjectId;}

    public void setProjectName(String s2){ProjectName = s2;}
    public String getProjectName(){return ProjectName;}

    public void setProjectRefNo(String s3){ProjectRefNo =s3;}
    public String getProjectRefNo(){return ProjectRefNo;}

    public void setProjectAddress(String s4){ProjectAddress = s4;}
    public String getProjectAddress(){return ProjectAddress;}

    public void setBitmapFileAddress(String s6){BitmapFileAddress = s6;}
    public String getBitmapFileAddress(){return BitmapFileAddress;}

    public void setProjectReports(ArrayList<Report_Object> s5){ProjectReports = s5;}
    public ArrayList<Report_Object> getProjectReports(){return ProjectReports;}


    public Project_Object(){
        this.ProjectId = UUID.randomUUID().toString();
        this.ProjectName = " ";
        this.ProjectRefNo = " ";
        this.ProjectAddress = " ";
        this.BitmapFileAddress =null;
        this.ProjectReports = new ArrayList<>();
    }

    private Project_Object(Parcel in){
        ProjectId = in.readString();
        ProjectName = in.readString();
        ProjectRefNo = in.readString();
        ProjectAddress = in.readString();
        BitmapFileAddress = in.readString();

        ProjectReports = new ArrayList<>();
        in.readList(ProjectReports,Report_Object.class.getClassLoader());
        //in.readList(ProjectReports,null);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ProjectId);
        dest.writeString(ProjectName);
        dest.writeString(ProjectRefNo);
        dest.writeString(ProjectAddress);
        dest.writeString(BitmapFileAddress);
        dest.writeList(ProjectReports);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Project_Object createFromParcel(Parcel in) {
            return new Project_Object(in);
        }

        public Project_Object[] newArray(int size) {
            return new Project_Object[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
