package com.projects.thakur.apnaschool.UploadFiles;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.ProgressDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.projects.thakur.apnaschool.R;

public class ShowGalleryActivity extends AppCompatActivity {

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
    private List<Upload> uploads;
    private Upload details;
    private ProgressDialog pd;

    public static String database_path,current_image_key;
    private Context context;
    private String databasePath,databaseKey;

    // Read parent activity message and set school firbase ID
    public static String school_firbasedataID,parentAtivityMsg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_gallery_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Gallery");

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


        recyclerView = (RecyclerView) findViewById(R.id.gallery_photos_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        progressDialog = new ProgressDialog(this);

        uploads = new ArrayList<>();

        // get parent activity status
        parentAtivityMsg = getIntent().getStringExtra("EXTRA_GALLERY_INFO_SESSION_ID");

        if(parentAtivityMsg.equals("OWNER")){
            // Pass orignal ID
            school_firbasedataID = mAuth.getCurrentUser().getUid();
        } else {
            // Pass school ID
            school_firbasedataID = parentAtivityMsg;
        }

        database_path = "UserNode/" + school_firbasedataID + "/Gallery/";

        showAllPhotos();


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

            if(parentAtivityMsg.equals("OWNER")) {
                Intent intent = new Intent(ShowGalleryActivity.this, UploadFilesActivity.class);
                intent.putExtra("EXTRA_UPLOAD_FILES_SESSION_ID", "PHOTO");
                startActivity(intent);
            }

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
                    Upload upload = postSnapshot.getValue(Upload.class);
                    uploads.add(upload);
                }
                //creating adapter
                adapter = new GalleryAdapter(getApplicationContext(), uploads);

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
    public void downloadImage(Upload eachDetails, Context context_m) {

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
                    Toast.makeText(context, "Downloaded : " + path, Toast.LENGTH_SHORT).show();

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
        Find Image key from database
     */
    public void deleteComplete(Upload eachDetails, Context context_m,String databasePath_m){

        /*
           Find Database Key
         */
        context = context_m;
        details = eachDetails;
        databasePath = databasePath_m;


        pd=new ProgressDialog(context);
        pd.setProgress(100);;
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Deleting");
        pd.setMessage("Please Wait ...");
        pd.setCancelable(false);
        pd.show();

        mDatabase = FirebaseDatabase.getInstance().getReference(databasePath);

        //adding an event listener to fetch values
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //dismissing the progress dialog

                //iterating through all the values in database
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);

                    // Compare with Name
                    if(details.getName().equals(upload.getName())){
                        //Toast.makeText(context, "Key Found !!"+upload.getName()+" - "+postSnapshot.getKey(), Toast.LENGTH_SHORT).show();

                        databaseKey = postSnapshot.getKey();

                        pd.setMessage("Key Found ... ");

                        // Start Delete operation
                        deleteImagesFromStorage();

                        deleteContent();

                    }

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
       Delete Content from database
     */
    public void deleteContent(){

        mDatabase = FirebaseDatabase.getInstance().getReference(databasePath);

        // app_title change listener
        mDatabase.child(databaseKey).addValueEventListener(new ValueEventListener() {
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
    public void deleteImagesFromStorage(){


        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(details.getUrl());

        // Delete the file
        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                // Delete content from database
                pd.setMessage("Image Deleted ... ");
                Toast.makeText(context, "Deleted Image !!", Toast.LENGTH_LONG).show();

                pd.dismiss();

                finish();

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
