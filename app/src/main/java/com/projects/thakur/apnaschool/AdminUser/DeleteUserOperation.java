package com.projects.thakur.apnaschool.AdminUser;


import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projects.thakur.apnaschool.Common.SendMail;

public class DeleteUserOperation {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String emailID = "";

    private String schoolID = "";

    private Context context;

    public DeleteUserOperation(String emailID,Context context) {

        // Firbase database access
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        this.emailID = emailID;
        this.context = context;

    }

    /*
      Get all schools Details
     */

    public void deleteSchoolDetails() {

        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Sub_User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {


                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        NewUserDetails allSchools=postSnapShot.getValue(NewUserDetails.class);

                        if(allSchools.getNewEmailID().toString().equals(emailID)){

                            schoolID = allSchools.getNewuserID().toString();

                            mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Sub_User").removeEventListener(this);

                            if(!schoolID.equals("")){
                                deleteMainTree(schoolID);
                                deleteFromAdminTree(schoolID);
                            }

                            break;
                        }

                    }




                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
      Delete Main Tree
     */
    private void deleteMainTree(String schoolID){

        // app_title change listener
        mDatabase.child("UserNode").child(schoolID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataSnapshot.getRef().setValue(null);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to delete Values.", error.toException());

            }
        });

    }

    /*
     Delete admin sub Tree
    */
    private void deleteFromAdminTree(String schoolID){

        // app_title change listener
        mDatabase.child("UserNode").child(mAuth.getCurrentUser().getUid()).child("Sub_User").child(schoolID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataSnapshot.getRef().setValue(null);

                sendMail();

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(">> ", "Failed to delete Values.", error.toException());

            }
        });

    }

    /*
      Send email
     */
    private void sendMail(){
        // Send MAIL
        String userBody="Dear User"+"\n \n School has been deleted from SchoolTrac Database.\n " +
                "\nSchool Email ID : "+emailID+"\nSchool Database ID : "+schoolID+
                "\n\n Thanks \n School Trace. ";

        String userSub="School Deleted";

        String[] emaildetails={"thakur.projects787@gmail.com"};

        new SendMail(userSub, userBody, emaildetails,"NO", context).send();
    }

}
