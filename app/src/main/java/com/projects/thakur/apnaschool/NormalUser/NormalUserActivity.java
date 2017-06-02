package com.projects.thakur.apnaschool.NormalUser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.projects.thakur.apnaschool.AdminUser.ShowAllSchoolsActivity;
import com.projects.thakur.apnaschool.Auth.LoginActivity;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.Common.SettingActivity;
import com.projects.thakur.apnaschool.DailyStatus.UpdateAttendenceStatus;
import com.projects.thakur.apnaschool.DailyStatus.UpdateMDMStatus;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.UpdateInfo.ShowEachSchoolDetails;

public class NormalUserActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Button btn_open_today_attendencs_window, btn_open_today_mdm_window, btn_my_all_task, btn_show_other_schools_data;

    private TextView txtv_logged_user_name,txtv_logged_user_email_id,txtv_logged_user_type,txtv_logged_user_address;

    private UserBasicDetails userDetails;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_user);



        btn_open_today_attendencs_window = (Button) findViewById(R.id.btn_open_today_attendencs_window);
        btn_open_today_mdm_window = (Button) findViewById(R.id.btn_open_today_mdm_window);
        btn_my_all_task = (Button) findViewById(R.id.btn_my_all_task);
        btn_show_other_schools_data = (Button) findViewById(R.id.btn_show_other_schools_data);


        // Click listeners
        btn_open_today_attendencs_window.setOnClickListener(this);
        btn_open_today_mdm_window.setOnClickListener(this);
        btn_my_all_task.setOnClickListener(this);
        btn_show_other_schools_data.setOnClickListener(this);


        //Textview
        txtv_logged_user_name = (TextView) findViewById(R.id.txtv_logged_user_name);
        txtv_logged_user_address = (TextView) findViewById(R.id.txtv_logged_user_address);
        txtv_logged_user_email_id = (TextView) findViewById(R.id.txtv_logged_user_email_id);
        txtv_logged_user_type = (TextView) findViewById(R.id.txtv_logged_user_type);


        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        // Firbase database access
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(NormalUserActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        userDetails = new UserBasicDetails();
        readUserObject();

        txtv_logged_user_name.setText(userDetails.getName());
        txtv_logged_user_address.setText(userDetails.getPlace_name()+","+ userDetails.getDistt()+","+userDetails.getState());
        txtv_logged_user_email_id.setText(userDetails.getSchool_emailID());
        txtv_logged_user_type.setText(userDetails.getType());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open_today_attendencs_window:
                if(!isConn()){
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(NormalUserActivity.this, UpdateAttendenceStatus.class);
                    startActivity(intent);
                }
                break;

            case R.id.btn_open_today_mdm_window:
                if(!isConn()){
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent_mdm = new Intent(NormalUserActivity.this, UpdateMDMStatus.class);
                    startActivity(intent_mdm);
                }
                break;

            case R.id.btn_my_all_task:
                if(!isConn()){
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent_mytask = new Intent(NormalUserActivity.this, UserShowMyAllTaskActivity.class);
                    startActivity(intent_mytask);
                }
                break;

            case R.id.btn_show_other_schools_data:
                if(!isConn()){
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                }
                else {

                    mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("adminUserID").addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String adminID = dataSnapshot.getValue().toString();

                            Intent intent = new Intent(NormalUserActivity.this, ShowAllSchoolsActivity.class);
                            intent.putExtra("EXTRA_SHOW_ALL_SCHOOLS_SESSION_ID", adminID);
                            startActivity(intent);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

                }
                break;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.admin_home_menu_account_details) {
            if(!isConn()){
                Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
            }
            else {
                Intent intent = new Intent(NormalUserActivity.this, ShowEachSchoolDetails.class);
                intent.putExtra("EXTRA_SHOW_SCHOOL_SESSION_ID", "OWNER");
                startActivity(intent);
            }
            return true;
        }

        if (id == R.id.admin_home_menu_account_setting) {
            if(!isConn()){
                Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
            }
            else {
                startActivity(new Intent(NormalUserActivity.this, SettingActivity.class));
            }
        }


        return super.onOptionsItemSelected(item);
    }


    private void readUserObject(){
        Gson gson = new Gson();
        String json = StartUpActivity.mPrefs.getString("UserObject", "");
        userDetails = gson.fromJson(json, UserBasicDetails.class);

    }

    /*
    Check internet is enabled or not.
   */
    public boolean isConn() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity.getActiveNetworkInfo() != null) {
            if (connectivity.getActiveNetworkInfo().isConnected())
                return true;
        }
        return false;
    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(NormalUserActivity.this);
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


}
