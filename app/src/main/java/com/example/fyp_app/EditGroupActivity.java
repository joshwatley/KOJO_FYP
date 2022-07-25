package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_app.Models.Groups;
import com.example.fyp_app.Models.User;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditGroupActivity extends AppCompatActivity {

    FirebaseUser fuser;
    FirebaseDatabase fb;
    DatabaseReference reference;

    TextView groupname;
    EditText showGroupName;
    EditText showGroupDesc;
    TextView adminCreation;
    Chip editmode;
    Button saveEdit;
    String currentGroup;

    Intent intent;
    User user;
    Groups openGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        groupname = findViewById(R.id.groupname);
        showGroupName = findViewById(R.id.showgroupname);
        showGroupDesc = findViewById(R.id.showgroupdesc);
        adminCreation = findViewById(R.id.currentadmin);
        saveEdit = findViewById(R.id.saveeditgroup);
        editmode = findViewById(R.id.chip);


        intent = getIntent();
        currentGroup = intent.getStringExtra("id");

        reference = FirebaseDatabase.getInstance().getReference("Groups").child(currentGroup);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                openGroup = snapshot.getValue(Groups.class);
                groupname.setText(openGroup.getName());
                // setting all the data about the group
                showGroupName.setText(openGroup.getName());
                showGroupDesc.setText(openGroup.getDesc());

                // get the current admin details
                DatabaseReference refAdmin = FirebaseDatabase.getInstance().getReference("Users").child(openGroup.getAdmin_ID());
                refAdmin.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                        adminCreation.setText("Owned By " + user.getFirstname() + " " + user.getLastname() + ", created on " + openGroup.getDate_created() + ".");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent1 = new Intent(EditGroupActivity.this, InsideGroupActivity.class);
            intent1.putExtra("id", currentGroup);
            startActivity(intent1);
            finish();
        });


        editmode.setOnClickListener(v -> {
            if (editmode.isChecked()){
                // if edit mode is on
                saveEdit.setVisibility(View.VISIBLE);
                showGroupName.setFocusableInTouchMode(true);
                showGroupName.setTypeface(null, Typeface.BOLD_ITALIC);
                showGroupDesc.setFocusableInTouchMode(true);
                showGroupDesc.setTypeface(null, Typeface.BOLD_ITALIC);


            }else{
                Toast.makeText(EditGroupActivity.this, "Changes Cancelled", Toast.LENGTH_SHORT).show();
                //if edit mode if turned off
                setGroupInfo();
                saveEdit.setVisibility(View.INVISIBLE);
                showGroupName.setFocusableInTouchMode(false);
                showGroupName.clearFocus();
                showGroupName.setTypeface(null, Typeface.NORMAL);
                showGroupDesc.setFocusableInTouchMode(false);
                showGroupDesc.clearFocus();
                showGroupDesc.setTypeface(null, Typeface.NORMAL);
            }


        });

        saveEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditGroupActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Group Details Editing");
            builder.setMessage("Do you want to confirm your changes?");
            builder.setPositiveButton("Continue",
                    (dialog, which) -> {
                        // update the database
                        fb = FirebaseDatabase.getInstance();
                        reference = fb.getReference("Groups");
                        reference.child(currentGroup).child("name").setValue(showGroupName.getText().toString());
                        reference.child(currentGroup).child("desc").setValue(showGroupDesc.getText().toString());

                        // go back to the main page
                        Intent intent = new Intent(EditGroupActivity.this, GroupsActivity.class);
                        intent.putExtra("id", currentGroup);
                        startActivity(intent);
                        finish();
                    });
            builder.setNegativeButton("Cancel",
                    (dialog, which) -> {
                        Toast.makeText(EditGroupActivity.this, "Changes Cancelled", Toast.LENGTH_SHORT).show();
                        //if edit mode if turned off
                        setGroupInfo();
                        editmode.setChecked(false);
                        saveEdit.setVisibility(View.INVISIBLE);
                        showGroupName.setFocusableInTouchMode(false);
                        showGroupName.clearFocus();
                        showGroupName.setTypeface(null, Typeface.NORMAL);
                        showGroupDesc.setFocusableInTouchMode(false);
                        showGroupDesc.clearFocus();
                        showGroupDesc.setTypeface(null, Typeface.NORMAL);

                    });

            AlertDialog dialog = builder.create();
            dialog.show();

        });

    }
    public void setGroupInfo(){
        showGroupName.setText(openGroup.getName());
        showGroupDesc.setText(openGroup.getDesc());
        adminCreation.setText("Owned By " + user.getFirstname() + " " + user.getLastname() + ", created on " + openGroup.getDate_created() + ".");
    }
}
