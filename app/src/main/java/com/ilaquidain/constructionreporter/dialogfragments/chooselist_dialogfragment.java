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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;


public class chooselist_dialogfragment extends DialogFragment implements View.OnClickListener {
    private  DialogInterface.OnDismissListener onDismissListener2;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener2) {
        this.onDismissListener2 = onDismissListener2;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener2 != null) {
            onDismissListener2.onDismiss(dialog);
        }
    }

    private int lightred;
    private Integer Option2;
    private String selectedoption;
    private Report_Object currentreport;
    private RecyclerView mrecyclerview;
    private adapter_1 madapter;
    private ArrayList<String> optionslist;
    private ItemTouchHelper itemtouchhelper9;
    private Saved_Info_Object savedinfo;
    private int reportnumber,projectnumber;
    private SharedPreferences mpref;
    private SharedPreferences.Editor mprefedit;

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

        savedinfo = ((MainActivity)getActivity()).getSaved_info();
        mpref = getActivity().getPreferences(Context.MODE_PRIVATE);
        if(savedinfo!=null) {
            projectnumber = mpref.getInt("projectnumber", -1);
            reportnumber = mpref.getInt("reportnumber", -1);
            Option2 = mpref.getInt("option",-1);
            if (projectnumber != -1 && reportnumber != -1){
                currentreport = savedinfo.getSavedProjects().get(projectnumber).
                        getProjectReports().get(reportnumber);}
            else if(projectnumber != -1){
                currentreport = savedinfo.getTempreport();
            }
        }else {
            if ( getFragmentManager().getBackStackEntryCount() > 0)
            {getFragmentManager().popBackStack();}
        }

        lightred = ContextCompat.getColor(getActivity(),R.color.selection_light_orange);

        View v = inflater.inflate(R.layout.dialogfragment_manpower,container,false);

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

        ItemTouchHelper.Callback callback3 = new HelperCallback9(madapter);
        itemtouchhelper9 = new ItemTouchHelper(callback3);
        itemtouchhelper9.attachToRecyclerView(mrecyclerview);

        return v;
    }
    private void GetSelectedOption() {
        optionslist = savedinfo.getListasOpciones().get(Option2);
        if(currentreport.getReportInfo().get(Option2).equals("")){
            currentreport.getReportInfo().set(Option2,
                    savedinfo.getListasOpciones().get(0).get(Option2));
        }
        selectedoption = currentreport.getReportInfo().get(Option2);
    }
    private class adapter_1 extends RecyclerView.Adapter<viewholder_1> implements HelperAdapter9{
        private final OnStarDragListener9 mdraglistener8;

        private adapter_1() {
            super();
            GetSelectedOption();
            mdraglistener8 = new OnStarDragListener9() {
                @Override
                public void onStartDrag2(RecyclerView.ViewHolder viewHolder) {
                    itemtouchhelper9.startDrag(viewHolder);
                }
            };
        }

        @Override
        public viewholder_1 onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,
                    parent,false);
            return new viewholder_1(v);
        }

        @Override
        public void onBindViewHolder(viewholder_1 holder, int position) {
            holder.textView.setBackgroundColor(0);
            if(optionslist.get(position).equals(selectedoption)){
                holder.textView.setBackgroundColor(lightred);
            }
            holder.textView.setText(optionslist.get(position));
        }

        @Override
        public int getItemCount() {
            return optionslist.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(optionslist,fromPosition,toPosition);
            notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onItemDismiss(final int position) {
            final String s1 = optionslist.get(position);
            Snackbar snackbar = Snackbar
                    .make(mrecyclerview, "ITEM REMOVED", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            optionslist.add(position,s1);
                            notifyItemInserted(position);
                            mrecyclerview.scrollToPosition(position);
                        }
                    });
            snackbar.show();
            optionslist.remove(position);
            notifyItemRemoved(position);
        }
    }
    private class viewholder_1 extends RecyclerView.ViewHolder implements View.OnClickListener, HelperViewHolder9{
        final TextView textView;
        private viewholder_1(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(android.R.id.text1);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            selectedoption = optionslist.get(getAdapterPosition());
            madapter.notifyDataSetChanged();
            final android.os.Handler handler = new android.os.Handler();
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    ExitMethod();
                }
            };
            handler.postDelayed(r,300);
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
    private void ExitMethod() {
        //Guradamos la lista de posibles opciones y la opcion default
        savedinfo.getListasOpciones().set(Option2,optionslist);
        savedinfo.getListasOpciones().get(0).set(Option2,selectedoption);

        if(reportnumber==-1){
            savedinfo.getTempreport().getReportInfo().set(Option2,selectedoption);
        }else {
            savedinfo.getSavedProjects().get(projectnumber).getProjectReports()
                    .get(reportnumber).getReportInfo().set(Option2,selectedoption);
        }

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
        builder.setTitle("Add New Option");
        final EditText edittext = new EditText(getActivity());
        builder.setView(edittext);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!edittext.getText().toString().equals("")&&
                        !optionslist.contains(edittext.getText().toString())){
                    optionslist.add(edittext.getText().toString());
                    selectedoption = edittext.getText().toString();
                    madapter.notifyDataSetChanged();
                    final android.os.Handler handler = new android.os.Handler();
                    final Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            ExitMethod();
                        }
                    };
                    handler.postDelayed(r,300);
                }else{
                    selectedoption = edittext.getText().toString();
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


    private class HelperCallback9 extends ItemTouchHelper.Callback{
        private static final float ALPHA_FULL = 1.0f;

        private final HelperAdapter9 helperadapter;

        private HelperCallback9(HelperAdapter9 mhelperadapter){
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
                if (viewHolder instanceof HelperViewHolder9) {
                    HelperViewHolder9 itemViewHolder = (HelperViewHolder9) viewHolder;
                    itemViewHolder.onItemSelected();
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(ALPHA_FULL);
            if (viewHolder instanceof HelperViewHolder9) {
                HelperViewHolder9 itemViewHolder = (HelperViewHolder9) viewHolder;
                itemViewHolder.onItemClear();
            }
        }
    }

    private interface HelperAdapter9{
        boolean onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }
    private interface HelperViewHolder9{
        void onItemSelected();
        void onItemClear();
    }
    private interface OnStarDragListener9{
        void onStartDrag2(RecyclerView.ViewHolder viewHolder);
    }
}
