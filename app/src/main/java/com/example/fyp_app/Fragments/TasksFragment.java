package com.example.fyp_app.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_app.Adapters.TaskAdapter;
import com.example.fyp_app.Adapters.UserAdapter;
import com.example.fyp_app.InsideGroupActivity;
import com.example.fyp_app.MainActivity;
import com.example.fyp_app.Models.Groups;
import com.example.fyp_app.Models.Task;
import com.example.fyp_app.Models.TaskGroups;
import com.example.fyp_app.Models.User;
import com.example.fyp_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TasksFragment extends Fragment {

    private TaskAdapter taskAdapter;
    private RecyclerView recyclerView;
    private String mUserID;
    private TextView noTasks;

    private List<Task> mTasks;
    private List<Task> ttodo;
    private List<Task> inpro;
    private List<Task> done;
    private List<String> mTaskIDs;

    private Groups currentGroup;
    private String currentgroupID;

    FirebaseUser fuser;
    DatabaseReference reference2;
    DatabaseReference reference;
    DatabaseReference reference3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        noTasks = view.findViewById(R.id.notasks);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        mTaskIDs = new ArrayList<>();
        mTasks = new ArrayList<>();
        ttodo = new ArrayList<>();
        inpro = new ArrayList<>();
        done = new ArrayList<>();

        // get the current group we are in

        currentgroupID = Objects.requireNonNull(getActivity()).getIntent().getStringExtra("id");

        reference2 = FirebaseDatabase.getInstance().getReference("Groups").child(currentgroupID);
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentGroup = snapshot.getValue(Groups.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        readTasks();

        return view;
    }

    public void readTasks(){
        reference = FirebaseDatabase.getInstance().getReference("TaskGroups");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mTaskIDs.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    TaskGroups t = dataSnapshot.getValue(TaskGroups.class);

                    if (t.getGroupid().equals(currentgroupID)){
                        mTaskIDs.add(t.getTaskid());
                    }
                }
                mTasks.clear();
                ttodo.clear();
                inpro.clear();
                done.clear();

                for (String taskid : mTaskIDs){

                    reference3 = FirebaseDatabase.getInstance().getReference("Tasks").child(taskid);
                    reference3.addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Task task2add = snapshot.getValue(Task.class);

                            assert task2add != null;
                            switch (task2add.getPriority()){
                                case "0":
                                    ttodo.add(task2add);
                                    break;
                                case "1":
                                    inpro.add(task2add);
                                    break;
                                case "2":
                                    done.add(task2add);
                                    break;
                            }
                            mTasks.addAll(ttodo);
                            mTasks.addAll(inpro);
                            mTasks.addAll(done);

                            List<Task> newList = mTasks.stream()
                                    .distinct()
                                    .collect(Collectors.toList());

                            if(newList.size() == 0){
                                noTasks.setVisibility(View.VISIBLE);
                            }else {
                                noTasks.setVisibility(View.INVISIBLE);
                                taskAdapter = new TaskAdapter(getContext(), newList);
                                recyclerView.setAdapter(taskAdapter);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
