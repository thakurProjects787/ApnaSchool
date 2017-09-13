package com.projects.thakur.apnaschool.DailyStatus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.thakur.apnaschool.AdminUser.NewUserDetails;
import com.projects.thakur.apnaschool.Common.CreateExcelReport;
import com.projects.thakur.apnaschool.Common.Logger;
import com.projects.thakur.apnaschool.Model.DailyMDMStatus;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AdminOverallMDMStatusActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference mFirebaseDatabase;

    private String todaySubmitDate,eachSchoolID,school_firebase_ID;

    //logger
    private Logger logger;

    private Context context;

    private ProgressDialog mProgressDialog;

    // Declare all textview bind variables
    private TextView txtv_admin_daily_mdm_infor_date_value;
    private TextView txtv_admin_daily_attn_tot_schools_value,txtv_admin_daily_attn_tot_schools_present_value,txtv_admin_daily_attn_tot_schools_pending_value,txtv_admin_daily_mdm_tot_student_value,txtv_admin_daily_attn_mdm_student_present_value;
    private TextView txtv_admin_daily_attn_mdm_student_absent_value,txtv_admin_daily_tot_mdm_rice_used_value,txtv_admin_daily_tot_mdm_rice_stock_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_overall_mdmstatus);

        Toolbar toolbar = (Toolbar) findViewById(R.id.admin_overall_mdm_status_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("MDM");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        // get parent activity status
        String parentAtivityMsg = getIntent().getStringExtra("EXTRA_ADMIN_STATE_MDM_SESSION_ID");

        //bind all texview variables
        txtv_admin_daily_mdm_infor_date_value = (TextView) findViewById(R.id.txtv_admin_daily_mdm_infor_date_value);
        txtv_admin_daily_attn_tot_schools_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_schools_value);
        txtv_admin_daily_attn_tot_schools_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_schools_present_value);
        txtv_admin_daily_attn_tot_schools_pending_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_tot_schools_pending_value);

        txtv_admin_daily_mdm_tot_student_value = (TextView) findViewById(R.id.txtv_admin_daily_mdm_tot_student_value);
        txtv_admin_daily_attn_mdm_student_present_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_mdm_student_present_value);
        txtv_admin_daily_attn_mdm_student_absent_value = (TextView) findViewById(R.id.txtv_admin_daily_attn_mdm_student_absent_value);

        txtv_admin_daily_tot_mdm_rice_used_value = (TextView) findViewById(R.id.txtv_admin_daily_tot_mdm_rice_used_value);
        txtv_admin_daily_tot_mdm_rice_stock_value = (TextView) findViewById(R.id.txtv_admin_daily_tot_mdm_rice_stock_value);

        // Get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy_MM_dd");

        todaySubmitDate = mdformat.format(calendar.getTime());


        SimpleDateFormat mdformat_2 = new SimpleDateFormat("dd/MM/yyyy");
        txtv_admin_daily_mdm_infor_date_value.setText(mdformat_2.format(calendar.getTime()));


         /*
           For DISTT - Read District school's and calculate all.
           For STATE - Read State districts and then read each district school's details and then calculate.
         */

        if(parentAtivityMsg.equals("DISTT")) {
        /*
          Read all District schools MDM details
         */
            getAdminAllSchoolsDetails();
        } else {
            /*
                Read first all State district and then read each school's details
             */

            getStateLevelData();
        }

    }

    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getAdminAllSchoolsDetails() {

        //delete old dataCal file
        logger.deleteFile("dataCal.txt",getApplicationContext());

        resetView();

        showProgressDialog();
        mFirebaseDatabase.child("UserNode").child(auth.getCurrentUser().getUid()).child("Sub_User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    long totalSchool = dataSnapshot.getChildrenCount();

                    txtv_admin_daily_attn_tot_schools_value.setText(Long.toString(totalSchool));

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        NewUserDetails allSchools=postSnapShot.getValue(NewUserDetails.class);
                        // Read school details
                        readSchoolDailyMDMDetails(allSchools.getNewuserID());
                    }

                }
                hideProgressDialog();

                mFirebaseDatabase.child("UserNode").child(auth.getCurrentUser().getUid()).child("Sub_User").removeEventListener(this);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });

    }

    /*
       Read each school MDM details.
     */

    private void readSchoolDailyMDMDetails(String schoolDataID){

        eachSchoolID = schoolDataID;

        mFirebaseDatabase.child("UserNode").child(eachSchoolID).child("Daily_Task").child("MDM_STATUS").child(todaySubmitDate).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    // each school details
                    DailyMDMStatus schoolmdmDetails = dataSnapshot.getValue(DailyMDMStatus.class);

                    if (schoolmdmDetails != null) {

                        txtv_admin_daily_attn_tot_schools_present_value.setText(Integer.toString(Integer.parseInt(txtv_admin_daily_attn_tot_schools_present_value.getText().toString()) + 1));

                        // Update Each school wise details
                        String eachSchool = "EACHSCHOOL@" + schoolmdmDetails.getSchool_details() + "&" + schoolmdmDetails.getMdmStudentsDetails() + "&" + schoolmdmDetails.getMdmRiceStockDetails() + "&" + schoolmdmDetails.getMdmtodayMenu().replace("\n","#");
                        logger.addDataIntoFile("dataCal.txt", eachSchool, getApplicationContext());


                        updateMDMCard(schoolmdmDetails);

                        txtv_admin_daily_attn_tot_schools_pending_value.setText( Integer.toString( Integer.parseInt(txtv_admin_daily_attn_tot_schools_value.getText().toString()) - Integer.parseInt(txtv_admin_daily_attn_tot_schools_present_value.getText().toString()) ) );


                    }

                    // Update all data into Database
                    updateCombinedDetails();

                    mFirebaseDatabase.child("UserNode").child(eachSchoolID).child("Daily_Task").child("MDM_STATUS").child(todaySubmitDate).removeEventListener(this);
                }
                else {
                    readSchoolBasicInfo(eachSchoolID);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to read value.", error.toException());
                hideProgressDialog();
            }
        });


    }

    /*
      Update MDM card view
     */
    private void updateMDMCard(DailyMDMStatus schoolmdmDetails){

        txtv_admin_daily_mdm_tot_student_value.setText( Integer.toString( Integer.parseInt( txtv_admin_daily_mdm_tot_student_value.getText().toString()) + Integer.parseInt(schoolmdmDetails.getMdmStudentsDetails().split("#")[0])));
        txtv_admin_daily_attn_mdm_student_present_value.setText( Integer.toString(Integer.parseInt( txtv_admin_daily_attn_mdm_student_present_value.getText().toString()) + Integer.parseInt(schoolmdmDetails.getMdmStudentsDetails().split("#")[1])));
        txtv_admin_daily_attn_mdm_student_absent_value.setText( Integer.toString(Integer.parseInt( txtv_admin_daily_attn_mdm_student_absent_value.getText().toString()) + Integer.parseInt(schoolmdmDetails.getMdmStudentsDetails().split("#")[2])));

        txtv_admin_daily_tot_mdm_rice_used_value.setText( Integer.toString( Integer.parseInt( txtv_admin_daily_tot_mdm_rice_used_value.getText().toString()) + Integer.parseInt(schoolmdmDetails.getMdmRiceStockDetails().split("#")[0])));
        txtv_admin_daily_tot_mdm_rice_stock_value.setText( Integer.toString(Integer.parseInt( txtv_admin_daily_tot_mdm_rice_stock_value.getText().toString()) + Integer.parseInt(schoolmdmDetails.getMdmRiceStockDetails().split("#")[1])));
    }

    /*
      Update all calculated details
     */

    private void updateCombinedDetails(){

        if(auth.getCurrentUser()!=null)
        {

            DailyMDMStatus todayMDMDetails = new DailyMDMStatus();

            // combined all data

            String studentsmdmdetails = txtv_admin_daily_mdm_tot_student_value.getText().toString()+"#"+txtv_admin_daily_attn_mdm_student_present_value.getText().toString()+"#"+txtv_admin_daily_attn_mdm_student_absent_value.getText().toString();
            String ricestockdetails = txtv_admin_daily_tot_mdm_rice_used_value.getText().toString()+"#"+txtv_admin_daily_tot_mdm_rice_stock_value.getText().toString();


            // add details into text file
            logger.addDataIntoFile("dataCal.txt","STUDENT@"+studentsmdmdetails,this);
            logger.addDataIntoFile("dataCal.txt","RICESTOCK@"+ricestockdetails,this);

            todayMDMDetails.setMdmStudentsDetails(studentsmdmdetails);
            todayMDMDetails.setMdmRiceStockDetails(ricestockdetails);
            todayMDMDetails.setMdmtodayMenu("");
            todayMDMDetails.setSchool_details("");

            // save the user at UserNode under user UID
            mFirebaseDatabase.child("UserNode").child(auth.getCurrentUser().getUid()).child("Daily_Task").child("MDM_STATUS").child(todaySubmitDate).setValue(todayMDMDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                    if(databaseError==null)
                    {
                        Toast.makeText(AdminOverallMDMStatusActivity.this, "Updated",
                                Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    /*
      Reset View
     */
    private void resetView(){

        //Reset

        txtv_admin_daily_attn_tot_schools_value.setText("0");
        txtv_admin_daily_attn_tot_schools_present_value.setText("0");
        txtv_admin_daily_attn_tot_schools_pending_value.setText("0");

        txtv_admin_daily_mdm_tot_student_value.setText("0");
        txtv_admin_daily_attn_mdm_student_present_value.setText("0");
        txtv_admin_daily_attn_mdm_student_absent_value.setText("0");

        txtv_admin_daily_tot_mdm_rice_used_value.setText("0");
        txtv_admin_daily_tot_mdm_rice_stock_value.setText("0");

    }


    /*
      Read school basic info
     */

    private void readSchoolBasicInfo(String schoolID){

        showProgressDialog();

        // app_title change listener
        mFirebaseDatabase.child("UserNode").child(schoolID).child("School_Basic_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserBasicDetails userDetails = dataSnapshot.getValue(UserBasicDetails.class);

                if(userDetails != null) {

                    // Update Each school wise details
                    String eachSchool = "EACHSCHOOL@" + userDetails.getId() + "#" + userDetails.getName() + "#" + userDetails.getDistt() + "#" + userDetails.getSchool_emailID() + "&" + "NS" + "&" + "NS" + "&" + "NS";
                    logger.addDataIntoFile("dataCal.txt", eachSchool, getApplicationContext());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_overall_attnd_status_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.admin_overall_attnd_status_send_report) {

            if(txtv_admin_daily_attn_tot_schools_present_value.getText().equals("0")){

                Toast.makeText(getApplicationContext(), "No one has submitted !!", Toast.LENGTH_LONG).show();

            } else {

                // Ask for another email ID.
                 /* Alert Dialog Code Start*/
                AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
                alert.setTitle("SEND REPORT"); //Set Alert dialog title here
                alert.setMessage("If you want report on new email id , please provide new email id address.\n\nOtherwise it will send to your default email ID."); //Message here


                // Set an EditText view to get user input
                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                input.setTextColor(Color.BLACK);
                alert.setView(input);

                alert.setPositiveButton("NEW", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //You will get as string input data in this variable.
                        // here we convert the input to a string and show in a toast.
                        String keyword = input.getEditableText().toString();
                        //Toast.makeText(getApplicationContext(),srt,Toast.LENGTH_LONG).show();
                        if (!keyword.isEmpty() && (keyword.contains("@")) && (keyword.contains(".com"))) {


                            String fileName = "MDMReport_" + todaySubmitDate + ".xls";

                            if (new CreateExcelReport().generateMDMReport(fileName, context,keyword)) {
                                Toast.makeText(getApplicationContext(), "Report Generated : " + fileName, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Report Not Generated", Toast.LENGTH_LONG).show();
                            }



                        } else {
                            //Toast.makeText(getApplicationContext(), "Wrong Password !!", Toast.LENGTH_LONG).show();

                            dialog.cancel();
                        }


                    } // End of onClick(DialogInterface dialog, int whichButton)
                }); //End of alert.setPositiveButton
                alert.setNegativeButton("DEFAULT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        //Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();

                        String fileName = "MDMReport_" + todaySubmitDate + ".xls";

                        if (new CreateExcelReport().generateMDMReport(fileName, context,"DEFAULT")) {
                            Toast.makeText(getApplicationContext(), "Report Generated : " + fileName, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Report Not Generated", Toast.LENGTH_LONG).show();
                        }



                        dialog.cancel();
                    }
                }); //End of alert.setNegativeButton
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                /* Alert Dialog Code End*/

            }


        }

        if (id == R.id.admin_overall_attnd_status_refresh) {
            getAdminAllSchoolsDetails();
        }


        return super.onOptionsItemSelected(item);
    }

    /*
       Read state district details
     */

    public void getStateLevelData() {

        new Logger().deleteFile("locations.txt",context);

        showProgressDialog();
        mFirebaseDatabase.child("UserNode").child(auth.getCurrentUser().getUid()).child("Sub_User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        NewUserDetails allSchools=postSnapShot.getValue(NewUserDetails.class);
                        // Read school details
                        getEachDisttSchoolDetails(allSchools.getNewuserID());

                    }

                    mFirebaseDatabase.child("UserNode").child(auth.getCurrentUser().getUid()).child("Sub_User").removeEventListener(this);
                }
                hideProgressDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    /*
        Read each school details
     */
    public void getEachDisttSchoolDetails(String firebase_ID) {

        school_firebase_ID = firebase_ID;

        //delete old dataCal file
        logger.deleteFile("dataCal.txt",getApplicationContext());

        resetView();

        showProgressDialog();
        mFirebaseDatabase.child("UserNode").child(firebase_ID).child("Sub_User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    long totalSchool = dataSnapshot.getChildrenCount();

                    txtv_admin_daily_attn_tot_schools_value.setText(Long.toString(totalSchool));

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        NewUserDetails allSchools=postSnapShot.getValue(NewUserDetails.class);
                        // Read school details
                        readSchoolDailyMDMDetails(allSchools.getNewuserID());

                    }
                }
                hideProgressDialog();

                mFirebaseDatabase.child("UserNode").child(school_firebase_ID).child("Sub_User").removeEventListener(this);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });

    }


}
