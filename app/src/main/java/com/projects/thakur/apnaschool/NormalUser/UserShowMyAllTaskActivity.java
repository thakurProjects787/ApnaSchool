package com.projects.thakur.apnaschool.NormalUser;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.thakur.apnaschool.Model.ShowUserAllTask;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.Task.QuestionTask.NewQuestionTaskModel;
import com.projects.thakur.apnaschool.Task.VirtualTask.NewVirtualTaskModel;

import java.util.ArrayList;

import static com.projects.thakur.apnaschool.Auth.StartUpActivity.userDetails;

public class UserShowMyAllTaskActivity extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ArrayList<ShowUserAllTask> allMyTask=new ArrayList<>();

    //List View
    ListView allDetails;

    //Inner class object
    ShowMyTaskAdapter adapter;
    private String showTaskType = "QUESTION";

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_show_my_all_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.user_show_all_mytask_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("My Task");

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


        allMyTask.clear();

        allDetails=(ListView)findViewById(R.id.user_show_all_mytask_lv);

        adapter=new ShowMyTaskAdapter(UserShowMyAllTaskActivity.this,allMyTask);

        allDetails.setAdapter(adapter);

        getAllQuestionTask();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.switch_btw_diff_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.switch_to_question_task) {

            showTaskType = "QUESTION";
            getAllQuestionTask();

            return true;
        }

        if (id == R.id.switch_to_virtual_task) {

            showTaskType = "VIRTUAL";
            getAllVirtualTask();

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getAllQuestionTask() {

        showProgressDialog();

        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("adminUserID").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mDatabase.child("UserNode").child(dataSnapshot.getValue().toString()).child("TASK").child("OPEN").child("QUESTION").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            allMyTask.clear();

                            for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                                NewQuestionTaskModel taskDetails = postSnapShot.getValue(NewQuestionTaskModel.class);

                                ShowUserAllTask taskInfo = new ShowUserAllTask();
                                taskInfo.setTaskID(taskDetails.getTask_firbase_ID());
                                taskInfo.setTaskHeading(taskDetails.getTask_heading());
                                taskInfo.setTaskLastDate(taskDetails.getTask_last_date());
                                taskInfo.setTaskType("QUESTION");

                                allMyTask.add(taskInfo);
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

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }

        });


    }

    public void getAllVirtualTask() {

        showProgressDialog();

        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("adminUserID").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mDatabase.child("UserNode").child(dataSnapshot.getValue().toString()).child("TASK").child("OPEN").child("VIRTUAL").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            allMyTask.clear();

                            for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                                NewVirtualTaskModel taskDetails = postSnapShot.getValue(NewVirtualTaskModel.class);

                                ShowUserAllTask taskInfo = new ShowUserAllTask();
                                taskInfo.setTaskID(taskDetails.getTask_firbase_ID());
                                taskInfo.setTaskHeading(taskDetails.getTask_heading());
                                taskInfo.setTaskLastDate(taskDetails.getTask_last_date());
                                taskInfo.setTaskType("VIRTUAL");

                                allMyTask.add(taskInfo);
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

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }

        });


    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(UserShowMyAllTaskActivity.this);
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

    private class ShowMyTaskAdapter extends BaseAdapter {

        Context c;
        LayoutInflater layoutInflater;

        ArrayList<ShowUserAllTask> achivDetails;
        public ShowMyTaskAdapter(Context c, ArrayList<ShowUserAllTask> achivDetails) {
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
                convertView = layoutInflater.inflate(R.layout.model_user_show_my_alltask, null, false);
                holder = new ViewHolder();

                holder.txtv_user_show_my_alltask_tasktype_value = (TextView) convertView.findViewById(R.id.txtv_user_show_my_alltask_tasktype_value);
                holder.txtv_user_show_my_alltask_task_heading_value = (TextView) convertView.findViewById(R.id.txtv_user_show_my_alltask_task_heading_value);
                holder.txtv_user_show_my_alltask_task_last_date_value = (TextView) convertView.findViewById(R.id.txtv_user_show_my_alltask_task_last_date_value);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ShowUserAllTask acDetails=allMyTask.get(position);

            holder.txtv_user_show_my_alltask_tasktype_value.setText(acDetails.getTaskType());
            holder.txtv_user_show_my_alltask_task_heading_value.setText(acDetails.getTaskHeading());
            holder.txtv_user_show_my_alltask_task_last_date_value.setText(acDetails.getTaskLastDate());


            final ShowUserAllTask obj= (ShowUserAllTask) this.getItem(position);

            //ONITECLICK
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(showTaskType.equals("QUESTION")) {
                        //Toast.makeText(c,s.getClass_name(),Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserShowMyAllTaskActivity.this, SubmitQuestionTaskActivity.class);
                        intent.putExtra("EXTRA_EACH_QUESTION_TASK_ANS_SESSION_ID", obj.getTaskID());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(UserShowMyAllTaskActivity.this, SubmitVirtualTaskActivity.class);
                        intent.putExtra("EXTRA_EACH_VIRTUAL_TASK_ANS_SESSION_ID", obj.getTaskID());
                        startActivity(intent);
                    }
                }
            });

            return convertView;
        }

        public class ViewHolder {
            TextView txtv_user_show_my_alltask_tasktype_value, txtv_user_show_my_alltask_task_heading_value,txtv_user_show_my_alltask_task_last_date_value;
        }

    }

}
