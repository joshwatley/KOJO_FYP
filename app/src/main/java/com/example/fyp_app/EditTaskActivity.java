package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_app.Models.Groups;
import com.example.fyp_app.Models.Task;
import com.example.fyp_app.Models.TaskGroups;
import com.example.fyp_app.Models.User;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
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
    Spinner userSpinner;
    String addedUser;
    EditText allocateduser;
    Boolean allowUserUpdate = false;
    Button saveedit;
    Button deleteTask;
    Button addUser;
    String theTId;
    Button changeFlag;

    List<User> mUsers;
    String currentUserName;


    Intent intent;
    Task openTask;
    String parentGroup;
    FirebaseDatabase fb;
    DatabaseReference reference;
    DatabaseReference referenceUser;

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
        userSpinner = findViewById(R.id.userspinner);
        userSpinner.setEnabled(false);
        datedue  = findViewById(R.id.datedue);
        flagpriority = findViewById(R.id.trafficpos);
        allocateduser = findViewById(R.id.allocateduser);
        saveedit = findViewById(R.id.saveedittask);
        saveedit.setEnabled(false);
        changeFlag = findViewById(R.id.changetraffic);
        changeFlag.setEnabled(false);
//        addUser = findViewById(R.id.addusertotask);
        deleteTask = findViewById(R.id.deletetask);

        toolbartitle = findViewById(R.id.ttitle);
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

                // show the current USERID NAME
                showTU(openTask);


                taskheader.setText(openTask.getHeader());
                taskpriority = openTask.getPriority();
                datedue.setText(openTask.getDate_due());
                addedUser = openTask.getUserid();

                // load data for the user addition function

                referenceUser = FirebaseDatabase.getInstance().getReference("Users");
                referenceUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mUsers.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            User user = dataSnapshot.getValue(User.class);
                            if (openTask.getUserid().equals(user.getId())){
                                allocateduser.setText("Allocated User: " + user.getFirstname() + " " + user.getLastname());
                            }
                            // add all the users to the list
                            mUsers.add(user);
                        }
                        // once all the users are in the list, now you can update the data in the spinner
                        ArrayAdapter<User> arrayAdapter = new ArrayAdapter<User>(EditTaskActivity.this ,android.R.layout.simple_spinner_item, mUsers);
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        userSpinner.setAdapter(arrayAdapter);
                        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                if (allowUserUpdate){
                                    currentUserName = parent.getItemAtPosition(position).toString();
                                    allocateduser.setText("Allocated User: " + currentUserName);
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView <?> parent) {
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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

                // get the group the task belongs to so you can close the activity
                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("TaskGroups");
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            TaskGroups tg = dataSnapshot.getValue(TaskGroups.class);
                            if (tg.getTaskid().equals(openTask.getTask_id())){
                                // get the group id
                                parentGroup = tg.getGroupid();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

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
                userSpinner.setVisibility(View.VISIBLE);
                userSpinner.setEnabled(true);
                userSpinner.setFocusableInTouchMode(true);
                changeFlag.setEnabled(true);
                ttitle.setFocusableInTouchMode(true);
                ttitle.setTypeface(null, Typeface.BOLD_ITALIC);
                tcontent.setFocusableInTouchMode(true);
                tcontent.setTypeface(null, Typeface.BOLD_ITALIC);
                taskheader.setFocusableInTouchMode(true);
                taskheader.setTypeface(null, Typeface.BOLD_ITALIC);
                datedue.setFocusableInTouchMode(true);
                datedue.setTypeface(null, Typeface.BOLD_ITALIC);
                allocateduser.setFocusableInTouchMode(true);
                allocateduser.setTypeface(null, Typeface.BOLD_ITALIC);
                flagpriority.setFocusableInTouchMode(true);
                flagpriority.setTypeface(null, Typeface.BOLD_ITALIC);

            }else{
                Toast.makeText(EditTaskActivity.this, "Changes Cancelled", Toast.LENGTH_SHORT).show();
                //if edit mode if turned off
                allowUserUpdate = false;
                setTaskInfo(openTask);
                saveedit.setEnabled(false);
                changeFlag.setEnabled(false);
                userSpinner.setEnabled(false);
                userSpinner.setFocusableInTouchMode(false);
                userSpinner.clearFocus();
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
                allocateduser.setFocusableInTouchMode(false);
                allocateduser.setTypeface(null, Typeface.NORMAL);
                allocateduser.clearFocus();
                flagpriority.setFocusableInTouchMode(false);
                flagpriority.setTypeface(null, Typeface.NORMAL);
                flagpriority.clearFocus();
                userSpinner.setEnabled(false);
                userSpinner.setFocusableInTouchMode(false);
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
                                    if ((u.getFirstname() + " " + u.getLastname()).equals(currentUserName)){
                                        fb = FirebaseDatabase.getInstance();
                                        reference = fb.getReference("Tasks");
                                        reference.child(openTask.getTask_id()).child("task_title").setValue(ttitle.getText().toString());
                                        reference.child(openTask.getTask_id()).child("task_content").setValue(tcontent.getText().toString());
                                        reference.child(openTask.getTask_id()).child("header").setValue(taskheader.getText().toString());
                                        reference.child(openTask.getTask_id()).child("date_due").setValue(datedue.getText().toString());
                                        reference.child(openTask.getTask_id()).child("userid").setValue(u.getId());
                                        reference.child(openTask.getTask_id()).child("priority").setValue(taskpriority);
                                        Toast.makeText(EditTaskActivity.this, "Changes Saved", Toast.LENGTH_SHORT).show();
                                        Intent intent1 = new Intent(EditTaskActivity.this, InsideGroupActivity.class);
                                        intent1.putExtra("id", parentGroup);
                                        startActivity(intent1);
                                        finish();
                                    }
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
                        userSpinner.setEnabled(false);
                        userSpinner.setFocusableInTouchMode(false);
                        userSpinner.clearFocus();
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
                        allocateduser.setFocusableInTouchMode(false);
                        allocateduser.setTypeface(null, Typeface.NORMAL);
                        allocateduser.clearFocus();
                        flagpriority.setFocusableInTouchMode(false);
                        flagpriority.setTypeface(null, Typeface.NORMAL);
                        flagpriority.clearFocus();
                        userSpinner.setEnabled(false);
                        userSpinner.setFocusableInTouchMode(false);

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
                    // update the database

                    // delete the task from the database,
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
                                    // if the taskgroup in the database contains the open task
                                    dataSnapshot1.getRef().removeValue();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Toast.makeText(EditTaskActivity.this, "TASK DELETED", Toast.LENGTH_SHORT).show();
//                    Intent intent1 = new Intent(EditTaskActivity.this, InsideGroupActivity.class);
//                    intent1.putExtra("id", parentGroup);
//                    startActivity(intent1);
                    finish();
                });

        builder.setNegativeButton("Cancel",
                (dialog, which) -> {
                    // cancel and go back
                    Toast.makeText(EditTaskActivity.this, "TASK DELETION CANCELED", Toast.LENGTH_SHORT).show();

//                    Intent intent1 = new Intent(EditTaskActivity.this, InsideGroupActivity.class);
//                    intent1.putExtra("id", parentGroup);
//                    startActivity(intent1);
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
        addedUser = inTask.getUserid();
        showTU(inTask);


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

    public void showTU(Task inTask){


        if (inTask.getUserid().equals("none")){
            currentUserName = "none";
        }else{
            DatabaseReference showTU = FirebaseDatabase.getInstance().getReference("Users").child(openTask.getUserid());
            showTU.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User tu = snapshot.getValue(User.class);
                    currentUserName = tu.getFirstname() + " " + tu.getLastname();
                    allocateduser.setText("Allocated User: " + currentUserName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}
