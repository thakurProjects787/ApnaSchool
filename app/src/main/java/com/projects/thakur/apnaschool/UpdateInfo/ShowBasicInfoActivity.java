package com.projects.thakur.apnaschool.UpdateInfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;

public class ShowBasicInfoActivity extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgressDialog;

    // get current user details
    UserBasicDetails currentUserDetails;

    // Read parent activity message and set school firbase ID
    private String school_firbasedataID,parentAtivityMsg;

    // Textviews

    private TextView txtv_school_id_value,txtv_school_name_value,txtv_school_type_value,txtv_address_value,txtv_school_place_name_value;
    private TextView txtv_school_distt_value,txtv_school_state_value,txtv_school_pincode_value,txtv_school_contact_value,txtv_school_emailid_value;
    private TextView txtv_total_students_value,txtv_total_teachers_value,txtv_total_admin_value,txtv_total_service_staff_value,txtv_school_desc_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_basic_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_basic_infor_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Basic Details");

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

        // TextView
        txtv_school_id_value=(TextView) findViewById(R.id.txtv_school_id_value);
        txtv_school_name_value=(TextView) findViewById(R.id.txtv_school_name_value);
        txtv_school_type_value=(TextView) findViewById(R.id.txtv_school_type_value);

        txtv_address_value=(TextView) findViewById(R.id.txtv_address_value);
        txtv_school_place_name_value=(TextView) findViewById(R.id.txtv_school_place_name_value);
        txtv_school_distt_value=(TextView) findViewById(R.id.txtv_school_distt_value);
        txtv_school_state_value=(TextView) findViewById(R.id.txtv_school_state_value);
        txtv_school_pincode_value=(TextView) findViewById(R.id.txtv_school_pincode_value);

        txtv_school_contact_value=(TextView) findViewById(R.id.txtv_school_contact_value);
        txtv_school_emailid_value=(TextView) findViewById(R.id.txtv_school_emailid_value);

        txtv_total_students_value=(TextView) findViewById(R.id.txtv_total_students_value);
        txtv_total_teachers_value=(TextView) findViewById(R.id.txtv_total_teachers_value);
        txtv_total_admin_value=(TextView) findViewById(R.id.txtv_total_admin_value);
        txtv_total_service_staff_value=(TextView) findViewById(R.id.txtv_total_service_staff_value);

        txtv_school_desc_value=(TextView) findViewById(R.id.txtv_school_desc_value);

        currentUserDetails = new UserBasicDetails();

        // get parent activity status
        parentAtivityMsg = getIntent().getStringExtra("EXTRA_SHOW_EACH_SCHOOL_SESSION_ID");
        if(parentAtivityMsg.equals("OWNER")){
            // Pass orignal ID
            school_firbasedataID = mAuth.getCurrentUser().getUid();
        } else {
            // Pass school ID
            school_firbasedataID = parentAtivityMsg;
        }

        /*
           Read user current basic details
         */

        readUserCurrentData();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.edit_info) {
            if(parentAtivityMsg.equals("OWNER")) {
                Intent intent = new Intent(ShowBasicInfoActivity.this, UpdateBasicInfoActivity.class);
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
       Read data from firebase database
     */
    private void readUserCurrentData(){

        showProgressDialog();

        // app_title change listener
        mDatabase.child("UserNode").child(school_firbasedataID).child("School_Basic_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentUserDetails = dataSnapshot.getValue(UserBasicDetails.class);

                // display user details
                txtv_school_id_value.setText(currentUserDetails.getId());
                txtv_school_name_value.setText(currentUserDetails.getName());
                txtv_school_type_value.setText(currentUserDetails.getType());

                txtv_address_value.setText(currentUserDetails.getComplete_address());
                txtv_school_place_name_value.setText(currentUserDetails.getPlace_name());
                txtv_school_distt_value.setText(currentUserDetails.getDistt());
                txtv_school_state_value.setText(currentUserDetails.getState());
                txtv_school_pincode_value.setText(currentUserDetails.getPin_code());

                txtv_school_contact_value.setText(currentUserDetails.getContact_details());
                txtv_school_emailid_value.setText(currentUserDetails.getSchool_emailID());

                txtv_total_students_value.setText(currentUserDetails.getTotal_student());
                txtv_total_teachers_value.setText(currentUserDetails.getTotal_teacher());
                txtv_total_admin_value.setText(currentUserDetails.getTotal_admin_staff());
                txtv_total_service_staff_value.setText(currentUserDetails.getTotal_service_staff());

                txtv_school_desc_value.setText(currentUserDetails.getSchool_description());

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
