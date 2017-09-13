package com.projects.thakur.apnaschool.NormalUser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.Task.QuestionTask.NewQuestionTaskModel;
import com.projects.thakur.apnaschool.Task.VirtualTask.NewVirtualTaskModel;
import com.projects.thakur.apnaschool.Task.VirtualTask.VirtualResultTaskModel;

import java.io.IOException;

public class SubmitVirtualTaskActivity extends AppCompatActivity implements View.OnClickListener {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase,imgDatabase;

    private String virtualTaskID;

    ProgressDialog mProgressDialog;

    private TextView txtv_virtual_task_heading_value,txtv_virtual_task_lastDate_value,txtv_virtual_task_details_value,txtv_virtual_task_school_details_value;

    private Button btn_virtual_task_answer_submit;

    private Context context;

    // ---- Upload Image
    private Button btn_select_virtual_file_and_upload;
    private EditText edtxt_upload_virtual_file_name;

    //a constant to track the file chooser intent
    private static final int PICK_IMAGE_REQUEST = 236;

    //a Uri object to store file path
    private Uri filePath;
    private String cloud_base_path = "SchoolTrac/";
    private String database_path,upload_file_name;

    //Firebase database access
    private StorageReference storageReference;

    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_virtual_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.submit_virtual_task_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Task");

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

        virtualTaskID = getIntent().getStringExtra("EXTRA_EACH_VIRTUAL_TASK_ANS_SESSION_ID");

        txtv_virtual_task_heading_value = (TextView) findViewById(R.id.txtv_virtual_task_heading_value);
        txtv_virtual_task_lastDate_value = (TextView) findViewById(R.id.txtv_virtual_task_lastDate_value);
        txtv_virtual_task_details_value = (TextView) findViewById(R.id.txtv_virtual_task_details_value);

        btn_virtual_task_answer_submit = (Button) findViewById(R.id.btn_virtual_task_answer_submit);
        btn_virtual_task_answer_submit.setOnClickListener(this);

        // ------- Upload Image -------------

        imgView = (ImageView) findViewById(R.id.show_upload_virtual_image);

        // configure buttons
        btn_select_virtual_file_and_upload = (Button) findViewById(R.id.btn_select_virtual_file_and_upload);

        // Click listeners
        btn_select_virtual_file_and_upload.setOnClickListener(this);

        //edit text
        edtxt_upload_virtual_file_name = (EditText) findViewById(R.id.edtxt_upload_virtual_file_name);


        storageReference = FirebaseStorage.getInstance().getReference();


        // -- Read curent task details
        getTaskDetails();

    }

    /*
      Get Task details
    */
    public void getTaskDetails() {

        showProgressDialog();

        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("adminUserID").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String admin_firebase_ID = dataSnapshot.getValue().toString();

                database_path = "UserNode/" + admin_firebase_ID + "/TASK/OPEN/VIRTUAL/" + virtualTaskID + "/ANSWER/" + mAuth.getCurrentUser().getUid();
                // Set firbase storage path
                cloud_base_path = cloud_base_path + StartUpActivity.userDetails.getState() + "/School_Virtual_Task/" + admin_firebase_ID + "/" + virtualTaskID + "/" + mAuth.getCurrentUser().getUid();


                imgDatabase = FirebaseDatabase.getInstance().getReference(database_path);

                mDatabase.child("UserNode").child(admin_firebase_ID).child("TASK").child("OPEN").child("VIRTUAL").child(virtualTaskID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            NewVirtualTaskModel taskDetails = dataSnapshot.getValue(NewVirtualTaskModel.class);

                            txtv_virtual_task_heading_value.setText(taskDetails.getTask_heading());
                            txtv_virtual_task_lastDate_value.setText(taskDetails.getTask_last_date());
                            txtv_virtual_task_details_value.setText(taskDetails.getTask_details());
                        }

                        //mDatabase.child("UserNode").child(dataSnapshot.getValue().toString()).child("TASK").child("OPEN").child("QUESTION").child(questionTaskID).removeEventListener(this);

                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        hideProgressDialog();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }

        });

    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(SubmitVirtualTaskActivity.this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_virtual_task_answer_submit:

                //====================== ASK DIALOG BOX ========================================
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
                alertDialogBuilder.setTitle("SUBMIT");
                alertDialogBuilder.setMessage("Are you sure,You want to Upload Image ?");

                alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        upload_file_name = edtxt_upload_virtual_file_name.getText().toString();
                        if (TextUtils.isEmpty(upload_file_name)){
                            edtxt_upload_virtual_file_name.setError("Please enter valid description !!");
                        } else {
                            edtxt_upload_virtual_file_name.setError(null);

                            uploadFile();

                            btn_virtual_task_answer_submit.setText("EXIT");
                            btn_virtual_task_answer_submit.setEnabled(false);


                            txtv_virtual_task_details_value.setText("Your Image has been Uploaded. \n\n Please exit from here !!");

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

                break;

            case R.id.btn_select_virtual_file_and_upload:
                showFileChooser();
                break;

        }
    }


    // ---- Upload Image ----
    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /*
       Upload File into firbase cloud
     */
    //this method will upload the file
    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // .child(fileUri.getLastPathSegment()); - get last file name.
            StorageReference riversRef = storageReference.child(cloud_base_path  + ".jpg");
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            //creating the upload object to store uploaded image details
                            String details = StartUpActivity.userDetails.getName() + "," + StartUpActivity.userDetails.getPlace_name() + "\n" + upload_file_name.trim();
                            VirtualResultTaskModel uploadResult = new VirtualResultTaskModel(details, taskSnapshot.getDownloadUrl().toString());

                            //adding an upload to firebase database
                            imgDatabase.setValue(uploadResult);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }


}
