package com.projects.thakur.apnaschool.Auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.projects.thakur.apnaschool.AdminUser.AdminHome;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.NormalUser.NormalUserActivity;
import com.projects.thakur.apnaschool.R;

public class StartUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ProgressBar progressBar;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mFirebaseDatabase;

    static public SharedPreferences mPrefs;

    private UserBasicDetails userDetails;

    //activity data
    private String activityData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);



        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("UserNode");

        // Get Login User Details
        activityData = getIntent().getStringExtra("EXTRA_SESSION_ID");

        mPrefs = getPreferences(MODE_PRIVATE);

        userDetails = new UserBasicDetails();

        readUserCurrentData();

    }



    /*
       Open main activity
     */

    private void getAuthStatus(){

        showProgressDialog();

        // app_title change listener
        mFirebaseDatabase.child(auth.getCurrentUser().getUid()).child("user_Type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_Type = dataSnapshot.getValue(String.class);
                Log.e("STARTUP", "> User Type : "+user_Type);

                hideProgressDialog();

                if(user_Type.equals("Admin")){
                    Intent intent = new Intent(StartUpActivity.this, AdminHome.class);
                    intent.putExtra("EXTRA_SESSION_ID", activityData);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(StartUpActivity.this, NormalUserActivity.class);
                    intent.putExtra("EXTRA_SESSION_ID", activityData);
                    startActivity(intent);
                    finish();
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {

                hideProgressDialog();

                // Failed to read value
                Log.e("STARTUP", "Failed to read value.", error.toException());
            }
        });
    }


    /*
       Read data from firebase database
     */
    private void readUserCurrentData(){

        showProgressDialog();

        // app_title change listener
        mFirebaseDatabase.child(auth.getCurrentUser().getUid()).child("School_Basic_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                updateUserDetails(dataSnapshot.getValue(UserBasicDetails.class));

                readUserObject();

                if(userDetails == null){

                    Toast.makeText(getApplicationContext(), "Problem with App !!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Done !!", Toast.LENGTH_LONG).show();

                    getAuthStatus();

                }



                hideProgressDialog();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to read value.", error.toException());
                hideProgressDialog();
            }
        });



    }

    // update all details into static class obj
    private void updateUserDetails(UserBasicDetails userDetails){
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userDetails);
        prefsEditor.putString("UserObject", json);
        prefsEditor.apply();

    }


    private void readUserObject(){
        Gson gson = new Gson();
        String json = mPrefs.getString("UserObject", "");
        userDetails = gson.fromJson(json, UserBasicDetails.class);

    }



    /*
      Progress bar
     */

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
