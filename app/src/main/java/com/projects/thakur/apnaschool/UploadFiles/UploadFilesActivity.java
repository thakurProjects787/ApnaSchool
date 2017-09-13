package com.projects.thakur.apnaschool.UploadFiles;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.UpdateInfo.ShowEachSchoolDetails;

import java.io.IOException;

public class UploadFilesActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_select_file_and_upload;
    private EditText edtxt_upload_file_name;

    //a constant to track the file chooser intent
    private static final int PICK_IMAGE_REQUEST = 234;

    //a Uri object to store file path
    private Uri filePath;
    private String cloud_base_path = "SchoolTrac/";
    private String database_path,upload_file_name;

    //Firebase database access
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private DatabaseReference mDatabase;

    private boolean uploadFileStatus = true;

    ImageView imgView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_files);

        Toolbar toolbar = (Toolbar) findViewById(R.id.upload_files_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Upload");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                //finish();
                // Direct jump to Main activity.
                Intent intent_gl = new Intent(UploadFilesActivity.this, ShowEachSchoolDetails.class);
                intent_gl.putExtra("EXTRA_SHOW_SCHOOL_SESSION_ID", "OWNER");
                startActivity(intent_gl);
            }
        });



        imgView = (ImageView) findViewById(R.id.show_upload_image);

        // configure buttons
        btn_select_file_and_upload = (Button) findViewById(R.id.btn_select_file_and_upload);

        // Click listeners
        btn_select_file_and_upload.setOnClickListener(this);

        //edit text
        edtxt_upload_file_name = (EditText) findViewById(R.id.edtxt_upload_file_name);


        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Set firbase path
        cloud_base_path = cloud_base_path + StartUpActivity.userDetails.getState() + "/School_Gallery/" +StartUpActivity.userDetails.getName() + "_" + mAuth.getCurrentUser().getUid() + "/Gallery/";
        database_path = "UserNode/" + mAuth.getCurrentUser().getUid() + "/Gallery/";

        mDatabase = FirebaseDatabase.getInstance().getReference(database_path);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.upload_gallery_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.upload_gallery_item) {

            // Check for Upload File Status

            if(uploadFileStatus) {

                upload_file_name = edtxt_upload_file_name.getText().toString();
                if (TextUtils.isEmpty(upload_file_name)) {
                    edtxt_upload_file_name.setError("Please enter valid description !!");
                } else {
                    edtxt_upload_file_name.setError(null);

                    uploadFile();
                }
            } else {

                Toast.makeText(getApplicationContext(), "Your Image already uploaded into Database.\n Please exit from here then upload new Image !! ", Toast.LENGTH_LONG).show();

            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_select_file_and_upload:
                showFileChooser();
                break;
        }
    }

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
            StorageReference riversRef = storageReference.child(cloud_base_path + upload_file_name.replace(" ","_")+".jpg");
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
                            Upload upload = new Upload(upload_file_name.trim(), taskSnapshot.getDownloadUrl().toString());

                            //adding an upload to firebase database
                            String uploadId = mDatabase.push().getKey();
                            mDatabase.child(uploadId).setValue(upload);

                            uploadFileStatus = false;

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
