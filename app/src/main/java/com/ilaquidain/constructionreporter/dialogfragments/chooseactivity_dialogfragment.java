package com.ilaquidain.constructionreporter.dialogfragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import java.util.ArrayList;
import java.util.Collections;


public class chooseactivity_dialogfragment extends DialogFragment implements View.OnClickListener {
    private  DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    private int lightred;
    private Report_Object currentreport;

    private RecyclerView mrecyclerview;
    private adapter_1 madapter;

    private ArrayList<String> AvailableTasks;
    private ArrayList<Task_Object> selectedtasks;

    private ItemTouchHelper itemtouchhelper8;

    private Saved_Info_Object savedinfo;
    private int projectnumber, reportnumber;
    private SharedPreferences mpref;

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

        mpref = getActivity().getPreferences(Context.MODE_PRIVATE);
        projectnumber = mpref.getInt("projectnumber",-1);
        reportnumber = mpref.getInt("reportnumber",-1);
        savedinfo = ((MainActivity)getActivity()).getSaved_info();
        if(savedinfo!=null && projectnumber != -1 && reportnumber!=-1) {
            currentreport = savedinfo.getSavedProjects().get(projectnumber).getProjectReports().get(reportnumber);
        }else if(savedinfo!=null && reportnumber==-1){
            currentreport = savedinfo.getTempreport();
        }else{
            if ( getFragmentManager().getBackStackEntryCount() > 0)
            {getFragmentManager().popBackStack();}
        }

        lightred = ContextCompat.getColor(getActivity(),R.color.selection_light_orange);
        //Saved_Info_Object  savedInfo = ((MainActivity)getActivity()).getSaved_info();
        //currentreport = (Report_Object)getArguments().getSerializable("currentreport");
        //getArguments().remove("currentreport");

        if(savedinfo.getListasOpciones().get(9)!=null){
            AvailableTasks = new ArrayList<>(savedinfo.getListasOpciones().get(9));}
        else {
            AvailableTasks = new ArrayList<>();
        }
        if(currentreport.getSelectedTasks()!=null){
            selectedtasks = currentreport.getSelectedTasks();
        }else {
            selectedtasks = new ArrayList<>();
        }

        View v = inflater.inflate(R.layout.dialogfragment_manpower,container,false);

        TextView title = (TextView)v.findViewById(R.id.recyclerviewtitle);
        title.setText(getResources().getText(R.string.AddActivityToReport));

        FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.fabaddmanpower);
        fab.setImageResource(R.drawable.ic_add_black_24dp);
        fab.setOnClickListener(this);

        FloatingActionButton fab2 = (FloatingActionButton)v.findViewById(R.id.fabaccept);
        fab2.setImageResource(R.drawable.ic_check_black_24dp);
        fab2.setOnClickListener(this);

        mrecyclerview = (RecyclerView)v.findViewById(R.id.recyclerview_manpower);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        madapter = new adapter_1();
        mrecyclerview.setAdapter(madapter);

        ItemTouchHelper.Callback callback3 = new HelperCallback8(madapter);
        itemtouchhelper8 = new ItemTouchHelper(callback3);
        itemtouchhelper8.attachToRecyclerView(mrecyclerview);

        return v;
    }

    private void ExitMethod() {
        currentreport.setSelectedTasks(selectedtasks);
        if(reportnumber==-1){
            savedinfo.setTempreport(currentreport);
        }else {
            savedinfo.getSavedProjects().get(projectnumber).getProjectReports().set(reportnumber,currentreport);
        }
        savedinfo.getListasOpciones().set(9,AvailableTasks);
        ((MainActivity)getActivity()).setSaved_info(savedinfo);
        getDialog().dismiss();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabaddmanpower:
                showinputdialog();
                break;
            case R.id.fabaccept:
                ExitMethod();
                break;
        }
    }
    private void showinputdialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add New Activity");
        final EditText edittext = new EditText(getActivity());
        builder.setView(edittext);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!edittext.getText().toString().equals("")&&
                        !AvailableTasks.contains(edittext.getText().toString())){
                    AvailableTasks.add(edittext.getText().toString());
                    madapter.notifyDataSetChanged();
                }else{
                    madapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }



    private class adapter_1 extends RecyclerView.Adapter<viewholder_1> implements HelperAdapter8{
        private final OnStarDragListener8 mdraglistener8;

        private adapter_1() {
            super();
            mdraglistener8 = new OnStarDragListener8() {
                @Override
                public void onStartDrag2(RecyclerView.ViewHolder viewHolder) {
                    itemtouchhelper8.startDrag(viewHolder);
                }
            };
        }

        @Override
        public viewholder_1 onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewitem_manpowergroup,
                    parent,false);
            return new viewholder_1(v);
        }

        @Override
        public void onBindViewHolder(viewholder_1 holder, int position) {
            holder.textView.setBackgroundColor(0);
            for(int i=0;i<selectedtasks.size();i++){
                if(selectedtasks.get(i).getTaskName().equals(AvailableTasks.get(position))){
                    holder.itemView.setBackgroundColor(lightred);
                }
            }
            holder.textView.setText(AvailableTasks.get(position));
        }

        @Override
        public int getItemCount() {
            return AvailableTasks.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(AvailableTasks,fromPosition,toPosition);
            notifyItemMoved(fromPosition,toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(final int position) {
            final String s1 = AvailableTasks.get(position);
            Snackbar snackbar = Snackbar
                    .make(mrecyclerview, "PHOTO REMOVED", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AvailableTasks.add(position,s1);
                            notifyItemInserted(position);
                            mrecyclerview.scrollToPosition(position);
                        }
                    });
            snackbar.show();
            AvailableTasks.remove(position);
            notifyItemRemoved(position);
        }
    }
    private class viewholder_1 extends RecyclerView.ViewHolder implements View.OnClickListener,HelperViewHolder8{
        final TextView textView;

        private viewholder_1(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.text1);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemView.setBackgroundColor(lightred);
            String name = AvailableTasks.get(getAdapterPosition());
            Boolean notincluded = true;
            for(int i=0;i<selectedtasks.size();i++){
                if(name.equals(selectedtasks.get(i).getTaskName())){notincluded=false;}
            }
            if(notincluded){
                Task_Object task = new Task_Object();
                task.setTaskName(name);
                selectedtasks.add(task);
            }
            madapter.notifyDataSetChanged();
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    private class HelperCallback8 extends ItemTouchHelper.Callback{
        private static final float ALPHA_FULL = 1.0f;

        private final HelperAdapter8 helperadapter;

        private HelperCallback8(HelperAdapter8 mhelperadapter){
            helperadapter = mhelperadapter;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            if (viewHolder.getItemViewType() != target.getItemViewType()) {
                return false;
            }

            // Notify the adapter of the move
            helperadapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            helperadapter.onItemDismiss(viewHolder.getAdapterPosition());
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                // Fade out the view as it is swiped out of the parent's bounds
                final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder instanceof HelperViewHolder8) {
                    HelperViewHolder8 itemViewHolder = (HelperViewHolder8) viewHolder;
                    itemViewHolder.onItemSelected();
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(ALPHA_FULL);
            if (viewHolder instanceof HelperViewHolder8) {
                HelperViewHolder8 itemViewHolder = (HelperViewHolder8) viewHolder;
                itemViewHolder.onItemClear();
            }
        }
    }

    private interface HelperAdapter8{
        boolean onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }

    private interface HelperViewHolder8{
        void onItemSelected();
        void onItemClear();
    }

    private interface OnStarDragListener8{
        void onStartDrag2(RecyclerView.ViewHolder viewHolder);
    }


}
