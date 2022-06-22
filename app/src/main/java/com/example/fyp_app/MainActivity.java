package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fyp_app.Adapters.TaskAdapter;
import com.example.fyp_app.Adapters.UserAdapter;
import com.example.fyp_app.Models.Task;
import com.example.fyp_app.Models.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView user_name;
    Toolbar toolbar;
    CircleImageView edit_u;
    RecyclerView recyclerView;
    TaskAdapter taskAdapter;
    List<Task> mTasks;
    TextView notasks;
    NavigationView navigationView;
    DrawerLayout drawerLayout;

    TextView showusername;
    TextView semail;



    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.dlayout);
        navigationView = findViewById(R.id.navbar);
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        notasks = findViewById(R.id.notasks);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.open_navigation_menu, R.string.close_navigation_menu);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        // this will be loading the header layout in the navigation drawer

        View headerView = navigationView.inflateHeaderView(R.layout.headerlayout);
        CircleImageView showuserimg = headerView.findViewById(R.id.suserpic);
        showusername = headerView.findViewById(R.id.susername);
        semail = headerView.findViewById(R.id.semail);
        edit_u = headerView.findViewById(R.id.edit_u);
        edit_u.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserActivity.class);
            intent.putExtra("id", firebaseUser.getUid());
            startActivity(intent);
            finish();
        });

        user_name = findViewById(R.id.user_name);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));


        mTasks = new ArrayList<>();
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

                // GET THE TASKS THAT THIS USER IS ALLOCATED TOO

                DatabaseReference getTasks = FirebaseDatabase.getInstance().getReference("Tasks");
                getTasks.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mTasks.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            Task task = snapshot1.getValue(Task.class);

                            if (task.getUserid().equals(firebaseUser.getUid())){
                                mTasks.add(task);
                            }
                        }

                        if(mTasks.size() == 0){
                            notasks.setVisibility(View.VISIBLE);
                        }else {
                            notasks.setVisibility(View.INVISIBLE);
                            taskAdapter = new TaskAdapter(MainActivity.this, mTasks);
                            recyclerView.setAdapter(taskAdapter);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
            case R.id.menu_pomo:
                startActivity(new Intent(MainActivity.this, PomodoroActivity.class));
                finish();
                return true;
            case R.id.menu_messages:
                startActivity(new Intent(MainActivity.this, MessagesActivity.class));
                finish();
                return true;
            case R.id.menu_groups:
                startActivity(new Intent(MainActivity.this, GroupsActivity.class));
                finish();
                return true;
            default:
                return false;
        }
    }

}
