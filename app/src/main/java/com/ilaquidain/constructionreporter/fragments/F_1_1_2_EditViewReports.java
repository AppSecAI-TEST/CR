package com.ilaquidain.constructionreporter.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ilaquidain.constructionreporter.R;
import com.ilaquidain.constructionreporter.activities.MainActivity;
import com.ilaquidain.constructionreporter.object.Project_Object;
import com.ilaquidain.constructionreporter.object.Report_Object;
import com.ilaquidain.constructionreporter.object.Saved_Info_Object;

import java.util.ArrayList;
import java.util.Collections;

public class F_1_1_2_EditViewReports extends Fragment {

    private Project_Object currentproject;
    private ArrayList<Report_Object> savedreports;
    private RecyclerView mrecyclerview;
    private adapter_edit_reports madapter;
    private ItemTouchHelper itemtouchhelper31;

    private int projectnumber, reportnumber;
    private Saved_Info_Object savedinfo;
    private SharedPreferences mpref;
    private SharedPreferences.Editor mprefedit;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_editreports,container,false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        mpref = getActivity().getPreferences(Context.MODE_PRIVATE);
        projectnumber = mpref.getInt("projectnumber",-1);
        savedinfo = ((MainActivity)getActivity()).getSaved_info();
        if(savedinfo!=null && projectnumber != -1){
            currentproject = savedinfo.getSavedProjects().get(projectnumber);
            savedreports = currentproject.getProjectReports();
        }else{
            if ( getFragmentManager().getBackStackEntryCount() > 0)
            {getFragmentManager().popBackStack();}
        }

        mrecyclerview = (RecyclerView)v.findViewById(R.id.recyclerview);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        madapter = new adapter_edit_reports();
        mrecyclerview.setAdapter(madapter);

        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mrecyclerview.getContext(),
                DividerItemDecoration.VERTICAL);
        mrecyclerview.addItemDecoration(dividerItemDecoration);*/

        ItemTouchHelper.Callback callback = new HelperCallback31(madapter);
        itemtouchhelper31 = new ItemTouchHelper(callback);
        itemtouchhelper31.attachToRecyclerView(mrecyclerview);

        return v;
    }

    private class adapter_edit_reports extends RecyclerView.Adapter<viewholder_edit_reports> implements HelperAdapter31{
        private final OnStarDragListener31 mdraglistener31;

        public adapter_edit_reports() {
            super();
            mdraglistener31 = new OnStarDragListener31() {
                @Override
                public void onStartDrag31(RecyclerView.ViewHolder viewHolder) {
                    itemtouchhelper31.startDrag(viewHolder);
                }
            };
        }

        @Override
        public viewholder_edit_reports onCreateViewHolder(ViewGroup parent, int viewType) {
            View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_pdfreport,
                    parent,false);
            return new viewholder_edit_reports(v2);
        }

        @Override
        public void onBindViewHolder(viewholder_edit_reports holder, int position) {
            String s1 = "Daily " + savedreports.get(position).getReportInfo().get(6);
            String s2 = savedreports.get(position).getReportInfo().get(8);
            holder.textView1.setText(s1);
            holder.textView2.setText(s2);
        }

        @Override
        public int getItemCount() {
            return savedreports.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(savedreports,fromPosition,toPosition);
            notifyItemMoved(fromPosition,toPosition);
            currentproject.setProjectReports(savedreports);
            ((MainActivity)getActivity()).getSaved_info().getSavedProjects().get(projectnumber).
                    setProjectReports(savedreports);
            return false;
        }

        @Override
        public void onItemDismiss(final int position) {
            final Report_Object report = savedreports.get(position);
            Snackbar snackbar = Snackbar
                    .make(mrecyclerview, "ITEM REMOVED", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            savedreports.add(position,report);
                            notifyItemInserted(position);
                            mrecyclerview.scrollToPosition(position);
                        }
                    });
            snackbar.show();
            savedreports.remove(position);
            currentproject.setProjectReports(savedreports);
            ((MainActivity)getActivity()).getSaved_info().getSavedProjects().get(projectnumber).
                    setProjectReports(savedreports);
            notifyItemRemoved(position);
        }
    }
    private class viewholder_edit_reports extends RecyclerView.ViewHolder implements HelperViewHolder31, View.OnClickListener{
        final TextView textView1;
        final TextView textView2;

        public viewholder_edit_reports(View itemView) {
            super(itemView);
            textView1 = (TextView)itemView.findViewById(R.id.text1);
            textView2 = (TextView)itemView.findViewById(R.id.text2);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            FragmentManager fm = getFragmentManager();
            F_1_1_1_NewReport fgmt = new F_1_1_1_NewReport();
            mpref = getActivity().getPreferences(Context.MODE_PRIVATE);
            mprefedit = mpref.edit();
            mprefedit = mpref.edit();
            mprefedit.putInt("reportnumber",getAdapterPosition());
            mprefedit.apply();
            fm.beginTransaction()
                    .add(R.id.MainFrame,fgmt,"tag_newreport")
                    .addToBackStack(null)
                    .commit();
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

    private class HelperCallback31 extends ItemTouchHelper.Callback{
        private static final float ALPHA_FULL = 1.0f;

        private final HelperAdapter31 helperadapter;

        private HelperCallback31(HelperAdapter31 mhelperadapter){
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
                if (viewHolder instanceof HelperViewHolder31) {
                    HelperViewHolder31 itemViewHolder = (HelperViewHolder31) viewHolder;
                    itemViewHolder.onItemSelected();
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(ALPHA_FULL);
            if (viewHolder instanceof HelperViewHolder31) {
                HelperViewHolder31 itemViewHolder = (HelperViewHolder31) viewHolder;
                itemViewHolder.onItemClear();
            }
        }
    }
    private interface HelperAdapter31{
        boolean onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }
    private interface HelperViewHolder31{
        void onItemSelected();
        void onItemClear();
    }
    private interface OnStarDragListener31{
        void onStartDrag31(RecyclerView.ViewHolder viewHolder);
    }
}
