package com.ilaquidain.constructionreporter.fragments_newreport;


import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ilaquidain.constructionreporter.R;
import com.ilaquidain.constructionreporter.activities.MainActivity;
import com.ilaquidain.constructionreporter.object.Project_Object;
import com.ilaquidain.constructionreporter.object.RectangularImageView;
import com.ilaquidain.constructionreporter.object.Saved_Info_Object;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import static android.app.Activity.RESULT_OK;


public class NewProjectFragment extends Fragment implements View.OnClickListener{

    private Saved_Info_Object savedinfo;
    private Project_Object currentproject;
    private String projectlogopath;
    private static final int PICK_IMAGE = 105;

    private String projectid;
    private EditText projectname,projectrefno,projectaddress;
    private RectangularImageView projectlogo;
    private BitmapFactory.Options bmOptions;
    private Bitmap projectlogobitmap;
    private Dialog dialog2;

    private Integer projectnumber, reportnumber;
    private SharedPreferences mpref;
    private SharedPreferences.Editor mprefedit;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_projectinfo,container,false);

        FloatingActionButton fabaccet = (FloatingActionButton)v.findViewById(R.id.fabaccept);
        fabaccet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitMethod();
            }
        });

        savedinfo = ((MainActivity)getActivity()).getSaved_info();
        mpref= getActivity().getPreferences(Context.MODE_PRIVATE);
        projectnumber = mpref.getInt("projectnumber",-1);
        if (savedinfo!=null && projectnumber!=-1) {
            currentproject = savedinfo.getSavedProjects().get(projectnumber);
        }else {
            currentproject = new Project_Object();
        }

        /*if(getArguments()!=null){
            projectnumber = getArguments().getInt("position");
            getArguments().remove("position");
            currentproject = savedinfo.getSavedProjects().get(projectnumber);}
        else {
            projectnumber = null;
            currentproject = new Project_Object();
        }*/

        projectid = currentproject.getProjectId();
        projectname = (EditText)v.findViewById(R.id.df_projectinfo_3);
        projectrefno = (EditText) v.findViewById(R.id.df_projectinfo_6);
        projectaddress = (EditText) v.findViewById(R.id.df_projectinfo_9);
        projectname.setText(currentproject.getProjectName());
        projectrefno.setText(currentproject.getProjectRefNo());
        projectaddress.setText(currentproject.getProjectAddress());

        projectlogo = (RectangularImageView)v.findViewById(R.id.projectlogoview);
        if(ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            projectlogo.setEnabled(false);
            ActivityCompat.requestPermissions(getActivity(),new String[]
                    {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }

        Bitmap bitmaplogo = null;
        String StoredPath2 = currentproject.getProjectId()+".jpg";
        try{
            File f = new File(getActivity().getApplicationContext().getFilesDir(),StoredPath2);
            bitmaplogo = BitmapFactory.decodeStream(new FileInputStream(f));
        }catch (Exception e){e.printStackTrace();}
        if(bitmaplogo!=null){
            projectlogo.setImageBitmap(bitmaplogo);
        }

        projectlogo.setOnClickListener(this);

        ImageButton deletelogo = (ImageButton)v.findViewById(R.id.deleteprojectlogo);
        deletelogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmaplogo = null;
                String StoredPath2 = currentproject.getProjectId()+".jpg";
                try{
                    File f = new File(getActivity().getApplicationContext().getFilesDir(),StoredPath2);
                    if(f.exists()){
                        Boolean delete = f.delete();
                        projectlogo.setImageResource(android.R.color.transparent);
                    }
                }catch (Exception e){e.printStackTrace();}
            }
        });

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.projectlogoview:
                SetUpBitMapOptions();
                Intent pickphoto = new Intent();
                pickphoto.setType("image/*");
                pickphoto.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(pickphoto,"Select Image"),PICK_IMAGE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK && data!=null){
            Uri uri1 = data.getData();
            projectlogopath = getImagePathFromUri(uri1);
            projectlogobitmap = BitmapFactory.decodeFile(projectlogopath, bmOptions);
            if(projectlogobitmap!=null){projectlogo.setImageBitmap(projectlogobitmap);}
            String StoredPathLogo = currentproject.getProjectId()+".jpg";
            try {
                File f = new File(getActivity().getApplicationContext().getFilesDir(),StoredPathLogo);
                FileOutputStream fos = new FileOutputStream(f);
                projectlogobitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                fos.close();
            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }
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
    private void SetUpBitMapOptions() {
        bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        int scaleFactor = 2;
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
    }

    private void ExitMethod() {
        currentproject.setProjectName(projectname.getText().toString());
        currentproject.setProjectRefNo(projectrefno.getText().toString());
        currentproject.setProjectAddress(projectaddress.getText().toString());
        //Guradamos la lista de posibles opciones y la opcion default
        if(projectnumber!=-1){
            savedinfo.getSavedProjects().set(projectnumber,currentproject);}
        else {savedinfo.getSavedProjects().add(currentproject);}
        ((MainActivity)getActivity()).setSaved_info(savedinfo);
        //Intent intent = new Intent();
        //intent.putExtra("currentproject",(Serializable)currentproject);
        //getTargetFragment().onActivityResult(5001, Activity.RESULT_OK,intent);
        if ( getFragmentManager().getBackStackEntryCount() > 0)
        {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==0){
            if(grantResults.length>0 &&
                    grantResults[0]== PackageManager.PERMISSION_GRANTED &&
                    grantResults[1]==PackageManager.PERMISSION_GRANTED){
                projectlogo.setEnabled(true);
            }
        }
    }
}
