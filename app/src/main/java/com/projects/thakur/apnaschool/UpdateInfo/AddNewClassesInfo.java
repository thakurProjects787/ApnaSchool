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
import com.projects.thakur.apnaschool.AdminUser.ShowDisttAdminUserDetails;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.Model.ClassDetails;
import com.projects.thakur.apnaschool.R;

public class AddNewClassesInfo extends AppCompatActivity implements View.OnClickListener {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Spinner staticSpinner;
    private ProgressDialog mProgressDialog;
    private Button btn_addNewClass,btn_delete_current_class;

    private EditText edtxt_total_student,edtxt_class_agenda;

    private String class_name,class_total_studens,class_agenda;

    //Get value from parent activity
    private String operationStatus;
    private boolean deletionProcess = false;

    // class model obj
    ClassDetails currentClassData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_classes_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_new_class_info_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Add New Class");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        operationStatus = getIntent().getStringExtra("EXTRA_CLASS_INFO_SESSION_ID");

        // Firbase database access
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //spinner class names
        staticSpinner = (Spinner) findViewById(R.id.sp_class_name);
        getClassName();

        //edit text
        edtxt_total_student = (EditText) findViewById(R.id.edtxt_total_student);
        edtxt_class_agenda = (EditText) findViewById(R.id.edtxt_class_agenda);

        btn_addNewClass = (Button) findViewById(R.id.btn_add_new_class);
        btn_addNewClass.setOnClickListener(this);

        btn_delete_current_class = (Button) findViewById(R.id.btn_delete_current_class);
        btn_delete_current_class.setOnClickListener(this);

        // change view according to operationStatus
        if(operationStatus.equals("ADD_NEW")){
            btn_delete_current_class.setVisibility(View.GONE);

        } else{
            btn_addNewClass.setText("UPDATE");
            staticSpinner.setEnabled(false);

            readCurrentClassData();
        }



    } // end of onCreate

    // Read value from class names drop down
    private void getClassName(){

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.classes_names,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                class_name = parent.getItemAtPosition(position).toString();
                //Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }

    /*
       Read Inputs and then validate then save into firbase database.
     */

    private void readAndSAVE(){
        boolean vaidation_status = true;

        class_total_studens = edtxt_total_student.getText().toString();
        if (TextUtils.isEmpty(class_total_studens)){
            edtxt_total_student.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_total_student.setError(null);
        }

        getClassName();

        class_agenda = edtxt_class_agenda.getText().toString();
        if(class_agenda.isEmpty()){
            vaidation_status = false;
            edtxt_class_agenda.setText("Please write your class agenda!!");
        }

        // if all vaidation is true then add all details into firbase database
        if(vaidation_status){

            //create ClassDetails model
            ClassDetails addNewClass = new ClassDetails();

            addNewClass.setClass_name(class_name);
            addNewClass.setTotal_students(class_total_studens);
            addNewClass.setClass_agenda(class_agenda);

            showProgressDialog();

            if(mAuth.getCurrentUser()!=null)
            {
                // save the user at UserNode under user UID
                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("School_Classes_Info").child(class_name).setValue(addNewClass, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        hideProgressDialog();

                        if(databaseError==null)
                        {
                            Toast.makeText(AddNewClassesInfo.this, "Your class Details has been saved !!",
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
            case R.id.btn_add_new_class:
                readAndSAVE();
                break;

            case R.id.btn_delete_current_class:

                //====================== ASK DIALOG BOX ========================================
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
                alertDialogBuilder.setTitle("SUBMIT");
                alertDialogBuilder.setMessage("Are you sure,You want to submit today MDM Details ?");

                alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        deleteCurrentClass();

                        if (StartUpActivity.userDetails.getType().equals("State")) {
                            Intent intent = new Intent(AddNewClassesInfo.this, ShowDisttAdminUserDetails.class);
                            intent.putExtra("EXTRA_SHOW_SCHOOL_SESSION_ID", "OWNER");
                            startActivity(intent);

                        } else if (StartUpActivity.userDetails.getType().equals("Admin")) {
                            Intent intent = new Intent(AddNewClassesInfo.this, ShowDisttAdminUserDetails.class);
                            intent.putExtra("EXTRA_SHOW_SCHOOL_SESSION_ID", "OWNER");
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(AddNewClassesInfo.this, ShowEachSchoolDetails.class);
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
    private void readCurrentClassData(){

        showProgressDialog();

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("School_Classes_Info").child(operationStatus).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!deletionProcess) {

                    currentClassData = dataSnapshot.getValue(ClassDetails.class);

                    // display user details
                    edtxt_total_student.setText(currentClassData.getTotal_students());
                    edtxt_class_agenda.setText(currentClassData.getClass_agenda());

                    setClassName(currentClassData.getClass_name());

                    hideProgressDialog();
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

    // set class name value
    private void setClassName(String className){

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.classes_names,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        int position = staticAdapter.getPosition(className);
        staticSpinner.setSelection(position);
    }

    // Delete current Class details
    private void deleteCurrentClass(){


        showProgressDialog();

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("School_Classes_Info").child(operationStatus).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                deletionProcess = true;

                dataSnapshot.getRef().setValue(null);

                Toast.makeText(AddNewClassesInfo.this, operationStatus+" Deleted!!",
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
