package com.ilaquidain.constructionreporter.activities;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.ilaquidain.constructionreporter.R;
import com.ilaquidain.constructionreporter.fragments.F_1_1_1_NewReport;
import com.ilaquidain.constructionreporter.fragments.F_1_MenuProjects;
import com.ilaquidain.constructionreporter.fragments.Settings_Fragment;
import com.ilaquidain.constructionreporter.object.Image_Object;
import com.ilaquidain.constructionreporter.object.Project_Object;
import com.ilaquidain.constructionreporter.object.Report_Object;
import com.ilaquidain.constructionreporter.object.Saved_Info_Object;
import com.ilaquidain.constructionreporter.object.Task_Object;
import com.ilaquidain.constructionreporter.object.Worker_Object;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Saved_Info_Object saved_info;
    public void setSaved_info(Saved_Info_Object msaved_info){saved_info = msaved_info;}
    public Saved_Info_Object getSaved_info(){return saved_info;}

    private Document doc;
    private PdfWriter writer;
    private Paragraph par;
    private PdfPTable table;
    private PdfPCell per;
    private final BaseColor lightgray = new BaseColor(230,230,230);
    private String tempstring;
    private SharedPreferences msharedpreferences;
    private SharedPreferences.Editor editor;
    private Report_Object mcurrentreport;
    private Project_Object mcurrentproject;
    private Integer mreportnumber;
    private BitmapFactory.Options bmOptions = new BitmapFactory.Options();
    private ArrayList<Image> imagenes;
    private final static String OriginatorName = "OriginatorName";
    private final static String OriginatorPosition = "OriginatorPosition";
    private final static String OriginatorCompany = "OrignatorCompany";
    private final static String IncludeManpower = "IncludeManpower";
    private final static String IncludeEquipment = "IncludeEquipment";
    private final static String IncludePhotos = "IncludePhotos";
    private final static String PhotosQuality = "PhotosQuality";
    private Font Cal11BU,Cal11B,Cal9,Cal9B_White;

    @Override
    protected void onPause() {
        super.onPause();
        SaveInfoToMemory();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        msharedpreferences = this.getPreferences(Context.MODE_PRIVATE);
        this.GetInfoFromMemory();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //CheckIfAlreadyInstalled();

        FragmentManager fm = getFragmentManager();
        Fragment fgmt = new F_1_MenuProjects();
        fm.beginTransaction()
                .add(R.id.MainFrame,fgmt)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FragmentManager fm = getFragmentManager();
            Settings_Fragment fgmt = new Settings_Fragment();
            fm.beginTransaction()
                    .replace(R.id.MainFrame,fgmt)
                    .addToBackStack(null)
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SaveInfoToMemory() {
        Saved_Info_Object tempinfo = getSaved_info();
        if(tempinfo==null){tempinfo = new Saved_Info_Object();}
        String FileNameTrades = "Info.data";
        try{
            File f = new File(getApplicationContext().getFilesDir(),FileNameTrades);
            FileOutputStream fout = new FileOutputStream(f);
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(tempinfo);
            out.close();
        }catch (Exception e){e.printStackTrace();}
    }
    public void GetInfoFromMemory() {
        Saved_Info_Object tempinfo2 = new Saved_Info_Object();
        String S1 = "Info.data";
        try{
            FileInputStream fileIn2 = new FileInputStream(getFilesDir()+ File.separator+S1);
            ObjectInput in2 = new ObjectInputStream(fileIn2);
            tempinfo2 = (Saved_Info_Object) in2.readObject();
            in2.close();
        }catch (Exception e){e.printStackTrace();}

        if(tempinfo2==null){
            tempinfo2 = new Saved_Info_Object();
            setSaved_info(tempinfo2);
        }else {
            setSaved_info(tempinfo2);
        }
        AddDefaultInfo();
    }
    private void AddDefaultInfo() {
        //Report Info
        //Option 0 -- Default Options
        //Option 1 -- Project Name
        //Option 2 -- Project Id
        //Option 3 -- Project Address
        //Option 4 -- List of Contractor
        //Option 5 -- Weather
        //Option 6 -- Report Type
        //Option 7 -- Report Discipline
        //Option 8 -- Report Date
        //Option 9 -- Activities
        //Option 10 -- Start Time
        //Option 11 -- End Time

        Saved_Info_Object savedinfo_2 = getSaved_info();
        ArrayList<ArrayList<String>> templist1 = savedinfo_2.getListasOpciones();
        if(templist1==null){templist1 = new ArrayList<>();}
        if(templist1.size()<10){
            for(int j=templist1.size();j<10;j++){
                ArrayList<String> templist2 = new ArrayList<>();
                switch (j){
                    case 0:
                        templist2.add("N/A");//0
                        templist2.add("Project A");//1
                        templist2.add("Project ID 000");//2
                        templist2.add("ABC Street, Los Angeles, 98001, CA");//3
                        templist2.add("Contractor A");//4
                        templist2.add("Clear");//5
                        templist2.add("QC Report");//6
                        templist2.add("Structural");//7
                        templist2.add("N/A");//8
                        templist2.add("N/A");//9
                        templist2.add("07:00");//10
                        templist2.add("16:00");//11
                    case 1:
                        templist2.add("Project A");
                        templist2.add("Project B");
                        templist2.add("Project C");
                        break;
                    case 2:
                        templist2.add("Project ID 001");
                        templist2.add("Project ID 002");
                        templist2.add("Project ID 003");
                        break;
                    case 3:
                        templist2.add("ABC Street, Los Angeles, 98001, CA");
                        templist2.add("XYZ Avenue, Long Beach, 98002, CA");
                        break;
                    case 4:
                        templist2.add("Contractor A");
                        templist2.add("Contractor B");
                        break;
                    case 5:
                        templist2.add("Clear");
                        templist2.add("Overcast");
                        templist2.add("Rain");
                        break;
                    case 6:
                        templist2.add("Production Report");
                        templist2.add("QC Report");
                        templist2.add("QA Report");
                        break;
                    case 7:
                        templist2.add("Highways");
                        templist2.add("Environmental");
                        templist2.add("Structural");
                        templist2.add("Mechanical");
                        templist2.add("Electrical");
                        break;
                    case 8:
                        templist2.add("Current Date");
                        break;
                    case 9:
                        templist2.add("Activity_1");
                        templist2.add("Activity_2");
                }
                templist1.add(templist2);
            }
            savedinfo_2.setListasOpciones(templist1);
        }

        if(savedinfo_2.getWorkerGroups().size()==0){
            ArrayList<String> templist3 = new ArrayList<>();
            templist3.add("Carpenters");
            templist3.add("Ironworkers");
            templist3.add("Laborers");
            templist3.add("Finishers");
            templist3.add("Operators");
            savedinfo_2.setWorkerGroups(templist3);
            if(savedinfo_2.getListAvailableManpower().size()==0){
                ArrayList<ArrayList<Worker_Object>> availablemanpower = new ArrayList<>();
                for(int i=0;i<templist3.size();i++){
                    ArrayList<Worker_Object> workerlist = new ArrayList<>();
                    Worker_Object worker = new Worker_Object();
                    worker.setName("Foreman");
                    worker.setActivity("N/A");
                    worker.setCompany("N/A");
                    worker.setTrade("N/A");
                    worker.setIdNumber(UUID.randomUUID().toString());
                    worker.setHours("8");
                    workerlist.add(worker);
                    worker = new Worker_Object();
                    worker.setName("Journeyman");
                    worker.setActivity("N/A");
                    worker.setCompany("N/A");
                    worker.setTrade("N/A");
                    worker.setIdNumber(UUID.randomUUID().toString());
                    worker.setHours("8");
                    workerlist.add(worker);
                    worker = new Worker_Object();
                    worker.setName("Apprentice");
                    worker.setActivity("N/A");
                    worker.setCompany("N/A");
                    worker.setTrade("N/A");
                    worker.setIdNumber(UUID.randomUUID().toString());
                    worker.setHours("8");
                    workerlist.add(worker);
                    availablemanpower.add(workerlist);
                }
                savedinfo_2.setListAvailableManpower(availablemanpower);
            }

        }
        if(savedinfo_2.getEquipmentGroups().size()==0){
            ArrayList<String> templist3 = new ArrayList<>();
            templist3.add("Cranes");
            templist3.add("Forklifts");
            templist3.add("Boomlifts");
            savedinfo_2.setEquipmentGroups(templist3);
            if(savedinfo_2.getListAvailableEquipment().size()==0){
                ArrayList<ArrayList<Worker_Object>> availableequipment = new ArrayList<>();
                for(int i=0;i<templist3.size();i++){
                    ArrayList<Worker_Object> workerlist = new ArrayList<>();
                    Worker_Object worker = new Worker_Object();
                    worker.setName(templist3.get(i).substring(0,templist3.get(i).length()-1)+" 1");
                    worker.setActivity("N/A");
                    worker.setCompany("N/A");
                    worker.setTrade("N/A");
                    worker.setIdNumber(UUID.randomUUID().toString());
                    worker.setHours("8");
                    workerlist.add(worker);
                    worker = new Worker_Object();
                    worker.setName(templist3.get(i).substring(0,templist3.get(i).length()-1)+" 2");
                    worker.setActivity("N/A");
                    worker.setCompany("N/A");
                    worker.setTrade("N/A");
                    worker.setIdNumber(UUID.randomUUID().toString());
                    worker.setHours("8");
                    workerlist.add(worker);
                    worker = new Worker_Object();
                    worker.setName(templist3.get(i).substring(0,templist3.get(i).length()-1)+" 3");
                    worker.setActivity("N/A");
                    worker.setCompany("N/A");
                    worker.setTrade("N/A");
                    worker.setIdNumber(UUID.randomUUID().toString());
                    worker.setHours("8");
                    workerlist.add(worker);
                    availableequipment.add(workerlist);
                }
                savedinfo_2.setListAvailableEquipment(availableequipment);
            }

            this.setSaved_info(savedinfo_2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==0){
            if(grantResults.length>0 &&
                    grantResults[0]!= PackageManager.PERMISSION_GRANTED){
                Toast toast = Toast.makeText(this,"PDF Could Not be Saved in Device",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }
    }


    /*private void CheckIfAlreadyInstalled() {
        Boolean firstime = msharedpreferences.getBoolean("FirstTime",true);
        editor = msharedpreferences.edit();
        if(firstime){
            CreateSampleReport();
            editor.putBoolean("FirstTime",false);
            editor.apply();
        }
    }*/
    /*private void CreateSampleReport() {
        Project_Object sampleproject = new Project_Object();
        sampleproject.setProjectName("Sample Project");
        sampleproject.setProjectId(UUID.randomUUID().toString());
        sampleproject.setProjectRefNo("Bridge No.: 0001");
        sampleproject.setProjectAddress("500 ABC Street, Los Angeles, California");

        editor.putString(OriginatorName,"John Smith");
        editor.putString(OriginatorPosition,"Field Engineer");
        editor.putString(OriginatorCompany,"United Contractors");
        editor.apply();

        Report_Object samplereport = new Report_Object();

        sampleproject = AddPhotos(sampleproject);
        samplereport = AddPhotosReport(samplereport);

        ArrayList<Worker_Object> reportworkers = new ArrayList<>();
        for(int i=0;i<10;i++){
            Worker_Object worker = new Worker_Object();
            worker.setName("Worker "+String.valueOf(i));
            worker.setCompany("United Contractors");
            worker.setActivity("South Tower Construction");
            worker.setHours("8");
            reportworkers.add(worker);
        }
        samplereport.setSelectedWorkers(reportworkers);

        ArrayList<Task_Object> tasks = new ArrayList<>();
        Task_Object task = new Task_Object();
        task.setTaskName("Foundation Construction");
        task.setTaskDescription(getResources().getString(R.string.sampletaskdescription));
        tasks.add(task);
        samplereport.setSelectedTasks(tasks);

        samplereport.getReportInfo().set(4,"United Contractors");
        samplereport.getReportInfo().set(5,"Fog");
        samplereport.getReportInfo().set(6,"QC Daily Report");
        samplereport.getReportInfo().set(7,"Structural");
        samplereport.getReportInfo().set(8,"07/24/2017");
        samplereport.getReportInfo().set(10,"05:00");
        samplereport.getReportInfo().set(11,"14:00");

        ArrayList<Project_Object> savedprojects = new ArrayList<>();
        sampleproject.getProjectReports().add(samplereport);
        savedprojects.add(sampleproject);
        this.getSaved_info().setSavedProjects(savedprojects);

        this.GeneratePDFReport(sampleproject,sampleproject.getProjectReports().indexOf(samplereport));
    }
    private Report_Object AddPhotosReport(Report_Object samplereport) {
        ArrayList<Image_Object> defaultactivityimages = new ArrayList<>();
        Bitmap sampleimage1 = BitmapFactory.decodeResource(getResources(),R.drawable.samp1);
        String StorePathLogo4 = "samp1.jpg";
        try{
            File f = new File(this.getApplicationContext().getFilesDir(),StorePathLogo4);
            FileOutputStream fos = new FileOutputStream(f);
            sampleimage1.compress(Bitmap.CompressFormat.JPEG,100,fos);
            Image_Object image1 = new Image_Object();
            image1.setPathDevice(f.getPath());
            image1.setPhotoDate("Monday 24, July 2017");
            image1.setPhotoActivity("Sample Project");
            image1.setPhotoDescription("View Looking North");
            defaultactivityimages.add(image1);
            fos.close();
        }catch (Exception e) {
            Log.v("log_tag", e.toString());
        }
        Bitmap sampleimage2 = BitmapFactory.decodeResource(getResources(),R.drawable.samp2);
        String StorePathLogo5 = "samp2.jpg";
        try{
            File f = new File(this.getApplicationContext().getFilesDir(),StorePathLogo5);
            FileOutputStream fos = new FileOutputStream(f);
            sampleimage2.compress(Bitmap.CompressFormat.JPEG,100,fos);
            Image_Object image1 = new Image_Object();
            image1.setPathDevice(f.getPath());
            image1.setPhotoDate("Monday 24, July 2017");
            image1.setPhotoActivity("Sample Project");
            image1.setPhotoDescription("Aerial View");
            defaultactivityimages.add(image1);
            fos.close();
        }catch (Exception e) {
            Log.v("log_tag", e.toString());
        }
        Bitmap sampleimage3 = BitmapFactory.decodeResource(getResources(),R.drawable.samp3);
        String StorePathLogo6 = "samp3.jpg";
        try{
            File f = new File(this.getApplicationContext().getFilesDir(),StorePathLogo6);
            FileOutputStream fos = new FileOutputStream(f);
            sampleimage3.compress(Bitmap.CompressFormat.JPEG,100,fos);
            Image_Object image1 = new Image_Object();
            image1.setPathDevice(f.getPath());
            image1.setPhotoDate("Monday 24, July 2017");
            image1.setPhotoActivity("Sample Project");
            image1.setPhotoDescription("View Looking South");
            defaultactivityimages.add(image1);
            fos.close();
        }catch (Exception e) {
            Log.v("log_tag", e.toString());
        }
        samplereport.setSelectedPhotos(defaultactivityimages);
        return samplereport;
    }
    private Project_Object AddPhotos(Project_Object sampleproject) {
        Bitmap companylogo = BitmapFactory.decodeResource(getResources(),R.drawable.ggc);
        String StoredPathLogo = "logo.jpg";
        try {
            File f = new File(this.getApplicationContext().getFilesDir(),StoredPathLogo);
            FileOutputStream fos = new FileOutputStream(f);
            companylogo.compress(Bitmap.CompressFormat.JPEG,90,fos);
            fos.close();
        } catch (Exception e) {
            Log.v("log_tag", e.toString());
        }
        Bitmap signature = BitmapFactory.decodeResource(getResources(),R.drawable.ggs);
        String StoredPathLogo2 = "signature.jpg";
        try {
            File f = new File(this.getApplicationContext().getFilesDir(),StoredPathLogo2);
            FileOutputStream fos = new FileOutputStream(f);
            signature.compress(Bitmap.CompressFormat.JPEG,90,fos);
            fos.close();
        } catch (Exception e) {
            Log.v("log_tag", e.toString());
        }
        Bitmap projectlogo = BitmapFactory.decodeResource(getResources(),R.drawable.ggi);
        String StoredPathLogo3 = sampleproject.getProjectId()+".jpg";
        try {
            File f = new File(this.getApplicationContext().getFilesDir(),StoredPathLogo3);
            FileOutputStream fos = new FileOutputStream(f);
            projectlogo.compress(Bitmap.CompressFormat.JPEG,90,fos);
            fos.close();
        } catch (Exception e) {
            Log.v("log_tag", e.toString());
        }

        return sampleproject;
    }*/

    public void GeneratePDFReport(Project_Object currentproject, int reportposition){

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]
                    {android.Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }


        mcurrentproject = currentproject;
        mreportnumber = reportposition;
        mcurrentreport = mcurrentproject.getProjectReports().get(reportposition);


        //msharedpreferences = this.getPreferences(Context.MODE_PRIVATE);
        File pdfFolderFile;
        File pdfSecFolderFile;
        if(Build.VERSION.SDK_INT>=19){
            String MainFolder = "ConstructionReporter";
            pdfFolderFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),MainFolder);
            if(!pdfFolderFile.exists()){
                Boolean created = pdfFolderFile.mkdirs();
                Log.i("TAG","Main Folder Created"+pdfFolderFile.getAbsolutePath()+String.valueOf(created));
            }
            String SecondaryFolder = mcurrentproject.getProjectName();
            pdfSecFolderFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+File.separator+
                    MainFolder,SecondaryFolder);
            if(!pdfSecFolderFile.exists()){
                pdfSecFolderFile.mkdirs();
                Log.i("TAG","Secondary Folder Created"+pdfSecFolderFile.getAbsolutePath());
            }
        }else{
            pdfFolderFile = new File(Environment.getExternalStorageDirectory()+File.separator+"Documents");
            if(!pdfFolderFile.exists()){
                pdfFolderFile.mkdir();
            }
            pdfSecFolderFile = new File(pdfFolderFile.getAbsolutePath()+"/"+
            mcurrentproject.getProjectName());
            if(!pdfSecFolderFile.exists()){
                pdfSecFolderFile.mkdir();
            }
        }

        //String date2 = new SimpleDateFormat("yyyyMMdd_hhmmss", Locale.US).format(new Date());
        String date = mcurrentreport.getReportInfo().get(8);
        date = RearrangeDate(date);
        String reportfilename = pdfSecFolderFile+File.separator+date+"_IDR"+".pdf";
        File Report = new File(reportfilename);
        Integer noreport = 2;
        while(Report.exists()){
            reportfilename = pdfSecFolderFile+File.separator+date+"_IDR"+"_"+String.valueOf(noreport)+".pdf";
            Report = new File(reportfilename);
            noreport = noreport+1;
        }
        try {
            doc = new Document(PageSize.LETTER);
            OutputStream output = new FileOutputStream(Report);
            writer = PdfWriter.getInstance(doc, output);

            DefineFonts();
            table = footertable();
            FooterTable footer = new FooterTable(table);
            writer.setPageEvent(footer);

            doc.open();
            CreateFirstPage();
            if(msharedpreferences.getBoolean(IncludeManpower,true)&&
                    mcurrentreport.getSelectedWorkers().size()!=0){
                CreateManPowerPage();
            }
            if(msharedpreferences.getBoolean(IncludeEquipment,true)&&
                    mcurrentreport.getSelectedEquipment().size()!=0){
                CreateEquipmentPage();
            }
            if(msharedpreferences.getBoolean(IncludePhotos,true)&&
                    mcurrentreport.getSelectedPhotos().size()!=0){
                CreatePhotosPage();
            }
            doc.close();

            Intent ViewPDFIntent = new Intent(Intent.ACTION_VIEW);
            ViewPDFIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_ACTIVITY_NO_HISTORY);
            File f = new File(reportfilename);
            Uri uri = FileProvider.getUriForFile(this,
                    "com.ilaquidain.constructionreporter.provider",f);
            ViewPDFIntent.setDataAndType(uri,this.getContentResolver().getType(uri));
            startActivity(ViewPDFIntent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private class FooterTable extends PdfPageEventHelper {
        private final PdfPTable footer;
        private FooterTable(PdfPTable footer) {
            this.footer = footer;
        }
        public void onEndPage(PdfWriter writer, Document document) {
            footer.writeSelectedRows(0, -1, 36, 108, writer.getDirectContent());
        }
    }
    private void CreateFirstPage() {
        try {
            table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{8,2});
            par = new Paragraph(mcurrentproject.getProjectName(),Cal11BU);
            par.add(new Paragraph("\n\n"));
            par.add(new Paragraph(mcurrentproject.getProjectRefNo(),Cal9));
            par.add(new Paragraph("\n"));
            par.add(new Paragraph(mcurrentproject.getProjectAddress(),Cal9));
            per = new PdfPCell(par);
            per.setBackgroundColor(lightgray);
            per.setBorderWidthRight(0);
            per.setPadding(5);
            table.addCell(per);
            String StoredPath2 = mcurrentproject.getProjectId()+".jpg";
            Bitmap bitmaplogo = null;
            try{
                File f = new File(this.getApplicationContext().getFilesDir(),StoredPath2);
                bitmaplogo = BitmapFactory.decodeStream(new FileInputStream(f));
                ByteArrayOutputStream btout = new ByteArrayOutputStream();
                bitmaplogo.compress(Bitmap.CompressFormat.JPEG,100,btout);
                Image image = Image.getInstance(btout.toByteArray());
                per = new PdfPCell(image,true);
                per.setBorderWidthLeft(0);
                per.setBackgroundColor(lightgray);
                per.setPadding(5);
                table.addCell(per);
            }catch (Exception e){
                e.printStackTrace();
                if(bitmaplogo==null){
                    per = new PdfPCell(new Paragraph(""));
                    per.setBorderWidthLeft(0);
                    per.setBackgroundColor(lightgray);
                    per.setPadding(5);
                    table.addCell(per);
                    Log.e("Error","Project Image Does Not Exist");
            }}
            doc.add(table);
            //Row 2
            table=new PdfPTable(1);
            table.setWidthPercentage(100);
            String s1 = mcurrentreport.getReportInfo().get(6)+" Daily Report - "+mcurrentreport.getReportInfo().get(7);
            par = new Paragraph(s1,Cal9);
            per = new PdfPCell(par);
            per.setPaddingLeft(5);
            per.setPaddingTop(2);
            per.setPaddingBottom(2);
            per.setBackgroundColor(lightgray);
            table.addCell(per);
            doc.add(table);
            //Row 3
            table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{6,9,6,9});
            //Row 3 column 1
            tempstring = "Date";
            par = new Paragraph(tempstring,Cal9);
            per = new PdfPCell(par);
            per.setPaddingLeft(5);
            per.setPaddingTop(2);
            per.setPaddingBottom(2);
            per.setBackgroundColor(lightgray);
            table.addCell(per);
            //Row 3 column 2
            tempstring = mcurrentreport.getReportInfo().get(8);
            par = new Paragraph(tempstring,Cal9);
            per = new PdfPCell(par);
            per.setPaddingLeft(5);
            per.setPaddingTop(2);
            per.setPaddingBottom(2);
            table.addCell(per);
            //Row 3 column 3
            tempstring = "Time";
            par = new Paragraph(tempstring,Cal9);
            per = new PdfPCell(par);
            per.setPaddingLeft(5);
            per.setPaddingTop(2);
            per.setPaddingBottom(2);
            per.setBackgroundColor(lightgray);
            table.addCell(per);
            //Row 3 column 4
            tempstring = mcurrentreport.getReportInfo().get(10)+" to "+mcurrentreport.getReportInfo().get(11);
            par = new Paragraph(tempstring,Cal9);
            per = new PdfPCell(par);
            per.setPaddingLeft(5);
            per.setPaddingTop(2);
            per.setPaddingBottom(2);
            table.addCell(per);
            doc.add(table);
            //Row 4
            table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{6,9,6,9});
            //Row 4 column 1
            tempstring = "Weather";
            par = new Paragraph(tempstring,Cal9);
            per = new PdfPCell(par);
            per.setPaddingLeft(5);
            per.setPaddingTop(2);
            per.setPaddingBottom(2);
            per.setBackgroundColor(lightgray);
            table.addCell(per);
            //Row 4 column 2
            tempstring = mcurrentreport.getReportInfo().get(5);
            par = new Paragraph(tempstring,Cal9);
            per = new PdfPCell(par);
            per.setPaddingLeft(5);
            per.setPaddingTop(2);
            per.setPaddingBottom(2);
            table.addCell(per);
            //Row 4 column 3
            tempstring = "";
            par = new Paragraph(tempstring,Cal9);
            per = new PdfPCell(par);
            per.setPaddingLeft(5);
            per.setPaddingTop(2);
            per.setPaddingBottom(2);
            per.setBackgroundColor(lightgray);
            table.addCell(per);
            //Row 4 column 4
            tempstring = "";
            par = new Paragraph(tempstring,Cal9);
            per = new PdfPCell(par);
            per.setPaddingLeft(5);
            per.setPaddingTop(2);
            per.setPaddingBottom(2);
            table.addCell(per);

            doc.add(table);


            //Create activity table
            ArrayList<Task_Object> reporttasks = mcurrentreport.getSelectedTasks();
            for(int j=0;j<reporttasks.size();j++){
                doc.add(new Paragraph("\n"));
                table = new PdfPTable(15);
                table.setKeepTogether(true);
                table.setWidthPercentage(100);
                per = getCell_15(3,"Activity:");
                per.setBackgroundColor(lightgray);
                table.addCell(per);
                tempstring = mcurrentreport.getSelectedTasks().get(j).getTaskName();
                table.addCell(getCell_15(12,tempstring));
                tempstring = mcurrentreport.getSelectedTasks().get(j).getTaskDescription();
                table.addCell(getCell_15_2(15,tempstring));
                table.completeRow();
                doc.add(table);
            }

        }catch (DocumentException e){
            e.printStackTrace();
        }

    }
    private void CreateManPowerPage(){
        doc.newPage();
        try{
            table = new PdfPTable(14);
            table.setWidthPercentage(100);
            per = getCell_15(14,"Manpower");
            per.setBackgroundColor(lightgray);
            table.addCell(per);

            per = getCell_15(5,"Name");per.setBackgroundColor(lightgray);
            table.addCell(per);
            per = getCell_15(4,"Contractor");per.setBackgroundColor(lightgray);
            table.addCell(per);
            per = getCell_15(4,"Activity");per.setBackgroundColor(lightgray);
            table.addCell(per);
            per = getCell_15(1,"Hours");per.setBackgroundColor(lightgray);
            table.addCell(per);

            ArrayList<Worker_Object> workers = mcurrentreport.getSelectedWorkers();
            for(int i=0;i<workers.size();i++){
                per = getCell_15(5,workers.get(i).getName());
                table.addCell(per);
                per = getCell_15(4,workers.get(i).getCompany());
                table.addCell(per);
                per = getCell_15(4,workers.get(i).getActivity());
                table.addCell(per);
                per = getCell_15(1,workers.get(i).getHours());
                table.addCell(per);
            }
            doc.add(table);
        }catch (DocumentException e){
            e.printStackTrace();
        }

    }

    private void CreateEquipmentPage() {
        try{
            par = new Paragraph("\n");
            doc.add(par);

            table = new PdfPTable(14);
            table.setWidthPercentage(100);

            per = getCell_15(14,"Equipment");
            per.setBackgroundColor(lightgray);
            table.addCell(per);
            per = getCell_15(5,"Name");
            per.setBackgroundColor(lightgray);
            table.addCell(per);
            per = getCell_15(4,"Contractor");
            per.setBackgroundColor(lightgray);
            table.addCell(per);
            per = getCell_15(4,"Activity");
            per.setBackgroundColor(lightgray);
            table.addCell(per);
            per = getCell_15(1,"Hours");
            per.setBackgroundColor(lightgray);
            table.addCell(per);

            ArrayList<Worker_Object> equipment = mcurrentreport.getSelectedEquipment();

            for(int i=0;i<equipment.size();i++){
                per = getCell_15(5,equipment.get(i).getName());
                table.addCell(per);
                per = getCell_15(4,equipment.get(i).getCompany());
                table.addCell(per);
                per = getCell_15(4,equipment.get(i).getActivity());
                table.addCell(per);
                per = getCell_15(1,equipment.get(i).getHours());
                table.addCell(per);
            }
            table.completeRow();
            table.setKeepTogether(true);
            doc.add(table);

        }catch (DocumentException e){
            e.printStackTrace();
        }
    }
    private void CreatePhotosPage() {
        ConvertPhotosToImages();
        int numberofphotopages;
        try {
            if(imagenes.size()!=0){
                double tempdob = Math.ceil((double)imagenes.size()/4);
                numberofphotopages = (int) tempdob;
            }else {return;}

            int counter =0;
            for(int u=0;u<numberofphotopages;u++){
                doc.newPage();
                table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{1,1});
                addrowofphotos(counter);
                addrowofdates(counter);
                addrowofactivities(counter);
                addrowofdesciptions(counter);
                counter = counter+2;
                addrowofphotos(counter);
                addrowofdates(counter);
                addrowofactivities(counter);
                addrowofdesciptions(counter);
                counter = counter +2;
                doc.add(table);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private void addrowofdesciptions(int counter) {
        if(counter<imagenes.size()){
            tempstring = mcurrentreport.getSelectedPhotos().get(counter).getPhotoDescription();
            if(tempstring==null){tempstring=" ";}
        }else {
            tempstring=" ";
        }
        par = new Paragraph(tempstring,Cal9);
        per = new PdfPCell(par);
        per.setBackgroundColor(lightgray);
        per.setPaddingLeft(5);
        per.setPaddingTop(2);
        per.setPaddingBottom(2);
        per.setBorderWidthTop(0);
        table.addCell(per);

        if(counter+1<imagenes.size()){
            tempstring = mcurrentreport.getSelectedPhotos().get(counter+1).getPhotoDescription();
            if(tempstring==null){tempstring=" ";}
        }else {
            tempstring=" ";
        }
        par = new Paragraph(tempstring,Cal9);
        per = new PdfPCell(par);
        per.setBackgroundColor(lightgray);
        per.setPaddingLeft(5);
        per.setPaddingTop(2);
        per.setPaddingBottom(2);
        per.setBorderWidthTop(0);
        table.addCell(per);
    }
    private void addrowofactivities(int counter) {
        if(counter<imagenes.size()){
            tempstring = mcurrentreport.getSelectedPhotos().get(counter).getPhotoActivity();
            if(tempstring==null){tempstring=" ";}
        }else {
            tempstring=" ";
        }
        par = new Paragraph(tempstring,Cal9);
        per = new PdfPCell(par);
        per.setBackgroundColor(lightgray);
        per.setPaddingLeft(5);
        per.setPaddingTop(2);
        per.setPaddingBottom(2);
        per.setBorderWidthTop(0);
        per.setBorderWidthBottom(0);
        table.addCell(per);

        if(counter+1<imagenes.size()){
            tempstring = mcurrentreport.getSelectedPhotos().get(counter+1).getPhotoActivity();
            if(tempstring==null){tempstring=" ";}
        }else {
            tempstring=" ";
        }
        par = new Paragraph(tempstring,Cal9);
        per = new PdfPCell(par);
        per.setBackgroundColor(lightgray);
        per.setPaddingLeft(5);
        per.setPaddingTop(2);
        per.setPaddingBottom(2);
        per.setBorderWidthTop(0);
        per.setBorderWidthBottom(0);
        table.addCell(per);
    }
    private void addrowofdates(int counter) {
        if(counter<imagenes.size()){
            tempstring = mcurrentreport.getSelectedPhotos().get(counter).getPhotoDate();
            if(tempstring==null){tempstring=" ";}
        }else {
            tempstring=" ";
        }
        par = new Paragraph(tempstring,Cal9);
        per = new PdfPCell(par);
        per.setBackgroundColor(lightgray);
        per.setPaddingLeft(5);
        per.setPaddingTop(2);
        per.setPaddingBottom(2);
        per.setBorderWidthBottom(0);
        table.addCell(per);

        if(counter+1<imagenes.size()){
            tempstring = mcurrentreport.getSelectedPhotos().get(counter+1).getPhotoDate();
            if(tempstring==null){tempstring=" ";}
        }else {
            tempstring=" ";
        }
        par = new Paragraph(tempstring,Cal9);
        per = new PdfPCell(par);
        per.setBackgroundColor(lightgray);
        per.setPaddingLeft(5);
        per.setPaddingTop(2);
        per.setPaddingBottom(2);
        per.setBorderWidthBottom(0);
        table.addCell(per);
    }
    private void addrowofphotos(int counter) {
        if(counter<imagenes.size()){
            per = new PdfPCell(imagenes.get(counter),true);
            per.setFixedHeight(250);
            per.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            per.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            per.setPadding(5);
            table.addCell(per);
        }else {
            addwhitesquare();
        }
        if(counter+1<imagenes.size()){
            per = new PdfPCell(imagenes.get(counter+1),true);
            per.setFixedHeight(250);
            per.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
            per.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            per.setPadding(5);
            table.addCell(per);
        }else {
            addwhitesquare();
        }
    }
    private void addwhitesquare() {
        try{
            PdfTemplate template = writer.getDirectContent().createTemplate(10, 10);
            template.rectangle(0, 0, 10, 10);
            template.setColorFill(BaseColor.WHITE);
            template.fill();
            writer.releaseTemplate(template);
            Image image = Image.getInstance(template);
            per = new PdfPCell(image,true);
            per.setFixedHeight(200);
            per.setHorizontalAlignment(Element.ALIGN_CENTER);
            per.setVerticalAlignment(Element.ALIGN_CENTER);
            per.setPadding(5);
            table.addCell(per);
        }
        catch (IOException | DocumentException e){
            e.printStackTrace();}
    }
    private void ConvertPhotosToImages() {
        bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        String photosquality = msharedpreferences.getString(PhotosQuality,"Medium");
        int scaleFactor;
        switch (photosquality){
            case "High":scaleFactor =2;break;
            case "Medium":scaleFactor=4;break;
            case "Low":scaleFactor =8;break;
            default:scaleFactor=4;break;}
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        imagenes = new ArrayList<>();
        try{
            for(int i=0;i<mcurrentreport.getSelectedPhotos().size();i++){
                String photopath = mcurrentreport.getSelectedPhotos().get(i).getPathDevice();
                Bitmap bitmapphoto = BitmapFactory.decodeFile(photopath, bmOptions);
                ByteArrayOutputStream btout = new ByteArrayOutputStream();
                bitmapphoto.compress(Bitmap.CompressFormat.JPEG, 100, btout);
                Image image = Image.getInstance(btout.toByteArray());
                imagenes.add(image);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private PdfPCell getCell_15(int units, String par) {
        PdfPCell cell = new PdfPCell();
        cell.setColspan(units);
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        Paragraph p = new Paragraph(par,Cal9);
        cell.setPaddingLeft(5);
        cell.setPaddingTop(2);
        cell.setPaddingBottom(2);
        cell.addElement(p);
        return cell;
    }
    private PdfPCell getCell_15_2(int units, String par) {
        PdfPCell cell = new PdfPCell();
        cell.setColspan(units);
        cell.setUseAscender(true);
        cell.setUseDescender(true);
        Paragraph p = new Paragraph(12,par,Cal9);
        cell.setPaddingTop(8);
        cell.setPaddingBottom(8);
        cell.setPaddingLeft(5);
        cell.addElement(p);
        return cell;
    }
    private PdfPTable footertable(){
        table = new PdfPTable(8);
        table.setTotalWidth(540);

        try{
            String StoredPath = "signature.jpg";
            File f = new File(this.getApplicationContext().getFilesDir(),StoredPath);
            Bitmap signaturebitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            if(signaturebitmap!=null) {
                ByteArrayOutputStream btout = new ByteArrayOutputStream();
                signaturebitmap.compress(Bitmap.CompressFormat.JPEG, 100, btout);
                Image image = Image.getInstance(btout.toByteArray());
                per = new PdfPCell(image, true);
                per.setColspan(1);
                per.setUseAscender(true);
                per.setUseAscender(true);
                per.setBorderWidth(0);
                per.setHorizontalAlignment(Element.ALIGN_LEFT);
                per.setPadding(5);
                table.addCell(per);
            }else{
                per = new PdfPCell(new Paragraph(""));
                per.setColspan(1);
                per.setUseAscender(true);
                per.setUseAscender(true);
                per.setBorderWidth(0);
                table.addCell(per);
            }

            per = new PdfPCell(new Paragraph(""));
            per.setColspan(6);
            per.setUseAscender(true);
            per.setUseAscender(true);
            per.setBorderWidth(0);
            table.addCell(per);

            String StoredPath2 = "logo.jpg";
            File f2 = new File(this.getApplicationContext().getFilesDir(),StoredPath2);
            Bitmap companybitmap = BitmapFactory.decodeStream(new FileInputStream(f2));
            if(companybitmap!=null) {
                ByteArrayOutputStream btout2 = new ByteArrayOutputStream();
                companybitmap.compress(Bitmap.CompressFormat.JPEG, 100, btout2);
                Image image2 = Image.getInstance(btout2.toByteArray());
                per = new PdfPCell(image2, true);
                per.setColspan(1);
                per.setUseAscender(true);
                per.setUseAscender(true);
                per.setBorderWidth(0);
                per.setHorizontalAlignment(Element.ALIGN_RIGHT);
                per.setPadding(5);
                table.addCell(per);
            }else {
                per = new PdfPCell(new Paragraph(""));
                per.setColspan(1);
                per.setUseAscender(true);
                per.setUseAscender(true);
                per.setBorderWidth(0);
                table.addCell(per);
            }

            tempstring = msharedpreferences.getString(OriginatorName,"");
            par = new Paragraph(tempstring, Cal9);
            per = new PdfPCell(par);
            per.setBorderWidth(0);
            per.setColspan(4);
            per.setUseAscender(true);
            per.setUseAscender(true);
            per.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(per);

            tempstring = msharedpreferences.getString(OriginatorCompany,"");
            par = new Paragraph(tempstring, Cal9);
            per = new PdfPCell(par);
            per.setBorderWidth(0);
            per.setColspan(4);
            per.setUseAscender(true);
            per.setUseAscender(true);
            per.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            table.addCell(per);

            tempstring = msharedpreferences.getString(OriginatorPosition,"");
            par = new Paragraph(tempstring, Cal9);
            per = new PdfPCell(par);
            per.setBorderWidth(0);
            per.setColspan(8);
            per.setUseAscender(true);
            per.setUseAscender(true);
            table.addCell(per);

        }catch (DocumentException | IOException e){
            e.printStackTrace();
        }

        return table;
    }
    private String RearrangeDate(String date) {
        date = date.replace("/","");
        date = date.substring(6,8)+date.substring(0,4);
        //String timecreate = new SimpleDateFormat("hhmmss",Locale.US).format(new Date());
        //date = date + "_"+timecreate;
        return date;
    }
    private void DefineFonts() {
        Cal11BU = new Font(Font.FontFamily.TIMES_ROMAN,11,Font.BOLD|Font.UNDERLINE);
        Cal11B = new Font(Font.FontFamily.TIMES_ROMAN,11,Font.BOLD);
        Cal9 = new Font(Font.FontFamily.TIMES_ROMAN,10);
        Cal9B_White = new Font(Font.FontFamily.TIMES_ROMAN,10,Font.NORMAL, BaseColor.WHITE);
    }

}
