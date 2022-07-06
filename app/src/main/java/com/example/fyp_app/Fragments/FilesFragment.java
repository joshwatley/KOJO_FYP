package com.example.fyp_app.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_app.Adapters.FileAdapter;
import com.example.fyp_app.Models.File;
import com.example.fyp_app.Models.Groups;
import com.example.fyp_app.Models.Task;
import com.example.fyp_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FilesFragment extends Fragment {

    private FileAdapter fileAdapter;
    private RecyclerView recyclerView;
    private String mUserID;
    private TextView noFiles;

    private List<File> mFiles;
    private List<Task> ttodo;
    private List<Task> inpro;
    private List<Task> done;
    private List<String> mFileIDs;

    private Groups currentGroup;
    private String currentgroupID;

    FirebaseUser fuser;
    DatabaseReference reference2;
    DatabaseReference reference;
    DatabaseReference reference3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_files, container, false);

        noFiles = view.findViewById(R.id.nofiles);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        mFileIDs = new ArrayList<>();
        mFiles = new ArrayList<>();

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

        readFiles();

        return view;
    }

    public void readFiles(){
        reference = FirebaseDatabase.getInstance().getReference("Files");

        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFiles.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    File f = dataSnapshot.getValue(File.class);

                    if (f.getGroup_id().equals(currentgroupID)){
                        mFiles.add(f);

                    }
                }
//                mFiles.clear();

//                List<File> newList = mFiles.stream().distinct().collect(Collectors.toList());
                System.out.println("Size of List " +mFiles.size());
                if(mFiles.size() == 0){
                                noFiles.setVisibility(View.VISIBLE);
                            }else {
                                noFiles.setVisibility(View.INVISIBLE);
                                fileAdapter = new FileAdapter(getContext(), mFiles);
                                recyclerView.setAdapter(fileAdapter);
                            }

//                for (String taskid : mFileIDs){
//
//                    reference3 = FirebaseDatabase.getInstance().getReference("Tasks").child(taskid);
//                    reference3.addValueEventListener(new ValueEventListener() {
//                        @RequiresApi(api = Build.VERSION_CODES.N)
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            Task task2add = snapshot.getValue(Task.class);
//
//                            assert task2add != null;
//                            switch (task2add.getPriority()){
//                                case "0":
//                                    ttodo.add(task2add);
//                                    break;
//                                case "1":
//                                    inpro.add(task2add);
//                                    break;
//                                case "2":
//                                    done.add(task2add);
//                                    break;
//                            }
//                            mFiles.addAll(ttodo);
//                            mFiles.addAll(inpro);
//                            mFiles.addAll(done);
//
//                            List<Task> newList = mFiles.stream()
//                                    .distinct()
//                                    .collect(Collectors.toList());
//
//                            if(newList.size() == 0){
//                                noFiles.setVisibility(View.VISIBLE);
//                            }else {
//                                noFiles.setVisibility(View.INVISIBLE);
//                                taskAdapter = new TaskAdapter(getContext(), newList);
//                                recyclerView.setAdapter(taskAdapter);
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {}
//                    });
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
