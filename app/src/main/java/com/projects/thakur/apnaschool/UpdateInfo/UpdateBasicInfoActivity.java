package com.projects.thakur.apnaschool.UpdateInfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;

public class UpdateBasicInfoActivity extends AppCompatActivity implements View.OnClickListener {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // intialize all edit text variables
    private EditText edtxt_id, edtxt_name,edtxt_cmp_address, edtxt_palce_name,edtxt_distt,edtxt_state,edtxt_pincode,edtxt_gps,edtxt_contacts;
    private EditText edtxt_tot_student,edtxt_tot_teacher,edtxt_tot_admins,edtxt_tot_serv_staff,edtxt_desc;

    private String school_id,school_name,school_type,school_cmp_address,school_place_name,school_distt,school_state,school_pincode,school_gps_loc,school_contacts;
    private String school_tot_students,school_tot_teachers,school_tot_admins,school_tot_service_staff,school_desc;
    private Spinner staticSpinner;

    private Button btn_getCurrentloc;

    private TrackGPS gps;
    private ProgressDialog mProgressDialog;

    // get current user details
    UserBasicDetails currentUserDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info_basic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.update_basic_infor_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Update Basic Info");

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
        edtxt_id = (EditText) findViewById(R.id.school_id);
        edtxt_name = (EditText) findViewById(R.id.school_name);

        edtxt_cmp_address = (EditText) findViewById(R.id.school_cmp_address);
        edtxt_palce_name = (EditText) findViewById(R.id.school_place_name);
        edtxt_distt = (EditText) findViewById(R.id.school_distt);

        edtxt_state = (EditText) findViewById(R.id.school_state);
        edtxt_state.setText(R.string.school_state_name);
        edtxt_state.setEnabled(false);


        edtxt_pincode = (EditText) findViewById(R.id.school_pincode);
        edtxt_gps = (EditText) findViewById(R.id.school_gps_location);

        edtxt_contacts = (EditText) findViewById(R.id.school_contact_details);

        edtxt_tot_student = (EditText) findViewById(R.id.school_total_student);
        edtxt_tot_teacher = (EditText) findViewById(R.id.school_total_teachers);
        edtxt_tot_admins = (EditText) findViewById(R.id.school_total_admin_staff);
        edtxt_tot_serv_staff = (EditText) findViewById(R.id.school_total_service_staff);

        edtxt_desc = (EditText) findViewById(R.id.school_description);

        btn_getCurrentloc = (Button) findViewById(R.id.btn_set_gps_location);
        btn_getCurrentloc.setOnClickListener(this);

        //spinner school type
         staticSpinner = (Spinner) findViewById(R.id.school_type);

        // set default spinner value
        getSchoolType();

        currentUserDetails = new UserBasicDetails();

        // Read user current Data
        readUserCurrentData();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.save_updated_info) {
            saveDetailsIntoDatabase();
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Read value from school drop down option
    private void getSchoolType(){

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.school_types,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        staticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                school_type = parent.getItemAtPosition(position).toString();
                //Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }

    // set school type value
    private void setSchoolType(String schoolType){

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.school_types,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        int position = staticAdapter.getPosition(schoolType);
        staticSpinner.setSelection(position);
    }

    /*
        Validate all inputs
     */

    private boolean vaidateInputs(){

        boolean vaidation_status = true;

        school_id = edtxt_id.getText().toString();
        if (TextUtils.isEmpty(school_id)){
            edtxt_id.setError("Please enter valid School ID !!");
            vaidation_status = false;
        } else {
            edtxt_id.setError(null);
        }

        school_name = edtxt_name.getText().toString();
        if (TextUtils.isEmpty(school_name)){
            edtxt_name.setError("Please enter valid School Name !!");
            vaidation_status = false;
        } else {
            edtxt_name.setError(null);
        }

        // Get school type value from drop down.
        getSchoolType();

        school_cmp_address = edtxt_cmp_address.getText().toString();


        school_place_name = edtxt_palce_name.getText().toString();
        if (TextUtils.isEmpty(school_place_name)){
            edtxt_palce_name.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_palce_name.setError(null);
        }

        school_distt = edtxt_distt.getText().toString();
        if (TextUtils.isEmpty(school_distt)){
            edtxt_distt.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_distt.setError(null);
        }

        school_state = edtxt_state.getText().toString();


        school_pincode = edtxt_pincode.getText().toString();
        if (TextUtils.isEmpty(school_pincode)){
            edtxt_pincode.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_pincode.setError(null);
        }

        school_gps_loc = edtxt_gps.getText().toString();



        school_contacts = edtxt_contacts.getText().toString();
        if (TextUtils.isEmpty(school_contacts)){
            edtxt_contacts.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_contacts.setError(null);
        }


        school_tot_students = edtxt_tot_student.getText().toString();
        if (TextUtils.isEmpty(school_tot_students)){
            edtxt_tot_student.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_tot_student.setError(null);
        }

        school_tot_teachers = edtxt_tot_teacher.getText().toString();
        if (TextUtils.isEmpty(school_tot_teachers)){
            edtxt_tot_teacher.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_tot_teacher.setError(null);
        }

        school_tot_admins = edtxt_tot_admins.getText().toString();
        if (TextUtils.isEmpty(school_tot_admins)){
            edtxt_tot_admins.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_tot_admins.setError(null);
        }


        school_tot_service_staff = edtxt_tot_serv_staff.getText().toString();
        if (TextUtils.isEmpty(school_tot_service_staff)){
            edtxt_tot_serv_staff.setError("Please enter valid details !!");
            vaidation_status = false;
        } else {
            edtxt_tot_serv_staff.setError(null);
        }



        school_desc = edtxt_desc.getText().toString();


        return vaidation_status;
    }

    /*
     Save details into Firbase Database
     */

    private void saveDetailsIntoDatabase(){

        // check validation
        if(vaidateInputs()){

            // create user object and set all the properties
            UserBasicDetails schoolBasicInfo = new UserBasicDetails();

            schoolBasicInfo.setId(school_id);
            schoolBasicInfo.setName(school_name);
            schoolBasicInfo.setType(school_type);

            schoolBasicInfo.setComplete_address(school_cmp_address);
            schoolBasicInfo.setPlace_name(school_place_name);
            schoolBasicInfo.setDistt(school_distt);
            schoolBasicInfo.setState(school_state);
            schoolBasicInfo.setPin_code(school_pincode);
            schoolBasicInfo.setGps_location(school_gps_loc);

            schoolBasicInfo.setContact_details(school_contacts);

            schoolBasicInfo.setTotal_student(school_tot_students);
            schoolBasicInfo.setTotal_teacher(school_tot_teachers);
            schoolBasicInfo.setTotal_admin_staff(school_tot_admins);
            schoolBasicInfo.setTotal_service_staff(school_tot_service_staff);

            schoolBasicInfo.setSchool_description(school_desc);

            schoolBasicInfo.setSchool_emailID(mAuth.getCurrentUser().getEmail());
            schoolBasicInfo.setSchool_firbaseDataID(mAuth.getCurrentUser().getUid());


            showProgressDialog();

            if(mAuth.getCurrentUser()!=null)
            {
                // save the user at UserNode under user UID
                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("School_Basic_Info").setValue(schoolBasicInfo, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        hideProgressDialog();

                        if(databaseError==null)
                        {
                            Toast.makeText(UpdateBasicInfoActivity.this, "Your School Details has been saved !!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        }// end of valid if


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_gps_location:

                gps = new TrackGPS(UpdateBasicInfoActivity.this);


                if(gps.canGetLocation()){


                    double longitude = gps.getLongitude();
                    double  latitude = gps .getLatitude();

                    Toast.makeText(getApplicationContext(),"Longitude:"+Double.toString(longitude)+"\nLatitude:"+Double.toString(latitude),Toast.LENGTH_SHORT).show();

                    edtxt_gps.setText(Double.toString(longitude)+","+Double.toString(latitude));

                }
                else
                {

                    gps.showSettingsAlert();
                }

                break;

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


    /*
       Read data from firebase database
     */
    private void readUserCurrentData(){

        showProgressDialog();

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("School_Basic_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentUserDetails = dataSnapshot.getValue(UserBasicDetails.class);

                // display user details
                edtxt_id.setText(currentUserDetails.getId());
                edtxt_name.setText(currentUserDetails.getName());
                setSchoolType(currentUserDetails.getType());

                edtxt_cmp_address.setText(currentUserDetails.getComplete_address());
                edtxt_palce_name.setText(currentUserDetails.getPlace_name());
                edtxt_distt.setText(currentUserDetails.getDistt());
                edtxt_state.setText(currentUserDetails.getState());
                edtxt_pincode.setText(currentUserDetails.getPin_code());
                edtxt_gps.setText(currentUserDetails.getGps_location());

                edtxt_contacts.setText(currentUserDetails.getContact_details());

                edtxt_tot_student.setText(currentUserDetails.getTotal_student());
                edtxt_tot_teacher.setText(currentUserDetails.getTotal_teacher());
                edtxt_tot_admins.setText(currentUserDetails.getTotal_admin_staff());
                edtxt_tot_serv_staff.setText(currentUserDetails.getTotal_service_staff());

                edtxt_desc.setText(currentUserDetails.getSchool_description());

                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to read app title value.", error.toException());

                hideProgressDialog();
            }
        });



    }


}
