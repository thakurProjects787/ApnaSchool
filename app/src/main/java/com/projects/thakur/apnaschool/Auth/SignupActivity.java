package com.projects.thakur.apnaschool.Auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.projects.thakur.apnaschool.AdminUser.Account_Details;
import com.projects.thakur.apnaschool.AdminUser.AdminHome;
import com.projects.thakur.apnaschool.AdminUser.NewUserDetails;
import com.projects.thakur.apnaschool.Common.SendMail;
import com.projects.thakur.apnaschool.Model.MDMDetails;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail;
    private Button btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mNewUserDatabase, mCurrentUserDatabase;
    private String currentUserID;

    private String userdetails;
    private String newUserID;
    private String newuserEmailID;

    private String password = "TempPass@123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.signup_toolbar);
        toolbar.setTitle("Setting");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        mCurrentUserDatabase = FirebaseDatabase.getInstance().getReference();
        currentUserID = auth.getCurrentUser().getUid();

        // Get Login User Details
        userdetails = getIntent().getStringExtra("EXTRA_SESSION_ID");

        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                //String password = inputPassword.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                showProgressDialog();
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                               // Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    // add child with empty values for new user and update their details
                                    // add new user details in admin user list
                                    // create user object and set all the properties

                                    mNewUserDatabase = FirebaseDatabase.getInstance().getReference();

                                    newuserEmailID = inputEmail.getText().toString().trim();
                                    newUserID = auth.getCurrentUser().getUid();

                                    // create user object and set all the properties
                                    UserBasicDetails schoolBasicInfo = new UserBasicDetails();

                                    schoolBasicInfo.setId("-");
                                    schoolBasicInfo.setName("-");
                                    schoolBasicInfo.setType("Primary School");

                                    schoolBasicInfo.setComplete_address("-");
                                    schoolBasicInfo.setPlace_name("-");
                                    schoolBasicInfo.setDistt("-");
                                    schoolBasicInfo.setPin_code("-");
                                    schoolBasicInfo.setGps_location("-");

                                    schoolBasicInfo.setContact_details("-");

                                    schoolBasicInfo.setTotal_student("0");
                                    schoolBasicInfo.setTotal_teacher("0");
                                    schoolBasicInfo.setTotal_admin_staff("0");
                                    schoolBasicInfo.setTotal_service_staff("0");

                                    schoolBasicInfo.setSchool_emailID(newuserEmailID);
                                    schoolBasicInfo.setSchool_firbaseDataID(newUserID);

                                    // create user object and set all the properties
                                    MDMDetails mdmInfo = new MDMDetails();

                                    mdmInfo.setTotal_students("-");
                                    mdmInfo.setRice_stock("-");
                                    mdmInfo.setOther_stock("-");
                                    mdmInfo.setDiet_menu_details("-");


                                    showProgressDialog();


                                    // create ref for admin user node

                                    // Get current date
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy/MM/dd");

                                    // update new user details in admin node

                                    NewUserDetails newuserdetails = new NewUserDetails();
                                    newuserdetails.setNewuserID(newUserID);
                                    newuserdetails.setNewEmailID(newuserEmailID);
                                    newuserdetails.setNewUserType("Normal");
                                    newuserdetails.setJoinDate(mdformat.format(calendar.getTime()));


                                    Account_Details newaccountdetails = new Account_Details();
                                    newaccountdetails.setEmail_ID(newuserEmailID);
                                    newaccountdetails.setUser_Type("Normal");
                                    newaccountdetails.setJoinDate(mdformat.format(calendar.getTime()));
                                    newaccountdetails.setAdminUserID(currentUserID);


                                    if(auth.getCurrentUser()!=null)
                                    {
                                        // save the user at UserNode under user UID
                                        mNewUserDatabase.child("UserNode").child(newUserID).setValue(newaccountdetails, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if(databaseError==null)
                                                {
                                                    //Toast.makeText(SignupActivity.this, "Data is saved successfully",
                                                    //       Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }
                                        });

                                        // save the basic info
                                        mNewUserDatabase.child("UserNode").child(auth.getCurrentUser().getUid()).child("MDM_Info").setValue(mdmInfo, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if(databaseError==null)
                                                {
                                                    //Toast.makeText(SignupActivity.this, "Data is saved successfully",
                                                     //       Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }
                                        });

                                        // save the basic info
                                        mNewUserDatabase.child("UserNode").child(auth.getCurrentUser().getUid()).child("School_Basic_Info").setValue(schoolBasicInfo, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if(databaseError==null)
                                                {
                                                    //Toast.makeText(SignupActivity.this, "Data is saved successfully",
                                                    //       Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }
                                        });

                                        // save the user at UserNode under user UID
                                        mCurrentUserDatabase.child("UserNode").child(currentUserID).child("Sub_User").child(newUserID).setValue(newuserdetails, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if(databaseError==null)
                                                {
                                                    Toast.makeText(SignupActivity.this, "New User addedd!!",
                                                            Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }
                                        });


                                    }

                                    // Send user login details in mail to user

                                    // Send MAIL
                                    String userBody="Hi User"+"\n \n You Account has been activated in Schooltrace App.\n " +
                                            "\n\n Details : \n User ID : "+newuserEmailID+"\n Password : "+password+"\n\n Thanks.\nADMIN";

                                    String userSub="Schooltrace Account Activatation!!";

                                    String[] emaildetails={newuserEmailID};

                                    new SendMail(userSub, userBody, emaildetails,"NO", null).send();


                                    hideProgressDialog();

                                    // sign out current user..
                                    auth.signOut();

                                    String email = userdetails.split("_PAssWD_")[0];
                                    String password = userdetails.split("_PAssWD_")[1];

                                    //authenticate user
                                    auth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    // If sign in fails, display a message to the user. If sign in succeeds
                                                    // the auth state listener will be notified and logic to handle the
                                                    // signed in user can be handled in the listener.
                                                    //progressBar.setVisibility(View.GONE);


                                                    if (!task.isSuccessful()) {
                                                        // there was an error
                                                            Toast.makeText(SignupActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                                        //btnLogin.setEnabled(true);

                                                    } else {
                                                        Intent intent = new Intent(SignupActivity.this, AdminHome.class);
                                                        intent.putExtra("EXTRA_SESSION_ID", userdetails);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });

                                    //startActivity(new Intent(SignupActivity.this, AdminHome.class));
                                    //finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
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