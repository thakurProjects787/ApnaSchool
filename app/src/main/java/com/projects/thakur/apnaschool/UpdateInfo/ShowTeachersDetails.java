package com.projects.thakur.apnaschool.UpdateInfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.projects.thakur.apnaschool.Model.TeachersDetails;
import com.projects.thakur.apnaschool.R;

import java.util.ArrayList;

public class ShowTeachersDetails extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ArrayList<TeachersDetails> NewTeacherDetails=new ArrayList<>();

    //List View
    ListView allDetails;

    //Inner class object
    ShowTeacherAdapter adapter;

    ProgressDialog mProgressDialog;

    // Read parent activity message and set school firbase ID
    private String school_firbasedataID,parentAtivityMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_teachers_details);


        Toolbar toolbar = (Toolbar) findViewById(R.id.show_teacher_details_toolbar);
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

        // get parent activity status
        parentAtivityMsg = getIntent().getStringExtra("EXTRA_SHOW_EACH_SCHOOL_SESSION_ID");
        if(parentAtivityMsg.equals("OWNER")){
            // Pass orignal ID
            school_firbasedataID = mAuth.getCurrentUser().getUid();
        } else {
            // Pass school ID
            school_firbasedataID = parentAtivityMsg;
        }

        NewTeacherDetails.clear();

        allDetails=(ListView)findViewById(R.id.show_teacher_details_lv);

        adapter=new ShowTeacherAdapter(ShowTeachersDetails.this,NewTeacherDetails);

        allDetails.setAdapter(adapter);

        getDataFromServer();


    } //end of onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_new_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_new_details) {

            if(parentAtivityMsg.equals("OWNER")) {
                Intent intent = new Intent(ShowTeachersDetails.this, AddNewTeachersInfo.class);
                intent.putExtra("EXTRA_TEACHER_INFO_SESSION_ID", "ADD_NEW");
                startActivity(intent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getDataFromServer() {

        showProgressDialog();
        mDatabase.child("UserNode").child(school_firbasedataID).child("Teachers_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    NewTeacherDetails.clear();

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        TeachersDetails newTeacher=postSnapShot.getValue(TeachersDetails.class);
                        NewTeacherDetails.add(newTeacher);
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
            mProgressDialog = new ProgressDialog(ShowTeachersDetails.this);
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

            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.model_show_all_teachers, null, false);
                holder = new ViewHolder();

                holder.txtv_teacher_id_value = (TextView) convertView.findViewById(R.id.txtv_teacher_id_value);
                holder.txtv_teacher_name_value = (TextView) convertView.findViewById(R.id.txtv_teacher_name_value);
                holder.txtv_teacher_designation_value = (TextView) convertView.findViewById(R.id.txtv_teacher_designation_value);

                holder.txtv_teacher_joining_value = (TextView) convertView.findViewById(R.id.txtv_teacher_joining_value);
                holder.txtv_teacher_edu_details_value = (TextView) convertView.findViewById(R.id.txtv_teacher_edu_details_value);
                holder.txtv_teacher_spec_area_value = (TextView) convertView.findViewById(R.id.txtv_teacher_spec_area_value);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
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

                    if(parentAtivityMsg.equals("OWNER")) {
                        Intent intent = new Intent(ShowTeachersDetails.this, AddNewTeachersInfo.class);
                        intent.putExtra("EXTRA_TEACHER_INFO_SESSION_ID", obj.getId());
                        startActivity(intent);
                    }
                }
            });

            return convertView;
        }

        public class ViewHolder {
            TextView txtv_teacher_id_value, txtv_teacher_name_value,txtv_teacher_designation_value,txtv_teacher_joining_value,txtv_teacher_edu_details_value,txtv_teacher_spec_area_value;
        }

    }


}
