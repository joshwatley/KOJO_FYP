package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fyp_app.Models.Groups;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class NewTaskActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView groupnme;
    EditText ttitle;
    EditText tcontent;
    EditText tduedate;
    EditText theading;
    Button savetask;
    TextView cancel;


    Intent intent;

    private FirebaseUser fuser;

    String currentGroupID;

    Groups currentGroup;
    DatabaseReference reference;
    DatabaseReference ref;

    DatabaseReference blankreference;

    FirebaseDatabase fbinstance;
    DatabaseReference reference2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        toolbar = findViewById(R.id.my_toolbar);
        groupnme = findViewById(R.id.groupnme);
        ttitle = findViewById(R.id.fileTitle);
        tcontent = findViewById(R.id.fileLink);
        theading = findViewById(R.id.taskHeading);
        tduedate = findViewById(R.id.tempdateentry);
        savetask = findViewById(R.id.btnSaveFile);
        cancel = findViewById(R.id.cancelfile);

        intent = getIntent();
        currentGroupID = intent.getStringExtra("id");

        //getting the current group data

        reference2 = FirebaseDatabase.getInstance().getReference("Groups").child(currentGroupID);
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentGroup = snapshot.getValue(Groups.class);
                groupnme.setText("New Task for " + currentGroup.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Intent i = new Intent(NewTaskActivity.this, InsideGroupActivity.class);
            i.putExtra("id", currentGroupID);
            finish();
        });

        savetask.setOnClickListener(v -> createTask());
    }


    private void createTask(){

        //validation

        if (ttitle.toString().isEmpty()){
            ttitle.setError("Task Title is required");
            ttitle.requestFocus();
            return;
        }
        if (tcontent.toString().isEmpty()){
            tcontent.setError("Task Content is required");
            tcontent.requestFocus();
            return;
        }

        if (tduedate.toString().isEmpty()){
            tcontent.setError("Task Content is required");
            tcontent.requestFocus();
            return;
        }


        //get date created
        DateFormat df;
        df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        String dntNOW = df.format(dateobj);


        // create the new task
        fbinstance = FirebaseDatabase.getInstance();
        reference = fbinstance.getReference("Tasks");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("task_title", ttitle.getText().toString());
        hashMap.put("task_content", tcontent.getText().toString());
        hashMap.put("date_created", dntNOW);
        hashMap.put("priority", "0");
        hashMap.put("header", theading.getText().toString());
        hashMap.put("date_due", tduedate.getText().toString());
        hashMap.put("userid", "none");

        blankreference = reference;
        ref = blankreference.push();
        String pTaskID = ref.getKey();
        hashMap.put("task_id", pTaskID);
        ref.setValue(hashMap);

        // this will add the task to the current group


        reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("groupid", currentGroupID);
        hashMap1.put("taskid", pTaskID);
        hashMap1.put("userid", "none");

        reference.child("TaskGroups").push().setValue(hashMap1);

        AlertDialog.Builder builder = new AlertDialog.Builder(NewTaskActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Task Creation");
        builder.setMessage("Task Created!");
        builder.setPositiveButton("Continue",
                (dialog, which) -> {
                    Intent intent = new Intent(NewTaskActivity.this, InsideGroupActivity.class);
                    intent.putExtra("id", currentGroupID);
                    startActivity(intent);
                    finish();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
