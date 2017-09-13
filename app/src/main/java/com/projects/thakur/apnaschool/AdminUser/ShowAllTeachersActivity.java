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
import com.projects.thakur.apnaschool.Model.TeachersDetails;
import com.projects.thakur.apnaschool.Model.UserBasicDetails;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.UpdateInfo.AddNewTeachersInfo;
import com.projects.thakur.apnaschool.UpdateInfo.ShowTeachersDetails;

import java.util.ArrayList;

public class ShowAllTeachersActivity extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String searchKeyword;

    ArrayList<TeachersDetails> NewTeacherDetails=new ArrayList<>();

    //List View
    ListView allDetails;

    //Inner class object
    ShowAllTeachersActivity.ShowTeacherAdapter adapter;

    ProgressDialog mProgressDialog;

    // Read parent activity message and set school firbase ID
    private String school_firbasedataID,parentAtivityMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_teachers);


        Toolbar toolbar = (Toolbar) findViewById(R.id.show_all_teacher_details_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Teacher Details");

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

        searchKeyword = "ALL";

        // get parent activity status
        parentAtivityMsg = getIntent().getStringExtra("EXTRA_SHOW_ALL_TEACHERS_SESSION_ID");


        NewTeacherDetails.clear();

        allDetails=(ListView)findViewById(R.id.show_all_teacher_details_lv);

        adapter=new ShowAllTeachersActivity.ShowTeacherAdapter(ShowAllTeachersActivity.this,NewTeacherDetails);

        allDetails.setAdapter(adapter);

        // check according to the intent request
        if(parentAtivityMsg.equals("STATE")){
            school_firbasedataID = mAuth.getCurrentUser().getUid();
            getStateLevelData();

        }else if(parentAtivityMsg.equals("ADMIN")){
            school_firbasedataID = mAuth.getCurrentUser().getUid();

            getSchoolDataFromServer();
        }
        else {
            school_firbasedataID = parentAtivityMsg;

            getSchoolDataFromServer();
        }

    } //end of onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_all_teachers_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.filter_teachers_details_from_all) {

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

                        if(parentAtivityMsg.equals("STATE")){
                            getStateLevelData();
                        } else {
                            getSchoolDataFromServer();
                        }


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

                    if(parentAtivityMsg.equals("STATE")){
                        getStateLevelData();
                    } else {
                        getSchoolDataFromServer();
                    }

                    dialog.cancel();
                }
            }); //End of alert.setNegativeButton
            AlertDialog alertDialog = alert.create();
            alertDialog.show();
                /* Alert Dialog Code End*/

            return true;
        }


        if (id == R.id.filter_teachers_details_refresh_all) {
            //Toast.makeText(getApplicationContext(), "Refreshing !!", Toast.LENGTH_SHORT).show();

            searchKeyword = "ALL";

            if(parentAtivityMsg.equals("STATE")){
                getStateLevelData();
            } else {
                getSchoolDataFromServer();
            }

            return true;
        }


        if (id == R.id.filter_teachers_details_generate_details) {

            return true;
        }

        if (id == R.id.filter_teachers_details_show_counts) {

            Toast.makeText(getApplicationContext(),"Total Teachers's : "+Integer.toString(NewTeacherDetails.size()) , Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getEachSchoolTeachersDetails(String school_ID) {

        showProgressDialog();
        mDatabase.child("UserNode").child(school_ID).child("Teachers_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        TeachersDetails newTeacher=postSnapShot.getValue(TeachersDetails.class);

                        if (searchKeyword.equals("ALL")) {

                            NewTeacherDetails.add(newTeacher);
                            adapter.notifyDataSetChanged();

                        } else {

                            if((searchKeyword.toLowerCase().contains(newTeacher.getId().toLowerCase())) || (searchKeyword.toLowerCase().contains(newTeacher.getName().toLowerCase())) || (searchKeyword.toLowerCase().contains(newTeacher.getDesignation().toLowerCase()))){
                                NewTeacherDetails.add(newTeacher);
                                adapter.notifyDataSetChanged();
                            }

                        }

                        adapter.notifyDataSetChanged();
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

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(ShowAllTeachersActivity.this);
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


    private class ShowTeacherAdapter extends BaseAdapter {

        Context c;
        LayoutInflater layoutInflater;

        ArrayList<TeachersDetails> NewTeacherDetails;
        public ShowTeacherAdapter(Context c, ArrayList<TeachersDetails> NewTeacherDetails) {
            this.c = c;
            layoutInflater = LayoutInflater.from(c);
            this.NewTeacherDetails = NewTeacherDetails;
        }

        @Override
        public int getCount() {
            return NewTeacherDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return NewTeacherDetails.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ShowAllTeachersActivity.ShowTeacherAdapter.ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.model_show_all_teachers, null, false);
                holder = new ShowAllTeachersActivity.ShowTeacherAdapter.ViewHolder();

                holder.txtv_teacher_id_value = (TextView) convertView.findViewById(R.id.txtv_teacher_id_value);
                holder.txtv_teacher_name_value = (TextView) convertView.findViewById(R.id.txtv_teacher_name_value);
                holder.txtv_teacher_designation_value = (TextView) convertView.findViewById(R.id.txtv_teacher_designation_value);

                holder.txtv_teacher_joining_value = (TextView) convertView.findViewById(R.id.txtv_teacher_joining_value);
                holder.txtv_teacher_edu_details_value = (TextView) convertView.findViewById(R.id.txtv_teacher_edu_details_value);
                holder.txtv_teacher_spec_area_value = (TextView) convertView.findViewById(R.id.txtv_teacher_spec_area_value);

                convertView.setTag(holder);
            } else {
                holder = (ShowAllTeachersActivity.ShowTeacherAdapter.ViewHolder) convertView.getTag();
            }

            TeachersDetails teacherDetails=NewTeacherDetails.get(position);

            holder.txtv_teacher_id_value.setText(teacherDetails.getId());
            holder.txtv_teacher_name_value.setText(teacherDetails.getName());
            holder.txtv_teacher_designation_value.setText(teacherDetails.getDesignation());

            holder.txtv_teacher_joining_value.setText(teacherDetails.getJoin_date());
            holder.txtv_teacher_edu_details_value.setText(teacherDetails.getEducation_details());
            holder.txtv_teacher_spec_area_value.setText(teacherDetails.getSpecial_areas());

            final TeachersDetails obj= (TeachersDetails) this.getItem(position);

            //ONITECLICK
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(c,s.getClass_name(),Toast.LENGTH_SHORT).show();
                    String teacherDetails = "Name : "+obj.getName()+"\nID : "+obj.getId()+"\n\n"+"Total Teachers's : "+Integer.toString(NewTeacherDetails.size());
                    Toast.makeText(getApplicationContext(),teacherDetails , Toast.LENGTH_SHORT).show();

//                    if(parentAtivityMsg.equals("OWNER")) {
//                        Intent intent = new Intent(ShowAllTeachersActivity.this, AddNewTeachersInfo.class);
//                        intent.putExtra("EXTRA_TEACHER_INFO_SESSION_ID", obj.getId());
//                        startActivity(intent);
//                    }
                }
            });

            return convertView;
        }

        public class ViewHolder {
            TextView txtv_teacher_id_value, txtv_teacher_name_value,txtv_teacher_designation_value,txtv_teacher_joining_value,txtv_teacher_edu_details_value,txtv_teacher_spec_area_value;
        }

    }


    /*
       Read first all school details

     */
    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getSchoolDataFromServer() {

        showProgressDialog();
        mDatabase.child("UserNode").child(school_firbasedataID).child("Sub_User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    NewTeacherDetails.clear();

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        NewUserDetails allSchools=postSnapShot.getValue(NewUserDetails.class);
                        // Read school details
                        getEachSchoolTeachersDetails(allSchools.getNewuserID());

                    }

                    mDatabase.child("UserNode").child(school_firbasedataID).child("Sub_User").removeEventListener(this);
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
       Read state district details
     */

    public void getStateLevelData() {

        showProgressDialog();
        mDatabase.child("UserNode").child(school_firbasedataID).child("Sub_User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    NewTeacherDetails.clear();

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        NewUserDetails allSchools=postSnapShot.getValue(NewUserDetails.class);
                        // Read school details
                        getEachDisttSchoolDetails(allSchools.getNewuserID());

                    }

                    mDatabase.child("UserNode").child(school_firbasedataID).child("Sub_User").removeEventListener(this);
                }
                hideProgressDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    public void getEachDisttSchoolDetails(String firbase_ID) {

        showProgressDialog();
        mDatabase.child("UserNode").child(firbase_ID).child("Sub_User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())                {


                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        NewUserDetails allSchools=postSnapShot.getValue(NewUserDetails.class);
                        // Read school details
                        getEachSchoolTeachersDetails(allSchools.getNewuserID());

                    }

                    //mDatabase.child("UserNode").child(firbase_ID).child("Sub_User").removeEventListener(this);
                }
                hideProgressDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }


}
