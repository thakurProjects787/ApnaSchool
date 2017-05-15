package com.projects.thakur.apnaschool.DailyStatus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.Model.DailyMDMStatus;
import com.projects.thakur.apnaschool.Model.MDMDetails;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UpdateMDMStatus extends AppCompatActivity implements View.OnClickListener {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgressDialog;

    //button
    private Button btn_update_school_daily_mdm_status;

    // edit text
    private EditText txtv_mdm_present_students,txtv_mdm_rice_used_amount,txtv_today_mdm_menus;

    //Textview
    private TextView txtv_mdm_total_students,txtv_mdm_total_rice_stock;

    //Combined details
    private String mdmStudentsDetails,mdmriceStockDetails,todayMDMMenuDetails,todaySubmitDate;

    //dialog box
    AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_mdmstatus);

        Toolbar toolbar = (Toolbar) findViewById(R.id.update_mdm_status_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("MDM Status");

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


        btn_update_school_daily_mdm_status = (Button) findViewById(R.id.btn_update_school_daily_mdm_status);
        btn_update_school_daily_mdm_status.setOnClickListener(this);

        // set edit text properties and text view properties
        txtv_mdm_present_students = (EditText) findViewById(R.id.txtv_mdm_present_students);
        txtv_mdm_rice_used_amount = (EditText) findViewById(R.id.txtv_mdm_rice_used_amount);
        txtv_today_mdm_menus = (EditText) findViewById(R.id.txtv_today_mdm_menus);

        txtv_mdm_total_students = (TextView) findViewById(R.id.txtv_mdm_total_students);
        txtv_mdm_total_rice_stock = (TextView) findViewById(R.id.txtv_mdm_total_rice_stock);

        alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);

        //check today MDM status
        readTodayMDMData();

        // update view with MDM details
        readMDMData();


    }

    /*
      Read data from firebase database
    */
    private void readMDMData(){

        showProgressDialog();

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("MDM_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                MDMDetails mdmDetails = dataSnapshot.getValue(MDMDetails.class);

                txtv_mdm_total_students.setText(mdmDetails.getTotal_students()+" Students");
                txtv_mdm_total_rice_stock.setText(mdmDetails.getRice_stock()+" Kg");


                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to read value.", error.toException());

                hideProgressDialog();
            }
        });



    }

    /*
      Validate input details
     */
    private boolean validation(){
        String presents,usedRiceAmount;

        boolean vaidation_status = true;

        // read mdm students details
        // Present status
        presents = txtv_mdm_present_students.getText().toString();
        if (TextUtils.isEmpty(presents)){
            txtv_mdm_present_students.setError("Please Fill !!");
            vaidation_status = false;
        } else {
            txtv_mdm_present_students.setError(null);
        }

        if(vaidation_status){

            //check for total
            int totalMDMStudents = Integer.parseInt(txtv_mdm_total_students.getText().toString().split(" ")[0]);
            int absents =   totalMDMStudents - Integer.parseInt(presents);

            if(Integer.parseInt(presents) > totalMDMStudents){
                txtv_mdm_present_students.setError("Wrong Entry !!");
                absents = 0;
                vaidation_status = false;
            } else {
                txtv_mdm_present_students.setError(null);

                mdmStudentsDetails =  totalMDMStudents+"#"+presents+"#"+absents;
            }

        }

        // read mdm rice stock details
        // Present status
        usedRiceAmount = txtv_mdm_rice_used_amount.getText().toString();
        if (TextUtils.isEmpty(usedRiceAmount)){
            txtv_mdm_rice_used_amount.setError("Please Fill !!");
            vaidation_status = false;
        } else {
            txtv_mdm_rice_used_amount.setError(null);
        }

        if(vaidation_status){

            //check for total
            int totalRiceStock = Integer.parseInt(txtv_mdm_total_rice_stock.getText().toString().split(" ")[0]);

            //check stock balance
            if(totalRiceStock <= 0){
                txtv_mdm_rice_used_amount.setError("Your Rice Stock is Empty !!");
                Toast.makeText(this, "Your Rice Stock is empty , Please contact your MDM Admin !!",Toast.LENGTH_SHORT).show();
                vaidation_status = false;
            } else {

                int currentAmount = totalRiceStock - Integer.parseInt(usedRiceAmount);

                if (Integer.parseInt(usedRiceAmount) > totalRiceStock) {
                    txtv_mdm_rice_used_amount.setError("Wrong Entry !!");
                    currentAmount = 0;
                    vaidation_status = false;
                } else {
                    txtv_mdm_rice_used_amount.setError(null);

                    mdmriceStockDetails = usedRiceAmount + "#" + currentAmount;

                    // update current amount details at MDM node
                    updateRiceStockDetails(Integer.toString(currentAmount));

                }
            }

        }//end of mdm validation


        // check for today MDM menu
        todayMDMMenuDetails = txtv_today_mdm_menus.getText().toString();
        if(todayMDMMenuDetails.isEmpty()){
            Toast.makeText(this, "Please provide today MDM menu details !!",Toast.LENGTH_SHORT).show();
            vaidation_status = false;
        }

        return vaidation_status;
    }

    //Geather all details
    private void gatherAllDetailsAndSubmit(){
        if(validation()){

            //====================== ASK DIALOG BOX ========================================
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
            alertDialogBuilder.setTitle("SUBMIT");
            alertDialogBuilder.setMessage("Are you sure,You want to submit today MDM Details ?");

            alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                    // combine all class details into one.



                    DailyMDMStatus todayMDMStatus = new DailyMDMStatus();
                    todayMDMStatus.setMdmStudentsDetails(mdmStudentsDetails);
                    todayMDMStatus.setMdmRiceStockDetails(mdmriceStockDetails);
                    todayMDMStatus.setMdmtodayMenu(todayMDMMenuDetails);

                    UserBasicDetails schoolDetails = new StartUpActivity().userDetails;
                    todayMDMStatus.setSchool_details(schoolDetails.getId()+"#"+schoolDetails.getName()+"#"+schoolDetails.getDistt()+"#"+schoolDetails.getSchool_emailID());



                    // Get current date
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy_MM_dd");

                    todaySubmitDate = mdformat.format(calendar.getTime());

                    showProgressDialog();

                    if(mAuth.getCurrentUser()!=null)
                    {
                        // save the user at UserNode under user UID
                        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Daily_Task").child("MDM_STATUS").child(todaySubmitDate).setValue(todayMDMStatus, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                hideProgressDialog();

                                if(databaseError==null)
                                {
                                    Toast.makeText(UpdateMDMStatus.this, "Your today MDM Status has been suubmitted !!",
                                            Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
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
    }

    /*
      Read data from firebase database
    */
    private void readTodayMDMData(){

        showProgressDialog();

        // Get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy_MM_dd");



        todaySubmitDate = mdformat.format(calendar.getTime());

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Daily_Task").child("MDM_STATUS").child(todaySubmitDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DailyMDMStatus todayMDMStatus = dataSnapshot.getValue(DailyMDMStatus.class);

                if(todayMDMStatus != null) {

                    //====================== ASK DIALOG BOX ========================================
                    alertDialogBuilder.setTitle("MESSAGE");
                    alertDialogBuilder.setMessage("You have already sumitted today MDM Status.\nSO you can't modifiy again !!");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // combine all class details into one.
                           finish();

                        }
                    });


                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    //======================================================================================
                }
                else {
                    Toast.makeText(getApplicationContext(), "Not Submitted", Toast.LENGTH_LONG).show();
                }

                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Daily_Task").child("MDM_STATUS").child(todaySubmitDate).removeEventListener(this);

                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to read value.", error.toException());

                hideProgressDialog();
            }
        });



    }//end of fcn

    /*
      Update rice amount into MDM node
     */
    private void updateRiceStockDetails(String amount){
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("MDM_Info").child("rice_stock").setValue(amount);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update_school_daily_mdm_status:
                gatherAllDetailsAndSubmit();
                break;

        }

    }



    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(UpdateMDMStatus.this);
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
}
