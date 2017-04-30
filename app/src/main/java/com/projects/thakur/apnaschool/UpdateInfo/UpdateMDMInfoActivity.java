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

import com.projects.thakur.apnaschool.Model.MDMDetails;
import com.projects.thakur.apnaschool.R;

public class UpdateMDMInfoActivity extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // intialize all edit text variables
    private EditText edtxt_mdm_total_students,edtxt_mdm_rice_stock,edtxt_mdm_other_stock,edtxt_mdm_diet_details;

    private String mdm_total_students,mdm_rice_stock,mdm_other_stock,mdm_diet_plan_details;


    private ProgressDialog mProgressDialog;

    // get current user details
    MDMDetails mdmDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_mdm_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.update_mdm_info_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Update MDM Info");

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
        edtxt_mdm_total_students = (EditText) findViewById(R.id.edtxt_mdm_total_students);
        edtxt_mdm_rice_stock = (EditText) findViewById(R.id.edtxt_mdm_rice_stock);

        edtxt_mdm_other_stock = (EditText) findViewById(R.id.edtxt_mdm_other_stock);
        edtxt_mdm_diet_details = (EditText) findViewById(R.id.edtxt_mdm_diet_details);


        mdmDetails = new MDMDetails();

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



    /*
        Validate all inputs
     */

    private boolean vaidateInputs(){

        boolean vaidation_status = true;

        mdm_total_students = edtxt_mdm_total_students.getText().toString();
        if (TextUtils.isEmpty(mdm_total_students)){
            edtxt_mdm_total_students.setError("Please enter Details !!");
            vaidation_status = false;
        } else {
            edtxt_mdm_total_students.setError(null);
        }

        mdm_rice_stock = edtxt_mdm_rice_stock.getText().toString();
        if (TextUtils.isEmpty(mdm_rice_stock)){
            edtxt_mdm_rice_stock.setError("Please enter Details !!");
            vaidation_status = false;
        } else {
            edtxt_mdm_rice_stock.setError(null);
        }


        mdm_other_stock = edtxt_mdm_other_stock.getText().toString();
        if (TextUtils.isEmpty(mdm_other_stock)){
            edtxt_mdm_other_stock.setError("Please enter Details !!");
            vaidation_status = false;
        } else {
            edtxt_mdm_other_stock.setError(null);
        }

        mdm_diet_plan_details = edtxt_mdm_diet_details.getText().toString();



        return vaidation_status;
    }

    /*
     Save details into Firbase Database
     */

    private void saveDetailsIntoDatabase(){

        // check validation
        if(vaidateInputs()){

            // create user object and set all the properties
            MDMDetails mdmInfo = new MDMDetails();

            mdmInfo.setTotal_students(mdm_total_students);
            mdmInfo.setRice_stock(mdm_rice_stock);
            mdmInfo.setOther_stock(mdm_other_stock);
            mdmInfo.setDiet_menu_details(mdm_diet_plan_details);
            showProgressDialog();

            if(mAuth.getCurrentUser()!=null)
            {
                // save the user at UserNode under user UID
                mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("MDM_Info").setValue(mdmInfo, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        hideProgressDialog();

                        if(databaseError==null)
                        {
                            Toast.makeText(UpdateMDMInfoActivity.this, "Your Details has been saved !!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        }// end of valid if


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
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("MDM_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mdmDetails = dataSnapshot.getValue(MDMDetails.class);

                // display user details
                edtxt_mdm_total_students.setText(mdmDetails.getTotal_students());
                edtxt_mdm_rice_stock.setText(mdmDetails.getRice_stock());
                edtxt_mdm_other_stock.setText(mdmDetails.getOther_stock());

                edtxt_mdm_diet_details.setText(mdmDetails.getDiet_menu_details());


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
