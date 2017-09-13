package com.projects.thakur.apnaschool.AdminUser;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.thakur.apnaschool.Common.Logger;
import com.projects.thakur.apnaschool.Common.NotifyService;
import com.projects.thakur.apnaschool.Model.AchivmentsDetails;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.UpdateInfo.AddNewAchivmentsActivity;
import com.projects.thakur.apnaschool.UpdateInfo.ShowAchivmentsActivity;

import java.util.ArrayList;

public class ShowAllNotification extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ArrayList<AchivmentsDetails> achivDetails=new ArrayList<>();

    //List View
    ListView allDetails;

    private String currentNotificationKey,senderKey,title;

    //Inner class object
    ShowNotificationAdapter adapter;

    ProgressDialog mProgressDialog;

    // Read parent activity message and set school firbase ID
    private String school_firbasedataID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_notification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_all_notification_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Notification");

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

        school_firbasedataID = mAuth.getCurrentUser().getUid();

        achivDetails.clear();

        allDetails=(ListView)findViewById(R.id.show_all_notification_lv);

        adapter=new ShowAllNotification.ShowNotificationAdapter(ShowAllNotification.this,achivDetails);

        allDetails.setAdapter(adapter);

        getDataFromServer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_notification_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.send_notification_clear_all) {
            clearAllNotif();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getDataFromServer() {

        showProgressDialog();
        mDatabase.child("UserNode").child(school_firbasedataID).child("Notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    achivDetails.clear();

                    int notif_count = 0;
                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        AchivmentsDetails achivClass=postSnapShot.getValue(AchivmentsDetails.class);
                        achivDetails.add(achivClass);
                        adapter.notifyDataSetChanged();
                    }

                    mDatabase.child("UserNode").child(school_firbasedataID).child("Notification").removeEventListener(this);
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
            mProgressDialog = new ProgressDialog(ShowAllNotification.this);
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

    /*
     Delete notification sub Tree
    */
    private void clearAllNotif(){

        // app_title change listener
        mDatabase.child("UserNode").child(school_firbasedataID).child("Notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(null);

                Toast.makeText(ShowAllNotification.this, "Clear All!!",
                        Toast.LENGTH_SHORT).show();

                mDatabase.child("UserNode").child(school_firbasedataID).child("Notification").removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to delete Values.", error.toException());

            }
        });

    }

    private void deleteNotif(String NotifID){

        // app_title change listener
        mDatabase.child("UserNode").child(school_firbasedataID).child("Notification").child(NotifID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(null);

                Toast.makeText(ShowAllNotification.this, "Notification Deleted!!",
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to delete Values.", error.toException());

            }
        });

    }


    private class ShowNotificationAdapter extends BaseAdapter {

        Context c;
        LayoutInflater layoutInflater;

        ArrayList<AchivmentsDetails> achivDetails;
        public ShowNotificationAdapter(Context c, ArrayList<AchivmentsDetails> achivDetails) {
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

            ShowAllNotification.ShowNotificationAdapter.ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.model_show_all_achivments, null, false);
                holder = new ShowAllNotification.ShowNotificationAdapter.ViewHolder();

                holder.txtv_notification_date_value = (TextView) convertView.findViewById(R.id.txtv_achivments_date_value);
                holder.txtv_notification_title_value = (TextView) convertView.findViewById(R.id.txtv_achivments_title_value);
                holder.txtv_notification_details = (TextView) convertView.findViewById(R.id.txtv_achivments_details);


                convertView.setTag(holder);
            } else {
                holder = (ShowAllNotification.ShowNotificationAdapter.ViewHolder) convertView.getTag();
            }

            AchivmentsDetails acDetails=achivDetails.get(position);

            holder.txtv_notification_date_value.setText(acDetails.getAchv_date());
            holder.txtv_notification_title_value.setText(acDetails.getAchv_titles());
            holder.txtv_notification_details.setText(acDetails.getAchv_details());

            final AchivmentsDetails acDetails_obj= (AchivmentsDetails) this.getItem(position);

            //ONITECLICK
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(c,s.getClass_name(),Toast.LENGTH_SHORT).show();

                    // Ask for reply or delete notification
                    /* Alert Dialog Code Start*/


                    currentNotificationKey = acDetails_obj.getAchv_firbase_ID().split("&&")[0];
                    senderKey = acDetails_obj.getAchv_firbase_ID().split("&&")[1];
                    title = acDetails_obj.getAchv_titles();


                    AlertDialog.Builder alert = new AlertDialog.Builder(ShowAllNotification.this, R.style.AppTheme_Dark_Dialog);
                    alert.setTitle("Notification Option"); //Set Alert dialog title here
                    alert.setMessage(title); //Message here

                    alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Toast.makeText(getApplicationContext(), "Delete Option!!", Toast.LENGTH_LONG).show();

                            deleteNotif(currentNotificationKey);
                            finish();

                        } // End of onClick(DialogInterface dialog, int whichButton)
                    }); //End of alert.setPositiveButton
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                    }); //End of alert.setNegativeButton
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                /* Alert Dialog Code End*/



                }
            });

            return convertView;
        }

        public class ViewHolder {
            TextView txtv_notification_date_value, txtv_notification_title_value,txtv_notification_details;
        }

    }

    /*
      Create Notification Builder
     */
    public void displayNotification(String notifTitle, String notifText, int mNotificationId) {

        //Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(this);

        // Call show all Activity after selecting notification
        Intent intent = new Intent(this, ShowAllNotification.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.ic_show_notif);
        mBuilder.setContentTitle(notifTitle);
        mBuilder.setContentText(notifText);


        // Gets an instance of the NotificationManager service//

        NotificationManager mNotificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//

        mNotificationManager.notify(mNotificationId, mBuilder.build());
    }

}
