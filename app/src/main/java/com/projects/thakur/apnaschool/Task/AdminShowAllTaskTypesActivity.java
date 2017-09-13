package com.projects.thakur.apnaschool.Task;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.projects.thakur.apnaschool.Auth.StartUpActivity;
import com.projects.thakur.apnaschool.R;
import com.projects.thakur.apnaschool.Task.QuestionTask.AdminCreateQuestionTaskActivity;
import com.projects.thakur.apnaschool.Task.QuestionTask.AdminShowAllQuestionTaskActivity;
import com.projects.thakur.apnaschool.Task.VirtualTask.CreateNewVirtualTaskActivity;
import com.projects.thakur.apnaschool.Task.VirtualTask.ShowAllVirtualTaskActivity;

public class AdminShowAllTaskTypesActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_show_question_type_task_details,btn_show_virtual_type_task_details;

    private String operationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_show_all_task_types);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_all_TaskType_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("New Task");

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
        btn_show_question_type_task_details = (Button) findViewById(R.id.btn_show_question_type_task_details);
        btn_show_virtual_type_task_details = (Button) findViewById(R.id.btn_show_virtual_type_task_details);

        // Click listeners
        btn_show_question_type_task_details.setOnClickListener(this);
        btn_show_virtual_type_task_details.setOnClickListener(this);

        operationStatus = getIntent().getStringExtra("EXTRA_SHOW_TASK_TYPE_SESSION_ID");

        // SHow virtual task only to district levels
        if(StartUpActivity.userDetails.getType().equals("State")){
            btn_show_virtual_type_task_details.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_question_type_task_details:

                if(operationStatus.equals("CREATE")) {
                    Intent intent_1 = new Intent(AdminShowAllTaskTypesActivity.this, AdminCreateQuestionTaskActivity.class);
                    intent_1.putExtra("EXTRA_QUESTION_TASK_SESSION_ID", "ADD_NEW");
                    startActivity(intent_1);

                } else {
                    Intent intent_1 = new Intent(AdminShowAllTaskTypesActivity.this, AdminShowAllQuestionTaskActivity.class);
                    intent_1.putExtra("EXTRA_QUESTION_TASK_SESSION_ID", "ADD_NEW");
                    startActivity(intent_1);
                }

                break;

            case R.id.btn_show_virtual_type_task_details:
                if(operationStatus.equals("CREATE")) {
                    Intent intent_1 = new Intent(AdminShowAllTaskTypesActivity.this, CreateNewVirtualTaskActivity.class);
                    intent_1.putExtra("EXTRA_VIRTUAL_TASK_SESSION_ID", "ADD_NEW");
                    startActivity(intent_1);
                } else {

                    Intent intent_1 = new Intent(AdminShowAllTaskTypesActivity.this, ShowAllVirtualTaskActivity.class);
                    intent_1.putExtra("EXTRA_VIRTUAL_TASK_SESSION_ID", "SHOW");
                    startActivity(intent_1);
                }
                break;

        }
    }
}
