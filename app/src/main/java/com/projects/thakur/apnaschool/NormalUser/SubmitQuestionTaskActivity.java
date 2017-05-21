package com.projects.thakur.apnaschool.NormalUser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.Task.QuestionTask.NewQuestionTaskModel;
import com.projects.thakur.apnaschool.Task.QuestionTask.QuestionResultModel;

public class SubmitQuestionTaskActivity extends AppCompatActivity implements View.OnClickListener{

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String questionTaskID;

    ProgressDialog mProgressDialog;

    private TextView txtv_question_task_heading_value,txtv_question_task_lastDate_value,txtv_question_task_details_value,txtv_question_task_school_details_value;
    private EditText edt_question_task_answer_value;

    private Button btn_question_task_answer_submit;

    private String question_answer_details;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_question_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.submit_question_task_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Task");

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

        questionTaskID = getIntent().getStringExtra("EXTRA_EACH_QUESTION_TASK_ANS_SESSION_ID");

        txtv_question_task_heading_value = (TextView) findViewById(R.id.txtv_question_task_heading_value);
        txtv_question_task_lastDate_value = (TextView) findViewById(R.id.txtv_question_task_lastDate_value);
        txtv_question_task_details_value = (TextView) findViewById(R.id.txtv_question_task_details_value);
        txtv_question_task_school_details_value = (TextView) findViewById(R.id.txtv_question_task_school_details_value);

        txtv_question_task_school_details_value.setVisibility(View.GONE);

        edt_question_task_answer_value = (EditText) findViewById(R.id.txtv_question_task_answer_value);

        btn_question_task_answer_submit = (Button) findViewById(R.id.btn_question_task_answer_submit);
        btn_question_task_answer_submit.setOnClickListener(this);

        getTaskDetails();

        getSchoolAnsDetails();

    }

    /*
       Get Task details
     */
    public void getTaskDetails() {

        showProgressDialog();

        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("adminUserID").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mDatabase.child("UserNode").child(dataSnapshot.getValue().toString()).child("TASK").child("OPEN").child("QUESTION").child(questionTaskID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            NewQuestionTaskModel taskDetails = dataSnapshot.getValue(NewQuestionTaskModel.class);

                            txtv_question_task_heading_value.setText(taskDetails.getTask_heading());
                            txtv_question_task_lastDate_value.setText(taskDetails.getTask_last_date());
                            txtv_question_task_details_value.setText(taskDetails.getTask_details());
                        }

                        //mDatabase.child("UserNode").child(dataSnapshot.getValue().toString()).child("TASK").child("OPEN").child("QUESTION").child(questionTaskID).removeEventListener(this);

                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        hideProgressDialog();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }

        });

    }

    /*
      Get current school details
     */
    public void getSchoolAnsDetails() {

        showProgressDialog();

        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("adminUserID").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mDatabase.child("UserNode").child(dataSnapshot.getValue().toString()).child("TASK").child("OPEN").child("QUESTION").child(questionTaskID).child("RESULT").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            QuestionResultModel taskDetails = dataSnapshot.getValue(QuestionResultModel.class);

                            edt_question_task_answer_value.setText(taskDetails.getAnswer());
                            txtv_question_task_school_details_value.setText(taskDetails.getSchoolDetails());

                        }

                        //mDatabase.child("UserNode").child(dataSnapshot.getValue().toString()).child("TASK").child("OPEN").child("QUESTION").child(questionTaskID).child("RESULT").child(mAuth.getCurrentUser().getUid()).removeEventListener(this);
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        hideProgressDialog();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }

        });

    }

    /*
       Read Inputs and then validate then save into firbase database.
     */

    private void readAndSAVE(){

        showProgressDialog();

        boolean vaidation_status = true;

        question_answer_details = edt_question_task_answer_value.getText().toString();
        if((question_answer_details.isEmpty() || (question_answer_details.equals("NA")))){
            vaidation_status = false;
            Toast.makeText(SubmitQuestionTaskActivity.this, "Please update answer field!!",Toast.LENGTH_SHORT).show();
            edt_question_task_answer_value.setText("NA");
        }

        // if all vaidation is true then add all details into firbase database
        if(vaidation_status){



            if(mAuth.getCurrentUser()!=null)
            {
                showProgressDialog();

                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("adminUserID").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //create  Details model
                        QuestionResultModel addNewQuestionAns = new QuestionResultModel();

                        addNewQuestionAns.setAnswer(edt_question_task_answer_value.getText().toString());
                        addNewQuestionAns.setSchoolDetails(txtv_question_task_school_details_value.getText().toString());

                        // save the details
                        mDatabase.child("UserNode").child(dataSnapshot.getValue().toString()).child("TASK").child("OPEN").child("QUESTION").child(questionTaskID).child("RESULT").child(mAuth.getCurrentUser().getUid()).setValue(addNewQuestionAns, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                Toast.makeText(SubmitQuestionTaskActivity.this, "Your Answer has been saved !!",Toast.LENGTH_SHORT).show();
                                //if(databaseError==null)
                                //{
                                //Toast.makeText(AdminCreateQuestionTaskActivity.this, "Your Details has been saved !!",Toast.LENGTH_SHORT).show();
                                //}
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        hideProgressDialog();
                    }

                });

            }



        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(SubmitQuestionTaskActivity.this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_question_task_answer_submit:

                //====================== ASK DIALOG BOX ========================================
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
                alertDialogBuilder.setTitle("SUBMIT");
                alertDialogBuilder.setMessage("Are you sure,You want to Submit Answer ?");

                alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        readAndSAVE();
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

}
