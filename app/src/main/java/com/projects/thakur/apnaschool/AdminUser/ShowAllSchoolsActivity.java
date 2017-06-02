package com.projects.thakur.apnaschool.AdminUser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.projects.thakur.apnaschool.Common.Logger;
import com.projects.thakur.apnaschool.Common.Maps.DisplaySchoolsOnMaps;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.UpdateInfo.ShowEachSchoolDetails;

import java.util.ArrayList;

public class ShowAllSchoolsActivity extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ArrayList<UserBasicDetails> allEachSchoolsDetails = new ArrayList<>();

    //List View
    ListView allDetails;

    private String searchKeyword,eachschoolid;

    //Inner class object
    ShowSchoolAdapter adapter;

    private Context context;

    ProgressDialog mProgressDialog;

    private String adminUserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_schools_details);


        Toolbar toolbar = (Toolbar) findViewById(R.id.show_all_schools_details_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("School's Details");

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

        String schoolIntentStatus = getIntent().getStringExtra("EXTRA_SHOW_ALL_SCHOOLS_SESSION_ID");

        allDetails=(ListView)findViewById(R.id.show_all_schools_details_lv);

        adapter=new ShowSchoolAdapter(ShowAllSchoolsActivity.this,allEachSchoolsDetails);

        allDetails.setAdapter(adapter);

        searchKeyword = "ALL";

        // check according to the intent request
        if(schoolIntentStatus.equals("ADMIN")){
            adminUserID = mAuth.getCurrentUser().getUid();
        } else {
            adminUserID = schoolIntentStatus;
        }

        getDataFromServer();


    } //end of onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_all_schools_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.show_all_schools_on_map) {

            Intent intent = new Intent(ShowAllSchoolsActivity.this, DisplaySchoolsOnMaps.class);
            intent.putExtra("EXTRA_SCHOOL_ON_MAP_SESSION_ID", "ALL");
            startActivity(intent);

            return true;
        }

        if (id == R.id.filter_schools_details_from_all) {

            /* Alert Dialog Code Start*/
            AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
            alert.setTitle("KEYWORD"); //Set Alert dialog title here
            alert.setMessage("Please type search keyword related to Name, ID, Place etc."); //Message here


            // Set an EditText view to get user input
            final EditText input = new EditText(getApplicationContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT );
            input.setTextColor(Color.BLACK);
            alert.setView(input);

            alert.setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //You will get as string input data in this variable.
                    // here we convert the input to a string and show in a toast.
                    String keyword = input.getEditableText().toString();
                    //Toast.makeText(getApplicationContext(),srt,Toast.LENGTH_LONG).show();
                    if (!keyword.isEmpty()) {

                        searchKeyword = keyword;

                        getDataFromServer();


                    } else {
                        //Toast.makeText(getApplicationContext(), "Wrong Password !!", Toast.LENGTH_LONG).show();

                        dialog.cancel();
                    }


                } // End of onClick(DialogInterface dialog, int whichButton)
            }); //End of alert.setPositiveButton
            alert.setNegativeButton("RESET", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                    //Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();
                    searchKeyword = "ALL";

                    getDataFromServer();

                    dialog.cancel();
                }
            }); //End of alert.setNegativeButton
            AlertDialog alertDialog = alert.create();
            alertDialog.show();
                /* Alert Dialog Code End*/

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getDataFromServer() {

        new Logger().deleteFile("locations.txt",context);

        showProgressDialog();
        mDatabase.child("UserNode").child(adminUserID).child("Sub_User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    allEachSchoolsDetails.clear();

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        NewUserDetails allSchools=postSnapShot.getValue(NewUserDetails.class);
                        // Read school details
                        readSchoolDetails(allSchools.getNewuserID());

                    }

                    mDatabase.child("UserNode").child(adminUserID).child("Sub_User").removeEventListener(this);
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
      Read each school details to display
     */
    private void readSchoolDetails(String schoolDataID){

        eachschoolid = schoolDataID;

        mDatabase.child("UserNode").child(eachschoolid).child("School_Basic_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // each school details
                UserBasicDetails schoolDetails = dataSnapshot.getValue(UserBasicDetails.class);

                if(schoolDetails != null) {

                /*
                   Enable Search Option
                 */

                    if (searchKeyword.equals("ALL")) {

                        //  lat + "#" + lng + "#" + name + "#" + mapDisplayLine + "%";
                        if (!schoolDetails.getGps_location().equals("-")) {

                            String schoolLocationDetails = "MAPS@" + schoolDetails.getGps_location().split(",")[1] + "#" + schoolDetails.getGps_location().split(",")[0] + "#" + schoolDetails.getName() + "#" + schoolDetails.getPlace_name() + "," + schoolDetails.getDistt();

                            new Logger().addDataIntoFile("locations.txt", schoolLocationDetails, context);
                        }

                        allEachSchoolsDetails.add(schoolDetails);
                        adapter.notifyDataSetChanged();
                    } else {

                    /*
                      Search with filterd options
                     */
                        if ((searchKeyword.toLowerCase().contains(schoolDetails.getId().toLowerCase())) || (searchKeyword.toLowerCase().contains(schoolDetails.getName().toLowerCase())) || (searchKeyword.toLowerCase().contains(schoolDetails.getPlace_name().toLowerCase())) || (searchKeyword.toLowerCase().contains(schoolDetails.getPin_code().toLowerCase())) || (searchKeyword.toLowerCase().contains(schoolDetails.getDistt().toLowerCase())) || (searchKeyword.toLowerCase().contains(schoolDetails.getComplete_address().toLowerCase()))) {

                            //  lat + "#" + lng + "#" + name + "#" + mapDisplayLine + "%";
                            if (!schoolDetails.getGps_location().equals("-")) {

                                String schoolLocationDetails = "MAPS@" + schoolDetails.getGps_location().split(",")[1] + "#" + schoolDetails.getGps_location().split(",")[0] + "#" + schoolDetails.getName() + "#" + schoolDetails.getPlace_name() + "," + schoolDetails.getDistt();

                                new Logger().addDataIntoFile("locations.txt", schoolLocationDetails, context);
                            }

                            allEachSchoolsDetails.add(schoolDetails);
                            adapter.notifyDataSetChanged();

                        }


                    }

                    mDatabase.child("UserNode").child(eachschoolid).child("School_Basic_Info").removeEventListener(this);

                    adapter.notifyDataSetChanged();
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


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(ShowAllSchoolsActivity.this);
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


    private class ShowSchoolAdapter extends BaseAdapter {

        Context c;
        LayoutInflater layoutInflater;

        ArrayList<UserBasicDetails> allSchoolsDetails;
        public ShowSchoolAdapter(Context c, ArrayList<UserBasicDetails> allSchoolsDetails) {
            this.c = c;
            layoutInflater = LayoutInflater.from(c);
            this.allSchoolsDetails = allSchoolsDetails;
        }

        @Override
        public int getCount() {
            return allSchoolsDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return allSchoolsDetails.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.model_show_all_shools, null, false);
                holder = new ViewHolder();

                holder.txtv_school_id_value = (TextView) convertView.findViewById(R.id.txtv_school_id_value);
                holder.txtv_school_name_value = (TextView) convertView.findViewById(R.id.txtv_school_name_value);
                holder.txtv_school_type_value = (TextView) convertView.findViewById(R.id.txtv_school_type_value);

                holder.txtv_school_emailid_value = (TextView) convertView.findViewById(R.id.txtv_school_emailid_value);
                holder.txtv_school_contacts_value = (TextView) convertView.findViewById(R.id.txtv_school_contacts_value);

                holder.txtv_school_address_value = (TextView) convertView.findViewById(R.id.txtv_school_address_value);
                holder.txtv_total_strength_value = (TextView) convertView.findViewById(R.id.txtv_total_strength_value);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            UserBasicDetails schoolsDetails=allSchoolsDetails.get(position);

            holder.txtv_school_id_value.setText(schoolsDetails.getId());
            holder.txtv_school_name_value.setText(schoolsDetails.getName());
            holder.txtv_school_type_value.setText(schoolsDetails.getType());

            holder.txtv_school_emailid_value.setText(schoolsDetails.getSchool_emailID());
            holder.txtv_school_contacts_value.setText(schoolsDetails.getContact_details());

            holder.txtv_school_address_value.setText(schoolsDetails.getPlace_name()+","+ schoolsDetails.getDistt()+","+schoolsDetails.getState());
            holder.txtv_total_strength_value.setText("S:"+schoolsDetails.getTotal_student()+" T:"+schoolsDetails.getTotal_teacher()+" A:"+schoolsDetails.getTotal_admin_staff()+" S:"+schoolsDetails.getTotal_service_staff());

            final UserBasicDetails obj= (UserBasicDetails) this.getItem(position);

            //ONITECLICK
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(c,s.getClass_name(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ShowAllSchoolsActivity.this, ShowEachSchoolDetails.class);
                    intent.putExtra("EXTRA_SHOW_SCHOOL_SESSION_ID", obj.getSchool_firbaseDataID());
                    startActivity(intent);
                }
            });

            return convertView;
        }

        public class ViewHolder {
            TextView txtv_school_id_value,txtv_school_name_value,txtv_school_type_value,txtv_school_emailid_value,txtv_school_contacts_value,txtv_school_address_value,txtv_total_strength_value;
        }

    }


}
