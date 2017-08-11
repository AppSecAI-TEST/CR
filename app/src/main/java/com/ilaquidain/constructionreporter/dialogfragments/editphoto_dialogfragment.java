package com.ilaquidain.constructionreporter.dialogfragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ilaquidain.constructionreporter.R;
import com.ilaquidain.constructionreporter.activities.MainActivity;
import com.ilaquidain.constructionreporter.object.Image_Object;
import com.ilaquidain.constructionreporter.object.Saved_Info_Object;

import java.io.Serializable;
import java.util.ArrayList;

public class editphoto_dialogfragment extends DialogFragment implements View.OnClickListener {
    private Spinner spinner;
    private TextView textView2;
    private Image_Object photoobject;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog!=null && dialog.getWindow()!=null){
            int Width = ViewGroup.LayoutParams.MATCH_PARENT;
            int Height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(Width,Height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialogfragment_photo,container,false);

        Saved_Info_Object savedinfo = ((MainActivity)getActivity()).getSaved_info();
        ArrayList<String> availableactivities = new ArrayList<>(savedinfo.getListasOpciones().get(9));
        photoobject = (Image_Object)getArguments().getSerializable("photo");
        Bitmap photoshown = getArguments().getParcelable("bitmap");
        getArguments().remove("photo");
        getArguments().remove("bitmap");

        ImageView imageView = (ImageView)v.findViewById(R.id.image1);
        spinner = (Spinner) v.findViewById(R.id.spinner1);

        TextView textView1 = (TextView)v.findViewById(R.id.textview1);
        textView2 = (TextView)v.findViewById(R.id.textview2);

        textView2.setOnClickListener(this);

        FloatingActionButton fabaccept = (FloatingActionButton)v.findViewById(R.id.fabaccept);
        fabaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitMethod();
            }
        });


        if(!availableactivities.contains("N/A")){availableactivities.add(0,"N/A");}
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(),
                R.layout.spinner_simpleitem, availableactivities); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdownitem);
        spinner.setAdapter(spinnerArrayAdapter);

        if(photoobject.getPhotoActivity()!=null && availableactivities.contains(photoobject.getPhotoActivity())){
            spinner.setSelection(availableactivities.indexOf(photoobject.getPhotoActivity()));
        }

        textView1.setText(photoobject.getPhotoDate());
        textView2.setText(photoobject.getPhotoDescription());
        imageView.setImageBitmap(photoshown);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textview2:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                //LayoutInflater inflater = getActivity().getLayoutInflater();
                View v3 = View.inflate(getActivity(),R.layout.builder_simpleadditem,null);
                builder.setView(v3);

                TextView title = (TextView)v3.findViewById(R.id.builder_title);
                title.setText(getResources().getText(R.string.PhotoDescription));

                final EditText editText = (EditText)v3.findViewById(R.id.edittext1);
                ImageButton accept = (ImageButton)v3.findViewById(R.id.fabaccept);
                ImageButton cancel = (ImageButton)v3.findViewById(R.id.fabcancel);

                if(photoobject.getPhotoDescription()!=null){
                    editText.setText(photoobject.getPhotoDescription());
                }

                final AlertDialog dialog = builder.create();
                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s1 = editText.getText().toString();
                        if(!s1.equals("")){
                            photoobject.setPhotoDescription(s1);
                            textView2.setText(s1);
                            dialog.dismiss();
                        }else {
                            dialog.dismiss();
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                if(dialog.getWindow()!=null){
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));}
                dialog.show();
        }
    }

    //Guradamos la lista de posibles opciones y la opcion default
    private void ExitMethod() {
        photoobject.setPhotoDescription(textView2.getText().toString());
        photoobject.setPhotoActivity(spinner.getSelectedItem().toString());
        Intent intent = new Intent();
        intent.putExtra("photo", (Serializable) photoobject);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        getDialog().dismiss();
    }
}
