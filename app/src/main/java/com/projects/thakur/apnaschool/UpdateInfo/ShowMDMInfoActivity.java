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

import com.projects.thakur.apnaschool.Model.MDMDetails;
import com.projects.thakur.apnaschool.R;

public class ShowMDMInfoActivity extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgressDialog;

    // get current user details
    MDMDetails mdmDetails;

    // Read parent activity message and set school firbase ID
    private String school_firbasedataID,parentAtivityMsg;

    // Textviews

    private TextView txtv_total_students_value,txtv_rice_stock_value,txtv_others_stock_value,txtv_mdm_diet_details_value;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_mdm_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_mdm_info_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("MDM Details");

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
        txtv_total_students_value=(TextView) findViewById(R.id.txtv_total_students_value);
        txtv_rice_stock_value=(TextView) findViewById(R.id.txtv_rice_stock_value);
        txtv_others_stock_value=(TextView) findViewById(R.id.txtv_others_stock_value);

        txtv_mdm_diet_details_value=(TextView) findViewById(R.id.txtv_mdm_diet_details_value);


        mdmDetails = new MDMDetails();

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
                Intent intent = new Intent(ShowMDMInfoActivity.this, UpdateMDMInfoActivity.class);
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
        mDatabase.child("UserNode").child(school_firbasedataID).child("MDM_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mdmDetails = dataSnapshot.getValue(MDMDetails.class);

                // display user details
                txtv_total_students_value.setText(mdmDetails.getTotal_students());
                txtv_rice_stock_value.setText(mdmDetails.getRice_stock());
                txtv_others_stock_value.setText(mdmDetails.getOther_stock());

                txtv_mdm_diet_details_value.setText(mdmDetails.getDiet_menu_details());


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
