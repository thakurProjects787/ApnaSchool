package com.projects.thakur.apnaschool.AdminUser;

import android.content.Intent;
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

import com.google.gson.Gson;
import com.projects.thakur.apnaschool.Auth.LoginActivity;
import com.projects.thakur.apnaschool.Auth.SignupActivity;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.Common.SettingActivity;
import com.projects.thakur.apnaschool.DailyStatus.AdminOverallAttendenceStatus;
import com.projects.thakur.apnaschool.DailyStatus.AdminOverallMDMStatusActivity;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.Task.AdminShowAllTaskTypesActivity;
import com.projects.thakur.apnaschool.UpdateInfo.ShowEachSchoolDetails;


public class AdminHome extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private String userdetails;


    private Button btn_addnewUser, btn_show_all_users, btn_today_attendencs_status,btn_today_mdm_status,btn_createTask,btn_show_all_task;

    private TextView txtv_logged_admin_name,txtv_logged_admin_email_id,txtv_admin_user_type,txtv_admin_user_address;

    private UserBasicDetails userDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        btn_addnewUser = (Button) findViewById(R.id.btn_add_new_User);
        btn_show_all_users = (Button) findViewById(R.id.btn_show_all_user);
        btn_today_attendencs_status = (Button) findViewById(R.id.btn_today_attendencs_status);
        btn_today_mdm_status = (Button) findViewById(R.id.btn_today_mdm_status);
        btn_createTask = (Button) findViewById(R.id.btn_create_new_task);
        btn_show_all_task = (Button) findViewById(R.id.btn_show_all_task);

        // Click listeners
        btn_addnewUser.setOnClickListener(this);
        btn_show_all_users.setOnClickListener(this);
        btn_today_attendencs_status.setOnClickListener(this);
        btn_today_mdm_status.setOnClickListener(this);
        btn_createTask.setOnClickListener(this);
        btn_show_all_task.setOnClickListener(this);

        //Textview
        txtv_logged_admin_name = (TextView) findViewById(R.id.txtv_logged_admin_name);
        txtv_admin_user_address = (TextView) findViewById(R.id.txtv_admin_user_address);
        txtv_logged_admin_email_id = (TextView) findViewById(R.id.txtv_logged_admin_email_id);
        txtv_admin_user_type = (TextView) findViewById(R.id.txtv_admin_user_type);

        // Get Login User Details
        userdetails = getIntent().getStringExtra("EXTRA_SESSION_ID");

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(AdminHome.this, LoginActivity.class));
                    finish();
                }
            }
        };

        userDetails = new UserBasicDetails();
        readUserObject();

        txtv_logged_admin_name.setText(userDetails.getName());
        txtv_admin_user_address.setText(userDetails.getPlace_name()+","+ userDetails.getDistt()+","+userDetails.getState());
        txtv_logged_admin_email_id.setText(userDetails.getSchool_emailID());
        txtv_admin_user_type.setText(userDetails.getType());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_new_User:
                if(userdetails!=null) {
                    Intent intent = new Intent(AdminHome.this, SignupActivity.class);
                    intent.putExtra("EXTRA_SESSION_ID", userdetails);
                    startActivity(intent);
                } else {
                    Toast.makeText(AdminHome.this, "Please Sign Out and then Login again !!!", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btn_show_all_user:
                Intent intent = new Intent(AdminHome.this, ShowAllSchoolsActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_today_attendencs_status:
                Intent intent_att = new Intent(AdminHome.this, AdminOverallAttendenceStatus.class);
                startActivity(intent_att);
                break;

            case R.id.btn_today_mdm_status:
                Intent intent_mdm = new Intent(AdminHome.this, AdminOverallMDMStatusActivity.class);
                startActivity(intent_mdm);
                break;

            case R.id.btn_create_new_task:
                Intent intent_ctask = new Intent(AdminHome.this, AdminShowAllTaskTypesActivity.class);
                intent_ctask.putExtra("EXTRA_SHOW_TASK_TYPE_SESSION_ID", "CREATE");
                startActivity(intent_ctask);
                break;

            case R.id.btn_show_all_task:
                Intent intent_stask = new Intent(AdminHome.this, AdminShowAllTaskTypesActivity.class);
                intent_stask.putExtra("EXTRA_SHOW_TASK_TYPE_SESSION_ID", "SHOW");
                startActivity(intent_stask);
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

            Intent intent = new Intent(AdminHome.this, ShowEachSchoolDetails.class);
            intent.putExtra("EXTRA_SHOW_SCHOOL_SESSION_ID", "OWNER");
            startActivity(intent);
            return true;
        }

        if (id == R.id.admin_home_menu_account_setting) {
            startActivity(new Intent(AdminHome.this, SettingActivity.class));
        }


        return super.onOptionsItemSelected(item);
    }

    private void readUserObject(){
        Gson gson = new Gson();
        String json = StartUpActivity.mPrefs.getString("UserObject", "");
        userDetails = gson.fromJson(json, UserBasicDetails.class);

    }


}
