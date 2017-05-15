package com.projects.thakur.apnaschool.DailyStatus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import com.projects.thakur.apnaschool.Model.ClassDetails;
import com.projects.thakur.apnaschool.Model.DailyStudentAttendance;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class UpdateAttendenceStatus extends AppCompatActivity implements View.OnClickListener {


    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgressDialog;

    // all active classes names
    ArrayList<String> allClassess;

    //button
    private Button btn_update_school_daily_attendence;


    //edit text
    private EditText txtv_Class_I_present_students,txtv_Class_II_present_students,txtv_Class_III_present_students,txtv_Class_IV_present_students,txtv_Class_V_present_students;
    private EditText txtv_Class_VI_present_students,txtv_Class_VII_present_students,txtv_Class_VIII_present_students,txtv_Class_IX_present_students,txtv_Class_X_present_students;
    private EditText txtv_Class_XI_present_students,txtv_Class_XII_present_students;

    private EditText txtv_school_present_teachers;

    //Textview
    private TextView txtv_Class_I_name,txtv_Class_II_name,txtv_Class_III_name,txtv_Class_IV_name,txtv_Class_V_name;
    private TextView txtv_Class_VI_name,txtv_Class_VIII_name,txtv_Class_VII_name,txtv_Class_IX_name,txtv_Class_X_name,txtv_Class_XI_name,txtv_Class_XII_name;

    private TextView txtv_Class_I_total_students,txtv_Class_II_total_students,txtv_Class_III_total_students,txtv_Class_IV_total_students,txtv_Class_V_total_students;
    private TextView txtv_Class_VI_total_students,txtv_Class_VIII_total_students,txtv_Class_VII_total_students,txtv_Class_IX_total_students,txtv_Class_X_total_students,txtv_Class_XI_total_students,txtv_Class_XII_total_students;

    private TextView txtv_school_total_teachers;

    //Cardview
    private CardView Class_I_card_view,Class_II_card_view,Class_III_card_view,Class_IV_card_view,Class_V_card_view;
    private CardView Class_VI_card_view,Class_VIII_card_view,Class_VII_card_view,Class_IX_card_view,Class_X_card_view,Class_XI_card_view,Class_XII_card_view;

    //Store details
    private String teachers_details;

    private String all_classes_details;
    private String todaySubmitDate;
    private int total_students,total_presents,total_absent;

    //dialog box
    AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_attendence_status);

        Toolbar toolbar = (Toolbar) findViewById(R.id.update_attendence_status_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Submit Attendence");

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

        allClassess = new ArrayList<>();

        // set edit text properties and text view properties
        txtv_Class_I_present_students = (EditText) findViewById(R.id.txtv_Class_I_present_students);
        txtv_Class_II_present_students = (EditText) findViewById(R.id.txtv_Class_II_present_students);
        txtv_Class_III_present_students = (EditText) findViewById(R.id.txtv_Class_III_present_students);
        txtv_Class_IV_present_students = (EditText) findViewById(R.id.txtv_Class_IV_present_students);
        txtv_Class_V_present_students = (EditText) findViewById(R.id.txtv_Class_V_present_students);
        txtv_Class_VI_present_students = (EditText) findViewById(R.id.txtv_Class_VI_present_students);
        txtv_Class_VII_present_students = (EditText) findViewById(R.id.txtv_Class_VII_present_students);
        txtv_Class_VIII_present_students = (EditText) findViewById(R.id.txtv_Class_VIII_present_students);
        txtv_Class_IX_present_students = (EditText) findViewById(R.id.txtv_Class_IX_present_students);
        txtv_Class_X_present_students = (EditText) findViewById(R.id.txtv_Class_X_present_students);
        txtv_Class_XI_present_students = (EditText) findViewById(R.id.txtv_Class_XI_present_students);
        txtv_Class_XII_present_students = (EditText) findViewById(R.id.txtv_Class_XII_present_students);


        txtv_Class_I_name = (TextView) findViewById(R.id.txtv_Class_I_name);
        txtv_Class_II_name = (TextView) findViewById(R.id.txtv_Class_II_name);
        txtv_Class_III_name = (TextView) findViewById(R.id.txtv_Class_III_name);
        txtv_Class_IV_name = (TextView) findViewById(R.id.txtv_Class_IV_name);
        txtv_Class_V_name = (TextView) findViewById(R.id.txtv_Class_V_name);
        txtv_Class_VI_name = (TextView) findViewById(R.id.txtv_Class_VI_name);
        txtv_Class_VII_name = (TextView) findViewById(R.id.txtv_Class_VII_name);
        txtv_Class_VIII_name = (TextView) findViewById(R.id.txtv_Class_VIII_name);
        txtv_Class_IX_name = (TextView) findViewById(R.id.txtv_Class_IX_name);
        txtv_Class_X_name = (TextView) findViewById(R.id.txtv_Class_X_name);
        txtv_Class_XI_name = (TextView) findViewById(R.id.txtv_Class_XI_name);
        txtv_Class_XII_name = (TextView) findViewById(R.id.txtv_Class_XII_name);

        txtv_Class_I_total_students = (TextView) findViewById(R.id.txtv_Class_I_total_students);
        txtv_Class_II_total_students = (TextView) findViewById(R.id.txtv_Class_II_total_students);
        txtv_Class_III_total_students = (TextView) findViewById(R.id.txtv_Class_III_total_students);
        txtv_Class_IV_total_students = (TextView) findViewById(R.id.txtv_Class_IV_total_students);
        txtv_Class_V_total_students = (TextView) findViewById(R.id.txtv_Class_V_total_students);
        txtv_Class_VI_total_students = (TextView) findViewById(R.id.txtv_Class_VI_total_students);
        txtv_Class_VII_total_students = (TextView) findViewById(R.id.txtv_Class_VII_total_students);
        txtv_Class_VIII_total_students = (TextView) findViewById(R.id.txtv_Class_VIII_total_students);
        txtv_Class_IX_total_students = (TextView) findViewById(R.id.txtv_Class_IX_total_students);
        txtv_Class_X_total_students = (TextView) findViewById(R.id.txtv_Class_X_total_students);
        txtv_Class_XI_total_students = (TextView) findViewById(R.id.txtv_Class_XI_total_students);
        txtv_Class_XII_total_students = (TextView) findViewById(R.id.txtv_Class_XII_total_students);

        //card view
        Class_I_card_view = (CardView) findViewById(R.id.Class_I_card_view);
        Class_II_card_view = (CardView) findViewById(R.id.Class_II_card_view);
        Class_III_card_view = (CardView) findViewById(R.id.Class_III_card_view);
        Class_IV_card_view = (CardView) findViewById(R.id.Class_IV_card_view);
        Class_V_card_view = (CardView) findViewById(R.id.Class_V_card_view);
        Class_VI_card_view = (CardView) findViewById(R.id.Class_VI_card_view);
        Class_VII_card_view = (CardView) findViewById(R.id.Class_VII_card_view);
        Class_VIII_card_view = (CardView) findViewById(R.id.Class_VIII_card_view);
        Class_IX_card_view = (CardView) findViewById(R.id.Class_IX_card_view);
        Class_X_card_view = (CardView) findViewById(R.id.Class_X_card_view);
        Class_XI_card_view = (CardView) findViewById(R.id.Class_XI_card_view);
        Class_XII_card_view = (CardView) findViewById(R.id.Class_XII_card_view);

        //set all Card view gone
        Class_I_card_view.setVisibility(View.GONE);
        Class_II_card_view.setVisibility(View.GONE);
        Class_III_card_view.setVisibility(View.GONE);
        Class_IV_card_view.setVisibility(View.GONE);
        Class_V_card_view.setVisibility(View.GONE);
        Class_VI_card_view.setVisibility(View.GONE);
        Class_VII_card_view.setVisibility(View.GONE);
        Class_VIII_card_view.setVisibility(View.GONE);
        Class_IX_card_view.setVisibility(View.GONE);
        Class_X_card_view.setVisibility(View.GONE);
        Class_XI_card_view.setVisibility(View.GONE);
        Class_XII_card_view.setVisibility(View.GONE);

        //Teachers details
        txtv_school_total_teachers = (TextView) findViewById(R.id.txtv_school_total_teachers);
        txtv_school_present_teachers = (EditText) findViewById(R.id.txtv_school_present_teachers);


        btn_update_school_daily_attendence = (Button) findViewById(R.id.btn_update_school_daily_attendence);
        btn_update_school_daily_attendence.setOnClickListener(this);

        alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);

        //init value for all details
        total_students = 0;
        total_presents = 0;
        total_absent = 0;

        all_classes_details = "";

        //check wheather today attendence is filled or not
        readTodayAttendemceData();


        getClassDataFromServer();

        getTeachersDataFromServer();





    }

    // Read Data from firebase server
    private void getClassDataFromServer() {

        showProgressDialog();
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("School_Classes_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        ClassDetails newclass=postSnapShot.getValue(ClassDetails.class);

                        String classname = newclass.getClass_name().replace(" ","_");

                        enableCardView(classname,newclass.getClass_name(),newclass.getTotal_students());

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

    // Read Data from firebase server
    private void getTeachersDataFromServer() {

        showProgressDialog();
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Teachers_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())                {

                    String total_teachers = Long.toString(dataSnapshot.getChildrenCount());

                    txtv_school_total_teachers.setText(total_teachers);

                }
                hideProgressDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    //enable card view according to class
    private void enableCardView(String className,String orignalClassName,String total_student){
        // add class details
        allClassess.add(orignalClassName);

        switch (className){
            case "Class_I":
                Class_I_card_view.setVisibility(View.VISIBLE);
                txtv_Class_I_name.setText(orignalClassName);
                txtv_Class_I_total_students.setText(total_student+" Students");
                break;
            case "Class_II":
                Class_II_card_view.setVisibility(View.VISIBLE);
                txtv_Class_II_name.setText(orignalClassName);
                txtv_Class_II_total_students.setText(total_student+" Students");
                break;
            case "Class_III":
                Class_III_card_view.setVisibility(View.VISIBLE);
                txtv_Class_III_name.setText(orignalClassName);
                txtv_Class_III_total_students.setText(total_student+" Students");
                break;
            case "Class_IV":
                Class_IV_card_view.setVisibility(View.VISIBLE);
                txtv_Class_IV_name.setText(orignalClassName);
                txtv_Class_IV_total_students.setText(total_student+" Students");
                break;
            case "Class_V":
                Class_V_card_view.setVisibility(View.VISIBLE);
                txtv_Class_V_name.setText(orignalClassName);
                txtv_Class_V_total_students.setText(total_student+" Students");
                break;
            case "Class_VI":
                Class_VI_card_view.setVisibility(View.VISIBLE);
                txtv_Class_VI_name.setText(orignalClassName);
                txtv_Class_VI_total_students.setText(total_student+" Students");
                break;
            case "Class_VII":
                Class_VII_card_view.setVisibility(View.VISIBLE);
                txtv_Class_VII_name.setText(orignalClassName);
                txtv_Class_VII_total_students.setText(total_student+" Students");
                break;
            case "Class_VIII":
                Class_VIII_card_view.setVisibility(View.VISIBLE);
                txtv_Class_VIII_name.setText(orignalClassName);
                txtv_Class_VIII_total_students.setText(total_student+" Students");
                break;
            case "Class_IX":
                Class_IX_card_view.setVisibility(View.VISIBLE);
                txtv_Class_IX_name.setText(orignalClassName);
                txtv_Class_IX_total_students.setText(total_student+" Students");
                break;
            case "Class_X":
                Class_X_card_view.setVisibility(View.VISIBLE);
                txtv_Class_X_name.setText(orignalClassName);
                txtv_Class_X_total_students.setText(total_student+" Students");
                break;
            case "Class_XI":
                Class_XI_card_view.setVisibility(View.VISIBLE);
                txtv_Class_XI_name.setText(orignalClassName);
                txtv_Class_XI_total_students.setText(total_student+" Students");
                break;
            case "Class_XII":
                Class_XII_card_view.setVisibility(View.VISIBLE);
                txtv_Class_XII_name.setText(orignalClassName);
                txtv_Class_XII_total_students.setText(total_student+" Students");
                break;

        }
    }

    //Validate data
    private boolean validateClassData(String className){

        String presents;

        boolean vaidation_status = true;


        switch (className){
            case "Class I":

                //class_I_details
                // Present status
                presents = txtv_Class_I_present_students.getText().toString();
                if (TextUtils.isEmpty(presents)){
                    txtv_Class_I_present_students.setError("Please Fill !!");
                    vaidation_status = false;
                } else {
                    txtv_Class_I_present_students.setError(null);
                }


                if(vaidation_status){

                    //check for total absent
                    int totalStudents = Integer.parseInt(txtv_Class_I_total_students.getText().toString().split(" ")[0]);
                    int absents =   totalStudents - Integer.parseInt(presents);
					
					// check total condition
					if(Integer.parseInt(presents) > totalStudents){
						txtv_Class_I_present_students.setError("Wrong Entry !!");
						absents = 0;
						vaidation_status = false;
					} else {
						txtv_Class_I_present_students.setError(null);

                        total_students = total_students + totalStudents;
                        total_presents = total_presents + Integer.parseInt(presents);
                        total_absent = total_absent + absents;

                        all_classes_details = all_classes_details + className+"#"+presents+"#"+absents + "%";
					}


                }

                break;
            case "Class II":

                // Present status
                presents = txtv_Class_II_present_students.getText().toString();
                if (TextUtils.isEmpty(presents)){
                    txtv_Class_II_present_students.setError("Please Fill !!");
                    vaidation_status = false;
                } else {
                    txtv_Class_II_present_students.setError(null);
                }


                if(vaidation_status){

                    //check for total
                    int totalStudents = Integer.parseInt(txtv_Class_II_total_students.getText().toString().split(" ")[0]);
                    int absents =   totalStudents - Integer.parseInt(presents);
					
					// check total condition
					if(Integer.parseInt(presents) > totalStudents){
						txtv_Class_II_present_students.setError("Wrong Entry !!");
						absents = 0;
						vaidation_status = false;
					} else {
						txtv_Class_II_present_students.setError(null);

                        total_students = total_students + totalStudents;
                        total_presents = total_presents + Integer.parseInt(presents);
                        total_absent = total_absent + absents;

                        all_classes_details = all_classes_details + className+"#"+presents+"#"+absents + "%";
					}


                }

                break;

            case "Class III":

                // Present status
                presents = txtv_Class_III_present_students.getText().toString();
                if (TextUtils.isEmpty(presents)){
                    txtv_Class_III_present_students.setError("Please Fill !!");
                    vaidation_status = false;
                } else {
                    txtv_Class_III_present_students.setError(null);
                }



                if(vaidation_status){

                    //check for total
                    int totalStudents = Integer.parseInt(txtv_Class_III_total_students.getText().toString().split(" ")[0]);
                    int absents =   totalStudents - Integer.parseInt(presents);
					
					// check total condition
					if(Integer.parseInt(presents) > totalStudents){
						txtv_Class_III_present_students.setError("Wrong Entry !!");
						absents = 0;
						vaidation_status = false;
					} else {
						txtv_Class_III_present_students.setError(null);

                        total_students = total_students + totalStudents;
                        total_presents = total_presents + Integer.parseInt(presents);
                        total_absent = total_absent + absents;

                        all_classes_details = all_classes_details + className+"#"+presents+"#"+absents + "%";
					}

                }

                break;
            case "Class IV":

                // Present status
                presents = txtv_Class_IV_present_students.getText().toString();
                if (TextUtils.isEmpty(presents)){
                    txtv_Class_IV_present_students.setError("Please Fill !!");
                    vaidation_status = false;
                } else {
                    txtv_Class_IV_present_students.setError(null);
                }

                if(vaidation_status){

                    //check for total
                    int totalStudents = Integer.parseInt(txtv_Class_IV_total_students.getText().toString().split(" ")[0]);
                    int absents =   totalStudents - Integer.parseInt(presents);
					
					// check total condition
					if(Integer.parseInt(presents) > totalStudents){
						txtv_Class_IV_present_students.setError("Wrong Entry !!");
						absents = 0;
						vaidation_status = false;
					} else {
						txtv_Class_IV_present_students.setError(null);

                        total_students = total_students + totalStudents;
                        total_presents = total_presents + Integer.parseInt(presents);
                        total_absent = total_absent + absents;

                        all_classes_details = all_classes_details + className+"#"+presents+"#"+absents + "%";
					}

                }

                break;
            case "Class V":

                // Present status
                presents = txtv_Class_V_present_students.getText().toString();
                if (TextUtils.isEmpty(presents)){
                    txtv_Class_V_present_students.setError("Please Fill !!");
                    vaidation_status = false;
                } else {
                    txtv_Class_V_present_students.setError(null);
                }


                if(vaidation_status){

                    //check for total
                    int totalStudents = Integer.parseInt(txtv_Class_V_total_students.getText().toString().split(" ")[0]);
                    int absents =   totalStudents - Integer.parseInt(presents);
					
					// check total condition
					if(Integer.parseInt(presents) > totalStudents){
						txtv_Class_V_present_students.setError("Wrong Entry !!");
						absents = 0;
						vaidation_status = false;
					} else {
						txtv_Class_V_present_students.setError(null);

                        total_students = total_students + totalStudents;
                        total_presents = total_presents + Integer.parseInt(presents);
                        total_absent = total_absent + absents;

                        all_classes_details = all_classes_details + className+"#"+presents+"#"+absents + "%";
					}

                }

                break;
            case "Class VI":

                // Present status
                presents = txtv_Class_VI_present_students.getText().toString();
                if (TextUtils.isEmpty(presents)){
                    txtv_Class_VI_present_students.setError("Please Fill !!");
                    vaidation_status = false;
                } else {
                    txtv_Class_VI_present_students.setError(null);
                }


                if(vaidation_status){

                    //check for total
                    int totalStudents = Integer.parseInt(txtv_Class_VI_total_students.getText().toString().split(" ")[0]);
                    int absents =   totalStudents - Integer.parseInt(presents);
					
					// check total condition
					if(Integer.parseInt(presents) > totalStudents){
						txtv_Class_VI_present_students.setError("Wrong Entry !!");
						absents = 0;
						vaidation_status = false;
					} else {
						txtv_Class_VI_present_students.setError(null);

                        total_students = total_students + totalStudents;
                        total_presents = total_presents + Integer.parseInt(presents);
                        total_absent = total_absent + absents;

                        all_classes_details = all_classes_details + className+"#"+presents+"#"+absents + "%";
					}

                }

                break;
            case "Class VII":

                // Present status
                presents = txtv_Class_VII_present_students.getText().toString();
                if (TextUtils.isEmpty(presents)){
                    txtv_Class_VII_present_students.setError("Please Fill !!");
                    vaidation_status = false;
                } else {
                    txtv_Class_VII_present_students.setError(null);
                }

                if(vaidation_status){

                    //check for total
                    int totalStudents = Integer.parseInt(txtv_Class_VII_total_students.getText().toString().split(" ")[0]);
                    int absents =   totalStudents - Integer.parseInt(presents);
					
					// check total condition
					if(Integer.parseInt(presents) > totalStudents){
						txtv_Class_VII_present_students.setError("Wrong Entry !!");
						absents = 0;
						vaidation_status = false;
					} else {
						txtv_Class_VII_present_students.setError(null);

                        total_students = total_students + totalStudents;
                        total_presents = total_presents + Integer.parseInt(presents);
                        total_absent = total_absent + absents;

                        all_classes_details = all_classes_details + className+"#"+presents+"#"+absents + "%";
					}

                }

                break;
            case "Class VIII":

                // Present status
                presents = txtv_Class_VIII_present_students.getText().toString();
                if (TextUtils.isEmpty(presents)){
                    txtv_Class_VIII_present_students.setError("Please Fill !!");
                    vaidation_status = false;
                } else {
                    txtv_Class_VIII_present_students.setError(null);
                }


                if(vaidation_status){

                    //check for total
                    int totalStudents = Integer.parseInt(txtv_Class_VIII_total_students.getText().toString().split(" ")[0]);
                    int absents =   totalStudents - Integer.parseInt(presents);
					
					// check total condition
					if(Integer.parseInt(presents) > totalStudents){
						txtv_Class_VIII_present_students.setError("Wrong Entry !!");
						absents = 0;
						vaidation_status = false;
					} else {
						txtv_Class_VIII_present_students.setError(null);

                        total_students = total_students + totalStudents;
                        total_presents = total_presents + Integer.parseInt(presents);
                        total_absent = total_absent + absents;

                        all_classes_details = all_classes_details + className+"#"+presents+"#"+absents + "%";
					}

                }

                break;
            case "Class IX":

                // Present status
                presents = txtv_Class_IX_present_students.getText().toString();
                if (TextUtils.isEmpty(presents)){
                    txtv_Class_IX_present_students.setError("Please Fill !!");
                    vaidation_status = false;
                } else {
                    txtv_Class_IX_present_students.setError(null);
                }


                if(vaidation_status){

                    //check for total
                    int totalStudents = Integer.parseInt(txtv_Class_IX_total_students.getText().toString().split(" ")[0]);
                    int absents =   totalStudents - Integer.parseInt(presents);
					
					// check total condition
					if(Integer.parseInt(presents) > totalStudents){
						txtv_Class_IX_present_students.setError("Wrong Entry !!");
						absents = 0;
						vaidation_status = false;
					} else {
						txtv_Class_IX_present_students.setError(null);

                        total_students = total_students + totalStudents;
                        total_presents = total_presents + Integer.parseInt(presents);
                        total_absent = total_absent + absents;

                        all_classes_details = all_classes_details + className+"#"+presents+"#"+absents + "%";
					}

                }

                break;
            case "Class X":

                // Present status
                presents = txtv_Class_X_present_students.getText().toString();
                if (TextUtils.isEmpty(presents)){
                    txtv_Class_X_present_students.setError("Please Fill !!");
                    vaidation_status = false;
                } else {
                    txtv_Class_X_present_students.setError(null);
                }

                if(vaidation_status){

                    //check for total
                    int totalStudents = Integer.parseInt(txtv_Class_X_total_students.getText().toString().split(" ")[0]);
                    int absents =   totalStudents - Integer.parseInt(presents);
					
					// check total condition
					if(Integer.parseInt(presents) > totalStudents){
						txtv_Class_X_present_students.setError("Wrong Entry !!");
						absents = 0;
						vaidation_status = false;
					} else {
						txtv_Class_X_present_students.setError(null);

                        total_students = total_students + totalStudents;
                        total_presents = total_presents + Integer.parseInt(presents);
                        total_absent = total_absent + absents;

                        all_classes_details = all_classes_details + className+"#"+presents+"#"+absents + "%";
					}

                }

                break;
            case "Class XI":

                // Present status
                presents = txtv_Class_XI_present_students.getText().toString();
                if (TextUtils.isEmpty(presents)){
                    txtv_Class_XI_present_students.setError("Please Fill !!");
                    vaidation_status = false;
                } else {
                    txtv_Class_XI_present_students.setError(null);
                }

                if(vaidation_status){

                    //check for total
                    int totalStudents = Integer.parseInt(txtv_Class_XI_total_students.getText().toString().split(" ")[0]);
                    int absents =   totalStudents - Integer.parseInt(presents);
					
					// check total condition
					if(Integer.parseInt(presents) > totalStudents){
						txtv_Class_XI_present_students.setError("Wrong Entry !!");
						absents = 0;
						vaidation_status = false;
					} else {
						txtv_Class_XI_present_students.setError(null);

                        total_students = total_students + totalStudents;
                        total_presents = total_presents + Integer.parseInt(presents);
                        total_absent = total_absent + absents;

                        all_classes_details = all_classes_details + className+"#"+presents+"#"+absents + "%";
					}

                }

                break;
            case "Class XII":

                // Present status
                presents = txtv_Class_XII_present_students.getText().toString();
                if (TextUtils.isEmpty(presents)){
                    txtv_Class_XII_present_students.setError("Please Fill !!");
                    vaidation_status = false;
                } else {
                    txtv_Class_XII_present_students.setError(null);
                }


                if(vaidation_status){

                    //check for total
                    int totalStudents = Integer.parseInt(txtv_Class_XII_total_students.getText().toString().split(" ")[0]);
                    int absents =   totalStudents - Integer.parseInt(presents);
					
					// check total condition
					if(Integer.parseInt(presents) > totalStudents){
						txtv_Class_XII_present_students.setError("Wrong Entry !!");
						absents = 0;
						vaidation_status = false;
					} else {
						txtv_Class_XII_present_students.setError(null);

                        total_students = total_students + totalStudents;
                        total_presents = total_presents + Integer.parseInt(presents);
                        total_absent = total_absent + absents;

                        all_classes_details = all_classes_details + className+"#"+presents+"#"+absents + "%";
					}

                }

                break;

        }

        return vaidation_status;

    }

    private boolean validateTeacherData(){

        String presents;

        boolean vaidation_status = true;

        // Present status
        presents = txtv_school_present_teachers.getText().toString();
        if (TextUtils.isEmpty(presents)){
            txtv_school_present_teachers.setError("Please Fill !!");
            vaidation_status = false;
        } else {
            txtv_school_present_teachers.setError(null);
        }


        if(vaidation_status){

            //check for total
            int totalTeachers = Integer.parseInt(txtv_school_total_teachers.getText().toString());
            int absents =   totalTeachers - Integer.parseInt(presents);

            if(Integer.parseInt(presents) > totalTeachers){
                txtv_school_present_teachers.setError("Wrong Entry !!");
                absents = 0;
                vaidation_status = false;
            } else {
                txtv_school_present_teachers.setError(null);


                teachers_details =  totalTeachers+"#"+presents+"#"+absents;
            }

        }

        return vaidation_status;

    }

    //Geather all details
    private void gatherAllDetailsAndSubmit(){

        boolean overallStatus = true;

        // validate each class
        Iterator<String> iterator = allClassess.iterator();
        while (iterator.hasNext()) {
            String classname = iterator.next();

            if (!validateClassData(classname)){
                overallStatus = false;
            }

        }

        if(overallStatus && validateTeacherData()){

            //====================== ASK DIALOG BOX ========================================
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
            alertDialogBuilder.setTitle("SUBMIT");
            alertDialogBuilder.setMessage("Are you sure,You want to submit today Attendence Details ?");

            alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                    // combine all class details into one.

                    String calculatedDetails = Integer.toString(total_students)+"#"+Integer.toString(total_presents)+"#"+Integer.toString(total_absent);

                    DailyStudentAttendance todayAttendence = new DailyStudentAttendance();
                    todayAttendence.setCalculated_attnd(calculatedDetails);
                    todayAttendence.setAll_classes_attnd(all_classes_details);
                    todayAttendence.setAll_teachers_sttnd(teachers_details);

                    UserBasicDetails schoolDetails = new StartUpActivity().userDetails;
                    todayAttendence.setSchool_details(schoolDetails.getId()+"#"+schoolDetails.getName()+"#"+schoolDetails.getDistt()+"#"+schoolDetails.getSchool_emailID());

                    // Get current date
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy_MM_dd");

                    todaySubmitDate = mdformat.format(calendar.getTime());

                    showProgressDialog();

                    if(mAuth.getCurrentUser()!=null)
                    {
                        // save the user at UserNode under user UID
                        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Daily_Task").child("Attendence").child(todaySubmitDate).setValue(todayAttendence, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                hideProgressDialog();

                                if(databaseError==null)
                                {
                                    Toast.makeText(UpdateAttendenceStatus.this, "Your today attendence has been suubmitted !!",
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

        }// end of validation if section


    }//end of fcn


    /*
       Read data from firebase database
     */
    private void readTodayAttendemceData(){

        showProgressDialog();

        // Get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy_MM_dd");



        todaySubmitDate = mdformat.format(calendar.getTime());

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Daily_Task").child("Attendence").child(todaySubmitDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                DailyStudentAttendance todayAttendence = dataSnapshot.getValue(DailyStudentAttendance.class);

                if(todayAttendence != null) {

                    //====================== ASK DIALOG BOX ========================================
                    alertDialogBuilder.setTitle("MESSAGE");
                    alertDialogBuilder.setMessage("You have already sumitted today attendence.\n So you can't submit again !!");
                    alertDialogBuilder.setCancelable(false);

                    alertDialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // combine all class details into one.

                            finish();

                        }
                    });

//                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    //======================================================================================
                }
                   else {
                    Toast.makeText(getApplicationContext(), "Not Submitted", Toast.LENGTH_LONG).show();
                }

                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Daily_Task").child("Attendence").child(todaySubmitDate).removeEventListener(this);

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




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update_school_daily_attendence:
                gatherAllDetailsAndSubmit();
                break;

        }

    }



    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(UpdateAttendenceStatus.this);
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
