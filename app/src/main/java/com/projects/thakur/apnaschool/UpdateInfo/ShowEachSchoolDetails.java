package com.projects.thakur.apnaschool.UpdateInfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.projects.thakur.apnaschool.AdminUser.SendNotificationActivity;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.UploadFiles.ShowGalleryActivity;

public class ShowEachSchoolDetails extends AppCompatActivity implements View.OnClickListener{

    private Button btn_show_basic_info, btn_show_class_details, btn_show_teachers_details, btn_show_mdm_details,btn_show_achivments_details,btn_show_openpoints_details,btn_show_gallery_details;

    private String school_firbasedataID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_each_school_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_each_school_infor_toolbar);
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

        // get parent activity status
        String parentAtivityMsg = getIntent().getStringExtra("EXTRA_SHOW_SCHOOL_SESSION_ID");
        if(parentAtivityMsg.equals("OWNER")){
            // Pass orignal ID
            school_firbasedataID = "OWNER";
        } else {
            // Pass school ID
            school_firbasedataID = parentAtivityMsg;
        }

        // configure buttons
        btn_show_basic_info = (Button) findViewById(R.id.btn_show_basic_info);
        btn_show_class_details = (Button) findViewById(R.id.btn_show_class_details);
        btn_show_teachers_details = (Button) findViewById(R.id.btn_show_teachers_details);
        btn_show_mdm_details = (Button) findViewById(R.id.btn_show_mdm_details);
        btn_show_achivments_details = (Button) findViewById(R.id.btn_show_achivments_details);
        btn_show_openpoints_details = (Button) findViewById(R.id.btn_show_openpoints_details);
        btn_show_gallery_details = (Button) findViewById(R.id.btn_show_gallery_details);

        // Click listeners
        btn_show_basic_info.setOnClickListener(this);
        btn_show_class_details.setOnClickListener(this);
        btn_show_teachers_details.setOnClickListener(this);
        btn_show_mdm_details.setOnClickListener(this);
        btn_show_achivments_details.setOnClickListener(this);
        btn_show_openpoints_details.setOnClickListener(this);
        btn_show_gallery_details.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_basic_info:
                Intent intent = new Intent(ShowEachSchoolDetails.this, ShowBasicInfoActivity.class);
                intent.putExtra("EXTRA_SHOW_EACH_SCHOOL_SESSION_ID", school_firbasedataID);
                startActivity(intent);
                break;

            case R.id.btn_show_class_details:
                Intent intent_c = new Intent(ShowEachSchoolDetails.this, UpdateClassDetails.class);
                intent_c.putExtra("EXTRA_SHOW_EACH_SCHOOL_SESSION_ID", school_firbasedataID);
                startActivity(intent_c);
                break;
            case R.id.btn_show_teachers_details:
                Intent intent_t = new Intent(ShowEachSchoolDetails.this, ShowTeachersDetails.class);
                intent_t.putExtra("EXTRA_SHOW_EACH_SCHOOL_SESSION_ID", school_firbasedataID);
                startActivity(intent_t);
                break;
            case R.id.btn_show_mdm_details:
                Intent intent_m = new Intent(ShowEachSchoolDetails.this, ShowMDMInfoActivity.class);
                intent_m.putExtra("EXTRA_SHOW_EACH_SCHOOL_SESSION_ID", school_firbasedataID);
                startActivity(intent_m);
                break;

            case R.id.btn_show_achivments_details:
                Intent intent_a = new Intent(ShowEachSchoolDetails.this, ShowAchivmentsActivity.class);
                intent_a.putExtra("EXTRA_ACHIVMENTS_INFO_SESSION_ID", school_firbasedataID);
                startActivity(intent_a);
                break;

            case R.id.btn_show_openpoints_details:
                Intent intent_op = new Intent(ShowEachSchoolDetails.this, ShowOpenPointsActivity.class);
                intent_op.putExtra("EXTRA_OPENPOINTS_INFO_SESSION_ID", school_firbasedataID);
                startActivity(intent_op);
                break;

            case R.id.btn_show_gallery_details:
                Intent intent_gl = new Intent(ShowEachSchoolDetails.this, ShowGalleryActivity.class);
                intent_gl.putExtra("EXTRA_GALLERY_INFO_SESSION_ID", school_firbasedataID);
                startActivity(intent_gl);
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
                Intent intent = new Intent(ShowEachSchoolDetails.this, SendNotificationActivity.class);
                intent.putExtra("EXTRA_NOTIFICATION_INFO_SESSION_ID", school_firbasedataID);
                startActivity(intent);
            }
        }


        return super.onOptionsItemSelected(item);
    }


}
