package com.projects.thakur.apnaschool.UpdateInfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.projects.thakur.apnaschool.Model.AchivmentsDetails;
import com.projects.thakur.apnaschool.R;

import java.util.ArrayList;

public class ShowAchivmentsActivity extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ArrayList<AchivmentsDetails> achivDetails=new ArrayList<>();

    //List View
    ListView allDetails;

    //Inner class object
    ShowAchivmentsAdapter adapter;

    ProgressDialog mProgressDialog;

    // Read parent activity message and set school firbase ID
    private String school_firbasedataID,parentAtivityMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_achivments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_all_achivments_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Achivments");

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
        parentAtivityMsg = getIntent().getStringExtra("EXTRA_ACHIVMENTS_INFO_SESSION_ID");

        if(parentAtivityMsg.equals("OWNER")){
            // Pass orignal ID
            school_firbasedataID = mAuth.getCurrentUser().getUid();
        } else {
            // Pass school ID
            school_firbasedataID = parentAtivityMsg;
        }

        achivDetails.clear();

        allDetails=(ListView)findViewById(R.id.show_all_achivments_lv);

        adapter=new ShowAchivmentsAdapter(ShowAchivmentsActivity.this,achivDetails);

        allDetails.setAdapter(adapter);

        getDataFromServer();


    }


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
                Intent intent = new Intent(ShowAchivmentsActivity.this, AddNewAchivmentsActivity.class);
                intent.putExtra("EXTRA_ACHIVMENTS_INFO_SESSION_ID", "ADD_NEW");
                startActivity(intent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getDataFromServer() {

        showProgressDialog();
        mDatabase.child("UserNode").child(school_firbasedataID).child("Achivments_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    achivDetails.clear();

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        AchivmentsDetails achivClass=postSnapShot.getValue(AchivmentsDetails.class);
                        achivDetails.add(achivClass);
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
            mProgressDialog = new ProgressDialog(ShowAchivmentsActivity.this);
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


    private class ShowAchivmentsAdapter extends BaseAdapter {

        Context c;
        LayoutInflater layoutInflater;

        ArrayList<AchivmentsDetails> achivDetails;
        public ShowAchivmentsAdapter(Context c, ArrayList<AchivmentsDetails> achivDetails) {
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

            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.model_show_all_achivments, null, false);
                holder = new ViewHolder();

                holder.txtv_achivments_date_value = (TextView) convertView.findViewById(R.id.txtv_achivments_date_value);
                holder.txtv_achivments_title_value = (TextView) convertView.findViewById(R.id.txtv_achivments_title_value);
                holder.txtv_achivments_details = (TextView) convertView.findViewById(R.id.txtv_achivments_details);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AchivmentsDetails acDetails=achivDetails.get(position);

            holder.txtv_achivments_date_value.setText(acDetails.getAchv_date());
            holder.txtv_achivments_title_value.setText(acDetails.getAchv_titles());
            holder.txtv_achivments_details.setText(acDetails.getAchv_details());

            final AchivmentsDetails obj= (AchivmentsDetails) this.getItem(position);

            //ONITECLICK
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(c,s.getClass_name(),Toast.LENGTH_SHORT).show();

                    if(parentAtivityMsg.equals("OWNER")) {
                        Intent intent = new Intent(ShowAchivmentsActivity.this, AddNewAchivmentsActivity.class);
                        intent.putExtra("EXTRA_ACHIVMENTS_INFO_SESSION_ID", obj.getAchv_firbase_ID());
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

}
