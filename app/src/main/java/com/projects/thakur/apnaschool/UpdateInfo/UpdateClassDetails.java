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
import com.projects.thakur.apnaschool.Model.ClassDetails;
import com.projects.thakur.apnaschool.R;

import java.util.ArrayList;

public class UpdateClassDetails extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ArrayList<ClassDetails> NewClassDetails=new ArrayList<>();

    //List View
    ListView allclassDetails;

    //Inner class object
    ShowClassAdapter adapter;

    ProgressDialog mProgressDialog;

    // Read parent activity message and set school firbase ID
    private String school_firbasedataID,parentAtivityMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_class_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.update_class_info_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Classes Details");

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

        NewClassDetails.clear();

        allclassDetails=(ListView)findViewById(R.id.update_class_info_lv);

        adapter=new ShowClassAdapter(UpdateClassDetails.this,NewClassDetails);

        allclassDetails.setAdapter(adapter);

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
                Intent intent = new Intent(UpdateClassDetails.this, AddNewClassesInfo.class);
                intent.putExtra("EXTRA_CLASS_INFO_SESSION_ID", "ADD_NEW");
                startActivity(intent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getDataFromServer() {

        showProgressDialog();
        mDatabase.child("UserNode").child(school_firbasedataID).child("School_Classes_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    NewClassDetails.clear();

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        ClassDetails newclass=postSnapShot.getValue(ClassDetails.class);
                        NewClassDetails.add(newclass);
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
            mProgressDialog = new ProgressDialog(UpdateClassDetails.this);
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


    private class ShowClassAdapter extends BaseAdapter {

        Context c;
        LayoutInflater layoutInflater;

        ArrayList<ClassDetails> NewClassDetails;
        public ShowClassAdapter(Context c, ArrayList<ClassDetails> NewClassDetails) {
            this.c = c;
            layoutInflater = LayoutInflater.from(c);
            this.NewClassDetails = NewClassDetails;
        }

        @Override
        public int getCount() {
            return NewClassDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return NewClassDetails.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.model_show_all_class, null, false);
                holder = new ViewHolder();

                holder.txtv_class_name_value = (TextView) convertView.findViewById(R.id.txtv_class_name_value);
                holder.txtv_class_total_student_value = (TextView) convertView.findViewById(R.id.txtv_class_total_student_value);
                holder.txtv_class_agenda_value = (TextView) convertView.findViewById(R.id.txtv_class_agenda_value);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ClassDetails classDetails=NewClassDetails.get(position);
            holder.txtv_class_name_value.setText(classDetails.getClass_name());
            holder.txtv_class_total_student_value.setText(classDetails.getTotal_students());
            holder.txtv_class_agenda_value.setText(classDetails.getClass_agenda());

            final ClassDetails classobj= (ClassDetails) this.getItem(position);

            //ONITECLICK
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(c,s.getClass_name(),Toast.LENGTH_SHORT).show();

                    if(parentAtivityMsg.equals("OWNER")) {
                        Intent intent = new Intent(UpdateClassDetails.this, AddNewClassesInfo.class);
                        intent.putExtra("EXTRA_CLASS_INFO_SESSION_ID", classobj.getClass_name());
                        startActivity(intent);
                    }
                }
            });

            return convertView;
        }

        public class ViewHolder {
            TextView txtv_class_name_value, txtv_class_total_student_value,txtv_class_agenda_value;
        }

    }

}
