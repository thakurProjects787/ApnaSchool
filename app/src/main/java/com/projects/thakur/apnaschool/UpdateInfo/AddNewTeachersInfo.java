package com.projects.thakur.apnaschool.UpdateInfo;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
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
import com.projects.thakur.apnaschool.Model.TeachersDetails;
import com.projects.thakur.apnaschool.R;

import java.util.Calendar;

public class AddNewTeachersInfo extends AppCompatActivity implements View.OnClickListener {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Spinner staticSpinner;
    private ProgressDialog mProgressDialog;
    private Button btn_add_new_teacher,btn_delete_current_teacher,btn_set_date;

    // ===== Date Section ============
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    //Edit text
    private EditText edtxt_teacher_ID,edtxt_teacher_name,edtxt_teacher_join_date,edtxt_teacher_edu_details,edtxt_teacher_special_areas;
    private String teacher_ID,teacher_Name,teacher_designation,teacher_joiningDate,teacher_edudetails,teacher_special_areas;

    //Get value from parent activity
    private String operationStatus;
    private boolean deletionProcess = false;

    // Teacher model obj
    TeachersDetails currentTeacherData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_teachers_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_new_teachers_info_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("New Teacher Details");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        operationStatus = getIntent().getStringExtra("EXTRA_TEACHER_INFO_SESSION_ID");

        // Firbase database access
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //spinner class names
        staticSpinner = (Spinner) findViewById(R.id.sp_teacher_designation);
        getTeacherDesignation();

        //edit text
        edtxt_teacher_ID = (EditText) findViewById(R.id.edtxt_teacher_ID);
        edtxt_teacher_name = (EditText) findViewById(R.id.edtxt_teacher_name);
        edtxt_teacher_join_date = (EditText) findViewById(R.id.edtxt_teacher_join_date);
        edtxt_teacher_edu_details = (EditText) findViewById(R.id.edtxt_teacher_edu_details);
        edtxt_teacher_special_areas = (EditText) findViewById(R.id.edtxt_teacher_special_areas);

        btn_add_new_teacher = (Button) findViewById(R.id.btn_add_new_teacher);
        btn_add_new_teacher.setOnClickListener(this);

        btn_delete_current_teacher = (Button) findViewById(R.id.btn_delete_current_teacher);
        btn_delete_current_teacher.setOnClickListener(this);

        btn_set_date = (Button) findViewById(R.id.btn_set_date);
        btn_set_date.setOnClickListener(this);

        // Get Datepicker objects
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        // change view according to operationStatus
        if(operationStatus.equals("ADD_NEW")){
            btn_delete_current_teacher.setVisibility(View.GONE);

        } else{
            btn_add_new_teacher.setText("UPDATE");
            edtxt_teacher_ID.setEnabled(false);

            readCurrentData();
        }
    } // end of onCreate

    // Read value from class names drop down
    private void getTeacherDesignation(){

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.teacher_designation,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                teacher_designation = parent.getItemAtPosition(position).toString();
                //Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

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
            case R.id.btn_add_new_teacher:
                readAndSAVE();
                break;

            case R.id.btn_delete_current_teacher:

                //====================== ASK DIALOG BOX ========================================
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
                alertDialogBuilder.setTitle("DELETE");
                alertDialogBuilder.setMessage("Are you sure,You want to Delete Current Teacher Details ?");

                alertDialogBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteCurrentTeacher();

                        if (StartUpActivity.userDetails.getType().equals("State")) {
                            Intent intent = new Intent(AddNewTeachersInfo.this, ShowDisttAdminUserDetails.class);
                            intent.putExtra("EXTRA_SHOW_SCHOOL_SESSION_ID", "OWNER");
                            startActivity(intent);

                        } else if (StartUpActivity.userDetails.getType().equals("Admin")) {
                            Intent intent = new Intent(AddNewTeachersInfo.this, ShowDisttAdminUserDetails.class);
                            intent.putExtra("EXTRA_SHOW_SCHOOL_SESSION_ID", "OWNER");
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(AddNewTeachersInfo.this, ShowEachSchoolDetails.class);
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

            case R.id.btn_set_date:
                setDate();
                break;

        }
    }

    /*
       Read Inputs and then validate then save into firbase database.
     */

    private void readAndSAVE(){
        boolean vaidation_status = true;

        teacher_ID = edtxt_teacher_ID.getText().toString();
        if (TextUtils.isEmpty(teacher_ID)){
            edtxt_teacher_ID.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_teacher_ID.setError(null);
        }

        teacher_Name = edtxt_teacher_name.getText().toString();
        if (TextUtils.isEmpty(teacher_Name)){
            edtxt_teacher_name.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_teacher_name.setError(null);
        }

        teacher_joiningDate = edtxt_teacher_join_date.getText().toString();
        if (TextUtils.isEmpty(teacher_joiningDate)){
            edtxt_teacher_join_date.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_teacher_join_date.setError(null);
        }

        getTeacherDesignation();

        teacher_edudetails = edtxt_teacher_edu_details.getText().toString();
        if(teacher_edudetails.isEmpty()){
            vaidation_status = false;
            edtxt_teacher_edu_details.setText("Please write here!!");
        }

        teacher_special_areas = edtxt_teacher_special_areas.getText().toString();
        if(teacher_special_areas.isEmpty()){
            vaidation_status = false;
            edtxt_teacher_special_areas.setText("Please write here!!");
        }


        // if all vaidation is true then add all details into firbase database
        if(vaidation_status){

            //create ClassDetails model
            TeachersDetails addNewTeacher = new TeachersDetails();

            addNewTeacher.setId(teacher_ID);
            addNewTeacher.setName(teacher_Name);
            addNewTeacher.setDesignation(teacher_designation);
            addNewTeacher.setJoin_date(teacher_joiningDate);
            addNewTeacher.setEducation_details(teacher_edudetails);
            addNewTeacher.setSpecial_areas(teacher_special_areas);

            showProgressDialog();

            if(mAuth.getCurrentUser()!=null)
            {
                // save the user at UserNode under user UID
                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Teachers_Info").child(teacher_ID).setValue(addNewTeacher, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        hideProgressDialog();

                        if(databaseError==null)
                        {
                            Toast.makeText(AddNewTeachersInfo.this, "Teacher Details has been saved !!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }



        }
    }

    /*
      Read data from firebase database
    */
    private void readCurrentData(){

        showProgressDialog();

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Teachers_Info").child(operationStatus).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!deletionProcess) {

                    currentTeacherData = dataSnapshot.getValue(TeachersDetails.class);

                    // display details
                    edtxt_teacher_ID.setText(currentTeacherData.getId());
                    edtxt_teacher_name.setText(currentTeacherData.getName());
                    edtxt_teacher_join_date.setText(currentTeacherData.getJoin_date());

                    edtxt_teacher_edu_details.setText(currentTeacherData.getEducation_details());
                    edtxt_teacher_special_areas.setText(currentTeacherData.getSpecial_areas());

                    setTeacherDes(currentTeacherData.getDesignation());

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

    // set teacher designation value
    private void setTeacherDes(String designation){

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.teacher_designation,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        int position = staticAdapter.getPosition(designation);
        staticSpinner.setSelection(position);
    }


    // Delete current Teacher details
    private void deleteCurrentTeacher(){


        showProgressDialog();

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Teachers_Info").child(operationStatus).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                deletionProcess = true;

                dataSnapshot.getRef().setValue(null);

                Toast.makeText(AddNewTeachersInfo.this, operationStatus+" Deleted!!",
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



    // =================================================
    // ---- Date Section -----
    @SuppressWarnings("deprecation")
    public void setDate() {
        showDialog(999);
        //Toast.makeText(getApplicationContext(), "ca",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        edtxt_teacher_join_date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    // ==================================================

}
