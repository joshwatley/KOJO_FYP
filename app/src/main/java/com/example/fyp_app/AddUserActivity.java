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

public class AddUserActivity extends AppCompatActivity {

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
    ArrayList<String> userIDStoadd;
    ArrayList<String> idInGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        chipGroup = findViewById(R.id.chipgroup);
        saveUserChange = findViewById(R.id.saveuserchange);

        allUsers = new ArrayList<>();
        usersInGroup = new ArrayList<>();
        userDetails = new ArrayList<>();
        userIDStoadd = new ArrayList<>();
        usernames2add = new ArrayList<>();
        ugroups = new ArrayList<>();

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        intent = getIntent();
        groupid = intent.getStringExtra("id");


        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent1 = new Intent(AddUserActivity.this, InsideGroupActivity.class);
            intent1.putExtra("id", groupid);
            startActivity(intent1);
            finish();
        });


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
                // get id of all users not in group
                DatabaseReference ingroupusers = FirebaseDatabase.getInstance().getReference("UserGroups");
                ingroupusers.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dsnapshot : snapshot.getChildren()){
                            UserGroups userGroups = dsnapshot.getValue(UserGroups.class);
                            if (!userGroups.getUserid().equals(fuser.getUid()) && (userGroups.getGroupid().equals(groupid)) ){
                                usersInGroup.add(userGroups.getUserid());
                            }
                        }


                        for (String users : allUsers){
                            ingroup = false;
                            for (String u : usersInGroup){
                                if (u.equals(users)){
                                    // if the user is in the group
                                    ingroup = true;
                                }
                            }

                            if (ingroup){

                            } else {
                                // get name and give it an unchecked chip
                                DatabaseReference refgetname = FirebaseDatabase.getInstance().getReference("Users").child(users);
                                refgetname.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User u = snapshot.getValue(User.class);
                                        LayoutInflater inflater = LayoutInflater.from(AddUserActivity.this);

                                        // Create a Chip from Layout.
                                        Chip newChip = (Chip) inflater.inflate(R.layout.layout_chip_entry, chipGroup, false);
                                        newChip.setText(u.getFirstname() + " " + u.getLastname());
                                        newChip.setCloseIconEnabled(false);

                                        chipGroup.addView(newChip);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) { }
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
                        userIDStoadd.add(u.getId());
                    }
                }
            }


            for (String u2 : userIDStoadd){
                ref = FirebaseDatabase.getInstance().getReference();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("userid", u2);
                hashMap.put("groupid", groupid);

                ref.child("UserGroups").push().setValue(hashMap);
                Toast.makeText(AddUserActivity.this, "Users added to Group", Toast.LENGTH_SHORT).show();

            }

            Intent intent1 = new Intent(AddUserActivity.this, InsideGroupActivity.class);
            intent1.putExtra("id", groupid);
            startActivity(intent1);
            finish();

        });


    }
}
