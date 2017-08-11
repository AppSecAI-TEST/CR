package com.ilaquidain.constructionreporter.fragments_newreport;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import com.ilaquidain.constructionreporter.R;
import com.ilaquidain.constructionreporter.activities.MainActivity;
import com.ilaquidain.constructionreporter.dialogfragments.chooselist_dialogfragment;
import com.ilaquidain.constructionreporter.object.Report_Object;
import com.ilaquidain.constructionreporter.object.Saved_Info_Object;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportInfoFragment extends Fragment implements View.OnClickListener {

    private Saved_Info_Object savedinfo;
    private Integer projectnumber, reportnumber;
    private SharedPreferences mpref;
    private SharedPreferences.Editor mprefedit;
    private Report_Object currentreport;

    private TextView reportdate;
    private TextView reporttype;
    private TextView discipline;
    private TextView reportweather;

    private TextView startime;
    private TextView endtime;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reporttinfo,container,false);

        savedinfo = ((MainActivity)getActivity()).getSaved_info();
        mpref = getActivity().getPreferences(Context.MODE_PRIVATE);
        projectnumber = mpref.getInt("projectnumber",-1);
        reportnumber = mpref.getInt("reportnumber",-1);

        if(savedinfo!=null && projectnumber != -1 && reportnumber!=-1){
            currentreport = savedinfo.getSavedProjects().get(projectnumber).getProjectReports().get(reportnumber);
        }else if(savedinfo!=null && projectnumber != -1 && reportnumber==-1){
            currentreport = savedinfo.getTempreport();
        }else{
            if ( getFragmentManager().getBackStackEntryCount() > 0)
            {getFragmentManager().popBackStack();}
        }


        FloatingActionButton fabaccet = (FloatingActionButton)v.findViewById(R.id.fabaccept);
        fabaccet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitMethod();
            }
        });

        reportdate = (TextView)v.findViewById(R.id.df_projectinfo_3);
        reporttype = (TextView)v.findViewById(R.id.df_projectinfo_6);
        discipline = (TextView)v.findViewById(R.id.df_projectinfo_9);
        reportweather = (TextView)v.findViewById(R.id.df_projectinfo_12);
        startime = (TextView)v.findViewById(R.id.df_projectinfo_15);
        endtime = (TextView)v.findViewById(R.id.df_projectinfo_16);

        reportdate.setText(currentreport.getReportInfo().get(8));
        reporttype.setText(currentreport.getReportInfo().get(6));
        discipline.setText(currentreport.getReportInfo().get(7));
        reportweather.setText(currentreport.getReportInfo().get(5));
        startime.setText(currentreport.getReportInfo().get(10));
        endtime.setText(currentreport.getReportInfo().get(11));

        reportdate.setOnClickListener(this);
        reporttype.setOnClickListener(this);
        discipline.setOnClickListener(this);
        reportweather.setOnClickListener(this);
        startime.setOnClickListener(this);
        endtime.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        final Fragment currentfragment = getActivity().getFragmentManager().findFragmentById(R.id.MainFrame);
        switch (v.getId()){
            case R.id.df_projectinfo_15:
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(),R.style.dialogdatepicker,
                        new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String mhour, mminute;
                        if(selectedHour<10){mhour="0"+String.valueOf(selectedHour);}
                        else {mhour=String.valueOf(selectedHour);}
                        if(selectedMinute<10){mminute="0"+String.valueOf(selectedMinute);}
                        else {mminute=String.valueOf(selectedMinute);}
                        String time = mhour+":"+mminute;
                        startime.setText(time);
                        currentreport.getReportInfo().set(10,time);
                    }
                }, 07, 00, true);//Yes 24 hour time
                mTimePicker.show();
                break;
            case R.id.df_projectinfo_16:
                TimePickerDialog mTimePicker2;
                mTimePicker2 = new TimePickerDialog(getActivity(),R.style.dialogdatepicker,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String mhour, mminute;
                                if(selectedHour<10){mhour="0"+String.valueOf(selectedHour);}
                                else {mhour=String.valueOf(selectedHour);}
                                if(selectedMinute<10){mminute="0"+String.valueOf(selectedMinute);}
                                else {mminute=String.valueOf(selectedMinute);}
                                String time = mhour+":"+mminute;
                                endtime.setText(time);
                                currentreport.getReportInfo().set(11,time);
                            }
                        }, 16, 00, true);//Yes 24 hour time
                mTimePicker2.show();
                break;
            case R.id.df_projectinfo_3:
                final Calendar myCalendar = Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        myCalendar.set(Calendar.YEAR, i);
                        myCalendar.set(Calendar.MONTH, i1);
                        myCalendar.set(Calendar.DAY_OF_MONTH, i2);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
                        currentreport.getReportInfo().set(8,sdf.format(myCalendar.getTime()));
                        reportdate.setText(currentreport.getReportInfo().get(8));
                    }
                };
                new DatePickerDialog(getActivity(),R.style.dialogdatepicker, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                break;
            case R.id.df_projectinfo_6:
            case R.id.df_projectinfo_9:
            case R.id.df_projectinfo_12:

                /*Bundle bundle2 = new Bundle();
                bundle2.putSerializable("currentreport",currentreport);*/
                Integer option =0;
                switch (v.getId()){
                    case R.id.df_projectinfo_6:option=6;break;
                    case R.id.df_projectinfo_9:option=7;break;
                    case R.id.df_projectinfo_12:option =5;break;
                }

                mpref = getActivity().getPreferences(Context.MODE_PRIVATE);
                mprefedit = mpref.edit();
                mprefedit.putInt("option",option);
                mprefedit.apply();

                chooselist_dialogfragment dialog_choose = new chooselist_dialogfragment();

                dialog_choose.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        savedinfo = ((MainActivity)getActivity()).getSaved_info();
                        if(reportnumber == -1){
                            currentreport = savedinfo.getTempreport();
                        }else {
                            currentreport = savedinfo.getSavedProjects().
                                    get(projectnumber).getProjectReports().get(reportnumber);
                        }
                        reportdate.setText(currentreport.getReportInfo().get(8));
                        reporttype.setText(currentreport.getReportInfo().get(6));
                        discipline.setText(currentreport.getReportInfo().get(7));
                        reportweather.setText(currentreport.getReportInfo().get(5));
                        startime.setText(currentreport.getReportInfo().get(10));
                        endtime.setText(currentreport.getReportInfo().get(11));
                    }
                });
                dialog_choose.show(getFragmentManager(),"Dialog2");
                break;
        }
    }

    private void ExitMethod() {
        if(reportnumber==-1){
            savedinfo.setTempreport(currentreport);
        }else {
            savedinfo.getSavedProjects().get(projectnumber).getProjectReports().set(reportnumber,currentreport);
        }
        ((MainActivity)getActivity()).setSaved_info(savedinfo);

        if ( getFragmentManager().getBackStackEntryCount() > 0)
        {getFragmentManager().popBackStack();}
    }
}
