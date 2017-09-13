package com.projects.thakur.apnaschool.AdminUser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.StateUsers.ShowStatesSchools;
import com.projects.thakur.apnaschool.UpdateInfo.ShowAchivmentsActivity;
import com.projects.thakur.apnaschool.UpdateInfo.ShowBasicInfoActivity;
import com.projects.thakur.apnaschool.UpdateInfo.ShowOpenPointsActivity;

public class ShowDisttAdminUserDetails extends AppCompatActivity implements View.OnClickListener{

    private Button btn_show_basic_info, btn_show_achivments_details,btn_show_all_schools_details_global,btn_show_all_teachers_details_global,btn_show_openpoints_distt_details;

    private String school_firbasedataID;

    private CardView card_view_global_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_distt_admin_user_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_distt_admin_user_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Information");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                finish();
            }
        });

        // configure buttons
        btn_show_basic_info = (Button) findViewById(R.id.btn_show_basic_info);
        btn_show_achivments_details = (Button) findViewById(R.id.btn_show_achivments_details);
        btn_show_all_schools_details_global = (Button) findViewById(R.id.btn_show_all_schools_details_global);
        btn_show_all_teachers_details_global = (Button) findViewById(R.id.btn_show_all_teachers_details_global);
        btn_show_openpoints_distt_details = (Button) findViewById(R.id.btn_show_openpoints_distt_details);

        // Click listeners
        btn_show_basic_info.setOnClickListener(this);
        btn_show_achivments_details.setOnClickListener(this);
        btn_show_all_schools_details_global.setOnClickListener(this);
        btn_show_all_teachers_details_global.setOnClickListener(this);
        btn_show_openpoints_distt_details.setOnClickListener(this);

        card_view_global_details = (CardView)  findViewById(R.id.card_view_global_details);

        // get parent activity status
        String parentAtivityMsg = getIntent().getStringExtra("EXTRA_SHOW_SCHOOL_SESSION_ID");
        if(parentAtivityMsg.equals("OWNER")){
            // Pass orignal ID
            school_firbasedataID = "OWNER";
            card_view_global_details.setVisibility(View.GONE);

        } else {
            // Pass school ID
            school_firbasedataID = parentAtivityMsg;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_basic_info:
                Intent intent = new Intent(ShowDisttAdminUserDetails.this, ShowBasicInfoActivity.class);
                intent.putExtra("EXTRA_SHOW_EACH_SCHOOL_SESSION_ID", school_firbasedataID);
                startActivity(intent);
                break;

            case R.id.btn_show_achivments_details:
                Intent intent_a = new Intent(ShowDisttAdminUserDetails.this, ShowAchivmentsActivity.class);
                intent_a.putExtra("EXTRA_ACHIVMENTS_INFO_SESSION_ID", school_firbasedataID);
                startActivity(intent_a);
                break;

            case R.id.btn_show_openpoints_distt_details:
                Intent intent_op = new Intent(ShowDisttAdminUserDetails.this, ShowOpenPointsActivity.class);
                intent_op.putExtra("EXTRA_OPENPOINTS_INFO_SESSION_ID", school_firbasedataID);
                startActivity(intent_op);
                break;

            case R.id.btn_show_all_schools_details_global:
                Intent intent_s = new Intent(ShowDisttAdminUserDetails.this, ShowAllSchoolsActivity.class);
                intent_s.putExtra("EXTRA_SHOW_ALL_SCHOOLS_SESSION_ID", school_firbasedataID);
                startActivity(intent_s);
                break;

            case R.id.btn_show_all_teachers_details_global:
                Intent intent_stask = new Intent(ShowDisttAdminUserDetails.this, ShowAllTeachersActivity.class);
                intent_stask.putExtra("EXTRA_SHOW_ALL_TEACHERS_SESSION_ID", school_firbasedataID);
                startActivity(intent_stask);
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.each_user_send_notif_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.each_user_send_notification) {

            if (!school_firbasedataID.equals("OWNER")) {
                Intent intent = new Intent(ShowDisttAdminUserDetails.this, SendNotificationActivity.class);
                intent.putExtra("EXTRA_NOTIFICATION_INFO_SESSION_ID", school_firbasedataID);
                startActivity(intent);
            }
        }


        return super.onOptionsItemSelected(item);
    }


}
