package com.projects.thakur.apnaschool.Task.VirtualTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
import com.projects.thakur.apnaschool.R;

import java.util.ArrayList;

public class ShowAllVirtualTaskActivity extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ArrayList<NewVirtualTaskModel> allVirtualTask=new ArrayList<>();

    //List View
    ListView allDetails;

    //Inner class object
    ShowVirtualTaskAdapter adapter;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_virtual_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_all_virtual_task_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("All Task");

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


        allVirtualTask.clear();

        allDetails=(ListView)findViewById(R.id.show_all_virtual_task_lv);

        adapter=new ShowVirtualTaskAdapter(ShowAllVirtualTaskActivity.this,allVirtualTask);

        allDetails.setAdapter(adapter);

        getDataFromServer();
    }

    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getDataFromServer() {

        showProgressDialog();
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("TASK").child("OPEN").child("VIRTUAL").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    allVirtualTask.clear();

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        NewVirtualTaskModel taskDetails = postSnapShot.getValue(NewVirtualTaskModel.class);
                        allVirtualTask.add(taskDetails);
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
            mProgressDialog = new ProgressDialog(ShowAllVirtualTaskActivity.this);
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

    private class ShowVirtualTaskAdapter extends BaseAdapter {

        Context c;
        LayoutInflater layoutInflater;

        ArrayList<NewVirtualTaskModel> achivDetails;
        public ShowVirtualTaskAdapter(Context c, ArrayList<NewVirtualTaskModel> achivDetails) {
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
                convertView = layoutInflater.inflate(R.layout.model_admin_show_all_question_task, null, false);
                holder = new ViewHolder();

                holder.txtv_admin_show_all_question_task_last_date_value = (TextView) convertView.findViewById(R.id.txtv_admin_show_all_question_task_last_date_value);
                holder.txtv_admin_show_all_question_task_heading_value = (TextView) convertView.findViewById(R.id.txtv_admin_show_all_question_task_heading_value);

                holder.txtv_admin_show_all_question_task_details = (TextView) convertView.findViewById(R.id.txtv_admin_show_all_question_task_details);
                holder.txtv_admin_show_all_question_task_extra = (TextView) convertView.findViewById(R.id.txtv_admin_show_all_question_task_extra);



                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            NewVirtualTaskModel acDetails=allVirtualTask.get(position);

            holder.txtv_admin_show_all_question_task_last_date_value.setText(acDetails.getTask_last_date());
            holder.txtv_admin_show_all_question_task_heading_value.setText(acDetails.getTask_heading());

            holder.txtv_admin_show_all_question_task_details.setText(acDetails.getTask_details());
            holder.txtv_admin_show_all_question_task_extra.setText("");


            final NewVirtualTaskModel obj= (NewVirtualTaskModel) this.getItem(position);

            //ONITECLICK
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(c,s.getClass_name(),Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ShowAllVirtualTaskActivity.this, ShowEachVirtualTaskActivity.class);
                    intent.putExtra("EXTRA_EACH_VIRTUAL_TASK_SESSION_ID", obj.getTask_firbase_ID());
                    startActivity(intent);
                }
            });

            return convertView;
        }

        public class ViewHolder {
            TextView txtv_admin_show_all_question_task_last_date_value, txtv_admin_show_all_question_task_heading_value,txtv_admin_show_all_question_task_details,txtv_admin_show_all_question_task_extra;
        }

    }

}
