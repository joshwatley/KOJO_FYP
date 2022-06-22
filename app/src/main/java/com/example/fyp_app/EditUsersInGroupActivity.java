package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.Sampler;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class EditUsersInGroupActivity extends AppCompatActivity {


    ChipGroup chipGroup;
    Button saveUserChange;

    FirebaseDatabase fb;
    DatabaseReference ref;
    FirebaseUser fuser;

    String groupid;
    Boolean ingroup;
    Intent intent;

    String whattodo;

    ArrayList<String> usersInGroup;
    ArrayList<String> allUsers;
    ArrayList<User> userDetails;
    ArrayList<String> ugroups;
    ArrayList<String> usernames2add;
    ArrayList<String> userIDStoadd;
    ArrayList<String> idInGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_users_in_group);


        chipGroup = findViewById(R.id.chipgroup);
        saveUserChange = findViewById(R.id.saveuserchange);
        allUsers = new ArrayList<>();
        usersInGroup = new ArrayList<>();
        userDetails = new ArrayList<>();
        userIDStoadd = new ArrayList<>();
        usernames2add = new ArrayList<>();
        ugroups = new ArrayList<>();
        whattodo = "";


        fuser = FirebaseAuth.getInstance().getCurrentUser();
        intent = getIntent();
        groupid = intent.getStringExtra("id");

        // get all users except the logged in user

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
                            if (!userGroups.getUserid().equals(fuser.getUid())){
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

                            if (ingroup){
                                // get name and give it a checked chip

                                DatabaseReference refgetname = FirebaseDatabase.getInstance().getReference("Users").child(users);
                                refgetname.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User u = snapshot.getValue(User.class);
                                        LayoutInflater inflater = LayoutInflater.from(EditUsersInGroupActivity.this);
                                        Toast.makeText(EditUsersInGroupActivity.this, "IS THIS HAPPENING OR NAH " + u.getFirstname(), Toast.LENGTH_SHORT).show();

                                        // Create a Chip from Layout.
                                        Chip newChip = (Chip) inflater.inflate(R.layout.layout_chip_entry, chipGroup, false);
                                        newChip.setText(u.getFirstname() + " " + u.getLastname());
                                        newChip.setChecked(true);
                                        newChip.setBackgroundColor(Color.parseColor("#00FA9A"));
                                        chipGroup.addView(newChip);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                // get name and give it an unchecked chip

                                DatabaseReference refgetname = FirebaseDatabase.getInstance().getReference("Users").child(users);
                                refgetname.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User u = snapshot.getValue(User.class);
                                        LayoutInflater inflater = LayoutInflater.from(EditUsersInGroupActivity.this);

                                        // Create a Chip from Layout.
                                        Chip newChip = (Chip) inflater.inflate(R.layout.layout_chip_entry, chipGroup, false);
                                        newChip.setText(u.getFirstname() + " " + u.getLastname());

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
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        saveUserChange.setOnClickListener(v -> {
            Toast.makeText(EditUsersInGroupActivity.this, "Event Fired", Toast.LENGTH_SHORT).show();

             //Get a list of the user ids we need to add to the database
             //to do this - check the checked chips names against the list of all users
             //and get all the ids to add

            // get a list of names of checked users
            int count = chipGroup.getChildCount();
            for(int i=0;i< count; i++) {
                Chip child = (Chip) chipGroup.getChildAt(i);
                if(!child.isChecked()){continue;}
                usernames2add.add(child.getText().toString());
                Toast.makeText(EditUsersInGroupActivity.this, "Event Fired", Toast.LENGTH_SHORT).show();

            }

            // get the ids for the names that are checked
            for (String s : usernames2add){
                for (User u : userDetails){
                    if (s.equals(u.getFirstname() + " " + u.getLastname())){
                        userIDStoadd.add(u.getId());
                    }
                }
            }

            //then go through the database, and remove all the usergroups that are linked to the group atm, except for the one with the fuser.

            // get list of user groups to remove
            DatabaseReference changeuser = FirebaseDatabase.getInstance().getReference("UserGroups");
            changeuser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        UserGroups userGroups = dataSnapshot.getValue(UserGroups.class);
//                        // if this is a record with this group
//                        if (userGroups.getGroupid().equals(groupid)){
//                            // if this is not referencing the admin/logged in user
//                            if (!userGroups.getUserid().equals(fuser.getUid())){
//                                // so now we are dealing with only what we want
//                                for (String u : userIDStoadd){
//                                    // check through the ids to add to see what to do with the record.
//                                    // if the current record is NOT inside userstoadd
//                                    if (!userGroups.getUserid().equals(u)){
//                                        whattodo = "delete";
//                                    }
//                                }
//                            }
//                        }
//                    }
                    // TODO SORT THIS OUT
                    // delete all the usergroups
//                    for (String ug : ugroups){
//                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserGroups").child(ug);
//                        ref.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                snapshot.getRef().removeValue();
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//                    }
                    // so now you need to create new records with these updated list of users
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            //then you can create a new user group using the current group id and all the ids that we are happy with adding to the database*/

            for (String u2 : userIDStoadd){
                ref = FirebaseDatabase.getInstance().getReference();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("userid", u2);
                hashMap.put("groupid", groupid);

                ref.child("UserGroups").push().setValue(hashMap);
            }

            Intent intent1 = new Intent(EditUsersInGroupActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();

        });




    }
}
