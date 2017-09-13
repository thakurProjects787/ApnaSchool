package com.projects.thakur.apnaschool.AdminUser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.gson.Gson;
import com.projects.thakur.apnaschool.Auth.LoginActivity;
import com.projects.thakur.apnaschool.Auth.SignupActivity;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.Common.Logger;
import com.projects.thakur.apnaschool.Common.NotifyService;
import com.projects.thakur.apnaschool.Common.SettingActivity;
import com.projects.thakur.apnaschool.DailyStatus.AdminOverallAttendenceStatus;
import com.projects.thakur.apnaschool.DailyStatus.AdminOverallMDMStatusActivity;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.NoticeBoard.AddNewNoticeActivity;
import com.projects.thakur.apnaschool.NoticeBoard.ShowNewNoticeActivity;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.Task.AdminShowAllTaskTypesActivity;
import com.projects.thakur.apnaschool.UpdateInfo.ShowEachSchoolDetails;


public class AdminHome extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    private String userdetails;


    private Button btn_addnewUser, btn_show_all_users,btn_show_all_teachers_details, btn_today_attendencs_status,btn_today_mdm_status,btn_createTask,btn_show_all_task,btn_delete_any_user,btn_show_all_openpoints_details,btn_add_new_notice_dist,btn_show_all_notices_dist;

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
        btn_delete_any_user = (Button) findViewById(R.id.btn_delete_any_user);
        btn_show_all_teachers_details = (Button) findViewById(R.id.btn_show_all_teachers_details);
        btn_show_all_openpoints_details = (Button) findViewById(R.id.btn_show_all_openpoints_details);

        btn_add_new_notice_dist = (Button) findViewById(R.id.btn_add_new_notice_dist);
        btn_show_all_notices_dist = (Button) findViewById(R.id.btn_show_all_notices_dist);

        // Click listeners
        btn_addnewUser.setOnClickListener(this);
        btn_show_all_users.setOnClickListener(this);
        btn_today_attendencs_status.setOnClickListener(this);
        btn_today_mdm_status.setOnClickListener(this);
        btn_createTask.setOnClickListener(this);
        btn_show_all_task.setOnClickListener(this);
        btn_delete_any_user.setOnClickListener(this);
        btn_show_all_teachers_details.setOnClickListener(this);
        btn_show_all_openpoints_details.setOnClickListener(this);

        btn_add_new_notice_dist.setOnClickListener(this);
        btn_show_all_notices_dist.setOnClickListener(this);

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


        //============ START Notification Service ==================
        // write school_firbasedataID into text file
        Logger.deleteFile("keys.txt",this);
        Logger.addDataIntoFile("keys.txt",userDetails.getSchool_firbaseDataID(),this);

        Intent intent = new Intent(AdminHome.this, NotifyService.class);
        AdminHome.this.startService(intent);
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
                if(!isConn()){
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(AdminHome.this, ShowAllSchoolsActivity.class);
                    intent.putExtra("EXTRA_SHOW_ALL_SCHOOLS_SESSION_ID", "ADMIN");
                    startActivity(intent);
                }
                break;

            case R.id.btn_show_all_teachers_details:
                if(!isConn()){
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(AdminHome.this, ShowAllTeachersActivity.class);
                    intent.putExtra("EXTRA_SHOW_ALL_TEACHERS_SESSION_ID", "ADMIN");
                    startActivity(intent);
                }
                break;

            case R.id.btn_show_all_openpoints_details:
                if(!isConn()){
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(AdminHome.this, ShowAllOpenPointsActivity.class);
                    intent.putExtra("EXTRA_OPENPOINTS_INFO_SESSION_ID", "ADMIN");
                    startActivity(intent);
                }
                break;

            case R.id.btn_delete_any_user:
                if(!isConn()){
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                }
                else {
                    // Ask for another email ID.
                 /* Alert Dialog Code Start*/
                    AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
                    alert.setTitle("DELETE SCHOOL"); //Set Alert dialog title here
                    alert.setMessage("Are you sure you want to delete complete schools details from Database?\n\nNote : After this operation you can't recover this school details.\nPlease provide schoold email ID."); //Message here


                    // Set an EditText view to get user input
                    final EditText input = new EditText(getApplicationContext());
                    input.setInputType(InputType.TYPE_CLASS_TEXT );
                    input.setTextColor(Color.BLACK);
                    alert.setView(input);

                    alert.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //You will get as string input data in this variable.
                            // here we convert the input to a string and show in a toast.
                            String keyword = input.getEditableText().toString();
                            //Toast.makeText(getApplicationContext(),srt,Toast.LENGTH_LONG).show();
                            if (!keyword.isEmpty() && (keyword.contains("@")) && (keyword.contains(".com"))) {


                                new DeleteUserOperation(keyword,getApplicationContext()).deleteSchoolDetails();
                                Toast.makeText(getApplicationContext(), "Done ", Toast.LENGTH_LONG).show();



                            } else {
                                //Toast.makeText(getApplicationContext(), "Wrong Password !!", Toast.LENGTH_LONG).show();

                                dialog.cancel();
                            }


                        } // End of onClick(DialogInterface dialog, int whichButton)
                    }); //End of alert.setPositiveButton
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                            //Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();

                            Toast.makeText(getApplicationContext(), "Cancel ", Toast.LENGTH_LONG).show();




                            dialog.cancel();
                        }
                    }); //End of alert.setNegativeButton
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                /* Alert Dialog Code End*/
                }
                break;


            case R.id.btn_today_attendencs_status:
                if(!isConn()){
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent_att = new Intent(AdminHome.this, AdminOverallAttendenceStatus.class);
                    intent_att.putExtra("EXTRA_ADMIN_STATE_ATTND_SESSION_ID", "DISTT");
                    startActivity(intent_att);
                }
                break;

            case R.id.btn_today_mdm_status:
                if(!isConn()){
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent_mdm = new Intent(AdminHome.this, AdminOverallMDMStatusActivity.class);
                    intent_mdm.putExtra("EXTRA_ADMIN_STATE_MDM_SESSION_ID", "DISTT");
                    startActivity(intent_mdm);
                }
                break;

            case R.id.btn_create_new_task:
                Intent intent_ctask = new Intent(AdminHome.this, AdminShowAllTaskTypesActivity.class);
                intent_ctask.putExtra("EXTRA_SHOW_TASK_TYPE_SESSION_ID", "CREATE");
                startActivity(intent_ctask);
                break;

            case R.id.btn_show_all_task:
                if(!isConn()){
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent_stask = new Intent(AdminHome.this, AdminShowAllTaskTypesActivity.class);
                    intent_stask.putExtra("EXTRA_SHOW_TASK_TYPE_SESSION_ID", "SHOW");
                    startActivity(intent_stask);
                }
                break;

            case R.id.btn_add_new_notice_dist:
                if(!isConn()){
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(AdminHome.this, AddNewNoticeActivity.class);
                    intent.putExtra("EXTRA_NEW_NOTICE_INFO_SESSION_ID", "ADD_NEW");
                    startActivity(intent);
                }
                break;

            case R.id.btn_show_all_notices_dist:
                if(!isConn()){
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(AdminHome.this, ShowNewNoticeActivity.class);
                    intent.putExtra("EXTRA_ALL_NOTICE_INFO_SESSION_ID", "ADMIN");
                    startActivity(intent);
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
                Intent intent = new Intent(AdminHome.this, ShowDisttAdminUserDetails.class);
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
                startActivity(new Intent(AdminHome.this, SettingActivity.class));
            }
        }

        if (id == R.id.admin_home_menu_send_notification) {
            if(!isConn()){
                Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
            }
            else {

                Intent intent = new Intent(AdminHome.this, SendNotificationActivity.class);
                intent.putExtra("EXTRA_NOTIFICATION_INFO_SESSION_ID", "SEND_ALL");
                startActivity(intent);

                //startActivity(new Intent(AdminHome.this, SendNotificationActivity.class));
            }
        }

        if (id == R.id.admin_home_menu_show_all_notification) {
            if(!isConn()){
                Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();
            }
            else {
                startActivity(new Intent(AdminHome.this, ShowAllNotification.class));
            }
        }

        if (id == R.id.admin_home_menu_open_file_manager) {
            Intent file_intent = new Intent(Intent.ACTION_GET_CONTENT);
            file_intent.setType("*/*");

            Uri uri = Uri.parse("/sdcard/Files/SchoolTrac"); // a directory
            file_intent.setDataAndType(uri, "*/*");
            startActivity(Intent.createChooser(file_intent, "Open folder"));
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


}
