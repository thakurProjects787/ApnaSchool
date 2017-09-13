package com.projects.thakur.apnaschool.AdminUser;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.Common.Logger;
import com.projects.thakur.apnaschool.Model.AchivmentsDetails;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class SendNotificationActivity extends AppCompatActivity implements View.OnClickListener {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    private ProgressDialog mProgressDialog;

    private Button btn_add_new_notif;

    private EditText edtxt_notification_header,edtxt_notification_details;

    private String notif_sub,notif_details,operationStatus,userNotificationKey;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.send_notification_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Send Notification");

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

        operationStatus = getIntent().getStringExtra("EXTRA_NOTIFICATION_INFO_SESSION_ID");


        //edit text
        edtxt_notification_header = (EditText) findViewById(R.id.edtxt_notification_header);
        edtxt_notification_details = (EditText) findViewById(R.id.edtxt_notification_details);

        btn_add_new_notif = (Button) findViewById(R.id.btn_add_new_notif);
        btn_add_new_notif.setOnClickListener(this);

        // change view according to operationStatus
        if(operationStatus.equals("SEND_ALL")){
            getAllSchoolDetails();
        }  else {
            userNotificationKey = operationStatus;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_new_notif:

                if(operationStatus.equals("SEND_ALL")) {
                    sendToAllUsers();
                } else {
                    sendToSingleUsers();
                }
                break;
        }
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


    /*
       Read Inputs and then validate then save into firbase database.
     */

    // Send to all Users
    private void sendToAllUsers(){
        boolean vaidation_status = true;

        notif_sub = edtxt_notification_header.getText().toString();
        if (TextUtils.isEmpty(notif_sub)){
            edtxt_notification_header.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_notification_header.setError(null);
        }

        notif_details = edtxt_notification_details.getText().toString();
        if (TextUtils.isEmpty(notif_details)){
            edtxt_notification_details.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_notification_details.setError(null);
        }

        // if all vaidation is true then add all details into firbase database
        if(vaidation_status){

            //create  Details model
            AchivmentsDetails addNewNotif = new AchivmentsDetails();

            // Get current date
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");

            String senderDetails = StartUpActivity.userDetails.getName()+"  "+mdformat.format(calendar.getTime());

            addNewNotif.setAchv_titles(notif_sub);
            addNewNotif.setAchv_date(senderDetails);
            addNewNotif.setAchv_details(notif_details);

            showProgressDialog();

            if(mAuth.getCurrentUser()!=null)
            {

                // Update in all user notification field
                // Add all schools fields
                // get all schools details
                ArrayList<String> allSchoolsDetails = new Logger().getDataFromSchoolFile("SchoolsDetails.txt",context);

                Iterator<String> iterator = allSchoolsDetails.iterator();
                while (iterator.hasNext()) {
                    String details = iterator.next();

                    String schoolKey = details.split("#")[0];
                    String schoolDetails = details.split("#")[1];

                    // Get new push key
                    String notif_key = mDatabase.child("UserNode").child(schoolKey).child("Notification").push().getKey();

                    String firbaseIds = notif_key+"&&"+StartUpActivity.userDetails.getSchool_firbaseDataID();

                    addNewNotif.setAchv_firbase_ID(firbaseIds);

                    // save the user at UserNode under user UID
                    mDatabase.child("UserNode").child(schoolKey).child("Notification").child(notif_key).setValue(addNewNotif, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            //if(databaseError==null)
                            //{
                            //Toast.makeText(SendNotificationActivity.this, "Each Notification send!!",Toast.LENGTH_SHORT).show();
                            //}
                        }
                    });

                }

                hideProgressDialog();

                Toast.makeText(SendNotificationActivity.this, "Notification Send !!",Toast.LENGTH_SHORT).show();
                finish();
            }



        }
    }

    // Send to single user
    private void sendToSingleUsers(){
        boolean vaidation_status = true;

        notif_sub = edtxt_notification_header.getText().toString();
        if (TextUtils.isEmpty(notif_sub)){
            edtxt_notification_header.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_notification_header.setError(null);
        }

        notif_details = edtxt_notification_details.getText().toString();
        if (TextUtils.isEmpty(notif_details)){
            edtxt_notification_details.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_notification_details.setError(null);
        }

        // if all vaidation is true then add all details into firbase database
        if(vaidation_status){

            //create  Details model
            AchivmentsDetails addNewNotif = new AchivmentsDetails();

            // Get current date
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");

            String senderDetails = StartUpActivity.userDetails.getName()+"  "+mdformat.format(calendar.getTime());

            addNewNotif.setAchv_titles(notif_sub);
            addNewNotif.setAchv_date(senderDetails);
            addNewNotif.setAchv_details(notif_details);

            showProgressDialog();

            if(mAuth.getCurrentUser()!=null) {

                // Get new push key
                String notif_key = mDatabase.child("UserNode").child(userNotificationKey).child("Notification").push().getKey();

                String firbaseIds = notif_key + "&&" + StartUpActivity.userDetails.getSchool_firbaseDataID();

                addNewNotif.setAchv_firbase_ID(firbaseIds);

                // save the user at UserNode under user UID
                mDatabase.child("UserNode").child(userNotificationKey).child("Notification").child(notif_key).setValue(addNewNotif, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        hideProgressDialog();

                        if(databaseError==null)
                        {
                            Toast.makeText(SendNotificationActivity.this, "Notification send!!",Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });


            }

        }
    }

    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getAllSchoolDetails() {

        showProgressDialog();

        new Logger().deleteFile("SchoolsDetails.txt",context);

        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Sub_User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        NewUserDetails allSchools=postSnapShot.getValue(NewUserDetails.class);
                        // Read school details
                        readSchoolDetails(allSchools.getNewuserID());

                    }
                }
                hideProgressDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    /*
      Read each school details to display
     */
    private void readSchoolDetails(String schoolDataID){

        mDatabase.child("UserNode").child(schoolDataID).child("School_Basic_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // each school details
                UserBasicDetails schoolDetails = dataSnapshot.getValue(UserBasicDetails.class);

                String schooldetails = "SCHOOL@"+schoolDetails.getSchool_firbaseDataID()+"#"+schoolDetails.getId()+"&"+schoolDetails.getName()+"&"+schoolDetails.getPlace_name()+"&"+schoolDetails.getDistt();

                new Logger().addDataIntoFile("SchoolsDetails.txt",schooldetails,context);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to read value.", error.toException());
                hideProgressDialog();
            }
        });
    }




}
