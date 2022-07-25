package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_app.Adapters.UserSearchAdapter;
import com.example.fyp_app.Models.Groups;
import com.example.fyp_app.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateGroupActivity extends AppCompatActivity implements UserForGroupInterface {

    private TextView groupName;
    private TextView groupDesc;
    private TextView showAddedUsers;
    private String added;
    private String gname;
    Boolean duplic;

    private Boolean iamadded = false;

    private RecyclerView recyclerView;
    private List<User> mUsers;
    private List<User> addedUsers;
    private Boolean shouldidadd;

    private UserSearchAdapter userSearchAdapter;

    private FirebaseUser fuser;
    DatabaseReference reference;
    DatabaseReference reference2;
    DatabaseReference reference3;
    DatabaseReference reference4;

    DatabaseReference blankreference;
    DatabaseReference ref;

    FirebaseDatabase fbinstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CreateGroupActivity.this));

        showAddedUsers = findViewById(R.id.showaddedusers);
        Button save_group = findViewById(R.id.btnSave_group);
        Toolbar toolbar = findViewById(R.id.my_toolbar);

        mUsers = new ArrayList<>();
        addedUsers = new ArrayList<>();
        added = "";


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // on back go to group screen
            startActivity(new Intent(CreateGroupActivity.this, GroupsActivity.class));
            finish();
        });


        EditText search_users = findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        save_group.setOnClickListener(v -> createGroup());
    }

    @SuppressLint("SimpleDateFormat")
    private void createGroup(){
        duplic = false;
        String currentUserID = fuser.getUid();
        groupName = findViewById(R.id.group_name);
        groupDesc = findViewById(R.id.group_desc);
        gname = "";
        gname = groupName.getText().toString();



        if (groupName.getText().toString().isEmpty()){
            groupName.setError("Group Name is required");
            groupName.requestFocus();
            return;
        }


        if (groupDesc.getText().toString().isEmpty()){
            groupDesc.setError("Group Description is empty");
            groupDesc.requestFocus();
            return;
        }

        if (showAddedUsers.getText().toString().isEmpty()){
            Toast.makeText(CreateGroupActivity.this, "Please add some users to the group.", Toast.LENGTH_SHORT).show();
            return;
        }

        DateFormat df;
        df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateobj = new Date();
        String dntNOW = df.format(dateobj);

//      this pushes the data about the group to firebase
        fbinstance = FirebaseDatabase.getInstance();
        reference = fbinstance.getReference("Groups");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", groupName.getText().toString());
        hashMap.put("desc", groupDesc.getText().toString());
        hashMap.put("admin_ID", currentUserID);
        hashMap.put("date_created", dntNOW);
        hashMap.put("headings", "none");

        blankreference = reference;
        ref = blankreference.push();
        String pGroupID = ref.getKey();
        hashMap.put("groupid", pGroupID);
        ref.setValue(hashMap);


        // this will add the users to the given group

        for (User user : addedUsers){
            reference3 = FirebaseDatabase.getInstance().getReference();

            HashMap<String, Object> hashMap2 = new HashMap<>();
            hashMap2.put("userid", user.getId());
            hashMap2.put("groupid", pGroupID);

            reference3.child("UserGroups").push().setValue(hashMap2);


        }

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateGroupActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Group Creation");
        builder.setMessage("Group Created!");
        builder.setPositiveButton("Continue",
                (dialog, which) -> {
                    Intent intent = new Intent(CreateGroupActivity.this, GroupsActivity.class);
                    startActivity(intent);
                    finish();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void searchUsers(String s){
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query  = FirebaseDatabase.getInstance().getReference("Users").orderByChild("firstname")
                .startAt(s)
                .endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();

                for (DataSnapshot d : snapshot.getChildren()){
                    User user = d.getValue(User.class);

                    assert user != null;
                    assert fuser != null;

                    if (!user.getId().equals(fuser.getUid())){
                        mUsers.add(user);
                    }
                }

                userSearchAdapter = new UserSearchAdapter(CreateGroupActivity.this, mUsers, CreateGroupActivity.this);
                recyclerView.setAdapter(userSearchAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    @Override
    public void setValue(String idToAdd) {
        shouldidadd = true;
        reference2 = FirebaseDatabase.getInstance().getReference("Users").child(idToAdd);
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user2 = snapshot.getValue(User.class);

                for (User u : addedUsers){
                    if (u.getId().equals(idToAdd)){
                        shouldidadd = false;
                        break;
                    }else{}
                }
                if (shouldidadd) {
                    addedUsers.add(user2);
                }
                showAddedUsers.setText("");
                added = "";

                for (User x : addedUsers){
                    added = added + x.getFirstname() + " " +x.getLastname() + ", ";
                }
                showAddedUsers.setText(added + " Will be added.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // add the current logged in user too
        if (!iamadded){
            reference2 = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            reference2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user2 = snapshot.getValue(User.class);
                    addedUsers.add(user2);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            iamadded = true;
        }


    }
}
