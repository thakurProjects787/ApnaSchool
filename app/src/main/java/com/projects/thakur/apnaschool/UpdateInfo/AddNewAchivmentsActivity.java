package com.projects.thakur.apnaschool.UpdateInfo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.thakur.apnaschool.Model.AchivmentsDetails;
import com.projects.thakur.apnaschool.Model.ClassDetails;
import com.projects.thakur.apnaschool.R;

public class AddNewAchivmentsActivity extends AppCompatActivity implements View.OnClickListener {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    private ProgressDialog mProgressDialog;

    private Button btn_add_new_achiv,btn_delete_current_achiv;

    private EditText edtxt_achiv_title,edtxt_achiv_date,edtxt_achiv_details;

    private String achiv_title,achiv_date,achiv_details;

    //Get value from parent activity
    private String operationStatus;
    private boolean deletionProcess = false;

    // class model obj
    AchivmentsDetails achivDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_achivments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_new_achivments_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("New Achivments");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        operationStatus = getIntent().getStringExtra("EXTRA_ACHIVMENTS_INFO_SESSION_ID");

        // Firbase database access
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        //edit text
        edtxt_achiv_title = (EditText) findViewById(R.id.edtxt_achiv_title);
        edtxt_achiv_date = (EditText) findViewById(R.id.edtxt_achiv_date);
        edtxt_achiv_details = (EditText) findViewById(R.id.edtxt_achiv_details);

        btn_add_new_achiv = (Button) findViewById(R.id.btn_add_new_achiv);
        btn_add_new_achiv.setOnClickListener(this);

        btn_delete_current_achiv = (Button) findViewById(R.id.btn_delete_current_achiv);
        btn_delete_current_achiv.setOnClickListener(this);

        // change view according to operationStatus
        if(operationStatus.equals("ADD_NEW")){
            btn_delete_current_achiv.setVisibility(View.GONE);

        } else{
            btn_add_new_achiv.setText("UPDATE");
            readCurrentData();
        }



    } // end of onCreate


    /*
       Read Inputs and then validate then save into firbase database.
     */

    private void readAndSAVE(){
        boolean vaidation_status = true;

        achiv_title = edtxt_achiv_title.getText().toString();
        if (TextUtils.isEmpty(achiv_title)){
            edtxt_achiv_title.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_achiv_title.setError(null);
        }

        achiv_date = edtxt_achiv_date.getText().toString();
        if (TextUtils.isEmpty(achiv_date)){
            edtxt_achiv_date.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_achiv_date.setError(null);
        }



        achiv_details = edtxt_achiv_details.getText().toString();
        if(achiv_details.isEmpty()){
            vaidation_status = false;
            edtxt_achiv_details.setText("Please write Details!!");
        }

        // if all vaidation is true then add all details into firbase database
        if(vaidation_status){

            //create  Details model
            AchivmentsDetails addNewAchiv = new AchivmentsDetails();

            addNewAchiv.setAchv_titles(achiv_title);
            addNewAchiv.setAchv_date(achiv_date);
            addNewAchiv.setAchv_details(achiv_details);

            showProgressDialog();

            if(mAuth.getCurrentUser()!=null)
            {
                //CHECK IF new achivments has been added or update process.
                String achiv_key;
                if(operationStatus.equals("ADD_NEW")) {

                    // Get new push key
                    achiv_key = mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Achivments_Info").push().getKey();
                    addNewAchiv.setAchv_firbase_ID(achiv_key);

                } else {
                    achiv_key = operationStatus;
                    addNewAchiv.setAchv_firbase_ID(achiv_key);
                }

                // save the user at UserNode under user UID
                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Achivments_Info").child(achiv_key).setValue(addNewAchiv, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        hideProgressDialog();

                        if(databaseError==null)
                        {
                            Toast.makeText(AddNewAchivmentsActivity.this, "Your Details has been saved !!",
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
            case R.id.btn_add_new_achiv:
                readAndSAVE();
                break;

            case R.id.btn_delete_current_achiv:

                //====================== ASK DIALOG BOX ========================================
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
                alertDialogBuilder.setTitle("SUBMIT");
                alertDialogBuilder.setMessage("Are you sure,You want to Delete Achivment ?");

                alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        deleteCurrentData();
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

        }
    }


    /*
      Read data from firebase database
    */
    private void readCurrentData(){

        showProgressDialog();

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Achivments_Info").child(operationStatus).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!deletionProcess) {

                    achivDetails = dataSnapshot.getValue(AchivmentsDetails.class);

                    // display user details
                    edtxt_achiv_title.setText(achivDetails.getAchv_titles());
                    edtxt_achiv_date.setText(achivDetails.getAchv_date());
                    edtxt_achiv_details.setText(achivDetails.getAchv_details());

                    hideProgressDialog();
                }

                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Achivments_Info").child(operationStatus).removeEventListener(this);
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
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Achivments_Info").child(operationStatus).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                deletionProcess = true;

                dataSnapshot.getRef().setValue(null);

                Toast.makeText(AddNewAchivmentsActivity.this, operationStatus+" Deleted!!",
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
