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
