package com.projects.thakur.apnaschool.AdminUser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.projects.thakur.apnaschool.AdminUser.NewUserDetails;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.UpdateInfo.ShowEachSchoolDetails;

import java.util.ArrayList;

public class ShowAllSchoolsActivity extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ArrayList<UserBasicDetails> allEachSchoolsDetails = new ArrayList<>();

    //Get all schools location details
    ArrayList<String> allSchoolsLocations = new ArrayList<>();

    //List View
    ListView allDetails;

    //Inner class object
    ShowSchoolAdapter adapter;

    ProgressDialog mProgressDialog;

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

        allDetails=(ListView)findViewById(R.id.show_all_schools_details_lv);

        adapter=new ShowSchoolAdapter(ShowAllSchoolsActivity.this,allEachSchoolsDetails);

        allDetails.setAdapter(adapter);

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

            //Intent intent = new Intent(ShowAllSchoolsActivity.this, AddNewTeachersInfo.class);
            //intent.putExtra("EXTRA_TEACHER_INFO_SESSION_ID", "ADD_NEW");
            //startActivity(intent);

            return true;
        }

        if (id == R.id.send_all_schools_details_on_mail) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getDataFromServer() {

        showProgressDialog();
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Sub_User").addValueEventListener(new ValueEventListener() {
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

        // app_title change listener
        mDatabase.child("UserNode").child(schoolDataID).child("School_Basic_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // each school details
                UserBasicDetails schoolDetails = dataSnapshot.getValue(UserBasicDetails.class);

                allEachSchoolsDetails.add(schoolDetails);
                adapter.notifyDataSetChanged();

                if(schoolDetails!=null) {

                    allSchoolsLocations.add(schoolDetails.getGps_location());
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to read app title value.", error.toException());
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
