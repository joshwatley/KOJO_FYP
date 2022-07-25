package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_app.Models.Task;
import com.example.fyp_app.Models.TaskGroups;
import com.example.fyp_app.Models.User;
import com.google.android.material.chip.Chip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditTaskActivity extends AppCompatActivity {

    EditText ttitle;
    EditText tcontent;
    TextView toolbartitle;
    TextView datecreated;
    Chip editmode;
    TextView taskheader;
    EditText datedue;
    EditText flagpriority;
    String taskpriority;
    Boolean allowUserUpdate = false;
    Button saveedit;
    Button deleteTask;
    Button addUser;
    String theTId;
    Button changeFlag;

    List<User> mUsers;


    Intent intent;
    Task openTask;
    FirebaseDatabase fb;
    DatabaseReference reference;

    DatabaseReference deltaskref;
    DatabaseReference delGroupTaskref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        mUsers = new ArrayList<>();

        editmode = findViewById(R.id.chip);
        ttitle = findViewById(R.id.showtasktitle);
        tcontent = findViewById(R.id.showtaskcontent);
        datecreated = findViewById(R.id.datecreated);
        taskheader = findViewById(R.id.showtaskheading);
        datedue  = findViewById(R.id.datedue);
        flagpriority = findViewById(R.id.trafficpos);
        saveedit = findViewById(R.id.saveedittask);
        saveedit.setEnabled(false);
        changeFlag = findViewById(R.id.changetraffic);
        changeFlag.setEnabled(false);
        deleteTask = findViewById(R.id.deletetask);

        toolbartitle = findViewById(R.id.ftitle);
        // get details about the task

        intent = getIntent();

        // SET ALL THE TASK INFORMATION

        reference = FirebaseDatabase.getInstance().getReference("Tasks").child(intent.getStringExtra("taskid"));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                openTask = snapshot.getValue(Task.class);

                // setting data about the task

                toolbartitle.setText("Editing " + openTask.getTask_title());
                ttitle.setText(openTask.getTask_title());
                tcontent.setText(openTask.getTask_content());
                datecreated.setText("Task created: " + openTask.getDate_created());

                taskheader.setText(openTask.getHeader());
                taskpriority = openTask.getPriority();
                datedue.setText(openTask.getDate_due());

                // show the flag of the task
                switch (Integer.valueOf(taskpriority)){
                    case 0: // to do
                        flagpriority.setText("Current Flag: TO DO" );
                        break;
                    case 1: // in progress
                        flagpriority.setText("Current Flag: IN PROGRESS" );
                        break;
                    case 2: // done
                        flagpriority.setText("Current Flag: DONE" );
                        break;
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });


        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        editmode.setOnClickListener(v -> {
            if (editmode.isChecked()){
                //if edit mode is on
                allowUserUpdate = true;
                saveedit.setEnabled(true);
                changeFlag.setEnabled(true);
                ttitle.setFocusableInTouchMode(true);
                ttitle.setTypeface(null, Typeface.BOLD_ITALIC);
                tcontent.setFocusableInTouchMode(true);
                tcontent.setTypeface(null, Typeface.BOLD_ITALIC);
                taskheader.setFocusableInTouchMode(true);
                taskheader.setTypeface(null, Typeface.BOLD_ITALIC);
                datedue.setFocusableInTouchMode(true);
                datedue.setTypeface(null, Typeface.BOLD_ITALIC);
                flagpriority.setFocusableInTouchMode(true);
                flagpriority.setTypeface(null, Typeface.BOLD_ITALIC);

            }else{
                Toast.makeText(EditTaskActivity.this, "Changes Cancelled", Toast.LENGTH_SHORT).show();
                //if edit mode if turned off
                allowUserUpdate = false;
                setTaskInfo(openTask);
                saveedit.setEnabled(false);
                changeFlag.setEnabled(false);
                ttitle.setFocusableInTouchMode(false);
                ttitle.setTypeface(null, Typeface.NORMAL);
                ttitle.clearFocus();
                tcontent.setFocusableInTouchMode(false);
                tcontent.setTypeface(null, Typeface.NORMAL);
                tcontent.clearFocus();
                taskheader.setFocusableInTouchMode(false);
                taskheader.setTypeface(null, Typeface.NORMAL);
                taskheader.clearFocus();
                datedue.setFocusableInTouchMode(false);
                datedue.setTypeface(null, Typeface.NORMAL);
                datedue.clearFocus();
                flagpriority.setFocusableInTouchMode(false);
                flagpriority.setTypeface(null, Typeface.NORMAL);
                flagpriority.clearFocus();
            }
        });

        changeFlag.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditTaskActivity.this);
            builder.setTitle("What would you like to change the flag too?");
            builder.setCancelable(true);
            String[] options = {"TO DO", "IN PROGRESS", "DONE"};

            //Pass the array list in Alert dialog
            builder.setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0: // Select to do
                        taskpriority = "0";
                        flagpriority.setText("Current Flag: TO DO" );
                        break;

                    case 1: // Select in progress
                        taskpriority = "1";
                        flagpriority.setText("Current Flag: IN PROGRESS" );
                        break;
                    case 2: // Select done
                        taskpriority = "2";
                        flagpriority.setText("Current Flag: DONE" );
                        break;
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();



        });

        saveedit.setOnClickListener(v -> {

            // ask the user if they want to confirm the update. if they do then you can look to update

            AlertDialog.Builder builder = new AlertDialog.Builder(EditTaskActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Task Details Editing");
            builder.setMessage("Do you want to confirm your changes?");
            builder.setPositiveButton("Continue",
                    (dialog, which) -> {
                        // update the database

                        DatabaseReference referencesaveuserid = FirebaseDatabase.getInstance().getReference("Users");
                        referencesaveuserid.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    User u = dataSnapshot.getValue(User.class);
//                                    if ((u.getFirstname() + " " + u.getLastname()).equals(currentUserName)){
                                        fb = FirebaseDatabase.getInstance();
                                        reference = fb.getReference("Tasks");
                                        reference.child(openTask.getTask_id()).child("task_title").setValue(ttitle.getText().toString());
                                        reference.child(openTask.getTask_id()).child("task_content").setValue(tcontent.getText().toString());
                                        reference.child(openTask.getTask_id()).child("header").setValue(taskheader.getText().toString());
                                        reference.child(openTask.getTask_id()).child("date_due").setValue(datedue.getText().toString());
//                                        reference.child(openTask.getTask_id()).child("userid").setValue(u.getId());
                                        reference.child(openTask.getTask_id()).child("priority").setValue(taskpriority);
                                        Toast.makeText(EditTaskActivity.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(EditTaskActivity.this, MainActivity.class));
                                        finish();
//                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    });

            builder.setNegativeButton("Cancel",
                    (dialog, which) -> {
                        Toast.makeText(EditTaskActivity.this, "Save Cancelled", Toast.LENGTH_SHORT).show();
                        //if edit mode if turned off
                        setTaskInfo(openTask);
                        editmode.setChecked(false);
                        saveedit.setEnabled(false);
                        changeFlag.setEnabled(false);
                        ttitle.setFocusableInTouchMode(false);
                        ttitle.setTypeface(null, Typeface.NORMAL);
                        ttitle.clearFocus();
                        tcontent.setFocusableInTouchMode(false);
                        tcontent.setTypeface(null, Typeface.NORMAL);
                        tcontent.clearFocus();
                        taskheader.setFocusableInTouchMode(false);
                        taskheader.setTypeface(null, Typeface.NORMAL);
                        taskheader.clearFocus();
                        datedue.setFocusableInTouchMode(false);
                        datedue.setTypeface(null, Typeface.NORMAL);
                        datedue.clearFocus();
                        flagpriority.setFocusableInTouchMode(false);
                        flagpriority.setTypeface(null, Typeface.NORMAL);
                        flagpriority.clearFocus();
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        });


        deleteTask.setOnClickListener(v -> {
            deletetheTask();
        });


    }

    public void deletetheTask(){
        // confirm with user:
        AlertDialog.Builder builder = new AlertDialog.Builder(EditTaskActivity.this);
        builder.setCancelable(true);
        builder.setTitle("TASK DELETION");
        builder.setMessage("PLEASE CONFIRM YOU WOULD LIKE TO DELETE THE TASK");
        builder.setPositiveButton("Continue",
                (dialog, which) -> {

                    theTId = openTask.getTask_id();

                    deltaskref = FirebaseDatabase.getInstance().getReference("Tasks").child(openTask.getTask_id());
                    deltaskref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().removeValue();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    // delete the grouptask from the database

                    delGroupTaskref = FirebaseDatabase.getInstance().getReference("TaskGroups");
                    delGroupTaskref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                TaskGroups tg = dataSnapshot1.getValue(TaskGroups.class);
                                if (tg.getTaskid().equals(openTask.getTask_id())){
                                    dataSnapshot1.getRef().removeValue();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Toast.makeText(EditTaskActivity.this, "TASK DELETED", Toast.LENGTH_SHORT).show();
                    finish();
                });

        builder.setNegativeButton("Cancel",
                (dialog, which) -> {
                    // cancel and go back
                    Toast.makeText(EditTaskActivity.this, "TASK DELETION CANCELED", Toast.LENGTH_SHORT).show();
                    finish();

                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void setTaskInfo(Task inTask){
        // this is all the default info of the task.
        ttitle.setText(inTask.getTask_title());
        tcontent.setText(inTask.getTask_content());
        taskheader.setText(inTask.getHeader());
        datedue.setText(inTask.getDate_due());
        taskpriority = inTask.getPriority();


        switch (Integer.valueOf(taskpriority)){
            case 0: // to do
                flagpriority.setText("Current Flag: TO DO" );
                break;
            case 1: // in progress
                flagpriority.setText("Current Flag: IN PROGRESS" );
                break;
            case 2: // done
                flagpriority.setText("Current Flag: DONE" );
                break;
        }

    }

}
