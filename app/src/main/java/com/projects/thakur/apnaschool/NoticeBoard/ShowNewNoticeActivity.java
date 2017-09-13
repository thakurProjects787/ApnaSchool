package com.projects.thakur.apnaschool.NoticeBoard;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.Model.NoticeDetails;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.UploadFiles.Upload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShowNewNoticeActivity extends AppCompatActivity {

    //Firebase database access
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    ArrayList<NoticeDetails> noticeDetails=new ArrayList<>();

    //List View
    ListView allDetails;

    //Inner class object
    ShowNoticeAdapter adapter;

    ProgressDialog mProgressDialog;
    private CharSequence options[];

    private String parentAtivityMsg,searchStatus = "ALL";

    NoticeDetails eachNoticeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_new_notice);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_new_notice_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("All Notice");

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
        parentAtivityMsg = getIntent().getStringExtra("EXTRA_ALL_NOTICE_INFO_SESSION_ID");

        noticeDetails.clear();

        allDetails=(ListView)findViewById(R.id.show_new_notice_lv);

        adapter=new ShowNoticeAdapter(ShowNewNoticeActivity.this,noticeDetails);

        allDetails.setAdapter(adapter);

        getDataFromServer();

    }

    // ------------------

    // getting the data from UserNode at Firebase and then adding the users in Arraylist and setting it to Listview
    public void getDataFromServer() {

        showProgressDialog();
        mDatabase.child("Global").child(StartUpActivity.userDetails.getState()).child("Notice_Board").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    noticeDetails.clear();

                    for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                    {
                        NoticeDetails noticeClass=postSnapShot.getValue(NoticeDetails.class);

                        // Compare with Search Status
                        // Search for ALL
                        if(searchStatus.equals("ALL")) {

                            noticeDetails.add(noticeClass);
                            adapter.notifyDataSetChanged();

                        } else if(searchStatus.equals("STATE")) {
                            // Search for state only
                            if(noticeClass.getNotice_user_type().equals("State")){

                                noticeDetails.add(noticeClass);
                                adapter.notifyDataSetChanged();
                            }
                        } else if(searchStatus.equals("DISTT")) {
                            // Search for district only
                            if(noticeClass.getNotice_user_district().equals(StartUpActivity.userDetails.getDistt())){

                                noticeDetails.add(noticeClass);
                                adapter.notifyDataSetChanged();

                            }
                        } else {
                            // Related to search operation
                            if( (searchStatus.toLowerCase().contains(noticeClass.getNotice_ID().toLowerCase())) || (searchStatus.toLowerCase().contains(noticeClass.getNotice_titles().toLowerCase())) || (searchStatus.toLowerCase().contains(noticeClass.getNotice_department().toLowerCase())) || (searchStatus.toLowerCase().contains(noticeClass.getNotice_approved_by().toLowerCase())) || (searchStatus.toLowerCase().contains(noticeClass.getNotice_announced_by().toLowerCase())) || (searchStatus.toLowerCase().contains(noticeClass.getNotice_user_district().toLowerCase()) )) {

                                noticeDetails.add(noticeClass);
                                adapter.notifyDataSetChanged();
                            }
                        }

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
            mProgressDialog = new ProgressDialog(ShowNewNoticeActivity.this);
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
        Adapter class
     */
    private class ShowNoticeAdapter extends BaseAdapter {

        Context c;
        LayoutInflater layoutInflater;

        ArrayList<NoticeDetails> noticeDetails;
        public ShowNoticeAdapter(Context c, ArrayList<NoticeDetails> noticeDetails) {
            this.c = c;
            layoutInflater = LayoutInflater.from(c);
            this.noticeDetails = noticeDetails;
        }

        @Override
        public int getCount() {
            return noticeDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return noticeDetails.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.model_show_all_notices, null, false);
                holder = new ViewHolder();

                holder.txtv_notice_date_value = (TextView) convertView.findViewById(R.id.txtv_notice_date_value);
                holder.txtv_notice_ID_value = (TextView) convertView.findViewById(R.id.txtv_notice_ID_value);
                holder.txtv_notice_title_value = (TextView) convertView.findViewById(R.id.txtv_notice_title_value);
                holder.txtv_notice_others_value = (TextView) convertView.findViewById(R.id.txtv_notice_others_value);



                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            NoticeDetails acDetails=noticeDetails.get(position);


            holder.txtv_notice_date_value.setText(acDetails.getNotice_date());
            holder.txtv_notice_ID_value.setText(acDetails.getNotice_ID());
            holder.txtv_notice_title_value.setText(acDetails.getNotice_titles());

            String details = acDetails.getNotice_department()+ "," + acDetails.getNotice_announced_by();
            holder.txtv_notice_others_value.setText(details);



            final NoticeDetails obj= (NoticeDetails) this.getItem(position);

            //ONITECLICK
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(c,s.getClass_name(),Toast.LENGTH_SHORT).show();

                    //showOtherOptions(obj);

                    Intent intent = new Intent(ShowNewNoticeActivity.this, ShowNoticeInDetailsActivity.class);
                    intent.putExtra("EXTRA_ALL_NOTICE_IN_DETAILS_SESSION_ID", obj.getNotice_firbase_ID());
                    startActivity(intent);


                }
            });

            return convertView;
        }

        public class ViewHolder {
            TextView txtv_notice_date_value, txtv_notice_ID_value,txtv_notice_title_value,txtv_notice_others_value;
        }

    }

    // Show option on click
    private void showOtherOptions(NoticeDetails noticeDetail){

        eachNoticeDetails = noticeDetail;
        // ************************************************************
        // --------- Alert Dialog Box with Option ---------------------

        if(parentAtivityMsg.equals("ADMIN")) {
            options = new CharSequence[]{"Download Attachments", "Delete Notice"};
        } else {
            options = new CharSequence[]{"Download Attachments"};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ShowNewNoticeActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Select your option:");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on options[which]


                // Selection of options
                if(options[which].equals("Download Attachments")){

                    // ---- Download all Attachments -----

                    //Toast.makeText(ShowNewNoticeActivity.this, eachNoticeDetails.getNotice_titles()+" - "+options[which], Toast.LENGTH_SHORT).show();

                    //getNoticeAllAttachments(eachNoticeDetails.getNotice_firbase_ID());

                }

                if(options[which].equals("Delete Notice")){

                    // ------ Delete Notice -------

                    if(parentAtivityMsg.equals("ADMIN")) {

                            //====================== ASK DIALOG BOX ========================================
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShowNewNoticeActivity.this,R.style.AppTheme_Dark_Dialog);
                            alertDialogBuilder.setTitle("DELETE NOTICE");
                            alertDialogBuilder.setMessage("Are you sure,You want to Delete Notice ?");

                            alertDialogBuilder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    //deleteComplete(eachNoticeDetails.getNotice_firbase_ID());

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


                        //Toast.makeText(ShowNewNoticeActivity.this, eachNoticeDetails.getNotice_titles() + " - " + options[which], Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(ShowNewNoticeActivity.this,"Permission Denied !!", Toast.LENGTH_SHORT).show();

                    }
                }


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //the user clicked on Cancel
            }
        });
        builder.show();

        // *************************************************************
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_all_notice_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.show_notice_all_notice) {
            searchStatus = "ALL";
            getDataFromServer();

            return true;
        }

        if (id == R.id.show_notice_only_state) {
            searchStatus = "STATE";
            getDataFromServer();

            return true;
        }

        if (id == R.id.show_notice_only_district) {
            searchStatus = "DISTT";
            getDataFromServer();

            return true;
        }

        if (id == R.id.show_notice_search) {

             /* Alert Dialog Code Start*/
            AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog);
            alert.setTitle("KEYWORD"); //Set Alert dialog title here
            alert.setMessage("Please type search keyword related to Name, ID, Place etc."); //Message here


            // Set an EditText view to get user input
            final EditText input = new EditText(getApplicationContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT );
            input.setTextColor(Color.BLACK);
            alert.setView(input);

            alert.setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //You will get as string input data in this variable.
                    // here we convert the input to a string and show in a toast.
                    String keyword = input.getEditableText().toString();
                    //Toast.makeText(getApplicationContext(),srt,Toast.LENGTH_LONG).show();
                    if (!keyword.isEmpty()) {

                        searchStatus = keyword;
                        getDataFromServer();

                    } else {
                        //Toast.makeText(getApplicationContext(), "Wrong Password !!", Toast.LENGTH_LONG).show();

                        dialog.cancel();
                    }


                } // End of onClick(DialogInterface dialog, int whichButton)
            }); //End of alert.setPositiveButton
            alert.setNegativeButton("RESET", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                    //Toast.makeText(getApplicationContext(), "Nothing!", Toast.LENGTH_LONG).show();
                    searchStatus = "ALL";
                    getDataFromServer();

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


}
