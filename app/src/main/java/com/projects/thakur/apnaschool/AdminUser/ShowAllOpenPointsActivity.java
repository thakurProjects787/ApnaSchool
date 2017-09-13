package com.projects.thakur.apnaschool.AdminUser;

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
import com.projects.thakur.apnaschool.Model.AchivmentsDetails;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.UpdateInfo.AddNewOpenPointsActivity;
import com.projects.thakur.apnaschool.UpdateInfo.ShowOpenPointsActivity;

import java.util.ArrayList;

public class ShowAllOpenPointsActivity extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ArrayList<AchivmentsDetails> openPointsDetails=new ArrayList<>();

    //List View
    ListView allDetails;

    //Inner class object
    ShowAllOpenPointsActivity.ShowOpenPointsAdapter adapter;

    ProgressDialog mProgressDialog;

    // Read parent activity message and set school firbase ID
    private String school_firbasedataID,parentAtivityMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_open_points);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_all_open_points_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Open Points");

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
        parentAtivityMsg = getIntent().getStringExtra("EXTRA_OPENPOINTS_INFO_SESSION_ID");

        if(parentAtivityMsg.equals("ADMIN")){
            // Pass orignal ID
            school_firbasedataID = mAuth.getCurrentUser().getUid();
        } else {
            // Pass school ID
            school_firbasedataID = parentAtivityMsg;
        }

        openPointsDetails.clear();

        allDetails=(ListView)findViewById(R.id.show_all_open_points_lv);

        adapter=new ShowAllOpenPointsActivity.ShowOpenPointsAdapter(ShowAllOpenPointsActivity.this,openPointsDetails);

        allDetails.setAdapter(adapter);

        getSchoolDataFromServer();


    }



    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getEachSchoolsOpenPoints(String schoold_ID) {

        showProgressDialog();
        mDatabase.child("UserNode").child(schoold_ID).child("Open_Points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    //openPointsDetails.clear();

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        AchivmentsDetails openPointsClass=postSnapShot.getValue(AchivmentsDetails.class);
                        openPointsDetails.add(openPointsClass);
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
            mProgressDialog = new ProgressDialog(ShowAllOpenPointsActivity.this);
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


    private class ShowOpenPointsAdapter extends BaseAdapter {

        Context c;
        LayoutInflater layoutInflater;

        ArrayList<AchivmentsDetails> achivDetails;
        public ShowOpenPointsAdapter(Context c, ArrayList<AchivmentsDetails> achivDetails) {
            this.c = c;
            layoutInflater = LayoutInflater.from(c);
            this.achivDetails = achivDetails;
        }

        @Override
        public int getCount() {
            return achivDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return achivDetails.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ShowAllOpenPointsActivity.ShowOpenPointsAdapter.ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.model_show_all_achivments, null, false);
                holder = new ShowAllOpenPointsActivity.ShowOpenPointsAdapter.ViewHolder();

                holder.txtv_achivments_date_value = (TextView) convertView.findViewById(R.id.txtv_achivments_date_value);
                holder.txtv_achivments_title_value = (TextView) convertView.findViewById(R.id.txtv_achivments_title_value);
                holder.txtv_achivments_details = (TextView) convertView.findViewById(R.id.txtv_achivments_details);


                convertView.setTag(holder);
            } else {
                holder = (ShowAllOpenPointsActivity.ShowOpenPointsAdapter.ViewHolder) convertView.getTag();
            }

            AchivmentsDetails acDetails=achivDetails.get(position);

            // Find Tags
            String tags = acDetails.getAchv_date().split("##")[1]+" - "+acDetails.getAchv_date().split("##")[0];

            holder.txtv_achivments_date_value.setText(tags);
            holder.txtv_achivments_title_value.setText(acDetails.getAchv_titles());
            holder.txtv_achivments_details.setText(acDetails.getAchv_details());

            final AchivmentsDetails obj= (AchivmentsDetails) this.getItem(position);

            //ONITECLICK
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(c,s.getClass_name(),Toast.LENGTH_SHORT).show();

                    if(parentAtivityMsg.equals("OWNER")) {
                        Intent intent = new Intent(ShowAllOpenPointsActivity.this, AddNewOpenPointsActivity.class);
                        intent.putExtra("EXTRA_OPENPOINTS_INFO_SESSION_ID", obj.getAchv_firbase_ID());
                        startActivity(intent);
                    }
                }
            });

            return convertView;
        }

        public class ViewHolder {
            TextView txtv_achivments_date_value, txtv_achivments_title_value,txtv_achivments_details;
        }

    }

    // Getting all school data
    public void getSchoolDataFromServer() {

        showProgressDialog();
        mDatabase.child("UserNode").child(school_firbasedataID).child("Sub_User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    openPointsDetails.clear();

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        NewUserDetails allSchools=postSnapShot.getValue(NewUserDetails.class);
                        // Read school details
                        getEachSchoolsOpenPoints(allSchools.getNewuserID());

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

}
