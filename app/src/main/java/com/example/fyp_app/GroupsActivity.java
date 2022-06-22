package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fyp_app.Adapters.GroupAdapter;
import com.example.fyp_app.Adapters.UserAdapter;
import com.example.fyp_app.Models.Groups;
import com.example.fyp_app.Models.User;
import com.example.fyp_app.Models.UserGroups;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    GroupAdapter groupAdapter;
    RecyclerView recyclerView;

    TextView user_name;
    Toolbar toolbar;
    CircleImageView edit_u;

    Boolean shouldiadd;
    Boolean shouldiadd2;

    List<Groups> mGroups;
    List<String> groupIDS;

    NavigationView navigationView;
    DrawerLayout drawerLayout;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    DatabaseReference reference2;

    Button createGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);


        // load all the groups the user is in
        mGroups = new ArrayList<>();
        groupIDS = new ArrayList<>();
        readGroups();

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        drawerLayout = findViewById(R.id.dlayout);
        navigationView = findViewById(R.id.navbar);
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);


        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(GroupsActivity.this, drawerLayout, toolbar, R.string.open_navigation_menu, R.string.close_navigation_menu);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();




        // this will be loading the header layout in the navigation drawer
        View headerView = navigationView.inflateHeaderView(R.layout.headerlayout);
        CircleImageView showuserimg = headerView.findViewById(R.id.suserpic);
        TextView showusername = headerView.findViewById(R.id.susername);
        TextView semail = headerView.findViewById(R.id.semail);
        edit_u = headerView.findViewById(R.id.edit_u);
        edit_u.setOnClickListener(v -> {
            Intent intent = new Intent(GroupsActivity.this, UserActivity.class);
            intent.putExtra("id", firebaseUser.getUid());
            startActivity(intent);
            finish();
        });




        user_name = findViewById(R.id.user_name);
        createGroup = findViewById(R.id.btnCreate_group);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                showusername.setText(user.getFirstname() + " " + user.getLastname());
                semail.setText(user.getEmail());
                if (user.getImageLoc().equals("default")){
                    showuserimg.setImageResource(R.mipmap.ic_launcher);
                } else{
                    // load image from whatever
                    Glide.with(getApplicationContext())
                            .load(user.getImageLoc())
                            .into(showuserimg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//         create group event
        createGroup.setOnClickListener(v -> {
            startActivity(new Intent(GroupsActivity.this, CreateGroupActivity.class));
            finish();
        });
    }


    public void readGroups(){
        shouldiadd = true;
        shouldiadd2 = true;

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserGroups");

        // get list of all the group ids the logged in user is in.

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupIDS.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    UserGroups userGroups = dataSnapshot.getValue(UserGroups.class);

                    assert userGroups != null;
                    assert firebaseUser != null;
                    if (userGroups.getUserid().equals(firebaseUser.getUid())){

                        // dont repeat groupids
                        for (String s : groupIDS){
                            if (s == userGroups.getGroupid()){
                                // dont add
                                shouldiadd = false;
                                break;
                            }
                        }
                        if (shouldiadd){
                            groupIDS.add(userGroups.getGroupid());
                        }
                    }
//
                }

                mGroups.clear();
                for (String groupIDs : groupIDS){

                    DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Groups").child(groupIDs);
                    reference2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Groups g = snapshot.getValue(Groups.class);
                            // dont repeat groups
                                    for (Groups l : mGroups){
                                        if (g.getGroupid().equals(l.getGroupid())){
                                            shouldiadd2 = false;
                                            break;
                                        }else{}
                                    }
                                    // check if it is in the list already


                                    // if not in the list
                                    if (shouldiadd2){ // add to the list
                                        mGroups.add(g); }

                            groupAdapter = new GroupAdapter(GroupsActivity.this, mGroups);
                            recyclerView.setAdapter(groupAdapter);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}});
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home:
                startActivity(new Intent(GroupsActivity.this, MainActivity.class));
                finish();
                return true;
            case R.id.menu_pomo:
                startActivity(new Intent(GroupsActivity.this, PomodoroActivity.class));
                finish();
                return true;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(GroupsActivity.this, LoginActivity.class));
                finish();
                return true;
            case R.id.menu_messages:
                startActivity(new Intent(GroupsActivity.this, MessagesActivity.class));
                finish();
                return true;
            case R.id.menu_groups:
                startActivity(new Intent(GroupsActivity.this, GroupsActivity.class));
                finish();
                return true;
            default:
                return false;
        }
    }
}
