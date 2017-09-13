package com.projects.thakur.apnaschool.NoticeBoard;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.Model.AchivmentsDetails;
import com.projects.thakur.apnaschool.Model.NoticeDetails;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.UploadFiles.Upload;

import java.util.Calendar;

public class AddNewNoticeActivity extends AppCompatActivity implements View.OnClickListener {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //the firebase objects for storage and database
    private StorageReference mStorageReference;
    private  ProgressDialog pd;

    private String cloud_base_path = "SchoolTrac/";
    private String attchmentFIleName = "NAN";

    //this is the pic pdf code used in file chooser
    final static int PICK_PDF_CODE = 2342;


    private ProgressDialog mProgressDialog;

    private Button btn_add_new_notice,btn_delete_current_notice,btn_new_notice_set_date;

    private EditText edtxt_new_notice_id,edtxt_new_notice_title,edtxt_new_notice_date,edtxt_new_notice_details,edtxt_new_notice_department,edtxt_new_notice_approved_by,edtxt_new_notice_announced_by;

    private TextView textView_new_notice_vlu;

    private String new_notice_id,new_notice_title,new_notice_date,new_notice_details,new_notice_key,department,approved_by,announced_by;

    private NoticeDetails newNoticeDetails;

    private boolean new_notice_created = false;

    // ===== Date Section ============
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    //Get value from parent activity
    private String operationStatus;
    private boolean deletionProcess = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_notice);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_new_notice_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add Notice");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        operationStatus = getIntent().getStringExtra("EXTRA_NEW_NOTICE_INFO_SESSION_ID");

        // Firbase database access
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //getting firebase objects
        mStorageReference = FirebaseStorage.getInstance().getReference();

        cloud_base_path = cloud_base_path + StartUpActivity.userDetails.getState() + "/Notice_Board/" + StartUpActivity.userDetails.getName() + "_" + mAuth.getCurrentUser().getUid() + "/";

        //edit text
        edtxt_new_notice_id  = (EditText) findViewById(R.id.edtxt_new_notice_id);
        edtxt_new_notice_title = (EditText) findViewById(R.id.edtxt_new_notice_title);
        edtxt_new_notice_date = (EditText) findViewById(R.id.edtxt_new_notice_date);
        edtxt_new_notice_details = (EditText) findViewById(R.id.edtxt_new_notice_details);

        edtxt_new_notice_department = (EditText) findViewById(R.id.edtxt_new_notice_department);
        edtxt_new_notice_approved_by = (EditText) findViewById(R.id.edtxt_new_notice_approved_by);
        edtxt_new_notice_announced_by = (EditText) findViewById(R.id.edtxt_new_notice_announced_by);

        edtxt_new_notice_announced_by.setText(StartUpActivity.userDetails.getName());

        textView_new_notice_vlu = (TextView) findViewById(R.id.textView_new_notice_vlu);


        btn_add_new_notice = (Button) findViewById(R.id.btn_add_new_notice);
        btn_add_new_notice.setOnClickListener(this);

        btn_delete_current_notice = (Button) findViewById(R.id.btn_delete_current_notice);
        btn_delete_current_notice.setOnClickListener(this);

        btn_new_notice_set_date = (Button) findViewById(R.id.btn_new_notice_set_date);
        btn_new_notice_set_date.setOnClickListener(this);


        // Get Datepicker objects
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // change view according to operationStatus
        if(operationStatus.equals("ADD_NEW")){
            btn_delete_current_notice.setVisibility(View.GONE);

        } else{
            btn_add_new_notice.setText("UPDATE");
            //readCurrentData();
        }



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_new_notice:

                if(!new_notice_created) {
                    readAndSAVE("CREATE_NEW");
                } else {
                    finish();
                }
                break;

            case R.id.btn_delete_current_notice:

                //====================== ASK DIALOG BOX ========================================
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
                alertDialogBuilder.setTitle("SUBMIT");
                alertDialogBuilder.setMessage("Are you sure,You want to Delete Notice ?");

                alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //deleteCurrentData();
                        finish();

                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Cancel !!", Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                //======================================================================================

                break;

            case R.id.btn_new_notice_set_date:
                setDate();
                break;

        }
    }

    // =================================================
    // ---- Date Section -----
    @SuppressWarnings("deprecation")
    public void setDate() {
        showDialog(999);
        //Toast.makeText(getApplicationContext(), "ca",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        edtxt_new_notice_date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    // ==================================================


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_new_notice_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_new_notice_attach_files) {

            // check for is new notice created  or not
            if(new_notice_created){

                // Upload new file as a attachment.
                askForFileName();

            } else {
                Toast.makeText(AddNewNoticeActivity.this, "Please first create new notice, After that only you can upload files !!",
                        Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        if (id == R.id.add_new_notice_delete_files) {

            if(new_notice_created) {
                deleteAttachments(new_notice_key);
            }

            return true;
        }


        return super.onOptionsItemSelected(item);
    }

     /*
       Read Inputs and then validate then save into firbase database.
     */

    private void readAndSAVE(String operation){
        boolean vaidation_status = true;

        new_notice_id = edtxt_new_notice_id.getText().toString();
        if (TextUtils.isEmpty(new_notice_id)){
            edtxt_new_notice_id.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_new_notice_id.setError(null);
        }

        new_notice_title = edtxt_new_notice_title.getText().toString();
        if (TextUtils.isEmpty(new_notice_title)){
            edtxt_new_notice_title.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_new_notice_title.setError(null);
        }

        new_notice_date = edtxt_new_notice_date.getText().toString();
        if (TextUtils.isEmpty(new_notice_date)){
            edtxt_new_notice_date.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_new_notice_date.setError(null);
        }

        //---department,approved_by,announced_by
        department = edtxt_new_notice_department.getText().toString();
        if (TextUtils.isEmpty(department)){
            edtxt_new_notice_department.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_new_notice_department.setError(null);
        }

        approved_by = edtxt_new_notice_approved_by.getText().toString();
        if (TextUtils.isEmpty(approved_by)){
            edtxt_new_notice_approved_by.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_new_notice_approved_by.setError(null);
        }

        announced_by = edtxt_new_notice_announced_by.getText().toString();
        if (TextUtils.isEmpty(announced_by)){
            edtxt_new_notice_announced_by.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_new_notice_announced_by.setError(null);
        }

        //--



        new_notice_details = edtxt_new_notice_details.getText().toString();
        if(new_notice_details.isEmpty()){
            vaidation_status = false;
            edtxt_new_notice_details.setText("Please write Details!!");
        }

        // if all vaidation is true then add all details into firbase database
        if(vaidation_status){

            //create  Details model
            NoticeDetails addNewNotice = new NoticeDetails();

            addNewNotice.setNotice_ID(new_notice_id);
            addNewNotice.setNotice_titles(new_notice_title);
            addNewNotice.setNotice_date(new_notice_date);
            addNewNotice.setNotice_details(new_notice_details);
            addNewNotice.setNotice_department(department);
            addNewNotice.setNotice_approved_by(approved_by);
            addNewNotice.setNotice_announced_by(announced_by);
            addNewNotice.setAttachmentsFilesName(textView_new_notice_vlu.getText().toString());

            addNewNotice.setNotice_user_type(StartUpActivity.userDetails.getType());
            addNewNotice.setNotice_user_state(StartUpActivity.userDetails.getState());
            addNewNotice.setNotice_user_district(StartUpActivity.userDetails.getDistt());

            addNewNotice.setUser_firebase_ID(StartUpActivity.userDetails.getSchool_firbaseDataID());

            showProgressDialog();

            if(mAuth.getCurrentUser()!=null)
            {
                //CHECK IF new achivments has been added or update process.
                if(operation.equals("CREATE_NEW")) {

                    // Get new push key
                    new_notice_key = mDatabase.child("Global").child(StartUpActivity.userDetails.getState()).child("Notice_Board").push().getKey();
                    addNewNotice.setNotice_firbase_ID(new_notice_key);

                } else {
                    addNewNotice.setNotice_firbase_ID(new_notice_key);
                }

                // save the user at Global under user UID
                mDatabase.child("Global").child(StartUpActivity.userDetails.getState()).child("Notice_Board").child(new_notice_key).setValue(addNewNotice, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        hideProgressDialog();

                        new_notice_created = true;

                        if(databaseError==null)
                        {
                            Toast.makeText(AddNewNoticeActivity.this, "Your Details has been saved !!",
                                    Toast.LENGTH_SHORT).show();

                            btn_add_new_notice.setText("EXIT");


                        }
                    }
                });
            }



        }
    }



    // Update Attachments files
    private void updateAttachmentsNames(){

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Global").child(StartUpActivity.userDetails.getState()).child("Notice_Board").child(new_notice_key).child("attachmentsFilesName");
        mDatabase.setValue(textView_new_notice_vlu.getText().toString());
    }


    /* ----------------------------------------------------
       Uploading PDF file inot Firbase Storage
    ---------------------------------------------------------- */

    //this function will get the pdf from the storage
    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"), PICK_PDF_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                uploadFile(data.getData());
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //this method is uploading the file
    //the code is same as the previous tutorial
    //so we are not explaining it
    private void uploadFile(Uri data) {

        //displaying a progress dialog while upload is going on
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference sRef = mStorageReference.child(cloud_base_path + new_notice_key + "/" + attchmentFIleName.replace(" ","_") + ".pdf");
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        progressDialog.dismiss();

                        //and displaying a success toast
                        Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                        Upload upload = new Upload(attchmentFIleName, taskSnapshot.getDownloadUrl().toString());

                        String newKey = mDatabase.child("Global").child(StartUpActivity.userDetails.getState()).child("Notice_Board").child(new_notice_key).child("Attachments").push().getKey();
                        mDatabase.child("Global").child(StartUpActivity.userDetails.getState()).child("Notice_Board").child(new_notice_key).child("Attachments").child(newKey).setValue(upload);

                        // Update Attachment field
                        String atthline = textView_new_notice_vlu.getText().toString() + "\n" + attchmentFIleName +".pdf";
                        textView_new_notice_vlu.setText(atthline);

                        // Update Attachments Name
                        updateAttachmentsNames();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        //hiding the progress dialog
                        progressDialog.dismiss();

                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                        //displaying percentage in progress dialog
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                    }
                });

    }

    // Ask for file name
    private void askForFileName(){

         /* Alert Dialog Code Start*/
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
        alert.setTitle("File Name"); //Set Alert dialog title here
        alert.setMessage("Please Enter File Name."); //Message here


        // Set an EditText view to get user input
        final EditText input = new EditText(getApplicationContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT );
        input.setTextColor(Color.BLACK);
        alert.setView(input);

        alert.setPositiveButton("Upload File", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //You will get as string input data in this variable.
                // here we convert the input to a string and show in a toast.
                String keyword = input.getEditableText().toString();
                //Toast.makeText(getApplicationContext(),srt,Toast.LENGTH_LONG).show();
                if (!keyword.isEmpty()) {

                    attchmentFIleName = keyword;

                    // Select file
                    getPDF();


                } else {
                    //Toast.makeText(getApplicationContext(), "Wrong Password !!", Toast.LENGTH_LONG).show();

                    dialog.cancel();
                }


            } // End of onClick(DialogInterface dialog, int whichButton)
        }); //End of alert.setPositiveButton
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                //Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();
                attchmentFIleName = "NAN";


                dialog.cancel();
            }
        }); //End of alert.setNegativeButton
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
                /* Alert Dialog Code End*/

    }


    // *************** End **********************************

    /*
       DELETE ALL ATTACHMENTS
     */

    // Delete complet Notice details

    public void deleteAttachments(String notice_firbaseID){

        pd=new ProgressDialog(AddNewNoticeActivity.this);
        pd.setProgress(100);;
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Deleting");
        pd.setMessage("Please Wait ...");
        pd.setCancelable(false);

        String attchFilesPath = "Global/" + StartUpActivity.userDetails.getState() + "/Notice_Board/" + notice_firbaseID + "/Attachments/";

        mDatabase = FirebaseDatabase.getInstance().getReference(attchFilesPath);

        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);

                    pd.show();

                    // Delete One by one all files
                    deleteFileFromStorage(upload);

                }

                mDatabase.removeEventListener(this);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pd.dismiss();
            }
        });

    }

    /*
        Delete Image from cloud storage
     */
    public void deleteFileFromStorage(Upload details){


        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(details.getUrl());

        // Delete the file
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // Delete content from database
                pd.setMessage("File Deleted ... ");
                Toast.makeText(AddNewNoticeActivity.this, "Deleted File !!", Toast.LENGTH_LONG).show();

                pd.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                Toast.makeText(AddNewNoticeActivity.this, "Failed to Delete !!" + exception, Toast.LENGTH_LONG).show();
            }
        });



    }

}
