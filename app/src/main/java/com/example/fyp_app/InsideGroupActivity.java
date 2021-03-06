package com.example.fyp_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_app.Fragments.MessageBoardFragment;
import com.example.fyp_app.Fragments.FilesFragment;
import com.example.fyp_app.Models.Groups;
import com.example.fyp_app.Models.UserGroups;
import com.google.android.material.tabs.TabLayout;
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

public class InsideGroupActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 0;
    Toolbar toolbar;
    TextView inGroupName;
    private FirebaseUser firebaseUser;

    CircleImageView edit_img;

    DatabaseReference reference;
    DatabaseReference reference2;
    DatabaseReference delT;
    Groups incurrentGroup;

    List<String> taskidstodelete;

    Intent intent;
    String group_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_group);

        taskidstodelete = new ArrayList<>();
        intent = getIntent();
        group_id = intent.getStringExtra("id");

        //getting the current group data

        reference2 = FirebaseDatabase.getInstance().getReference("Groups").child(group_id);
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                incurrentGroup = snapshot.getValue(Groups.class);
                inGroupName.setText(incurrentGroup.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        inGroupName = findViewById(R.id.inGroupName);
        edit_img = findViewById(R.id.edit_img);

        edit_img.setOnClickListener(v -> {
            if(firebaseUser.getUid().equals(incurrentGroup.getAdmin_ID())){
                Intent intent2 = new Intent(InsideGroupActivity.this, EditGroupActivity.class);
                intent2.putExtra("id", group_id);
                startActivity(intent2);
                finish();
            }else {
                Toast.makeText(InsideGroupActivity.this, "You dont have the permissions to edit this group! ", Toast.LENGTH_SHORT).show();
            }
        });


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        InsideGroupActivity.ViewPagerAdapter viewPagerAdapter = new InsideGroupActivity.ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new FilesFragment(), "Files");
        viewPagerAdapter.addFragment(new MessageBoardFragment(), "Group Chat");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

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

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);

        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }

    private void selectFile(){
        Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
        fileintent.setType("gagt/sdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(fileintent, READ_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_addtask:
                Intent intent = new Intent(InsideGroupActivity.this, CreateTaskActivity.class);
                intent.putExtra("id", group_id);
                startActivity(intent);
                finish();
                return true;
            case R.id.menu_media_center:
                // open file uploader
                Intent intent2 = new Intent(InsideGroupActivity.this, CreateFileActivity.class);
                intent2.putExtra("id", group_id);
                startActivity(intent2);
                finish();
                return true;
            case R.id.menu_leavegroup:
                // if you are the admin of the group
                if(firebaseUser.getUid().equals(incurrentGroup.getAdmin_ID())){
                    Toast.makeText(InsideGroupActivity.this, "You are the admin, you cant leave this group!", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(InsideGroupActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("Leaving Group");
                    builder.setMessage("Are you sure you want to leave the group?");
                    builder.setPositiveButton("Continue",
                            (dialog, which) -> {
                                Toast.makeText(InsideGroupActivity.this, "You've left the group", Toast.LENGTH_SHORT).show();

                                DatabaseReference leaveref = FirebaseDatabase.getInstance().getReference("UserGroups");
                                leaveref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            UserGroups userGroups = dataSnapshot.getValue(UserGroups.class);
                                            // if same group and same user
                                            if (userGroups.getGroupid().equals(group_id) && userGroups.getUserid().equals(firebaseUser.getUid())) {
                                                // now you can delete the record
                                                dataSnapshot.getRef().removeValue();
                                                break;
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });

                                // go back to the main page
                                Intent intent3 = new Intent(InsideGroupActivity.this, MainActivity.class);
                                startActivity(intent3);
                                finish();
                            });

                    builder.setNegativeButton("Cancel",
                            (dialog, which) -> {
                                // just close
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            case R.id.menu_addusers:
                // ony admin can add users
                if(firebaseUser.getUid().equals(incurrentGroup.getAdmin_ID())){
                    Intent intent4 = new Intent(InsideGroupActivity.this, AddUserActivity.class);
                    intent4.putExtra("id", group_id);
                    startActivity(intent4);
                    finish();
                }else{
                    Toast.makeText(InsideGroupActivity.this, "Only admins can remove/add users!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.menu_removeusers:
                // only admin can remove users
                if(firebaseUser.getUid().equals(incurrentGroup.getAdmin_ID())){
                    // if admin
                    Intent intent5 = new Intent(InsideGroupActivity.this, RemoveUserActivity.class);
                    intent5.putExtra("id", group_id);
                    startActivity(intent5);
                    finish();
                }else{
                    Toast.makeText(InsideGroupActivity.this, "Only admins can remove/add users!", Toast.LENGTH_SHORT).show();

                }
                return true;
            case R.id.menu_deletegroup:
                //
                if(firebaseUser.getUid().equals(incurrentGroup.getAdmin_ID())){
                    // del group
                }else{
                    Toast.makeText(InsideGroupActivity.this, "You are not the admin, you cant delete this group!", Toast.LENGTH_SHORT).show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
