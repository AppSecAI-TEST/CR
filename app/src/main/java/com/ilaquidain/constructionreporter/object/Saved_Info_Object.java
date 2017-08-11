package com.ilaquidain.constructionreporter.object;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import java.util.ArrayList;

public class Saved_Info_Object implements Parcelable, Serializable{

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

    private ArrayList<Project_Object> SavedProjects;
    public void setSavedProjects(ArrayList<Project_Object> mreports){SavedProjects = mreports;}
    public ArrayList<Project_Object>getSavedProjects(){return SavedProjects;}

    private Report_Object tempreport;
    public void setTempreport(Report_Object mtempreport){tempreport = mtempreport;}
    public Report_Object getTempreport(){return tempreport;}

    public Saved_Info_Object(){
        this.ListAvailableManpower = new ArrayList<>();
        this.ListAvailableEquipment = new ArrayList<>();
        this.WorkerGroups = new ArrayList<>();
        this.EquipmentGroups = new ArrayList<>();
        this.ListasOpciones = new ArrayList<>();
        this.SavedProjects = new ArrayList<>();
        this.tempreport = new Report_Object();

    }

    private Saved_Info_Object(Parcel in){
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
        SavedProjects = new ArrayList<>();
        in.readList(SavedProjects,null);
        tempreport = (Report_Object)in.readSerializable();

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
        //Lista 10 -- Start Time
        //Lista 11 -- End Time
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(ListasOpciones);
        dest.writeList(ListAvailableManpower);
        dest.writeList(ListAvailableEquipment);
        dest.writeList(WorkerGroups);
        dest.writeList(EquipmentGroups);
        dest.writeList(SavedProjects);
        dest.writeSerializable(tempreport);
    }

    public static final Creator CREATOR = new Creator() {
        public Saved_Info_Object createFromParcel(Parcel in) {
            return new Saved_Info_Object(in);
        }

        public Saved_Info_Object[] newArray(int size) {
            return new Saved_Info_Object[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
