package com.ilaquidain.constructionreporter.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Settings_Information implements Parcelable, Serializable{

    private ArrayList<ArrayList<Worker_Object>> ListAvailableManpower;
    public void setListAvailableManpower(ArrayList<ArrayList<Worker_Object>> mListAvailableManpower){ListAvailableManpower = mListAvailableManpower;}
    public ArrayList<ArrayList<Worker_Object>> getListAvailableManpower(){return ListAvailableManpower;}

    private ArrayList<ArrayList<Worker_Object>> ListAvailableEquipment;

    public void setListAvailableEquipment(ArrayList<ArrayList<Worker_Object>> mListAvailableEquipment){ListAvailableEquipment = mListAvailableEquipment;}
    public ArrayList<ArrayList<Worker_Object>> getListAvailableEquipment(){return ListAvailableEquipment;}

    private ArrayList<String> WorkerGroups;
    public void setWorkerGroups(ArrayList<String> mWorkerGroups){WorkerGroups = mWorkerGroups;}
    public ArrayList<String> getWorkerGroups(){return WorkerGroups;}

    private ArrayList<String> EquipmentGroups;
    public void setEquipmentGroups(ArrayList<String> mEquipmentGroups){EquipmentGroups = mEquipmentGroups;}
    public ArrayList<String> getEquipmentGroups(){return EquipmentGroups;}

    private ArrayList<ArrayList<String>> ListasOpciones;
    public void setListasOpciones(ArrayList<ArrayList<String>> mlistasopciones){ListasOpciones = mlistasopciones;}
    public ArrayList<ArrayList<String>> getListasOpciones(){return ListasOpciones;}

    private ArrayList<Report_Object> SavedReports;
    public void setSavedReports(ArrayList<Report_Object> mreports){SavedReports = mreports;}
    public ArrayList<Report_Object>getSavedReports(){return SavedReports;}

    public Settings_Information(){
        this.ListAvailableManpower = new ArrayList<>();
        this.ListAvailableEquipment = new ArrayList<>();
        this.WorkerGroups = new ArrayList<>();
        this.EquipmentGroups = new ArrayList<>();
        this.ListasOpciones = new ArrayList<>();
        this.SavedReports = new ArrayList<>();

    }

    private Settings_Information(Parcel in){
        ListAvailableManpower = new ArrayList<>();
        in.readList(ListAvailableManpower,null);
        ListAvailableEquipment = new ArrayList<>();
        in.readList(ListAvailableEquipment,null);
        WorkerGroups = new ArrayList<>();
        in.readList(WorkerGroups,null);
        EquipmentGroups = new ArrayList<>();
        in.readList(EquipmentGroups,null);
        ListasOpciones = new ArrayList<>();
        in.readList(ListasOpciones,null);
        SavedReports = new ArrayList<>();
        in.readList(SavedReports,null);

        //Lista 0 -- Default Options (Not used here - only for report object)
        //Lista 1 -- Project Names
        //Lista 2 -- Project Ids
        //Lista 3 -- Project Addresses
        //Lista 4 -- Contractor
        //Lista 5 -- Weather
        //Lista 6 -- Report Type
        //Lista 7 -- Report Area
        //Lista 8 -- Date (Not used here - only for report object)
        //Lista 9 -- Tasks
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(ListasOpciones);
        dest.writeList(ListAvailableManpower);
        dest.writeList(ListAvailableEquipment);
        dest.writeList(WorkerGroups);
        dest.writeList(EquipmentGroups);
        dest.writeList(SavedReports);
    }

    public static final Creator CREATOR = new Creator() {
        public Settings_Information createFromParcel(Parcel in) {
            return new Settings_Information(in);
        }

        public Settings_Information[] newArray(int size) {
            return new Settings_Information[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
