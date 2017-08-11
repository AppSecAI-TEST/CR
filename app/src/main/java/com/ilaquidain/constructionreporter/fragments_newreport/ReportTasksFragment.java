package com.ilaquidain.constructionreporter.fragments_newreport;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ilaquidain.constructionreporter.R;
import com.ilaquidain.constructionreporter.activities.MainActivity;
import com.ilaquidain.constructionreporter.dialogfragments.chooseactivity_dialogfragment;
import com.ilaquidain.constructionreporter.dialogfragments.edittask_dialogfragment;
import com.ilaquidain.constructionreporter.fragments.F_1_1_1_NewReport;
import com.ilaquidain.constructionreporter.object.Report_Object;
import com.ilaquidain.constructionreporter.object.Saved_Info_Object;
import com.ilaquidain.constructionreporter.object.Task_Object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class ReportTasksFragment extends Fragment implements View.OnClickListener {


    private int taskposition;

    private Report_Object currentreport;

    private RecyclerView mrecyclerView_7;
    private ItemTouchHelper mhelper_7;
    private adapter_7 madapter_7;

    Saved_Info_Object savedinfo;
    Integer projectnumber, reportnumber;
    SharedPreferences mpref;
    SharedPreferences.Editor mprefedit;

    private ArrayList<Task_Object> selectedtasks;

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
        selectedtasks = currentreport.getSelectedTasks();

        int lightred = ContextCompat.getColor(getActivity(),R.color.selection_light_orange);

        View v = inflater.inflate(R.layout.dialogfragment_manpower,container,false);

        TextView title = (TextView)v.findViewById(R.id.recyclerviewtitle);
        title.setText("Activities Selected");

        FloatingActionButton fab = (FloatingActionButton)v.findViewById(R.id.fabaddmanpower);
        fab.setImageResource(R.drawable.ic_add_black_24dp);
        fab.setOnClickListener(this);

        FloatingActionButton fab2 = (FloatingActionButton)v.findViewById(R.id.fabaccept);
        fab2.setImageResource(R.drawable.ic_check_black_24dp);
        fab2.setOnClickListener(this);

        mrecyclerView_7 = (RecyclerView)v.findViewById(R.id.recyclerview_manpower);
        mrecyclerView_7.setLayoutManager(new LinearLayoutManager(getActivity()));
        madapter_7 = new adapter_7(getActivity());
        mrecyclerView_7.setAdapter(madapter_7);

        ItemTouchHelper.Callback mcallback_7 = new callback_7(madapter_7);
        mhelper_7 = new ItemTouchHelper(mcallback_7);
        mhelper_7.attachToRecyclerView(mrecyclerView_7);

        return v;
    }

    private class adapter_7 extends RecyclerView.Adapter<viewholder_7> implements Interface_Adapter_7 {
        private final Drag_Listener_7 mdraglistener;
        private int mCurrentType;

        private adapter_7(Context context) {
            mdraglistener = new Drag_Listener_7() {
                @Override
                public void OnStartDrag_5(RecyclerView.ViewHolder viewHolder) {
                    mhelper_7.startDrag(viewHolder);
                }
            };
        }

        @Override
        public viewholder_7 onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewitem_manpowergroup,
                    parent,false);
            return new viewholder_7(v);
        }

        @Override
        public void onBindViewHolder(viewholder_7 holder, int position) {
            holder.textView.setText(selectedtasks.get(position).getTaskName());
        }

        @Override
        public int getItemCount() {
            return selectedtasks.size();
        }
        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(selectedtasks,fromPosition,toPosition);
            notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onItemDismiss(final int position) {
            final Task_Object temptask = selectedtasks.get(position);
            Snackbar snackbar = Snackbar
                    .make(mrecyclerView_7, "ITEM REMOVED", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            selectedtasks.add(position,temptask);
                            notifyItemInserted(position);
                            mrecyclerView_7.scrollToPosition(position);
                        }
                    });
            snackbar.show();
            selectedtasks.remove(position);
            notifyItemRemoved(position);
        }
    }

    private class viewholder_7 extends RecyclerView.ViewHolder implements Interface_ViewHolder_7,View.OnClickListener{
        final TextView textView;

        private viewholder_7(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.text1);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Fragment currentfragment = getActivity().getFragmentManager().findFragmentById(R.id.MainFrame);
            edittask_dialogfragment edittask = new edittask_dialogfragment();
            taskposition = getAdapterPosition();
            mprefedit = mpref.edit();
            mprefedit.putInt("tasknumber",taskposition);
            mprefedit.apply();

            //Bundle bundle42 = new Bundle();
            //bundle42.putSerializable("task",taskslist.get(getAdapterPosition()));
            //edittask.setArguments(bundle42);
            //edittask.setTargetFragment(currentfragment,104);
            edittask.show(getFragmentManager(),"Dialog");
        }

        @Override
        public void onItemSelected() {itemView.setBackgroundColor(Color.LTGRAY);}
        @Override
        public void onItemClear() {itemView.setBackgroundColor(0);}
    }

    private void ExitMethod() {
        currentreport.setSelectedTasks(selectedtasks);
        if(reportnumber==-1){
            savedinfo.setTempreport(currentreport);
        }else {
            savedinfo.getSavedProjects().get(projectnumber).getProjectReports().set(reportnumber,currentreport);
        }
        ((MainActivity)getActivity()).setSaved_info(savedinfo);
        FragmentManager fm = getFragmentManager();
        F_1_1_1_NewReport fgmt = new F_1_1_1_NewReport();
        fm.beginTransaction()
                .add(R.id.MainFrame,fgmt,"tag_newreport")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabaddmanpower:
                ChooseActivityDialogFragment();
                break;
            case R.id.fabaccept:
                ExitMethod();
                break;
        }
    }

    private void ChooseActivityDialogFragment() {
        currentreport.setSelectedTasks(selectedtasks);
        chooseactivity_dialogfragment dialog_choose = new chooseactivity_dialogfragment();
        dialog_choose.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                savedinfo = ((MainActivity)getActivity()).getSaved_info();
                if(reportnumber==-1){
                    currentreport = savedinfo.getTempreport();
                }else {
                    currentreport = savedinfo.getSavedProjects().get(projectnumber).getProjectReports().get(reportnumber);
                }
                selectedtasks = currentreport.getSelectedTasks();
                madapter_7.notifyDataSetChanged();
            }
        });
        dialog_choose.show(getFragmentManager(),"Dialog");
    }

    private class callback_7 extends ItemTouchHelper.Callback{
        private static final float ALPHA_FULL = 1.0f;
        private final adapter_7 madapter_7;

        private callback_7(adapter_7 adapter) {madapter_7 = adapter;}
        @Override
        public boolean isLongPressDragEnabled() {return true;}
        @Override
        public boolean isItemViewSwipeEnabled() {return true;}
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            if(viewHolder.getItemViewType()!=target.getItemViewType()){return false;}
            madapter_7.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
            return false;
        }
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            madapter_7.onItemDismiss(viewHolder.getAdapterPosition());
        }
        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if(actionState != ItemTouchHelper.ACTION_STATE_IDLE ){
                if(viewHolder instanceof viewholder_7){
                    viewholder_7 v5 = (viewholder_7) viewHolder;
                    v5.onItemSelected();
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(ALPHA_FULL);
            if(viewHolder instanceof viewholder_7){
                viewholder_7 v5 = (viewholder_7) viewHolder;
                v5.onItemClear();
            }
        }
    }
    private interface Interface_Adapter_7{
        boolean onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }
    private interface Interface_ViewHolder_7{
        void onItemSelected();
        void onItemClear();
    }
    private interface Drag_Listener_7{
        void OnStartDrag_5(RecyclerView.ViewHolder viewHolder);
    }

}
