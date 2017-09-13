package com.projects.thakur.apnaschool.NoticeBoard;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.projects.thakur.apnaschool.AdminUser.AdminHome;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.Common.Logger;
import com.projects.thakur.apnaschool.Model.NoticeDetails;
import com.projects.thakur.apnaschool.NormalUser.NormalUserActivity;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.StateUsers.StateHome;
import com.projects.thakur.apnaschool.UploadFiles.Upload;

import java.io.File;
import java.util.List;

public class ShowNoticeInDetailsActivity extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //the firebase objects for storage and database
    private StorageReference mStorageReference;

    // TextView objects
    private TextView txtv_notice_details_id_value,txtv_notice_details_title_value,txtv_notice_details_date_value,txtv_notice_details_department_value,txtv_notice_details_approved_by_value,txtv_notice_details_announced_by_value,txtv_notice_details_in_details_value,txtv_notice_details_attch_value;

    private String parentAtivityMsg;
    private ProgressDialog mProgressDialog;

    // get current notice details
    NoticeDetails currentNoticeDetails;

    private String noticeAttachmentPath = "NAN";
    private boolean noticeDeleted = false;

    private  ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notice_in_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_notice_in_details_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Details");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        // Firbase database access
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // TextView
        txtv_notice_details_id_value = (TextView) findViewById(R.id.txtv_notice_details_id_value);
        txtv_notice_details_title_value= (TextView) findViewById(R.id.txtv_notice_details_title_value);
        txtv_notice_details_date_value= (TextView) findViewById(R.id.txtv_notice_details_date_value);
        txtv_notice_details_department_value= (TextView) findViewById(R.id.txtv_notice_details_department_value);
        txtv_notice_details_approved_by_value= (TextView) findViewById(R.id.txtv_notice_details_approved_by_value);
        txtv_notice_details_announced_by_value= (TextView) findViewById(R.id.txtv_notice_details_announced_by_value);
        txtv_notice_details_in_details_value= (TextView) findViewById(R.id.txtv_notice_details_in_details_value);
        txtv_notice_details_attch_value= (TextView) findViewById(R.id.txtv_notice_details_attch_value);

        // get parent activity status
        parentAtivityMsg = getIntent().getStringExtra("EXTRA_ALL_NOTICE_IN_DETAILS_SESSION_ID");


        // Read all details
        readCurrentNoticeData();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_notice_in_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.notice_in_details_download_attachments) {

            getNoticeAllAttachments(currentNoticeDetails.getNotice_firbase_ID());

            return true;
        }

        if (id == R.id.notice_in_details_delete_notice) {

            // Check User check
            if(StartUpActivity.userDetails.getSchool_firbaseDataID().equals(currentNoticeDetails.getUser_firebase_ID())) {

                //====================== ASK DIALOG BOX ========================================
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShowNoticeInDetailsActivity.this, R.style.AppTheme_Dark_Dialog);
                alertDialogBuilder.setTitle("DELETE NOTICE");
                alertDialogBuilder.setMessage("Are you sure,You want to Delete Notice ?");

                alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        deleteComplete(currentNoticeDetails.getNotice_firbase_ID());

                        // Switch to another activity only after complete Deletion
                        if (StartUpActivity.userDetails.getType().equals("State")) {
                            Intent intent = new Intent(ShowNoticeInDetailsActivity.this, StateHome.class);
                            startActivity(intent);
                        } else if (StartUpActivity.userDetails.getType().equals("Admin")) {
                            Intent intent = new Intent(ShowNoticeInDetailsActivity.this, AdminHome.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(ShowNoticeInDetailsActivity.this, NormalUserActivity.class);
                            startActivity(intent);
                        }


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

            }
            return true;
        }



        return super.onOptionsItemSelected(item);
    }


    /*
       Read Current Notice Data
     */
    private void readCurrentNoticeData(){

            showProgressDialog();

            // app_title change listener
            mDatabase.child("Global").child(StartUpActivity.userDetails.getState()).child("Notice_Board").child(parentAtivityMsg).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    currentNoticeDetails = dataSnapshot.getValue(NoticeDetails.class);

                    // display notice details
                    txtv_notice_details_id_value.setText(currentNoticeDetails.getNotice_ID());
                    txtv_notice_details_title_value.setText(currentNoticeDetails.getNotice_titles());
                    txtv_notice_details_date_value.setText(currentNoticeDetails.getNotice_date());
                    txtv_notice_details_department_value.setText(currentNoticeDetails.getNotice_department());
                    txtv_notice_details_approved_by_value.setText(currentNoticeDetails.getNotice_approved_by());
                    txtv_notice_details_announced_by_value.setText(currentNoticeDetails.getNotice_announced_by());
                    txtv_notice_details_in_details_value.setText(currentNoticeDetails.getNotice_details());

                    String attchmentFilesName = currentNoticeDetails.getAttachmentsFilesName();

                    if(attchmentFilesName.equals("")) {
                        txtv_notice_details_attch_value.setText("No Attachments");
                    } else {
                        txtv_notice_details_attch_value.setText(attchmentFilesName);
                    }

                    hideProgressDialog();

                    mDatabase.child("Global").child(StartUpActivity.userDetails.getState()).child("Notice_Board").child(parentAtivityMsg).removeEventListener(this);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e(">> ", "Failed to read notice details.", error.toException());
                    hideProgressDialog();
                }
            });

    }


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


    /* ----------------------------------------------
               DOWNLOAD ALL ATTACHMENTS
     -------------------------------------------------*/

    /*
       Get notice all Attachments Details
     */
    private void getNoticeAllAttachments(String notice_firbaseID){

        pd=new ProgressDialog(ShowNoticeInDetailsActivity.this);
        pd.setProgress(100);;
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Downloading");
        pd.setMessage("Please Wait ...");
        pd.setCancelable(false);

        noticeAttachmentPath = "Global/" + StartUpActivity.userDetails.getState() + "/Notice_Board/" + notice_firbaseID + "/Attachments/";


        mDatabase = FirebaseDatabase.getInstance().getReference(noticeAttachmentPath);

        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);

                    pd.show();

                    // Download attachments
                    downloadFile(upload);
                    //uploads.add(upload);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pd.dismiss();
            }
        });



    }


    /*
       Download pdf fie
     */
    private void downloadFile(Upload eachDetails) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(eachDetails.getUrl());


        File rootPath = new File(Environment.getExternalStorageDirectory(), "SchoolTrac/Notice");
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        final File localFile = new File(rootPath,eachDetails.getName().replace(" ","_")+".pdf");

        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(ShowNoticeInDetailsActivity.this, "Attachment Downloaded : " + localFile, Toast.LENGTH_SHORT).show();

                Log.e("firebase ",";local tem file created  created " +localFile.toString());
                //  updateDb(timestamp,localFile.toString(),position);

                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ",";local tem file not created  created " +exception.toString());

                Toast.makeText(ShowNoticeInDetailsActivity.this, "Attachment Downloaded - FAILED !!", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });
    }

    // -------------------------------------------------

    /*
       DELETE Firebase Content and Files from Storage
     */

    // Delete complet Notice details

    public void deleteComplete(String notice_firbaseID){

        pd=new ProgressDialog(ShowNoticeInDetailsActivity.this);
        pd.setProgress(100);;
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Deleting");
        pd.setMessage("Please Wait ...");
        pd.setCancelable(false);

        String attchFilesPath = "Global/" + StartUpActivity.userDetails.getState() + "/Notice_Board/" + notice_firbaseID + "/Attachments/";

        noticeAttachmentPath = "Global/" + StartUpActivity.userDetails.getState() + "/Notice_Board/" + notice_firbaseID;

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


        // Delete complete content
        deleteDatabaseContent(noticeAttachmentPath);

        noticeDeleted = true;

    }

    /*
       Delete Content from database
     */
    public void deleteDatabaseContent(String noticeDtabsePath){

        mDatabase = FirebaseDatabase.getInstance().getReference(noticeDtabsePath);

        // app_title change listener
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(null);
                //Toast.makeText(context, " Deleted!!", Toast.LENGTH_LONG).show();
                Toast.makeText(ShowNoticeInDetailsActivity.this, "Deleted Notice Conetnt !!", Toast.LENGTH_LONG).show();

                mDatabase.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to delete Values.", error.toException());
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
                Toast.makeText(ShowNoticeInDetailsActivity.this, "Deleted File !!", Toast.LENGTH_LONG).show();
                pd.dismiss();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                Toast.makeText(ShowNoticeInDetailsActivity.this, "Failed to Delete!!" + exception, Toast.LENGTH_LONG).show();
            }
        });



    }



}
