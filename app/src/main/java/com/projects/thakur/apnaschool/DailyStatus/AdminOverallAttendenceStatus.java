package com.projects.thakur.apnaschool.DailyStatus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.thakur.apnaschool.AdminUser.NewUserDetails;
import com.projects.thakur.apnaschool.Common.CreateExcelReport;
import com.projects.thakur.apnaschool.Common.Logger;
import com.projects.thakur.apnaschool.Model.DailyStudentAttendance;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AdminOverallAttendenceStatus extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference mFirebaseDatabase;

    private ProgressDialog mProgressDialog;

    private String todaySubmitDate,eachSchoolID;

    private Context context;

    //logger
    private Logger logger;

    // Declare all textview bind variables
    private TextView txtv_admin_daily_attn_tot_schools_value,txtv_admin_daily_attn_tot_schools_present_value,txtv_admin_daily_attn_tot_schools_pending_value;
    private TextView txtv_admin_daily_attn_tot_student_value,txtv_admin_daily_attn_tot_student_present_value,txtv_admin_daily_attn_tot_student_absent_value;
    private TextView txtv_admin_daily_attn_tot_teacher_value,txtv_admin_daily_attn_tot_teacher_present_value,txtv_admin_daily_attn_tot_teacher_absent_value;

    private TextView txtv_admin_daily_attn_tot_Class_I_tot_value,txtv_admin_daily_attn_tot_Class_I_present_value,txtv_admin_daily_attn_tot_Class_I_absent_value;
    private TextView txtv_admin_daily_attn_tot_Class_II_tot_value,txtv_admin_daily_attn_tot_Class_II_present_value,txtv_admin_daily_attn_tot_Class_II_absent_value;
    private TextView txtv_admin_daily_attn_tot_Class_III_tot_value,txtv_admin_daily_attn_tot_Class_III_present_value,txtv_admin_daily_attn_tot_Class_III_absent_value;
    private TextView txtv_admin_daily_attn_tot_Class_IV_tot_value,txtv_admin_daily_attn_tot_Class_IV_present_value,txtv_admin_daily_attn_tot_Class_IV_absent_value;
    private TextView txtv_admin_daily_attn_tot_Class_V_tot_value,txtv_admin_daily_attn_tot_Class_V_present_value,txtv_admin_daily_attn_tot_Class_V_absent_value;
    private TextView txtv_admin_daily_attn_tot_Class_VI_tot_value,txtv_admin_daily_attn_tot_Class_VI_present_value,txtv_admin_daily_attn_tot_Class_VI_absent_value;
    private TextView txtv_admin_daily_attn_tot_Class_VII_tot_value,txtv_admin_daily_attn_tot_Class_VII_present_value,txtv_admin_daily_attn_tot_Class_VII_absent_value;
    private TextView txtv_admin_daily_attn_tot_Class_VIII_tot_value,txtv_admin_daily_attn_tot_Class_VIII_present_value,txtv_admin_daily_attn_tot_Class_VIII_absent_value;
    private TextView txtv_admin_daily_attn_tot_Class_IX_tot_value,txtv_admin_daily_attn_tot_Class_IX_present_value,txtv_admin_daily_attn_tot_Class_IX_absent_value;
    private TextView txtv_admin_daily_attn_tot_Class_X_tot_value,txtv_admin_daily_attn_tot_Class_X_present_value,txtv_admin_daily_attn_tot_Class_X_absent_value;
    private TextView txtv_admin_daily_attn_tot_Class_XI_tot_value,txtv_admin_daily_attn_tot_Class_XI_present_value,txtv_admin_daily_attn_tot_Class_XI_absent_value;
    private TextView txtv_admin_daily_attn_tot_Class_XII_tot_value,txtv_admin_daily_attn_tot_Class_XII_present_value,txtv_admin_daily_attn_tot_Class_XII_absent_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_overall_attendence_status);

        Toolbar toolbar = (Toolbar) findViewById(R.id.admin_overall_attn_status_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Attendance");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        //bind all texview variables
        txtv_admin_daily_attn_tot_schools_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_schools_value);
        txtv_admin_daily_attn_tot_schools_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_schools_present_value);
        txtv_admin_daily_attn_tot_schools_pending_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_schools_pending_value);

        txtv_admin_daily_attn_tot_student_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_student_value);
        txtv_admin_daily_attn_tot_student_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_student_present_value);
        txtv_admin_daily_attn_tot_student_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_student_absent_value);

        txtv_admin_daily_attn_tot_teacher_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_teacher_value);
        txtv_admin_daily_attn_tot_teacher_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_teacher_present_value);
        txtv_admin_daily_attn_tot_teacher_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_teacher_absent_value);

        txtv_admin_daily_attn_tot_Class_I_tot_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_I_tot_value);
        txtv_admin_daily_attn_tot_Class_I_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_I_present_value);
        txtv_admin_daily_attn_tot_Class_I_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_I_absent_value);
        txtv_admin_daily_attn_tot_Class_II_tot_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_II_tot_value);
        txtv_admin_daily_attn_tot_Class_II_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_II_present_value);
        txtv_admin_daily_attn_tot_Class_II_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_II_absent_value);
        txtv_admin_daily_attn_tot_Class_III_tot_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_III_tot_value);
        txtv_admin_daily_attn_tot_Class_III_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_III_present_value);
        txtv_admin_daily_attn_tot_Class_III_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_III_absent_value);
        txtv_admin_daily_attn_tot_Class_IV_tot_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_IV_tot_value);
        txtv_admin_daily_attn_tot_Class_IV_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_IV_present_value);
        txtv_admin_daily_attn_tot_Class_IV_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_IV_absent_value);
        txtv_admin_daily_attn_tot_Class_V_tot_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_V_tot_value);
        txtv_admin_daily_attn_tot_Class_V_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_V_present_value);
        txtv_admin_daily_attn_tot_Class_V_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_V_absent_value);
        txtv_admin_daily_attn_tot_Class_VI_tot_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_VI_tot_value);
        txtv_admin_daily_attn_tot_Class_VI_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_VI_present_value);
        txtv_admin_daily_attn_tot_Class_VI_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_VI_absent_value);
        txtv_admin_daily_attn_tot_Class_VII_tot_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_VII_tot_value);
        txtv_admin_daily_attn_tot_Class_VII_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_VII_present_value);
        txtv_admin_daily_attn_tot_Class_VII_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_VII_absent_value);
        txtv_admin_daily_attn_tot_Class_VIII_tot_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_VIII_tot_value);
        txtv_admin_daily_attn_tot_Class_VIII_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_VIII_present_value);
        txtv_admin_daily_attn_tot_Class_VIII_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_VIII_absent_value);
        txtv_admin_daily_attn_tot_Class_IX_tot_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_IX_tot_value);
        txtv_admin_daily_attn_tot_Class_IX_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_IX_present_value);
        txtv_admin_daily_attn_tot_Class_IX_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_IX_absent_value);
        txtv_admin_daily_attn_tot_Class_X_tot_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_X_tot_value);
        txtv_admin_daily_attn_tot_Class_X_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_X_present_value);
        txtv_admin_daily_attn_tot_Class_X_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_X_absent_value);
        txtv_admin_daily_attn_tot_Class_XI_tot_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_XI_tot_value);
        txtv_admin_daily_attn_tot_Class_XI_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_XI_present_value);
        txtv_admin_daily_attn_tot_Class_XI_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_XI_absent_value);
        txtv_admin_daily_attn_tot_Class_XII_tot_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_XII_tot_value);
        txtv_admin_daily_attn_tot_Class_XII_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_XII_present_value);
        txtv_admin_daily_attn_tot_Class_XII_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_Class_XII_absent_value);

        // Get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy_MM_dd");

        todaySubmitDate = mdformat.format(calendar.getTime());


        /*
          Read all schools attendence details
         */
        getAdminAllSchoolsDetails();


    } //end of create


    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getAdminAllSchoolsDetails() {

        //delete old dataCal file
        logger.deleteFile("dataCal.txt",getApplicationContext());

        resetView();

        showProgressDialog();
        mFirebaseDatabase.child("UserNode").child(auth.getCurrentUser().getUid()).child("Sub_User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    long totalSchool = dataSnapshot.getChildrenCount();

                    txtv_admin_daily_attn_tot_schools_value.setText(Long.toString(totalSchool));

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        NewUserDetails allSchools=postSnapShot.getValue(NewUserDetails.class);
                        // Read school details
                        readSchoolDailyAttendenceDetails(allSchools.getNewuserID());

                    }
                }
                hideProgressDialog();

                mFirebaseDatabase.child("UserNode").child(auth.getCurrentUser().getUid()).child("Sub_User").removeEventListener(this);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });

    }


    /*
       Read each school attendence details.
     */

    private void readSchoolDailyAttendenceDetails(String schoolDataID){

        eachSchoolID = schoolDataID;

        mFirebaseDatabase.child("UserNode").child(eachSchoolID).child("Daily_Task").child("Attendence").child(todaySubmitDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    // each school details
                    DailyStudentAttendance schoolAttenDetails = dataSnapshot.getValue(DailyStudentAttendance.class);

                    if (schoolAttenDetails != null) {

                        txtv_admin_daily_attn_tot_schools_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_schools_present_value.getText().toString()) + 1));

                        // Update Each school wise details
                        String eachSchool = "EACHSCHOOL@" + schoolAttenDetails.getSchool_details() + "&" + schoolAttenDetails.getAll_teachers_sttnd() + "&" + schoolAttenDetails.getCalculated_attnd() + "&" + schoolAttenDetails.getAll_classes_attnd();
                        logger.addDataIntoFile("dataCal.txt", eachSchool, getApplicationContext());


                        updateStudentTeachersCard(schoolAttenDetails);

                        //Log.d(">>", "Not Submitted !!");
                        txtv_admin_daily_attn_tot_schools_pending_value.setText( Integer.toString( Integer.parseInt(txtv_admin_daily_attn_tot_schools_value.getText().toString()) - Integer.parseInt(txtv_admin_daily_attn_tot_schools_present_value.getText().toString()) ) );
                        //Toast.makeText(getApplicationContext(), "Not Submitted", Toast.LENGTH_LONG).show();


                        //Log.d(">>", "Submitted !!");
                        //Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_LONG).show();

                    }



                    // Update all data into Database
                    updateCombinedDetails();

                    mFirebaseDatabase.child("UserNode").child(eachSchoolID).child("Daily_Task").child("Attendence").child(todaySubmitDate).removeEventListener(this);
                }
                else {
                    readSchoolBasicInfo(eachSchoolID);
                }
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
      Update Students and teachers card view
     */
    private void updateStudentTeachersCard(DailyStudentAttendance schoolAttenDetails){

        txtv_admin_daily_attn_tot_student_value.setText( Integer.toString( Integer.parseInt( txtv_admin_daily_attn_tot_student_value.getText().toString()) + Integer.parseInt(schoolAttenDetails.getCalculated_attnd().split("#")[0])));
        txtv_admin_daily_attn_tot_student_present_value.setText( Integer.toString(Integer.parseInt( txtv_admin_daily_attn_tot_student_present_value.getText().toString()) + Integer.parseInt(schoolAttenDetails.getCalculated_attnd().split("#")[1])));
        txtv_admin_daily_attn_tot_student_absent_value.setText( Integer.toString(Integer.parseInt( txtv_admin_daily_attn_tot_student_absent_value.getText().toString()) + Integer.parseInt(schoolAttenDetails.getCalculated_attnd().split("#")[2])));

        txtv_admin_daily_attn_tot_teacher_value.setText( Integer.toString( Integer.parseInt( txtv_admin_daily_attn_tot_teacher_value.getText().toString()) + Integer.parseInt(schoolAttenDetails.getAll_teachers_sttnd().split("#")[0])));
        txtv_admin_daily_attn_tot_teacher_present_value.setText( Integer.toString(Integer.parseInt( txtv_admin_daily_attn_tot_teacher_present_value.getText().toString()) + Integer.parseInt(schoolAttenDetails.getAll_teachers_sttnd().split("#")[1])));
        txtv_admin_daily_attn_tot_teacher_absent_value.setText( Integer.toString(Integer.parseInt( txtv_admin_daily_attn_tot_teacher_absent_value.getText().toString()) + Integer.parseInt(schoolAttenDetails.getAll_teachers_sttnd().split("#")[2])));


        //Update class view
        updateClassCardView(schoolAttenDetails);


    }

    /*
      Update CLass card view
     */

    private void updateClassCardView(DailyStudentAttendance schoolAttenDetails){

        String[] allClassesDetails = schoolAttenDetails.getAll_classes_attnd().split("%");

        for(String eachclass : allClassesDetails) {
            claculateClassWise(eachclass);
        }

    }

    /*
      Calculate class wise attendence status
     */
    private void claculateClassWise(String details){
        String className = details.split("#")[0];
        String presents = details.split("#")[1];
        String absents = details.split("#")[2];

        // calculate each class wise info
        switch (className){
            case "Class I":
                txtv_admin_daily_attn_tot_Class_I_tot_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_I_tot_value.getText().toString()) + Integer.parseInt(presents) + Integer.parseInt(absents)));
                txtv_admin_daily_attn_tot_Class_I_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_I_present_value.getText().toString()) + Integer.parseInt(presents)));
                txtv_admin_daily_attn_tot_Class_I_absent_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_I_absent_value.getText().toString()) + + Integer.parseInt(absents)));

                break;

            case "Class II":
                txtv_admin_daily_attn_tot_Class_II_tot_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_II_tot_value.getText().toString()) + Integer.parseInt(presents) + Integer.parseInt(absents)));
                txtv_admin_daily_attn_tot_Class_II_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_II_present_value.getText().toString()) + Integer.parseInt(presents)));
                txtv_admin_daily_attn_tot_Class_II_absent_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_II_absent_value.getText().toString()) + + Integer.parseInt(absents)));

                break;

            case "Class III":

                txtv_admin_daily_attn_tot_Class_III_tot_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_III_tot_value.getText().toString()) + Integer.parseInt(presents) + Integer.parseInt(absents)));
                txtv_admin_daily_attn_tot_Class_III_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_III_present_value.getText().toString()) + Integer.parseInt(presents)));
                txtv_admin_daily_attn_tot_Class_III_absent_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_III_absent_value.getText().toString()) + + Integer.parseInt(absents)));

                break;
            case "Class IV":

                txtv_admin_daily_attn_tot_Class_IV_tot_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_IV_tot_value.getText().toString()) + Integer.parseInt(presents) + Integer.parseInt(absents)));
                txtv_admin_daily_attn_tot_Class_IV_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_IV_present_value.getText().toString()) + Integer.parseInt(presents)));
                txtv_admin_daily_attn_tot_Class_IV_absent_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_IV_absent_value.getText().toString()) + + Integer.parseInt(absents)));

                break;
            case "Class V":

                txtv_admin_daily_attn_tot_Class_V_tot_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_V_tot_value.getText().toString()) + Integer.parseInt(presents) + Integer.parseInt(absents)));
                txtv_admin_daily_attn_tot_Class_V_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_V_present_value.getText().toString()) + Integer.parseInt(presents)));
                txtv_admin_daily_attn_tot_Class_V_absent_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_V_absent_value.getText().toString()) + + Integer.parseInt(absents)));

                break;
            case "Class VI":

                txtv_admin_daily_attn_tot_Class_VI_tot_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_VI_tot_value.getText().toString()) + Integer.parseInt(presents) + Integer.parseInt(absents)));
                txtv_admin_daily_attn_tot_Class_VI_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_VI_present_value.getText().toString()) + Integer.parseInt(presents)));
                txtv_admin_daily_attn_tot_Class_VI_absent_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_VI_absent_value.getText().toString()) + + Integer.parseInt(absents)));

                break;
            case "Class VII":

                txtv_admin_daily_attn_tot_Class_VII_tot_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_VII_tot_value.getText().toString()) + Integer.parseInt(presents) + Integer.parseInt(absents)));
                txtv_admin_daily_attn_tot_Class_VII_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_VII_present_value.getText().toString()) + Integer.parseInt(presents)));
                txtv_admin_daily_attn_tot_Class_VII_absent_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_VII_absent_value.getText().toString()) + + Integer.parseInt(absents)));

                break;
            case "Class VIII":

                txtv_admin_daily_attn_tot_Class_VIII_tot_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_VIII_tot_value.getText().toString()) + Integer.parseInt(presents) + Integer.parseInt(absents)));
                txtv_admin_daily_attn_tot_Class_VIII_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_VIII_present_value.getText().toString()) + Integer.parseInt(presents)));
                txtv_admin_daily_attn_tot_Class_VIII_absent_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_VIII_absent_value.getText().toString()) + + Integer.parseInt(absents)));

                break;
            case "Class IX":

                txtv_admin_daily_attn_tot_Class_IX_tot_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_IX_tot_value.getText().toString()) + Integer.parseInt(presents) + Integer.parseInt(absents)));
                txtv_admin_daily_attn_tot_Class_IX_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_IX_present_value.getText().toString()) + Integer.parseInt(presents)));
                txtv_admin_daily_attn_tot_Class_IX_absent_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_IX_absent_value.getText().toString()) + + Integer.parseInt(absents)));

                break;
            case "Class X":

                txtv_admin_daily_attn_tot_Class_X_tot_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_X_tot_value.getText().toString()) + Integer.parseInt(presents) + Integer.parseInt(absents)));
                txtv_admin_daily_attn_tot_Class_X_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_X_present_value.getText().toString()) + Integer.parseInt(presents)));
                txtv_admin_daily_attn_tot_Class_X_absent_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_X_absent_value.getText().toString()) + + Integer.parseInt(absents)));

                break;
            case "Class XI":

                txtv_admin_daily_attn_tot_Class_XI_tot_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_XI_tot_value.getText().toString()) + Integer.parseInt(presents) + Integer.parseInt(absents)));
                txtv_admin_daily_attn_tot_Class_XI_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_XI_present_value.getText().toString()) + Integer.parseInt(presents)));
                txtv_admin_daily_attn_tot_Class_XI_absent_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_XI_absent_value.getText().toString()) + + Integer.parseInt(absents)));

                break;
            case "Class XII":

                txtv_admin_daily_attn_tot_Class_XII_tot_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_XII_tot_value.getText().toString()) + Integer.parseInt(presents) + Integer.parseInt(absents)));
                txtv_admin_daily_attn_tot_Class_XII_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_XII_present_value.getText().toString()) + Integer.parseInt(presents)));
                txtv_admin_daily_attn_tot_Class_XII_absent_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_Class_XII_absent_value.getText().toString()) + + Integer.parseInt(absents)));

                break;

        }//end of switch

    }

    /*
      Update all calculated details
     */

    private void updateCombinedDetails(){

        if(auth.getCurrentUser()!=null)
        {

            DailyStudentAttendance todayAttendence = new DailyStudentAttendance();

            // combined all data

            String studentsAttndetails = txtv_admin_daily_attn_tot_student_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_student_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_student_absent_value.getText().toString();
            String teachersAttndetails = txtv_admin_daily_attn_tot_teacher_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_teacher_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_teacher_absent_value.getText().toString();

            String Class_I = "Class I#"+txtv_admin_daily_attn_tot_Class_I_tot_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_I_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_I_absent_value.getText().toString();
            String Class_II = "Class II#"+txtv_admin_daily_attn_tot_Class_II_tot_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_II_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_II_absent_value.getText().toString();
            String Class_III = "Class III#"+txtv_admin_daily_attn_tot_Class_III_tot_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_III_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_III_absent_value.getText().toString();
            String Class_IV = "Class IV#"+txtv_admin_daily_attn_tot_Class_IV_tot_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_IV_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_IV_absent_value.getText().toString();
            String Class_V = "Class V#"+txtv_admin_daily_attn_tot_Class_V_tot_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_V_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_V_absent_value.getText().toString();
            String Class_VI = "Class VI#"+txtv_admin_daily_attn_tot_Class_VI_tot_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_VI_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_VI_absent_value.getText().toString();
            String Class_VII = "Class VII#"+txtv_admin_daily_attn_tot_Class_VII_tot_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_VII_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_VII_absent_value.getText().toString();
            String Class_VIII = "Class VIII#"+txtv_admin_daily_attn_tot_Class_VIII_tot_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_VIII_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_VIII_absent_value.getText().toString();
            String Class_IX = "Class IX#"+txtv_admin_daily_attn_tot_Class_IX_tot_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_IX_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_IX_absent_value.getText().toString();
            String Class_X = "Class X#"+txtv_admin_daily_attn_tot_Class_X_tot_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_X_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_X_absent_value.getText().toString();
            String Class_XI = "Class XI#"+txtv_admin_daily_attn_tot_Class_XI_tot_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_XI_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_XI_absent_value.getText().toString();
            String Class_XII = "Class XII#"+txtv_admin_daily_attn_tot_Class_XII_tot_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_XII_present_value.getText().toString()+"#"+txtv_admin_daily_attn_tot_Class_XII_absent_value.getText().toString();


            String classwiseStatus =Class_I+ "%" +Class_II+ "%" +Class_III+ "%" +Class_IV+ "%" +Class_V+ "%" +Class_VI+ "%" +Class_VII+ "%" +Class_VIII+ "%" +Class_IX+ "%" +Class_X+ "%" +Class_XI+ "%" +Class_XII;

            // add details into text file
            logger.addDataIntoFile("dataCal.txt","STUDENT@"+studentsAttndetails,this);
            logger.addDataIntoFile("dataCal.txt","TEACHER@"+teachersAttndetails,this);
            logger.addDataIntoFile("dataCal.txt","CLASSES@"+classwiseStatus,this);

            todayAttendence.setCalculated_attnd(studentsAttndetails);
            todayAttendence.setAll_teachers_sttnd(teachersAttndetails);
            todayAttendence.setAll_classes_attnd(classwiseStatus);
            todayAttendence.setSchool_details("");

            // save the user at UserNode under user UID
            mFirebaseDatabase.child("UserNode").child(auth.getCurrentUser().getUid()).child("Daily_Task").child("Attendence").child(todaySubmitDate).setValue(todayAttendence, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                    if(databaseError==null)
                    {
                        Toast.makeText(AdminOverallAttendenceStatus.this, "Updated",
                                Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    /*
      Reset View
     */
    private void resetView(){

        //Reset
        txtv_admin_daily_attn_tot_schools_value.setText("0");
        txtv_admin_daily_attn_tot_schools_present_value.setText("0");
        txtv_admin_daily_attn_tot_schools_pending_value.setText("0");

        txtv_admin_daily_attn_tot_student_value.setText("0");
        txtv_admin_daily_attn_tot_student_present_value.setText("0");
        txtv_admin_daily_attn_tot_student_absent_value.setText("0");

        txtv_admin_daily_attn_tot_teacher_value.setText("0");
        txtv_admin_daily_attn_tot_teacher_present_value.setText("0");
        txtv_admin_daily_attn_tot_teacher_absent_value.setText("0");

        txtv_admin_daily_attn_tot_Class_I_tot_value.setText("0");
        txtv_admin_daily_attn_tot_Class_I_present_value.setText("0");
        txtv_admin_daily_attn_tot_Class_I_absent_value.setText("0");
        txtv_admin_daily_attn_tot_Class_II_tot_value.setText("0");
        txtv_admin_daily_attn_tot_Class_II_present_value.setText("0");
        txtv_admin_daily_attn_tot_Class_II_absent_value.setText("0");
        txtv_admin_daily_attn_tot_Class_III_tot_value.setText("0");
        txtv_admin_daily_attn_tot_Class_III_present_value.setText("0");
        txtv_admin_daily_attn_tot_Class_III_absent_value.setText("0");
        txtv_admin_daily_attn_tot_Class_IV_tot_value.setText("0");
        txtv_admin_daily_attn_tot_Class_IV_present_value.setText("0");
        txtv_admin_daily_attn_tot_Class_IV_absent_value.setText("0");
        txtv_admin_daily_attn_tot_Class_V_tot_value.setText("0");
        txtv_admin_daily_attn_tot_Class_V_present_value.setText("0");
        txtv_admin_daily_attn_tot_Class_V_absent_value.setText("0");
        txtv_admin_daily_attn_tot_Class_VI_tot_value.setText("0");
        txtv_admin_daily_attn_tot_Class_VI_present_value.setText("0");
        txtv_admin_daily_attn_tot_Class_VI_absent_value.setText("0");
        txtv_admin_daily_attn_tot_Class_VII_tot_value.setText("0");
        txtv_admin_daily_attn_tot_Class_VII_present_value.setText("0");
        txtv_admin_daily_attn_tot_Class_VII_absent_value.setText("0");
        txtv_admin_daily_attn_tot_Class_VIII_tot_value.setText("0");
        txtv_admin_daily_attn_tot_Class_VIII_present_value.setText("0");
        txtv_admin_daily_attn_tot_Class_VIII_absent_value.setText("0");
        txtv_admin_daily_attn_tot_Class_IX_tot_value.setText("0");
        txtv_admin_daily_attn_tot_Class_IX_present_value.setText("0");
        txtv_admin_daily_attn_tot_Class_IX_absent_value.setText("0");
        txtv_admin_daily_attn_tot_Class_X_tot_value.setText("0");
        txtv_admin_daily_attn_tot_Class_X_present_value.setText("0");
        txtv_admin_daily_attn_tot_Class_X_absent_value.setText("0");
        txtv_admin_daily_attn_tot_Class_XI_tot_value.setText("0");
        txtv_admin_daily_attn_tot_Class_XI_present_value.setText("0");
        txtv_admin_daily_attn_tot_Class_XI_absent_value.setText("0");
        txtv_admin_daily_attn_tot_Class_XII_tot_value.setText("0");
        txtv_admin_daily_attn_tot_Class_XII_present_value.setText("0");
        txtv_admin_daily_attn_tot_Class_XII_absent_value.setText("0");



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_overall_attnd_status_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.admin_overall_attnd_status_send_report) {

            // check submitted schools status

            if(txtv_admin_daily_attn_tot_schools_present_value.getText().equals("0")){

                Toast.makeText(getApplicationContext(), "No one has submitted !!", Toast.LENGTH_LONG).show();

            } else {

                // Ask for another email ID.
                 /* Alert Dialog Code Start*/
                AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
                alert.setTitle("SEND REPORT"); //Set Alert dialog title here
                alert.setMessage("If you want report on new email id , please provide new email id address.\n\nOtherwise it will send to your default email ID."); //Message here


                // Set an EditText view to get user input
                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                input.setTextColor(Color.BLACK);
                alert.setView(input);

                alert.setPositiveButton("NEW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //You will get as string input data in this variable.
                        // here we convert the input to a string and show in a toast.
                        String keyword = input.getEditableText().toString();
                        //Toast.makeText(getApplicationContext(),srt,Toast.LENGTH_LONG).show();
                        if (!keyword.isEmpty() && (keyword.contains("@")) && (keyword.contains(".com"))) {


                            String fileName = "AttendenceReport_" + todaySubmitDate + ".xls";

                            if (new CreateExcelReport().generateAttndReport(fileName, context,keyword)) {
                                Toast.makeText(getApplicationContext(), "Report Generated : " + fileName, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Report Not Generated", Toast.LENGTH_LONG).show();
                            }


                        } else {
                            //Toast.makeText(getApplicationContext(), "Wrong Password !!", Toast.LENGTH_LONG).show();

                            dialog.cancel();
                        }


                    } // End of onClick(DialogInterface dialog, int whichButton)
                }); //End of alert.setPositiveButton
                alert.setNegativeButton("DEFAULT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        //Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();

                        String fileName = "AttendenceReport_" + todaySubmitDate + ".xls";

                        if (new CreateExcelReport().generateAttndReport(fileName, context,"DEFAULT")) {
                            Toast.makeText(getApplicationContext(), "Report Generated : " + fileName, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Report Not Generated", Toast.LENGTH_LONG).show();
                        }


                        dialog.cancel();
                    }
                }); //End of alert.setNegativeButton
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                /* Alert Dialog Code End*/

            }


        }

        if (id == R.id.admin_overall_attnd_status_refresh) {
            getAdminAllSchoolsDetails();
        }


        return super.onOptionsItemSelected(item);
    }

    /*
      Read school basic info
     */

    private void readSchoolBasicInfo(String schoolID){

        showProgressDialog();

        // app_title change listener
        mFirebaseDatabase.child("UserNode").child(schoolID).child("School_Basic_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserBasicDetails userDetails = dataSnapshot.getValue(UserBasicDetails.class);

                if(userDetails != null) {

                    // Update Each school wise details
                    String eachSchool = "EACHSCHOOL@" + userDetails.getId() + "#" + userDetails.getName() + "#" + userDetails.getDistt() + "#" + userDetails.getSchool_emailID() + "&" + "NS" + "&" + "NS" + "&" + "NS";
                    logger.addDataIntoFile("dataCal.txt", eachSchool, getApplicationContext());

                }

//                } else {
//                    // Update Each school wise details
//                    String eachSchool = "EACHSCHOOL@"+eachSchoolID+"&"+"NS"+"&"+"NS"+"&"+"NS";
//                    logger.addDataIntoFile("dataCal.txt",eachSchool,getApplicationContext());
//
//                }



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
      Progress bar
     */

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



}
