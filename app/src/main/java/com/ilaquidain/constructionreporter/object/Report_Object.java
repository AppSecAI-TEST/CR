package com.ilaquidain.constructionreporter.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Report_Object implements Parcelable, Serializable{

    private String ReportId;
    public void setReportId(String ss){ReportId=ss;}
    public String getReportId(){return ReportId;}

    private ArrayList<Worker_Object> SelectedWorkers;
    public void setSelectedWorkers(ArrayList<Worker_Object> mSelectedWorkers){SelectedWorkers = mSelectedWorkers;}
    public ArrayList<Worker_Object> getSelectedWorkers(){return SelectedWorkers;}

    private ArrayList<Worker_Object> SelectedEquipment;
    public void setSelectedEquipment(ArrayList<Worker_Object> mSelectedEquipment){SelectedEquipment = mSelectedEquipment;}
    public ArrayList<Worker_Object> getSelectedEquipment(){return SelectedEquipment;}

    private ArrayList<String> ReportInfo;
    public void setReportInfo(ArrayList<String> mReporInfo){ReportInfo = mReporInfo;}
    public ArrayList<String> getReportInfo(){return ReportInfo;}

    private ArrayList<Task_Object> SelectedTasks;
    public void setSelectedTasks(ArrayList<Task_Object> to){SelectedTasks =to;}
    public ArrayList<Task_Object> getSelectedTasks(){return SelectedTasks;}

    private ArrayList<Image_Object> SelectedPhotos;
    public void setSelectedPhotos(ArrayList<Image_Object> io){SelectedPhotos = io;}
    public ArrayList<Image_Object> getSelectedPhotos(){return SelectedPhotos;}


    //Report Info
    //Option 0 -- Default Options
    //Option 1 -- Project Name
    //Option 2 -- Project Id
    //Option 3 -- Project Address
    //Option 4 -- Contractor
    //Option 5 -- Weather
    //Option 6 -- Report Type
    //Option 7 -- Report Discipline
    //Option 8 -- Report Date
    //Option 9 -- Tasks
    //Option 10 -- Start Time
    //Option 11 -- End Time

    public Report_Object(){
        this.ReportId = UUID.randomUUID().toString();
        this.SelectedWorkers = new ArrayList<>();
        this.SelectedEquipment = new ArrayList<>();
        this.SelectedPhotos = new ArrayList<>();
        this.SelectedTasks = new ArrayList<>();

        ArrayList<String> templist = new ArrayList<>();
        for(int i=0;i<12;i++){templist.add("");}
        this.ReportInfo = templist;

    }

    private Report_Object(Parcel in){
        //this.SelectedEquipment = in.readList(Report_Object.class.getClassLoader());
        SelectedEquipment = new ArrayList<>();
        in.readList(SelectedEquipment,Worker_Object.class.getClassLoader());
        //in.readList(SelectedEquipment,null);
        SelectedWorkers = new ArrayList<>();
        in.readList(SelectedWorkers,Worker_Object.class.getClassLoader());
        //in.readList(SelectedWorkers,null);
        SelectedPhotos = new ArrayList<>();
        in.readList(SelectedPhotos,Image_Object.class.getClassLoader());
        //in.readList(SelectedPhotos,null);
        ReportInfo = new ArrayList<>();
        in.readList(ReportInfo,String.class.getClassLoader());
        //in.readList(ReportInfo,null);
        SelectedTasks = new ArrayList<>();
        in.readList(SelectedTasks,Task_Object.class.getClassLoader());
        //in.readList(SelectedTasks,null);
        ReportId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(SelectedPhotos);
        dest.writeList(SelectedEquipment);
        dest.writeList(SelectedWorkers);
        dest.writeList(ReportInfo);
        dest.writeList(SelectedTasks);
        dest.writeString(ReportId);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Report_Object createFromParcel(Parcel in) {
            return new Report_Object(in);
        }

        public Report_Object[] newArray(int size) {
            return new Report_Object[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
