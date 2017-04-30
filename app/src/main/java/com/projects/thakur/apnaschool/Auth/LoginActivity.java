package com.projects.thakur.apnaschool.Auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import android.util.Log;

import com.projects.thakur.apnaschool.AdminUser.AdminHome;
import com.projects.thakur.apnaschool.NormalUser.NormalUserActivity;
import com.projects.thakur.apnaschool.R;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnLogin, btnReset;

    private ProgressDialog mProgressDialog;

    private DatabaseReference mFirebaseDatabase;

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference("UserNode");


        /*
           Check for internet connection
         */

        if(!isConn()){

            Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_LONG).show();

        } else {

            if (auth.getCurrentUser() != null) {
                startActivity(new Intent(LoginActivity.this, StartUpActivity.class));
                finish();
            }
        }

        // set the view now
        setContentView(R.layout.activity_login);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);


        if(isConn()) {

                btnReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
                    }
                });

                btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String email = inputEmail.getText().toString();
                        final String password = inputPassword.getText().toString();

                        if (TextUtils.isEmpty(email)) {
                            //Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                            inputEmail.setError("Please enter email address!");
                            return;
                        } else {
                            inputEmail.setError(null);
                        }

                        if (TextUtils.isEmpty(password)) {
                            //Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                            inputPassword.setError("Please enter password!");
                            return;
                        } else {
                            inputPassword.setError(null);
                        }

                        showProgressDialog();

                        //authenticate user
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        //progressBar.setVisibility(View.GONE);


                                        if (!task.isSuccessful()) {

                                            hideProgressDialog();

                                            // there was an error
                                            if (password.length() < 6) {
                                                inputPassword.setError(getString(R.string.minimum_password));
                                            } else {
                                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                            }


                                        } else {

                                            Intent intent = new Intent(LoginActivity.this, StartUpActivity.class);
                                            intent.putExtra("EXTRA_SESSION_ID", email + "_PAssWD_" + password);
                                            startActivity(intent);
                                            finish();

                                            // app_title change listener
//                                            mFirebaseDatabase.child(auth.getCurrentUser().getUid()).child("user_Type").addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                                                    hideProgressDialog();
//
//                                                    String user_Type = dataSnapshot.getValue(String.class);
//                                                    Log.e(TAG, "> User Type : " + user_Type);
//
//                                                    if (user_Type.equals("Admin")) {
//                                                        Intent intent = new Intent(LoginActivity.this, AdminHome.class);
//                                                        intent.putExtra("EXTRA_SESSION_ID", email + "_PAssWD_" + password);
//                                                        startActivity(intent);
//                                                        finish();
//                                                    } else {
//                                                        Intent intent = new Intent(LoginActivity.this, NormalUserActivity.class);
//                                                        intent.putExtra("EXTRA_SESSION_ID", email + "_PAssWD_" + password);
//                                                        startActivity(intent);
//                                                        finish();
//                                                    }
//
//
//                                                }
//
//                                                @Override
//                                                public void onCancelled(DatabaseError error) {
//                                                    // Failed to read value
//                                                    Log.e(TAG, "Failed to read app title value.", error.toException());
//
//                                                    hideProgressDialog();
//                                                }
//                                            });

                                        }
                                    }
                                });

                    }
                });
        }//only work when internet connection

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



}// end of class

