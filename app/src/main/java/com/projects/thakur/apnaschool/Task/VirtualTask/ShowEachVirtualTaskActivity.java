package com.projects.thakur.apnaschool.Task.VirtualTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.storage.StorageReference;
import com.projects.thakur.apnaschool.AdminUser.AdminHome;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.NormalUser.NormalUserActivity;
import com.projects.thakur.apnaschool.NoticeBoard.ShowNoticeInDetailsActivity;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.StateUsers.StateHome;
import com.projects.thakur.apnaschool.UploadFiles.GalleryAdapter;
import com.projects.thakur.apnaschool.UploadFiles.Upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowEachVirtualTaskActivity extends AppCompatActivity {

    //recyclerview object
    private RecyclerView recyclerView;

    //adapter object
    private RecyclerView.Adapter adapter;

    //database reference
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //progress dialog
    private ProgressDialog progressDialog;

    //list to hold all the uploaded images
    private List<VirtualResultTaskModel> uploads;
    private VirtualResultTaskModel details;
    private ProgressDialog pd;

    public static String database_path,database_content_path;
    private Context context;
    private String databasePath,databaseKey;

    // Read parent activity message and set school firbase ID
    public static String virtualTaskID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_each_virtual_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_each_virtual_task_toolbar);
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

        mAuth = FirebaseAuth.getInstance();


        recyclerView = (RecyclerView) findViewById(R.id.show_each_virtual_photo_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        progressDialog = new ProgressDialog(this);

        uploads = new ArrayList<>();

        // get parent activity status
        virtualTaskID = getIntent().getStringExtra("EXTRA_EACH_VIRTUAL_TASK_SESSION_ID");

        database_path = "UserNode/" + mAuth.getCurrentUser().getUid() + "/TASK/OPEN/VIRTUAL/" + virtualTaskID + "/ANSWER";

        database_content_path = "UserNode/" + mAuth.getCurrentUser().getUid() + "/TASK/OPEN/VIRTUAL/" + virtualTaskID;

        showAllPhotos();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.admin_task_edit_option) {

            Toast.makeText(ShowEachVirtualTaskActivity.this, "Not Applicable !!", Toast.LENGTH_SHORT).show();

            return true;
        }

        if (id == R.id.admin_task_resolve_option) {

            //====================== ASK DIALOG BOX ========================================
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
            alertDialogBuilder.setTitle("Resolve");
            alertDialogBuilder.setMessage("Are you sure,You want to Resolve Current Task ?");

            alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                    deleteComplete();

                    if (StartUpActivity.userDetails.getType().equals("State")) {
                        Intent intent = new Intent(ShowEachVirtualTaskActivity.this, StateHome.class);
                        startActivity(intent);
                    } else if (StartUpActivity.userDetails.getType().equals("Admin")) {
                        Intent intent = new Intent(ShowEachVirtualTaskActivity.this, AdminHome.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ShowEachVirtualTaskActivity.this, NormalUserActivity.class);
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

            return true;
        }

        if (id == R.id.admin_task_send_report_option) {

            Toast.makeText(ShowEachVirtualTaskActivity.this, "Not Applicable !!", Toast.LENGTH_SHORT).show();

            return true;
        }


        return super.onOptionsItemSelected(item);

    }

    /*
       Fetch all photos and display
     */
    private void showAllPhotos(){

        //displaying progress dialog while fetching images
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);

        mDatabase = FirebaseDatabase.getInstance().getReference(database_path);

        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog
                progressDialog.dismiss();

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    VirtualResultTaskModel upload = postSnapshot.getValue(VirtualResultTaskModel.class);
                    uploads.add(upload);
                }
                //creating adapter
                adapter = new ImageAdapter(getApplicationContext(), uploads);

                //adding adapter to recyclerview
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });



    }

    /*
      Download files
     */
    public void downloadImage(VirtualResultTaskModel eachDetails, Context context_m) {

        context = context_m;
        details = eachDetails;

        pd=new ProgressDialog(context);
        pd.setProgress(100);;
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Downloading");
        pd.setMessage("Please Wait ...");
        pd.setCancelable(false);
        pd.show();


        FirebaseStorage storage=FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl(details.getUrl());

        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Use the bytes to display the image
                File rootPath = new File(Environment.getExternalStorageDirectory(), "SchoolTrac/Gallery");
                if(!rootPath.exists()) {
                    rootPath.mkdirs();
                }

                String path= rootPath+"/"+details.getName().replace(" ","_")+".jpg";

                try {
                    FileOutputStream fos=new FileOutputStream(path);
                    fos.write(bytes);
                    fos.close();
                    Toast.makeText(context, "Success!!!", Toast.LENGTH_SHORT).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                pd.dismiss();
                Toast.makeText(context, exception.toString()+"!!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*
    ----------  DELETE TASK -------------------
     */

    /*
    Find Image key from database
 */
    public void deleteComplete(){

        pd=new ProgressDialog(ShowEachVirtualTaskActivity.this);
        pd.setProgress(100);;
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Deleting");
        pd.setMessage("Please Wait ...");
        pd.setCancelable(false);
        pd.show();

        mDatabase = FirebaseDatabase.getInstance().getReference(database_path);

        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    VirtualResultTaskModel imageDetails = postSnapshot.getValue(VirtualResultTaskModel.class);

                    deleteImagesFromStorage(imageDetails);
                }

                deleteContent();

                mDatabase.removeEventListener(this);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pd.dismiss();
            }
        });

    }

    /*
       Delete Content from database
     */
    public void deleteContent(){

        mDatabase = FirebaseDatabase.getInstance().getReference(database_content_path);

        // app_title change listener
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(null);
                //Toast.makeText(context, " Deleted!!", Toast.LENGTH_LONG).show();
                pd.setMessage("Delete Image Content ... ");

                mDatabase.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                pd.dismiss();
                // Failed to read value
                Log.e(">> ", "Failed to delete Values.", error.toException());
            }
        });

    }

    /*
        Delete Image from cloud storage
     */
    public void deleteImagesFromStorage(VirtualResultTaskModel details){


        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(details.getUrl());

        // Delete the file
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // Delete content from database
                pd.setMessage("Image Deleted ... ");
                Toast.makeText(context, "Deleted Image !!", Toast.LENGTH_LONG).show();

                pd.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                Toast.makeText(context, "Failed !!" + exception, Toast.LENGTH_LONG).show();
            }
        });



    }


}
