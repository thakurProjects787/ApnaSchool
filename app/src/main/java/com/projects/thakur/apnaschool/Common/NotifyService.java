package com.projects.thakur.apnaschool.Common;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.thakur.apnaschool.AdminUser.ShowAllNotification;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.Model.AchivmentsDetails;
import com.projects.thakur.apnaschool.R;

public class NotifyService extends Service {

    private DatabaseReference mDatabase;

    String school_firbasedataID;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        school_firbasedataID = Logger.getDataFromkeyFile("keys.txt",this);

        //school_firbasedataID = "ipbJ935c6fUeL4dgP59LRJPk5a23";
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Context context = getApplicationContext();
        getDataFromServer();

        return android.app.Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }


    // Read Data from Firbase database
    public void getDataFromServer() {

        mDatabase.child("UserNode").child(school_firbasedataID).child("Notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    int notif_count = 0;
                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        AchivmentsDetails achivClass=postSnapShot.getValue(AchivmentsDetails.class);

                        // Display notification
                        displayNotification(achivClass.getAchv_titles(), achivClass.getAchv_date(),notif_count++);
                    }
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

