package com.projects.thakur.apnaschool.Task.QuestionTask;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.thakur.apnaschool.AdminUser.AdminHome;
import com.projects.thakur.apnaschool.AdminUser.NewUserDetails;
import com.projects.thakur.apnaschool.Common.Logger;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;

import java.util.ArrayList;
import java.util.Iterator;

public class AdminCreateQuestionTaskActivity extends AppCompatActivity implements View.OnClickListener{

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgressDialog;

    private Button btn_add_new_question_task,btn_delete_current_question_task;

    private EditText edtxt_question_task_heading,edtxt_question_task_Submittion_date,edtxt_question_task_details;

    private String question_task_heading,question_task_Submittion_date,question_task_details;

    //Get value from parent activity
    private String operationStatus;
    private boolean deletionProcess = false;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_question_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.admin_create_question_task_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Question Task");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        operationStatus = getIntent().getStringExtra("EXTRA_QUESTION_TASK_SESSION_ID");

        // Firbase database access
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //edit text
        edtxt_question_task_heading = (EditText) findViewById(R.id.edtxt_question_task_heading);
        edtxt_question_task_Submittion_date = (EditText) findViewById(R.id.edtxt_question_task_Submittion_date);
        edtxt_question_task_details = (EditText) findViewById(R.id.edtxt_question_task_details);

        btn_add_new_question_task = (Button) findViewById(R.id.btn_add_new_question_task);
        btn_add_new_question_task.setOnClickListener(this);

        btn_delete_current_question_task = (Button) findViewById(R.id.btn_delete_current_question_task);
        btn_delete_current_question_task.setOnClickListener(this);

        // change view according to operationStatus
        if(operationStatus.equals("ADD_NEW")){

            getAllSchoolDetails();

            btn_delete_current_question_task.setVisibility(View.GONE);

        } else{
            btn_add_new_question_task.setText("UPDATE");
            readCurrentData();
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
            case R.id.btn_add_new_question_task:
                readAndSAVE();
                break;

            case R.id.btn_delete_current_question_task:

                //====================== ASK DIALOG BOX ========================================
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
                alertDialogBuilder.setTitle("DELETE");
                alertDialogBuilder.setMessage("Are you sure,You want to Delete Question Task ?");

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
       Read Inputs and then validate then save into firbase database.
     */

    private void readAndSAVE(){

        showProgressDialog();

        boolean vaidation_status = true;

        question_task_heading = edtxt_question_task_heading.getText().toString();
        if (TextUtils.isEmpty(question_task_heading)){
            edtxt_question_task_heading.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_question_task_heading.setError(null);
        }

        question_task_Submittion_date = edtxt_question_task_Submittion_date.getText().toString();
        if (TextUtils.isEmpty(question_task_Submittion_date)){
            edtxt_question_task_Submittion_date.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_question_task_Submittion_date.setError(null);
        }



        question_task_details = edtxt_question_task_details.getText().toString();
        if(question_task_details.isEmpty()){
            vaidation_status = false;
            edtxt_question_task_details.setText("Please write Details!!");
        }

        // if all vaidation is true then add all details into firbase database
        if(vaidation_status){

            //create  Details model
            NewQuestionTaskModel addNewQuestionTask = new NewQuestionTaskModel();

            addNewQuestionTask.setTask_heading(question_task_heading);
            addNewQuestionTask.setTask_last_date(question_task_Submittion_date);
            addNewQuestionTask.setTask_details(question_task_details);
            addNewQuestionTask.setTask_stage("OPEN");

            if(mAuth.getCurrentUser()!=null)
            {
                //CHECK IF new achivments has been added or update process.
                String achiv_key;
                if(operationStatus.equals("ADD_NEW")) {

                    // Get new push key
                    achiv_key = mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("TASK").child("OPEN").child("QUESTION").push().getKey();
                    addNewQuestionTask.setTask_firbase_ID(achiv_key);

                } else {
                    achiv_key = operationStatus;
                    addNewQuestionTask.setTask_firbase_ID(achiv_key);
                }

                // save the details
                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("TASK").child("OPEN").child("QUESTION").child(achiv_key).setValue(addNewQuestionTask, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        //if(databaseError==null)
                        //{
                            //Toast.makeText(AdminCreateQuestionTaskActivity.this, "Your Details has been saved !!",Toast.LENGTH_SHORT).show();
                        //}
                    }
                });


                // Add all schools fields
                // get all schools details
                ArrayList<String> allSchoolsDetails = new Logger().getDataFromSchoolFile("SchoolsDetails.txt",context);

                Iterator<String> iterator = allSchoolsDetails.iterator();
                while (iterator.hasNext()) {
                    String details = iterator.next();

                    String schoolKey = details.split("#")[0];
                    String schoolDetails = details.split("#")[1];

                    QuestionResultModel schoolResult = new QuestionResultModel();
                    schoolResult.setSchoolDetails(schoolDetails);
                    schoolResult.setAnswer("NA");

                    // save the details
                    mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("TASK").child("OPEN").child("QUESTION").child(achiv_key).child("RESULT").child(schoolKey).setValue(schoolResult, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            //if(databaseError==null)
                            //{
                            //Toast.makeText(AdminCreateQuestionTaskActivity.this, "Your Details has been saved !!",Toast.LENGTH_SHORT).show();
                            //}
                        }
                    });



                }

                hideProgressDialog();

                Toast.makeText(AdminCreateQuestionTaskActivity.this, "New Question Task Created !!",Toast.LENGTH_SHORT).show();

                finish();

            }



        }
    }

    /*
      Read data from firebase database
    */
    private void readCurrentData(){

        showProgressDialog();

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("TASK").child("OPEN").child("QUESTION").child(operationStatus).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!deletionProcess) {

                    NewQuestionTaskModel questTaskDetails = dataSnapshot.getValue(NewQuestionTaskModel.class);

                    // display user details
                    edtxt_question_task_heading.setText(questTaskDetails.getTask_heading());
                    edtxt_question_task_Submittion_date.setText(questTaskDetails.getTask_last_date());
                    edtxt_question_task_details.setText(questTaskDetails.getTask_details());

                    hideProgressDialog();
                }

                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("TASK").child("OPEN").child("QUESTION").child(operationStatus).removeEventListener(this);
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
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("TASK").child("OPEN").child("QUESTION").child(operationStatus).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                deletionProcess = true;

                dataSnapshot.getRef().setValue(null);

                Toast.makeText(AdminCreateQuestionTaskActivity.this, operationStatus+" Deleted!!",
                        Toast.LENGTH_LONG).show();

                hideProgressDialog();

                Intent intent_1 = new Intent(AdminCreateQuestionTaskActivity.this, AdminHome.class);
                startActivity(intent_1);

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
