package com.ilaquidain.constructionreporter.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ilaquidain.constructionreporter.R;
import com.ilaquidain.constructionreporter.activities.MainActivity;
import com.ilaquidain.constructionreporter.fragments_newreport.NewProjectFragment;
import com.ilaquidain.constructionreporter.object.Project_Object;
import com.ilaquidain.constructionreporter.object.Saved_Info_Object;
import com.ilaquidain.constructionreporter.object.SquareImageView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;

public class F_1_MenuProjects extends Fragment implements View.OnClickListener{

    private RecyclerView mRecyclerView;
    private ProjectAdapter mAdapter;
    private ItemTouchHelper mitemTouchHelper;
    private Saved_Info_Object saved_info_object;
    private ArrayList<Project_Object> mProjects;
    private Bitmap bitmaplogo = null;
    private SquareImageView projectlogo;
    private Fragment currentfragment;
    //private final Project_Object currentproject;
    private Bundle bundle;
    private FragmentManager fm;

    private SharedPreferences mpref;
    private SharedPreferences.Editor mprefedit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saved_info_object = ((MainActivity)getActivity()).getSaved_info();
        mProjects = new ArrayList<>();
        if(saved_info_object!=null){
            mProjects = saved_info_object.getSavedProjects();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saved_info_object.setSavedProjects(mProjects);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_projectmenu,container,false);

        mpref = getActivity().getPreferences(Context.MODE_PRIVATE);

        currentfragment = getActivity().getFragmentManager().findFragmentById(R.id.MainFrame);
        fm = getFragmentManager();

        mRecyclerView = (RecyclerView)v.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);*/
        mAdapter = new ProjectAdapter();
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new HelperCallback14(mAdapter);
        mitemTouchHelper = new ItemTouchHelper(callback);
        mitemTouchHelper.attachToRecyclerView(mRecyclerView);


        FloatingActionButton fabaddproject = (FloatingActionButton)v.findViewById(R.id.fabadd);
        fabaddproject.setOnClickListener(this);

        return v;
    }

    private class ProjectAdapter extends RecyclerView.Adapter<ProjectViewHolder> implements HelperAdapter14{
        private final OnStarDragListener14 mdraglistener14;

        public ProjectAdapter() {
            super();
            mdraglistener14 = new OnStarDragListener14() {
                @Override
                public void onStartDrag14(RecyclerView.ViewHolder viewHolder) {
                    mitemTouchHelper.startDrag(viewHolder);
                }
            };
        }

        @Override
        public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_project,parent,false);
            return new ProjectViewHolder(v2);
        }

        @Override
        public void onBindViewHolder(ProjectViewHolder holder, int position) {
            holder.projectname.setText(mProjects.get(position).getProjectName());
            holder.projectrefno.setText(mProjects.get(position).getProjectRefNo());
            holder.projectaddress.setText(mProjects.get(position).getProjectAddress());
            bitmaplogo = null;
            String StoredPath2 = mProjects.get(position).getProjectId()+".jpg";
            try{
                File f = new File(getActivity().getApplicationContext().getFilesDir(),StoredPath2);
                bitmaplogo = BitmapFactory.decodeStream(new FileInputStream(f));
            }catch (Exception e){e.printStackTrace();}
            if(bitmaplogo!=null){
                holder.projectlogo.setImageBitmap(bitmaplogo);
            }else{
                holder.projectlogo.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        @Override
        public int getItemCount() {
            return mProjects.size();
        }
        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(mProjects,fromPosition,toPosition);
            notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onItemDismiss(final int position) {
            final Project_Object tempproject = mProjects.get(position);
            Snackbar snackbar = Snackbar
                    .make(mRecyclerView, "PROJECT REMOVED", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mProjects.add(position,tempproject);
                            notifyItemInserted(position);
                            mRecyclerView.scrollToPosition(position);
                        }
                    });
            snackbar.show();
            mProjects.remove(position);
            notifyItemRemoved(position);
        }
    }
    private class ProjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, HelperViewHolder14{
        final CardView cardView;
        final TextView projectname;
        final TextView projectrefno;
        final TextView projectaddress;
        final SquareImageView projectlogo;
        final ImageButton editproject;
        public ProjectViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.projectcardview);
            editproject = (ImageButton) itemView.findViewById(R.id.menu_icon);
            projectname = (TextView)itemView.findViewById(R.id.projectname);
            projectrefno = (TextView)itemView.findViewById(R.id.projectrefno);
            projectaddress = (TextView)itemView.findViewById(R.id.projectaddress);
            projectlogo = (SquareImageView)itemView.findViewById(R.id.ProjectLogo);
            cardView.setOnClickListener(this);
            editproject.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mprefedit = mpref.edit();
            mprefedit.putInt("projectnumber",getAdapterPosition());
            mprefedit.apply();

            switch (v.getId()){
                case R.id.menu_icon:
                    NewProjectFragment newproject = new NewProjectFragment();
                    newproject.setTargetFragment(currentfragment,5001);
                    /*bundle = new Bundle();
                    bundle.putInt("position",getAdapterPosition());
                    newproject.setArguments(bundle);*/
                    /*bundle = new Bundle();
                    Project_Object project_object = mProjects.get(getAdapterPosition());
                    bundle.putSerializable("currentproject",project_object);
                    newproject.setArguments(bundle);*/
                    fm.beginTransaction()
                            .replace(R.id.MainFrame,newproject)
                            .addToBackStack(null)
                            .commit();
                    break;
                case R.id.projectcardview:
                    F_1_1_ReportMenu mainmenuproject = new F_1_1_ReportMenu();
                    /*bundle = new Bundle();
                    bundle.putInt("position",getAdapterPosition());
                    mainmenuproject.setArguments(bundle);*/
                    /*mprefedit = mpref.edit();
                    mprefedit.putInt("projectnumber",getAdapterPosition());
                    mprefedit.apply();*/
                    fm.beginTransaction()
                            .replace(R.id.MainFrame,mainmenuproject)
                            .addToBackStack(null)
                            .commit();
                    break;
            }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabadd:
                NewProjectFragment newproject = new NewProjectFragment();
                newproject.setTargetFragment(currentfragment,5001);

                mprefedit = mpref.edit();
                mprefedit.putInt("projectnumber",-1);
                mprefedit.apply();

                //bundle = new Bundle();
                //Project_Object project_object = new Project_Object();
                //bundle.putSerializable("currentproject",project_object);
                //newproject.setArguments(bundle);
                fm.beginTransaction()
                        .replace(R.id.MainFrame,newproject)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }
    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK&&data!=null){
            switch (requestCode){
                case 5001:
                    //currentproject = (Project_Object)data.getSerializableExtra("currentproject");
                    saved_info_object = ((MainActivity)getActivity()).getSaved_info();


                    *//*Boolean contained = true;
                    for(int i=0;i<mProjects.size();i++){
                        if(mProjects.get(i).getProjectId().equals(currentproject.getProjectId())){
                            mProjects.set(i,currentproject);
                            contained = false;
                        }
                    }
                    if(contained){
                        mProjects.add(currentproject);
                        CreatePdfFileFolder(currentproject.getProjectName());
                    }*//*
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }*/

    /*private void CreatePdfFileFolder(String projectName) {
        String MainFolder = "ConstructionReporter";
        File pdfFolderFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS),MainFolder);
        if(!pdfFolderFile.exists()){
            pdfFolderFile.mkdir();
            Log.i("TAG","Main Folder Created");
        }
        String SecondaryFolder = currentproject.getProjectName();
        File pdfSecFolderFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/"+
                MainFolder,SecondaryFolder);
        if(!pdfSecFolderFile.exists()){
            pdfSecFolderFile.mkdir();
            Log.i("TAG","Secondary Folder Created");
        }
    }*/

    private class HelperCallback14 extends ItemTouchHelper.Callback{
        private static final float ALPHA_FULL = 1.0f;

        private final HelperAdapter14 helperadapter;

        private HelperCallback14(HelperAdapter14 mhelperadapter){
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
                if (viewHolder instanceof HelperViewHolder14) {
                    HelperViewHolder14 itemViewHolder = (HelperViewHolder14) viewHolder;
                    itemViewHolder.onItemSelected();
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(ALPHA_FULL);
            if (viewHolder instanceof HelperViewHolder14) {
                HelperViewHolder14 itemViewHolder = (HelperViewHolder14) viewHolder;
                itemViewHolder.onItemClear();
            }
        }
    }
    private interface HelperAdapter14{
        boolean onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }
    private interface HelperViewHolder14{
        void onItemSelected();
        void onItemClear();
    }
    private interface OnStarDragListener14{
        void onStartDrag14(RecyclerView.ViewHolder viewHolder);
    }
}
