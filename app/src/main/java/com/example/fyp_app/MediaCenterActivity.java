package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
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

public class MediaCenterActivity extends AppCompatActivity {

    Intent intent;

    private FirebaseUser fuser;

    String currentGroupID;

    Toolbar toolbar;
    TextView groupnme;
    EditText ttitle;

    DatabaseReference reference;
    DatabaseReference ref;

    DatabaseReference blankreference;

    FirebaseDatabase fbinstance;
    DatabaseReference reference2;

    Groups currentGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_center);

        toolbar = findViewById(R.id.my_toolbar);
        groupnme = findViewById(R.id.groupnme);
        ttitle = findViewById(R.id.fileTitle);

        intent = getIntent();
        currentGroupID = intent.getStringExtra("id");

        reference2 = FirebaseDatabase.getInstance().getReference("Groups").child(currentGroupID);
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentGroup = snapshot.getValue(Groups.class);
                // this is text of the title
//                groupnme.setText("" + currentGroup.getName());.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}

//* So this is will be a way for people to upload files
//things to display:
//score (uploads this week)
//other peoples uploads
//button to upload file
//
//So at the top it will have your upload score in the middle
//
//Then a list of uploads to this group, and we can have a menu button or something for adding a link
//
//
//
//Upload link page:
//
//Let user set name of upload
//File - regex for drop box link
//Store:
//Name
//File link
//Created datetime
//Name of creator