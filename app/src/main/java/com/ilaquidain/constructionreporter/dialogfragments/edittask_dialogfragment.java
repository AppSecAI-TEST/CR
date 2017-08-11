package com.ilaquidain.constructionreporter.dialogfragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ilaquidain.constructionreporter.R;
import com.ilaquidain.constructionreporter.activities.MainActivity;
import com.ilaquidain.constructionreporter.object.Report_Object;
import com.ilaquidain.constructionreporter.object.Saved_Info_Object;
import com.ilaquidain.constructionreporter.object.Task_Object;

import java.io.Serializable;

public class edittask_dialogfragment extends DialogFragment implements View.OnClickListener {

    private Task_Object task;
    private EditText editText;
    private Saved_Info_Object savedinfo;
    private Report_Object currentreport;
    private int projectnumber, reportnumber, tasknumber;
    private SharedPreferences mpref;
    private SharedPreferences.Editor mprefedit;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(),getTheme()) {
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                ExitMethod();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog!=null){
            int Width = ViewGroup.LayoutParams.MATCH_PARENT;
            int Height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(Width,Height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialogfragment_taskdescription,container,false);

        savedinfo = ((MainActivity)getActivity()).getSaved_info();
        mpref = getActivity().getPreferences(Context.MODE_PRIVATE);
        projectnumber = mpref.getInt("projectnumber",-1);
        reportnumber = mpref.getInt("reportnumber",-1);
        tasknumber = mpref.getInt("tasknumber",-1);

        if(savedinfo==null || projectnumber==-1 || tasknumber == -1){
            if ( getFragmentManager().getBackStackEntryCount() > 0)
            {getFragmentManager().popBackStack();}
        }else if(reportnumber==-1){
            currentreport = savedinfo.getTempreport();
        }else {
            currentreport = savedinfo.getSavedProjects().get(projectnumber).getProjectReports().get(reportnumber);
        }

        task = currentreport.getSelectedTasks().get(tasknumber);
        editText = (EditText)v.findViewById(R.id.edittext1);
        TextView title = (TextView)v.findViewById(R.id.recyclerviewtitle);

        if(task!=null){
            String s1 = task.getTaskName()+" Description";
            title.setText(s1);
        editText.setText(task.getTaskDescription());
        editText.setSelection(editText.getText().length());}

        FloatingActionButton fabaccept = (FloatingActionButton)v.findViewById(R.id.fabaccept);
        fabaccept.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.fabaccept){
            ExitMethod();
        }
    }

    private void ExitMethod(){
        task.setTaskDescription(editText.getText().toString());
        currentreport.getSelectedTasks().set(tasknumber,task);
        if(reportnumber==-1){
            savedinfo.setTempreport(currentreport);
        }else {
            savedinfo.getSavedProjects().get(projectnumber).getProjectReports().set(reportnumber,currentreport);
        }
        ((MainActivity)getActivity()).setSaved_info(savedinfo);
        getDialog().dismiss();
    }
}
