package com.projects.thakur.apnaschool.Task.VirtualTask;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.projects.thakur.apnaschool.AdminUser.NewUserDetails;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.Common.Logger;
import com.projects.thakur.apnaschool.Model.AchivmentsDetails;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class CreateNewVirtualTaskActivity extends AppCompatActivity implements View.OnClickListener {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgressDialog;

    private Button btn_add_new_virtual_task,btn_virtual_task_set_date;

    private EditText edtxt_virtual_task_heading,edtxt_virtual_task_Submittion_date,edtxt_virtual_task_details;

    private String virtual_task_heading,virtual_task_Submittion_date,virtual_task_details;

    // ===== Date Section ============
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    //Get value from parent activity
    private String allSchoolsNames = "";

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_virtual_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.admin_create_new_virtual_task_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Virtual Task");

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

        //edit text
        edtxt_virtual_task_heading = (EditText) findViewById(R.id.edtxt_virtual_task_heading);
        edtxt_virtual_task_Submittion_date = (EditText) findViewById(R.id.edtxt_virtual_task_Submittion_date);
        edtxt_virtual_task_details = (EditText) findViewById(R.id.edtxt_virtual_task_details);

        btn_add_new_virtual_task = (Button) findViewById(R.id.btn_add_new_virtual_task);
        btn_add_new_virtual_task.setOnClickListener(this);

        btn_virtual_task_set_date = (Button) findViewById(R.id.btn_virtual_task_set_date);
        btn_virtual_task_set_date.setOnClickListener(this);


        getAllSchoolDetails();


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
            case R.id.btn_add_new_virtual_task:
                readAndSAVE();
                break;

            case R.id.btn_virtual_task_set_date:
                setDate();
                break;


        }
    }


    /*
       Read Inputs and then validate then save into firbase database.
     */

    private void readAndSAVE(){

        showProgressDialog();

        boolean vaidation_status = true;

        virtual_task_heading = edtxt_virtual_task_heading.getText().toString();
        if (TextUtils.isEmpty(virtual_task_heading)){
            edtxt_virtual_task_heading.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_virtual_task_heading.setError(null);
        }

        virtual_task_Submittion_date = edtxt_virtual_task_Submittion_date.getText().toString();
        if (TextUtils.isEmpty(virtual_task_Submittion_date)){
            edtxt_virtual_task_Submittion_date.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_virtual_task_Submittion_date.setError(null);
        }



        virtual_task_details = edtxt_virtual_task_details.getText().toString();
        if(virtual_task_details.isEmpty()){
            vaidation_status = false;
            edtxt_virtual_task_details.setText("Please write Details!!");
        }

        // if all vaidation is true then add all details into firbase database
        if(vaidation_status){

            //create  Details model
            NewVirtualTaskModel addNewVirtualTask = new NewVirtualTaskModel();

            addNewVirtualTask.setTask_heading(virtual_task_heading);
            addNewVirtualTask.setTask_last_date(virtual_task_Submittion_date);
            addNewVirtualTask.setTask_details(virtual_task_details);

            addNewVirtualTask.setTask_all_schoolsNames(allSchoolsNames);
            addNewVirtualTask.setTask_submittedSchoolNames("NA");

            addNewVirtualTask.setTask_user_firbase_ID(StartUpActivity.userDetails.getSchool_firbaseDataID());
            addNewVirtualTask.setTask_user_createdby(StartUpActivity.userDetails.getName());

            //***************** Notification Status **********************
            //create  Details model
            AchivmentsDetails addNewNotif = new AchivmentsDetails();

            // Get current date
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");

            String senderDetails = StartUpActivity.userDetails.getName()+"  "+mdformat.format(calendar.getTime());

            addNewNotif.setAchv_titles("New Task Created");
            addNewNotif.setAchv_date(senderDetails);
            addNewNotif.setAchv_details(virtual_task_heading);

            // ***********************************************************

            if(mAuth.getCurrentUser()!=null)
            {
                //CHECK IF new achivments has been added or update process.
                String achiv_key;


                // Get new push key
                achiv_key = mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("TASK").child("OPEN").child("VIRTUAL").push().getKey();
                addNewVirtualTask.setTask_firbase_ID(achiv_key);


                // save the details
                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("TASK").child("OPEN").child("VIRTUAL").child(achiv_key).setValue(addNewVirtualTask, new DatabaseReference.CompletionListener() {
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


                    // ********** Add notification status *******************
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

                    // **********************************************************



                }

                hideProgressDialog();

                Toast.makeText(CreateNewVirtualTaskActivity.this, "New Virtual Task Created !!",Toast.LENGTH_SHORT).show();

                finish();

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

                allSchoolsNames = allSchoolsNames + schoolDetails.getSchool_firbaseDataID()+ "@" + schoolDetails.getName() + "#";

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
        edtxt_virtual_task_Submittion_date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    // ==================================================
}
