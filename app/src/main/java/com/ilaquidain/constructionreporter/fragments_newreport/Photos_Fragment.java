package com.ilaquidain.constructionreporter.fragments_newreport;


import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.ilaquidain.constructionreporter.R;
import com.ilaquidain.constructionreporter.activities.MainActivity;
import com.ilaquidain.constructionreporter.dialogfragments.editphoto_dialogfragment;
import com.ilaquidain.constructionreporter.object.Image_Object;
import com.ilaquidain.constructionreporter.object.Report_Object;
import com.ilaquidain.constructionreporter.object.Saved_Info_Object;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class Photos_Fragment extends Fragment implements View.OnClickListener{

    static final int PICK_IMAGE_4 = 423;
    private int positionsent;
    private String mCurrentPhotoPath;
    private BitmapFactory.Options bmOptions = new BitmapFactory.Options();

    private RecyclerView mrecyclerview;
    private photoadapter madapter;
    private ArrayList<Image_Object> photolist = new ArrayList<>();
    private final ArrayList<Bitmap> photosshown = new ArrayList<>();

    private ItemTouchHelper itemtouchhelper12;
    private FloatingActionButton add_Photo;

    private Report_Object currentreport;

    private Integer projectnumber, reportnumber;
    private SharedPreferences mpref;
    private Saved_Info_Object savedinfo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photos,container,false);

        SetUpBitMapOptions();
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

        //currentreport = (Report_Object) getArguments().getSerializable("currentreport");
        //getArguments().remove("currentreport");
        if(currentreport!=null){
            photolist=currentreport.getSelectedPhotos();
            for (int j=0;j<photolist.size();j++){
                AddBitmapOfPhotoTaken(photolist.get(j).getPathDevice());
            }
        }

        add_Photo = (FloatingActionButton)v.findViewById(R.id.fabaddphoto);
        add_Photo.setOnClickListener(this);
        FloatingActionButton fabaccept = (FloatingActionButton)v.findViewById(R.id.fabaccept);
        fabaccept.setOnClickListener(this);

        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            add_Photo.setEnabled(false);
            ActivityCompat.requestPermissions(getActivity(),new String[]
                    {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }

        mrecyclerview = (RecyclerView)v.findViewById(R.id.recyclerview);
        mrecyclerview.setLayoutManager(new GridLayoutManager(getActivity(),2));
        madapter = new photoadapter();
        mrecyclerview.setAdapter(madapter);

        ItemTouchHelper.Callback callback = new HelperCallback12(madapter);
        itemtouchhelper12 = new ItemTouchHelper(callback);
        itemtouchhelper12.attachToRecyclerView(mrecyclerview);

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==0){
            if(grantResults.length>0 &&
                    grantResults[0]== PackageManager.PERMISSION_GRANTED &&
                    grantResults[1]==PackageManager.PERMISSION_GRANTED){
                add_Photo.setEnabled(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabaddphoto:
                add_photo_to_report();
                break;
            case R.id.fabaccept:
                ExitMethod();
                break;
        }
    }

    private void ExitMethod() {
        currentreport.setSelectedPhotos(photolist);
        if(reportnumber==-1){
            savedinfo.setTempreport(currentreport);
        }else {
            savedinfo.getSavedProjects().get(projectnumber).getProjectReports().set(reportnumber,currentreport);
        }
        ((MainActivity)getActivity()).setSaved_info(savedinfo);
        if ( getFragmentManager().getBackStackEntryCount() > 0)
        {getFragmentManager().popBackStack();}
    }

    private void add_photo_to_report() {
        Intent pickphoto = new Intent();
        pickphoto.setType("image/*");
        pickphoto.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickphoto,"Select Image"),PICK_IMAGE_4);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PICK_IMAGE_4 && resultCode==RESULT_OK && data!=null){
            Uri uri1 = data.getData();
            String photopath = getImagePathFromUri(uri1);
            mCurrentPhotoPath = photopath;
            File f = new File(photopath);
            Date date1 = new Date(f.lastModified());
            String photodate = new SimpleDateFormat("EEEE dd, MMM yyyy - HH:mm:ss", Locale.US).format(date1);
            AddImageToList(photopath,photodate);
        }else if(requestCode==2001 && resultCode==RESULT_OK&&data!=null){
            Image_Object receivedimate = (Image_Object)data.getSerializableExtra("photo");
            photolist.set(positionsent,receivedimate);
        }
    }
    private void AddImageToList(String ImagePath, String ImageDate){
        Image_Object  Photo2 = new Image_Object();
        Photo2.setPathDevice(ImagePath);
        Photo2.setPhotoDate(ImageDate);
        photolist.add(Photo2);
        AddBitmapOfPhotoTaken(ImagePath);
        madapter.notifyDataSetChanged();
        galleryAddPic();
    }
    private String getImagePathFromUri(Uri Uri1){

        String pathfromuri = "";
        if(Build.VERSION.SDK_INT>=19){
            String WholeID = DocumentsContract.getDocumentId(Uri1);
            String id = WholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID+"=?";
            Cursor cursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{ id }, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                pathfromuri = cursor.getString(columnIndex);
            }
            cursor.close();
            return pathfromuri;
        }else{
            String[] proj = { MediaStore.Images.Media.DATA };
            pathfromuri = null;

            CursorLoader cursorLoader = new CursorLoader(
                    getActivity(),Uri1, proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();

            if(cursor != null){
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                pathfromuri = cursor.getString(column_index);
            }
            return pathfromuri;
        }
    }
    private void AddBitmapOfPhotoTaken(String PhotoPath){
        Bitmap bitmap = BitmapFactory.decodeFile(PhotoPath, bmOptions);
        photosshown.add(bitmap);
    }
    private void SetUpBitMapOptions() {
        bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        int scaleFactor = 8;
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
        MediaScannerConnection.scanFile(getActivity(), new String[]{mCurrentPhotoPath}, new String[]{"image/jpeg"}, null);
    }

    private class photoadapter extends RecyclerView.Adapter<photoviewholder> implements HelperAdapter12{
        private final OnStarDragListener12 onStarDragListener12;

        public photoadapter() {
            super();
            onStarDragListener12 = new OnStarDragListener12() {
                @Override
                public void onStartDrag12(RecyclerView.ViewHolder viewHolder) {
                    itemtouchhelper12.startDrag(viewHolder);
                }
            };
        }

        @Override
        public photoviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewitem_photoitem,
                    parent,false);
            return new photoviewholder(v2);
        }

        @Override
        public void onBindViewHolder(photoviewholder holder, int position) {
            holder.view.setImageBitmap(photosshown.get(position));
        }

        @Override
        public int getItemCount() {
            return photosshown.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(photolist,fromPosition,toPosition);
            Collections.swap(photosshown,fromPosition,toPosition);
            notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onItemDismiss(final int position) {
        }
    }
    private class photoviewholder extends RecyclerView.ViewHolder implements View.OnClickListener, HelperViewHolder12{
        final ViewFlipper viewFlipper;
        final ImageView view;
        final Button viewphoto;
        final Button editphoto;
        final Button deletephoto;

        public photoviewholder(View itemView) {
            super(itemView);
            viewFlipper = (ViewFlipper)itemView.findViewById(R.id.viewflipper);
            viewphoto = (Button)itemView.findViewById(R.id.btn_viewphoto);
            editphoto = (Button)itemView.findViewById(R.id.btn_editphoto);
            deletephoto = (Button)itemView.findViewById(R.id.btn_deletephoto);

            viewphoto.setOnClickListener(this);
            editphoto.setOnClickListener(this);
            deletephoto.setOnClickListener(this);

            view = (ImageView)itemView.findViewById(R.id.image1);
            viewFlipper.setInAnimation(getActivity(),android.R.anim.slide_in_left);
            viewFlipper.setOutAnimation(getActivity(),android.R.anim.slide_out_right);
            viewFlipper.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.viewflipper:
                    viewFlipper.setDisplayedChild(1);
                    android.os.Handler handler = new android.os.Handler();
                    final Runnable r = new Runnable() {
                        @Override
                        public void run() {viewFlipper.setDisplayedChild(0);}};
                    handler.postDelayed(r,1500);
                    break;
                case R.id.btn_viewphoto:
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    File f8 = new File(photolist.get(getAdapterPosition()).getPathDevice());
                    Uri PhotoUri2 = FileProvider.getUriForFile(getActivity(),
                            "com.ilaquidain.constructionreporter.provider",f8);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(PhotoUri2,"image");
                    startActivity(intent);
                    break;
                case R.id.btn_deletephoto:
                    viewFlipper.setDisplayedChild(0);
                    photolist.remove(getAdapterPosition());
                    photosshown.remove(getAdapterPosition());
                    madapter.notifyItemRemoved(getAdapterPosition());
                    break;
                case R.id.btn_editphoto:
                    positionsent = getAdapterPosition();
                    Fragment currentfragment = getActivity().getFragmentManager().findFragmentById(R.id.MainFrame);
                    editphoto_dialogfragment dialog_choose = new editphoto_dialogfragment();

                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("photo",photolist.get(getAdapterPosition()));
                    bundle2.putParcelable("bitmap",photosshown.get(getAdapterPosition()));
                    dialog_choose.setArguments(bundle2);

                    dialog_choose.setTargetFragment(currentfragment,2001);
                    dialog_choose.show(getFragmentManager(),"Dialog");
                    break;
            }

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(
                    ContextCompat.getColor(getActivity(),R.color.selection_light_orange)
            );
        }

        @Override
        public void onItemClear() {
        }
    }
    private class HelperCallback12 extends ItemTouchHelper.Callback{
        private static final float ALPHA_FULL = 1.0f;

        private final HelperAdapter12 helperadapter;

        private HelperCallback12(HelperAdapter12 mhelperadapter){
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
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
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
                if (viewHolder instanceof HelperViewHolder12) {
                    HelperViewHolder12 itemViewHolder = (HelperViewHolder12) viewHolder;
                    itemViewHolder.onItemSelected();
                }
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setAlpha(ALPHA_FULL);
            if (viewHolder instanceof HelperViewHolder12) {
                HelperViewHolder12 itemViewHolder = (HelperViewHolder12) viewHolder;
                itemViewHolder.onItemClear();
            }
        }
    }
    private interface HelperAdapter12{
        boolean onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }
    private interface HelperViewHolder12{
        void onItemSelected();
        void onItemClear();
    }
    private interface OnStarDragListener12{
        void onStartDrag12(RecyclerView.ViewHolder viewHolder);
    }
}
