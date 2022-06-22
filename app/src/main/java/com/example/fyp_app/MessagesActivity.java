package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fyp_app.Adapters.MessageAdapter;
import com.example.fyp_app.Fragments.ChatsFragment;
import com.example.fyp_app.Fragments.UsersFragment;
import com.example.fyp_app.Models.User;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    TextView user_name;
    CircleImageView edit_u;
    CircleImageView showuserimg;
    TextView showusername;
    TextView semail;
    private FirebaseUser firebaseUser;

    NavigationView navigationView;
    DrawerLayout drawerLayout;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.dlayout);
        navigationView = findViewById(R.id.navbar);

        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MessagesActivity.this, drawerLayout, toolbar, R.string.open_navigation_menu, R.string.close_navigation_menu);
        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();

        View headerView = navigationView.inflateHeaderView(R.layout.headerlayout);
        CircleImageView showuserimg = headerView.findViewById(R.id.suserpic);
        showusername = headerView.findViewById(R.id.susername);
        semail = headerView.findViewById(R.id.semail);
        edit_u = headerView.findViewById(R.id.edit_u);
        edit_u.setOnClickListener(v -> {
            Intent intent = new Intent(MessagesActivity.this, UserActivity.class);
            intent.putExtra("id", firebaseUser.getUid());
            startActivity(intent);
            finish();
        });

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

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
        viewPagerAdapter.addFragment(new UsersFragment(), "Users");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home:
                startActivity(new Intent(MessagesActivity.this, MainActivity.class));
                finish();
                return true;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MessagesActivity.this, LoginActivity.class));
                finish();
                return true;
            case R.id.menu_messages:
                startActivity(new Intent(MessagesActivity.this, MessagesActivity.class));
                finish();
                return true;
            case R.id.menu_groups:
                startActivity(new Intent(MessagesActivity.this, GroupsActivity.class));
                finish();
                return true;
            default:
                return false;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

}


