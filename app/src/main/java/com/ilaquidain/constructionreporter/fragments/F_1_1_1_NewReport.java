package com.ilaquidain.constructionreporter.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ilaquidain.constructionreporter.R;
import com.ilaquidain.constructionreporter.activities.MainActivity;
import com.ilaquidain.constructionreporter.fragments_newreport.EquipmentFragment;
import com.ilaquidain.constructionreporter.fragments_newreport.Photos_Fragment;
import com.ilaquidain.constructionreporter.fragments_newreport.ReportInfoFragment;
import com.ilaquidain.constructionreporter.fragments_newreport.ReportTasksFragment;
import com.ilaquidain.constructionreporter.fragments_newreport.ManpowerFragment;
import com.ilaquidain.constructionreporter.object.Project_Object;
import com.ilaquidain.constructionreporter.object.Report_Object;
import com.ilaquidain.constructionreporter.object.Saved_Info_Object;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class F_1_1_1_NewReport extends Fragment implements View.OnClickListener{

    public F_1_1_1_NewReport() {
        super();
    }

    private Project_Object currentproject;
    private Integer projectnumber = null;
    private Integer reportnumber = null;
    private Report_Object currentreport = null;
    private Saved_Info_Object savedinfo = null;
    private SharedPreferences mpref;
    private SharedPreferences.Editor mprefedit;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_newreport,container,false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        savedinfo = ((MainActivity)getActivity()).getSaved_info();
        mpref = getActivity().getPreferences(Context.MODE_PRIVATE);
        projectnumber = mpref.getInt("projectnumber",-1);
        if(savedinfo==null || projectnumber==-1){
            if ( getFragmentManager().getBackStackEntryCount() > 0)
            {getFragmentManager().popBackStack();}
        }else {
            currentproject = savedinfo.getSavedProjects().get(projectnumber);
        }
        reportnumber = mpref.getInt("reportnumber",-1);
        if(reportnumber == -1){
            int newreport = mpref.getInt("newreport",1);
            if(newreport == 1){
                currentreport = new Report_Object();
            }else {
                currentreport = savedinfo.getTempreport();
            }
            savedinfo.setTempreport(currentreport);
            mprefedit = mpref.edit();
            mprefedit.putInt("newreport",0);
            mprefedit.apply();
            ((MainActivity)getActivity()).setSaved_info(savedinfo);
        }else {
            currentreport = savedinfo.getSavedProjects().get(projectnumber).getProjectReports().get(reportnumber);
        }

        TextView title = (TextView)v.findViewById(R.id.newreport_title);
        if(reportnumber==-1){title.setText(getResources().getText(R.string.NewReport));}
        else {title.setText(getResources().getText(R.string.EditReport));}

        RelativeLayout lay1 = (RelativeLayout)v.findViewById(R.id.lay1);
        lay1.setOnClickListener(this);
        RelativeLayout lay5 = (RelativeLayout)v.findViewById(R.id.lay5);
        lay5.setOnClickListener(this);
        RelativeLayout lay6 = (RelativeLayout)v.findViewById(R.id.lay6);
        lay6.setOnClickListener(this);
        RelativeLayout lay7 = (RelativeLayout)v.findViewById(R.id.lay7);
        lay7.setOnClickListener(this);
        RelativeLayout lay8 = (RelativeLayout)v.findViewById(R.id.lay8);
        lay8.setOnClickListener(this);
        FloatingActionButton fabaccept = (FloatingActionButton)v.findViewById(R.id.fabsave);
        FloatingActionButton fabpdf = (FloatingActionButton)v.findViewById(R.id.fabpdf);
        fabaccept.setOnClickListener(this);
        fabpdf.setOnClickListener(this);

        setDefaultValues();

        return v;
    }

    private void setDefaultValues() {
        if(currentreport.getReportInfo().get(1).equals("")){
            currentreport.getReportInfo().set(1,savedinfo.getListasOpciones().get(0).get(1));}
        if(currentreport.getReportInfo().get(2).equals("")){
            currentreport.getReportInfo().set(2,savedinfo.getListasOpciones().get(0).get(2));}
        if(currentreport.getReportInfo().get(3).equals("")){
            currentreport.getReportInfo().set(3,savedinfo.getListasOpciones().get(0).get(3));}
        if(currentreport.getReportInfo().get(8).equals("")){
            currentreport.getReportInfo().set(8,new SimpleDateFormat("MM/dd/yyyy",Locale.US).format(new Date()));}
        if(currentreport.getReportInfo().get(6).equals("")){
            currentreport.getReportInfo().set(6,savedinfo.getListasOpciones().get(0).get(6));}
        if (currentreport.getReportInfo().get(7).equals("")){
            currentreport.getReportInfo().set(7,savedinfo.getListasOpciones().get(0).get(7));}
        if(currentreport.getReportInfo().get(5).equals("")){
            currentreport.getReportInfo().set(5,savedinfo.getListasOpciones().get(0).get(5));}
        if(currentreport.getReportInfo().get(10).equals("")){
            currentreport.getReportInfo().set(10,savedinfo.getListasOpciones().get(0).get(10));}
        if(currentreport.getReportInfo().get(11).equals("")){
            currentreport.getReportInfo().set(11,savedinfo.getListasOpciones().get(0).get(11));}
    }

    @Override
    public void onClick(View v) {
        Fragment currentfragment = getActivity().getFragmentManager().findFragmentById(R.id.MainFrame);
        FragmentManager fm = getFragmentManager();
        switch (v.getId()){
            case R.id.lay1:
                ReportInfoFragment reportinfo = new ReportInfoFragment();
                reportinfo.setTargetFragment(currentfragment,1002);
                fm.beginTransaction()
                        .replace(R.id.MainFrame,reportinfo)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.lay5:
                ReportTasksFragment fgmt7 = new ReportTasksFragment();
                fgmt7.setTargetFragment(currentfragment,1003);
                fm.beginTransaction()
                        .replace(R.id.MainFrame,fgmt7)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.lay6:
                ManpowerFragment dialogmanpower = new ManpowerFragment();
                dialogmanpower.setTargetFragment(currentfragment,1004);
                fm.beginTransaction()
                        .replace(R.id.MainFrame,dialogmanpower)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.lay7:
                EquipmentFragment dialogequipment = new EquipmentFragment();
                dialogequipment.setTargetFragment(currentfragment,1005);
                fm.beginTransaction()
                        .replace(R.id.MainFrame,dialogequipment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.lay8:
                Photos_Fragment dialogphotos = new Photos_Fragment();
                dialogphotos.setTargetFragment(currentfragment,1006);
                fm.beginTransaction()
                        .replace(R.id.MainFrame,dialogphotos)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.fabsave:
                ExitFragment(0);
                break;
            case R.id.fabpdf:
                ExitFragment(1);
                break;
        }
    }

    private void ExitFragment(int createpdf) {

        if(createpdf==1 && reportnumber != -1){
            ((MainActivity)getActivity()).GeneratePDFReport(currentproject,reportnumber);
            Toast toast = Toast.makeText(getActivity(),"PDF Report Created",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }else if(createpdf==1 && reportnumber ==-1){
            currentproject.getProjectReports().add(currentreport);
            savedinfo.getSavedProjects().set(projectnumber,currentproject);
            ((MainActivity)getActivity()).setSaved_info(savedinfo);
            ((MainActivity)getActivity()).GeneratePDFReport(currentproject,currentproject.getProjectReports().size()-1);
            Toast toast = Toast.makeText(getActivity(),"PDF Report Created",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        } else {
            if(reportnumber == -1){
                currentproject.getProjectReports().add(savedinfo.getTempreport());
            }else {
                currentproject.getProjectReports().set(reportnumber,currentreport);
            }
            Toast toast = Toast.makeText(getActivity(),"Report Saved Succesfully",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        savedinfo.getSavedProjects().set(projectnumber,currentproject);
        ((MainActivity)getActivity()).setSaved_info(savedinfo);

        FragmentManager fm = getFragmentManager();
        F_1_1_ReportMenu fgmt = new F_1_1_ReportMenu();
        fm.beginTransaction()
                .replace(R.id.MainFrame,fgmt)
                .addToBackStack(null)
                .commit();

    }
}
