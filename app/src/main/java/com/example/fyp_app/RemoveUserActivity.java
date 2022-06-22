package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.Toast;

import com.example.fyp_app.Models.User;
import com.example.fyp_app.Models.UserGroups;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RemoveUserActivity extends AppCompatActivity {

    ChipGroup chipGroup;
    Button saveUserChange;

    DatabaseReference ref;
    FirebaseUser fuser;
    String groupid;

    Intent intent;

    Boolean ingroup;

    ArrayList<String> usersInGroup;
    ArrayList<String> allUsers;
    ArrayList<User> userDetails;
    ArrayList<String> ugroups;
    ArrayList<String> usernames2add;
    ArrayList<String> userIDStoremove;
    ArrayList<String> idInGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_user);

        chipGroup = findViewById(R.id.chipgroup);
        saveUserChange = findViewById(R.id.saveuserchange);

        allUsers = new ArrayList<>();
        usersInGroup = new ArrayList<>();
        userDetails = new ArrayList<>();
        userIDStoremove = new ArrayList<>();
        usernames2add = new ArrayList<>();
        ugroups = new ArrayList<>();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        intent = getIntent();
        groupid = intent.getStringExtra("id");

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            //get the group details that this task belongs to so you can go back to the inside group for the same group
            Intent intent1 = new Intent(RemoveUserActivity.this, InsideGroupActivity.class);
            intent1.putExtra("id", groupid);
            startActivity(intent1);
            finish();
        });


        // load the chip group - with all the users that are IN THE GROUP
        DatabaseReference allusers = FirebaseDatabase.getInstance().getReference("Users");
        allusers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allUsers.clear();
                usersInGroup.clear();
                // get all user ids
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getId().equals(fuser.getUid())){
                        allUsers.add(user.getId());
                        // for getting user details later
                        userDetails.add(user);
                    }
                }
                // get id of all users in group
                DatabaseReference ingroupusers = FirebaseDatabase.getInstance().getReference("UserGroups");
                ingroupusers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dsnapshot : snapshot.getChildren()){
                            UserGroups userGroups = dsnapshot.getValue(UserGroups.class);
                            if (!userGroups.getUserid().equals(fuser.getUid()) && (userGroups.getGroupid().equals(groupid))){
                                usersInGroup.add(userGroups.getUserid());
                            }
                        }

                        // you now have 2 lists of the users, and users in the group

                        for (String users : allUsers){
                            ingroup = false;
                            for (String u : usersInGroup){
                                if (u.equals(users)){
                                    // if the user is in the group
                                    ingroup = true;
                                }
                            }

                            if (ingroup) {

                                // set the chips and when you click them they turn red.
                                DatabaseReference refgetname = FirebaseDatabase.getInstance().getReference("Users").child(users);
                                refgetname.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User u = snapshot.getValue(User.class);
                                        LayoutInflater inflater = LayoutInflater.from(RemoveUserActivity.this);

                                        // Create a Chip from Layout.
                                        Chip newChip = (Chip) inflater.inflate(R.layout.layout_chip_entry2, chipGroup, false);
                                        newChip.setText(u.getFirstname() + " " + u.getLastname());
                                        newChip.setCloseIconEnabled(false);

                                        chipGroup.addView(newChip);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        saveUserChange.setOnClickListener(v -> {
            //Get a list of the user ids we need to add to the database
            //to do this - check the checked chips names against the list of all users
            //and get all the ids to add

            // get a list of names of checked users
            int count = chipGroup.getChildCount();
            for(int i=0;i< count; i++) {
                Chip child = (Chip) chipGroup.getChildAt(i);
                if(!child.isChecked()){continue;}
                usernames2add.add(child.getText().toString());
            }

            // get the ids for the names that are checked
            for (String s : usernames2add){
                for (User u : userDetails){
                    if (s.equals(u.getFirstname() + " " + u.getLastname())){
                        userIDStoremove.add(u.getId());
                    }
                }
            }

            // remove the correct users

            for (String u2 : userIDStoremove){
                // remove all the usergroups with this userid
                ref = FirebaseDatabase.getInstance().getReference("UserGroups");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            // for all the groups get the one with the id we are now checking.
                            UserGroups userGroups = dataSnapshot.getValue(UserGroups.class);
                            if (userGroups.getUserid().equals(u2)){
                                dataSnapshot.getRef().removeValue();
                            }
                        }

                        Intent intent1 = new Intent(RemoveUserActivity.this, InsideGroupActivity.class);
                        intent1.putExtra("id", groupid);
                        startActivity(intent1);
                        finish();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });

                Toast.makeText(RemoveUserActivity.this, "Users removed from group", Toast.LENGTH_SHORT).show();

            }

        });


    }
}
