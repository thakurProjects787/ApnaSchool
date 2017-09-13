package com.projects.thakur.apnaschool.UpdateInfo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.thakur.apnaschool.AdminUser.ShowDisttAdminUserDetails;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.Model.AchivmentsDetails;
import com.projects.thakur.apnaschool.R;

import java.util.Calendar;

public class AddNewOpenPointsActivity extends AppCompatActivity implements View.OnClickListener {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    private ProgressDialog mProgressDialog;

    private Button btn_add_new_openpoints,btn_delete_current_openpoints;

    private EditText edtxt_openpoints_title,edtxt_openpoints_tags,edtxt_openpoints_details;

    private String openpoints_title,openpoints_tags,openpoints_details;

    //Get value from parent activity
    private String operationStatus;
    private boolean deletionProcess = false;

    // class model obj
    AchivmentsDetails achivDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_open_points);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_new_open_points_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add Open Points");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        operationStatus = getIntent().getStringExtra("EXTRA_OPENPOINTS_INFO_SESSION_ID");

        // Firbase database access
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        //edit text
        edtxt_openpoints_title = (EditText) findViewById(R.id.edtxt_openpoints_title);
        edtxt_openpoints_tags = (EditText) findViewById(R.id.edtxt_openpoints_tags);
        edtxt_openpoints_details = (EditText) findViewById(R.id.edtxt_openpoints_details);

        btn_add_new_openpoints = (Button) findViewById(R.id.btn_add_new_openpoints);
        btn_add_new_openpoints.setOnClickListener(this);

        btn_delete_current_openpoints = (Button) findViewById(R.id.btn_delete_current_openpoints);
        btn_delete_current_openpoints.setOnClickListener(this);



        // change view according to operationStatus
        if(operationStatus.equals("ADD_NEW")){
            btn_delete_current_openpoints.setVisibility(View.GONE);

        } else{
            btn_add_new_openpoints.setText("UPDATE");
            readCurrentData();
        }



    } // end of onCreate


    /*
       Read Inputs and then validate then save into firbase database.
     */

    private void readAndSAVE(){
        boolean vaidation_status = true;

        openpoints_title = edtxt_openpoints_title.getText().toString();
        if (TextUtils.isEmpty(openpoints_title)){
            edtxt_openpoints_title.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_openpoints_title.setError(null);
        }

        openpoints_tags = edtxt_openpoints_tags.getText().toString();
        if (TextUtils.isEmpty(openpoints_tags)){
            edtxt_openpoints_tags.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_openpoints_tags.setError(null);
        }



        openpoints_details = edtxt_openpoints_details.getText().toString();
        if(openpoints_details.isEmpty()){
            vaidation_status = false;
            edtxt_openpoints_details.setText("Please write Details!!");
        }

        // if all vaidation is true then add all details into firbase database
        if(vaidation_status){

            //create  Details model
            AchivmentsDetails addNewAchiv = new AchivmentsDetails();

            addNewAchiv.setAchv_titles(openpoints_title);
            addNewAchiv.setAchv_date(openpoints_tags+"##"+ StartUpActivity.userDetails.getName());
            addNewAchiv.setAchv_details(openpoints_details);

            showProgressDialog();

            if(mAuth.getCurrentUser()!=null)
            {
                //CHECK IF new achivments has been added or update process.
                String achiv_key;
                if(operationStatus.equals("ADD_NEW")) {

                    // Get new push key
                    achiv_key = mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Open_Points").push().getKey();
                    addNewAchiv.setAchv_firbase_ID(achiv_key);

                } else {
                    achiv_key = operationStatus;
                    addNewAchiv.setAchv_firbase_ID(achiv_key);
                }

                // save the user at UserNode under user UID
                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Open_Points").child(achiv_key).setValue(addNewAchiv, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        hideProgressDialog();

                        if(databaseError==null)
                        {
                            Toast.makeText(AddNewOpenPointsActivity.this, "Your Details has been saved !!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }



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



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_new_openpoints:
                readAndSAVE();
                break;

            case R.id.btn_delete_current_openpoints:

                //====================== ASK DIALOG BOX ========================================
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
                alertDialogBuilder.setTitle("SUBMIT");
                alertDialogBuilder.setMessage("Are you sure,You want to Delete Achivment ?");

                alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        deleteCurrentData();

                        if (StartUpActivity.userDetails.getType().equals("State")) {
                            Intent intent = new Intent(AddNewOpenPointsActivity.this, ShowDisttAdminUserDetails.class);
                            intent.putExtra("EXTRA_SHOW_SCHOOL_SESSION_ID", "OWNER");
                            startActivity(intent);

                        } else if (StartUpActivity.userDetails.getType().equals("Admin")) {
                            Intent intent = new Intent(AddNewOpenPointsActivity.this, ShowDisttAdminUserDetails.class);
                            intent.putExtra("EXTRA_SHOW_SCHOOL_SESSION_ID", "OWNER");
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(AddNewOpenPointsActivity.this, ShowEachSchoolDetails.class);
                            intent.putExtra("EXTRA_SHOW_SCHOOL_SESSION_ID", "OWNER");
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

                break;

        }
    }


    /*
      Read data from firebase database
    */
    private void readCurrentData(){

        showProgressDialog();

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Open_Points").child(operationStatus).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!deletionProcess) {

                    achivDetails = dataSnapshot.getValue(AchivmentsDetails.class);

                    // display user details
                    edtxt_openpoints_title.setText(achivDetails.getAchv_titles());
                    edtxt_openpoints_tags.setText(achivDetails.getAchv_date());
                    edtxt_openpoints_details.setText(achivDetails.getAchv_details());

                    hideProgressDialog();
                }

                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Open_Points").child(operationStatus).removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to read value.", error.toException());

                hideProgressDialog();
            }
        });



    }


    // Delete current details
    private void deleteCurrentData(){


        showProgressDialog();

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Open_Points").child(operationStatus).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                deletionProcess = true;

                dataSnapshot.getRef().setValue(null);

                Toast.makeText(AddNewOpenPointsActivity.this, operationStatus+" Deleted!!",
                        Toast.LENGTH_LONG).show();

                hideProgressDialog();

                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to delete Values.", error.toException());

                hideProgressDialog();

                finish();
            }
        });

        finish();

    }
}
