package com.ilaquidain.constructionreporter.fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.ilaquidain.constructionreporter.R;
import com.ilaquidain.constructionreporter.object.RectangularImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Settings_Fragment extends Fragment {

    private SharedPreferences msharedpreferences;
    private SharedPreferences.Editor meditor;

    private final static String OriginatorName = "OriginatorName";
    private final static String OriginatorPosition = "OriginatorPosition";
    private final static String OriginatorCompany = "OrignatorCompany";
    private final static String IncludeManpower = "IncludeManpower";
    private final static String IncludeEquipment = "IncludeEquipment";
    private final static String IncludePhotos = "IncludePhotos";
    private final static String PhotosQuality = "PhotosQuality";

    private EditText originatorname;
    private EditText originatorpositoin;
    private EditText originatorcompany;
    private Switch includemanpower;
    private Switch includeequipment;
    private Switch includephotos;
    private Spinner photosquality;

    private Dialog dialog;
    private Dialog dialog2;
    private LinearLayout mContent;
    private Bitmap bitmap;
    private signature mSignature;
    private Button mClear,mGetSign,mCancel;
    private Button mClear2, mCancel2, mSelect2, mAccept2;
    private ImageView companylogoview;
    private View view3;

    private static final int PICK_IMAGE = 2;
    private Bitmap companylogo;
    private BitmapFactory.Options bmOptions = new BitmapFactory.Options();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings,container,false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        msharedpreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        meditor = msharedpreferences.edit();

        originatorname = (EditText)v.findViewById(R.id.settingoption1_1);
        String s1 = msharedpreferences.getString(OriginatorName,null);
        if(s1!=null){originatorname.setText(s1);}
        originatorpositoin = (EditText)v.findViewById(R.id.settingoption2_1);
        String s2 = msharedpreferences.getString(OriginatorPosition,null);
        if(s2!=null){originatorpositoin.setText(s2);}
        originatorcompany = (EditText)v.findViewById(R.id.settingoption3_1);
        String s3 = msharedpreferences.getString(OriginatorCompany,null);
        if(s3!=null){originatorcompany.setText(s3);}
        includemanpower = (Switch)v.findViewById(R.id.switch1);
        Boolean b1 = msharedpreferences.getBoolean(IncludeManpower,true);
        includemanpower.setChecked(b1);
        includeequipment = (Switch)v.findViewById(R.id.switch2);
        Boolean b2 = msharedpreferences.getBoolean(IncludeEquipment,true);
        includeequipment.setChecked(b2);
        includephotos = (Switch)v.findViewById(R.id.switch3);
        Boolean b3 = msharedpreferences.getBoolean(IncludePhotos,true);
        includephotos.setChecked(b3);
        photosquality = (Spinner)v.findViewById(R.id.photoqualityspinner);
        String s4 = msharedpreferences.getString(PhotosQuality,"Medium");
        List<String> array = Arrays.asList(getResources().getStringArray(R.array.photoquality));
        photosquality.setSelection(array.indexOf(s4));

        ImageButton save = (ImageButton)v.findViewById(R.id.btn_accept);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meditor.putString(OriginatorName,originatorname.getText().toString());
                meditor.putString(OriginatorPosition,originatorpositoin.getText().toString());
                meditor.putString(OriginatorCompany,originatorcompany.getText().toString());
                meditor.putBoolean(IncludeManpower,includemanpower.isChecked());
                meditor.putBoolean(IncludeEquipment,includeequipment.isChecked());
                meditor.putBoolean(IncludePhotos,includephotos.isChecked());
                meditor.putString(PhotosQuality,photosquality.getSelectedItem().toString());
                meditor.apply();

                Toast toast = Toast.makeText(getActivity(),"Changes Saved Succesfully",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                if ( getFragmentManager().getBackStackEntryCount() > 0)
                {
                    getFragmentManager().popBackStack();
                }
            }
        });



        ImageButton signature = (ImageButton)v.findViewById(R.id.editsignature);
        signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSignatureWindow();
            }

            private void ShowSignatureWindow() {
                dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_signature);
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog2) {
                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.WRAP_CONTENT);
                    }
                });

                mContent = (LinearLayout) dialog.findViewById(R.id.linearLayout);
                mSignature = new signature(getActivity(), null);
                mSignature.setBackgroundColor(Color.WHITE);
                // Dynamically generating Layout through java code
                mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mClear = (Button) dialog.findViewById(R.id.clear);
                mGetSign = (Button) dialog.findViewById(R.id.getsign);
                mGetSign.setEnabled(false);
                mCancel = (Button) dialog.findViewById(R.id.cancel);
                view3 = mContent;

                mClear.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.v("tag", "Panel Cleared");
                        mSignature.clear();
                        mGetSign.setEnabled(false);
                    }
                });
                mGetSign.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        Log.v("tag", "Panel Saved");
                        view3.setDrawingCacheEnabled(true);
                        mSignature.save(view3);
                        dialog.dismiss();
                        // Calling the same class
                        //recreate();
                    }
                });
                mCancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Log.v("tag", "Panel Cancelled");
                        dialog.dismiss();
                        // Calling the same class
                        //recreate();
                    }
                });
                dialog.show();
            }

        });

        final ImageButton companylogo_btn = (ImageButton)v.findViewById(R.id.editcompanylogo);
        companylogo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogcompanylogo();
            }

            private void dialogcompanylogo() {
                dialog2 = new Dialog(getActivity());
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.dialog_company);
                dialog2.setCancelable(true);
                dialog2.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog4) {
                        dialog2.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                WindowManager.LayoutParams.WRAP_CONTENT);
                        if(ContextCompat.checkSelfPermission(getActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                            mSelect2.setEnabled(false);
                            ActivityCompat.requestPermissions(getActivity(),new String[]
                                    {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                        }
                        SetUpBitMapOptions();
                        mAccept2.setEnabled(false);

                        Bitmap bitmaplogo = null;
                        String StoredPath2 = "logo.jpg";
                        try{
                            File f = new File(getActivity().getApplicationContext().getFilesDir(),StoredPath2);
                            bitmaplogo = BitmapFactory.decodeStream(new FileInputStream(f));
                        }catch (Exception e){e.printStackTrace();}
                        if(bitmaplogo!=null){
                            companylogoview.setImageBitmap(bitmaplogo);
                        }
                    }
                });

                companylogoview = (RectangularImageView)dialog2.findViewById(R.id.companylogo_view);

                mClear2 = (Button)dialog2.findViewById(R.id.companylogo_clear);
                mCancel2 = (Button)dialog2.findViewById(R.id.companylogo_cancel);
                mSelect2 = (Button)dialog2.findViewById(R.id.companylogo_select);
                mAccept2 = (Button)dialog2.findViewById(R.id.companylogo_accept);

                mClear2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        companylogoview.setImageResource(android.R.color.transparent);
                        mAccept2.setEnabled(true);
                    }
                });
                mCancel2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                });

                mAccept2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String StoredPathLogo = "logo.jpg";
                        try {
                            File f = new File(getActivity().getApplicationContext().getFilesDir(),StoredPathLogo);
                            FileOutputStream fos = new FileOutputStream(f);
                            companylogo.compress(Bitmap.CompressFormat.JPEG,100,fos);
                            fos.close();
                        } catch (Exception e) {
                            Log.v("log_tag", e.toString());
                        }
                        dialog2.dismiss();
                    }
                });

                mSelect2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickphoto = new Intent();
                        pickphoto.setType("image/*");
                        pickphoto.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(pickphoto,"Select Image"),PICK_IMAGE);

                    }
                });

                dialog2.show();
            }
        });



        return v;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==0){
            if(grantResults.length>0 &&
                    grantResults[0]== PackageManager.PERMISSION_GRANTED &&
                    grantResults[1]==PackageManager.PERMISSION_GRANTED){
                mSelect2.setEnabled(true);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK && data!=null){
            mAccept2.setEnabled(true);
            Uri uri1 = data.getData();
            String logopath = getImagePathFromUri(uri1);
            companylogo = BitmapFactory.decodeFile(logopath, bmOptions);
            if(companylogo!=null){companylogoview.setImageBitmap(companylogo);}
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
    public class signature extends View {

        private static final float STROKE_WIDTH = 5f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private final Paint paint = new Paint();
        private final Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        Bitmap bitmap1;
        Boolean shownphoto = true;


        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            obtainexistingsignature();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        private void obtainexistingsignature() {
            bitmap1 = null;
            String StoredPath = "signature.jpg";
            try{
                File f = new File(getActivity().getApplicationContext().getFilesDir(),StoredPath);
                bitmap1 = BitmapFactory.decodeStream(new FileInputStream(f));
            }catch (Exception e){e.printStackTrace();}
            shownphoto = bitmap1 != null;
        }

        public void save(View v) {
            Log.v("tag", "Width: " + v.getWidth());
            Log.v("tag", "Height: " + v.getHeight());
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            try {
                // Output the file
                v.draw(canvas);
                // Convert the output file to Image
                String s1 = "signature.jpg";
                File f = new File(getActivity().getApplicationContext().getFilesDir(),s1);
                FileOutputStream fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.JPEG,90,fos);
                fos.close();
            } catch (Exception e) {
                Log.v("log_tag", e.toString());
            }
        }

        public void clear() {
            shownphoto = false;
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if(shownphoto){
                canvas.drawBitmap(bitmap1,0,0,null);}
            else {
                canvas.drawPath(path,paint);
            }
            //canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {
            Log.v("log_tag", string);
        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
    private void SetUpBitMapOptions() {
        bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        int scaleFactor = 4;
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
    }
}
