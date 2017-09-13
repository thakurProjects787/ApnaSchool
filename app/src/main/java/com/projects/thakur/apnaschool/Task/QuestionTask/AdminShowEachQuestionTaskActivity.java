package com.projects.thakur.apnaschool.Task.QuestionTask;

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
import com.projects.thakur.apnaschool.AdminUser.AdminHome;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.Common.CreateExcelReport;
import com.projects.thakur.apnaschool.Common.Logger;
import com.projects.thakur.apnaschool.NormalUser.NormalUserActivity;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.StateUsers.StateHome;
import com.projects.thakur.apnaschool.Task.AdminShowAllTaskTypesActivity;
import com.projects.thakur.apnaschool.Task.VirtualTask.ShowEachVirtualTaskActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AdminShowEachQuestionTaskActivity extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ArrayList<QuestionResultModel> allQuestionTask=new ArrayList<>();

    private String questionTaskID,todaySubmitDate;

    private Context context;

    //List View
    ListView allDetails;

    //Inner class object
    ShowQuestionTaskAdapter adapter;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_each_question_task);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_each_question_task_toolbar);
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

        // Get current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy_MM_dd");

        todaySubmitDate = mdformat.format(calendar.getTime());

        questionTaskID = getIntent().getStringExtra("EXTRA_EACH_QUESTION_TASK_SESSION_ID");


        allQuestionTask.clear();

        allDetails=(ListView)findViewById(R.id.show_each_question_task_lv);

        adapter=new ShowQuestionTaskAdapter(AdminShowEachQuestionTaskActivity.this,allQuestionTask);

        allDetails.setAdapter(adapter);

        getTaskDetails();

        getDataFromServer();


    }

    public void getTaskDetails() {

        new Logger().deleteFile("TaskData.txt",context);

        showProgressDialog();
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("TASK").child("OPEN").child("QUESTION").child(questionTaskID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    NewQuestionTaskModel taskDetails = dataSnapshot.getValue(NewQuestionTaskModel.class);
                    String task_d = "TASK@"+taskDetails.getTask_heading()+"&"+taskDetails.getTask_last_date()+"&"+taskDetails.getTask_details().replace("\n","%");

                    new Logger().addDataIntoFile("TaskData.txt",task_d,context);
                }
                hideProgressDialog();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }

    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getDataFromServer() {

        showProgressDialog();
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("TASK").child("OPEN").child("QUESTION").child(questionTaskID).child("RESULT").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    allQuestionTask.clear();

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        QuestionResultModel taskDetails = postSnapShot.getValue(QuestionResultModel.class);

                        String eachDetails = "EACHDETAILS@"+taskDetails.getSchoolDetails()+"#"+taskDetails.getAnswer().replace("\n","%");
                        new Logger().addDataIntoFile("TaskData.txt",eachDetails,context);

                        allQuestionTask.add(taskDetails);
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
            mProgressDialog = new ProgressDialog(AdminShowEachQuestionTaskActivity.this);
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

    private class ShowQuestionTaskAdapter extends BaseAdapter {

        Context c;
        LayoutInflater layoutInflater;

        ArrayList<QuestionResultModel> achivDetails;
        public ShowQuestionTaskAdapter(Context c, ArrayList<QuestionResultModel> achivDetails) {
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
                convertView = layoutInflater.inflate(R.layout.model_admin_show_each_question_task, null, false);
                holder = new ViewHolder();

                holder.txtv_admin_show_all_question_task_school_id_value = (TextView) convertView.findViewById(R.id.txtv_admin_show_all_question_task_school_id_value);
                holder.txtv_admin_show_all_question_task_school_name_value = (TextView) convertView.findViewById(R.id.txtv_admin_show_all_question_task_school_name_value);
                holder.txtv_admin_show_all_question_task_school_answer_value = (TextView) convertView.findViewById(R.id.txtv_admin_show_all_question_task_school_answer_value);



                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            QuestionResultModel acDetails=allQuestionTask.get(position);

            holder.txtv_admin_show_all_question_task_school_id_value.setText(acDetails.getSchoolDetails().split("&")[0]);
            holder.txtv_admin_show_all_question_task_school_name_value.setText(acDetails.getSchoolDetails().split("&")[1]);
            holder.txtv_admin_show_all_question_task_school_answer_value.setText(acDetails.getAnswer());


            final QuestionResultModel obj= (QuestionResultModel) this.getItem(position);

            //ONITECLICK
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(c,s.getClass_name(),Toast.LENGTH_SHORT).show();
                    //Intent intent = new Intent(ShowAchivmentsActivity.this, AddNewAchivmentsActivity.class);
                    //intent.putExtra("EXTRA_ACHIVMENTS_INFO_SESSION_ID", obj.getAchv_firbase_ID());
                    //startActivity(intent);
                }
            });

            return convertView;
        }

        public class ViewHolder {
            TextView txtv_admin_show_all_question_task_school_id_value, txtv_admin_show_all_question_task_school_name_value,txtv_admin_show_all_question_task_school_answer_value;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.admin_task_edit_option) {
                Intent intent = new Intent(AdminShowEachQuestionTaskActivity.this, AdminCreateQuestionTaskActivity.class);
                intent.putExtra("EXTRA_QUESTION_TASK_SESSION_ID", questionTaskID);
                startActivity(intent);

            return true;
        }

        if (id == R.id.admin_task_resolve_option) {

            //====================== ASK DIALOG BOX ========================================
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,R.style.AppTheme_Dark_Dialog);
            alertDialogBuilder.setTitle("Resolve");
            alertDialogBuilder.setMessage("Have to generated report?\nAre you sure,You want to Resolve Current Task ?");

            alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                    resolveTask();

                    if (StartUpActivity.userDetails.getType().equals("State")) {
                        Intent intent = new Intent(AdminShowEachQuestionTaskActivity.this, StateHome.class);
                        startActivity(intent);
                    } else if (StartUpActivity.userDetails.getType().equals("Admin")) {
                        Intent intent = new Intent(AdminShowEachQuestionTaskActivity.this, AdminHome.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(AdminShowEachQuestionTaskActivity.this, NormalUserActivity.class);
                        startActivity(intent);
                    }



                }
            });

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Cancel !!", Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            //======================================================================================

            return true;
        }

        if (id == R.id.admin_task_send_report_option) {

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


                        String fileName = "TaskReport_"+todaySubmitDate+".xls";

                        if(new CreateExcelReport().generateQuestionTaskReport(fileName,context,keyword)){
                            Toast.makeText(getApplicationContext(), "Report Generated : "+fileName, Toast.LENGTH_LONG).show();
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

                    String fileName = "TaskReport_"+todaySubmitDate+".xls";

                    if(new CreateExcelReport().generateQuestionTaskReport(fileName,context,"DEFAULT")){
                        Toast.makeText(getApplicationContext(), "Report Generated : "+fileName, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Report Not Generated", Toast.LENGTH_LONG).show();
                    }




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

    // Delete current details
    private void resolveTask(){


        showProgressDialog();

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("TASK").child("OPEN").child("QUESTION").child(questionTaskID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataSnapshot.getRef().setValue(null);

                Toast.makeText(AdminShowEachQuestionTaskActivity.this, questionTaskID+" Deleted!!",
                        Toast.LENGTH_LONG).show();

                hideProgressDialog();

                Intent intent_1 = new Intent(AdminShowEachQuestionTaskActivity.this, AdminHome.class);
                startActivity(intent_1);

                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to delete Values.", error.toException());

                hideProgressDialog();

                finish();
            }
        });

        finish();

    }


}
