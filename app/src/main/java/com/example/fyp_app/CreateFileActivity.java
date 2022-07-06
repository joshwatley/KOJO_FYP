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

public class CreateFileActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView groupnme;
    EditText ftitle;
    EditText flink;
    Button saveFile;
    TextView cancel;
    Intent intent;

    private FirebaseUser fuser;

    String currentGroupID;

    Groups currentGroup;
    DatabaseReference reference;

    FirebaseDatabase fbinstance;
    DatabaseReference reference2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_file);

        toolbar = findViewById(R.id.my_toolbar);
        groupnme = findViewById(R.id.groupnme);
        ftitle = findViewById(R.id.fileTitle);
        flink = findViewById(R.id.fileLink);
        saveFile = findViewById(R.id.btnSaveFile);
        cancel = findViewById(R.id.cancelfile);

        intent = getIntent();
        currentGroupID = intent.getStringExtra("id");

        //getting group data

        reference2 = FirebaseDatabase.getInstance().getReference("Groups").child(currentGroupID);
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentGroup = snapshot.getValue(Groups.class);
                groupnme.setText("Upload File for " + currentGroup.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Intent i = new Intent(CreateFileActivity.this, InsideGroupActivity.class);
            i.putExtra("id", currentGroupID);
            finish();
        });

        saveFile.setOnClickListener(v -> uploadFile());
    }


    private void uploadFile(){

        //validation

        if (ftitle.toString().isEmpty()){
            ftitle.setError("File Title is required");
            ftitle.requestFocus();
            return;
        }
        if (flink.toString().isEmpty()){
            flink.setError("File Link is required");
            flink.requestFocus();
            return;
        } else if (!flink.getText().toString().startsWith("https://www.dropbox.com/")){
            System.out.println("32 " + flink.toString().startsWith("https://www.dropbox.com/"));
            System.out.println("32 " + "https://www.dropbox.com/");
            System.out.println("32 " + flink.toString());
            flink.setError("Please upload a valid dropbox link");
            flink.requestFocus();
            return;
        }
        // example dropbox link https://www.dropbox.com/scl/fo/p6h1fsa10v36jgjowmw2c/h?dl=0&rlkey=kv70a88b1lk9d2lt94w4wlmb3



        //get date created
        DateFormat df;
        df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        String dntNOW = df.format(dateobj);


        // create the new file
        fbinstance = FirebaseDatabase.getInstance();
        reference = fbinstance.getReference("Files");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("file_title", ftitle.getText().toString());
        hashMap.put("file_link", flink.getText().toString());
        hashMap.put("date_created", dntNOW);
        hashMap.put("group_id", currentGroupID);
        hashMap.put("userid", "none");


        reference.push().setValue(hashMap);

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateFileActivity.this);
        builder.setCancelable(true);
        builder.setTitle("File Uploaded");
        builder.setMessage("File Uploaded");
        builder.setPositiveButton("Continue",
                (dialog, which) -> {
                    Intent intent = new Intent(CreateFileActivity.this, InsideGroupActivity.class);
                    intent.putExtra("id", currentGroupID);
                    startActivity(intent);
                    finish();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
