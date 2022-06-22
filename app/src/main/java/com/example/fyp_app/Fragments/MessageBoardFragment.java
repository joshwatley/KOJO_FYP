package com.example.fyp_app.Fragments;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_app.Adapters.MessageBoardAdapter;
import com.example.fyp_app.Adapters.TaskAdapter;
import com.example.fyp_app.Adapters.UserAdapter;
import com.example.fyp_app.InMessageActivity;
import com.example.fyp_app.MainActivity;
import com.example.fyp_app.Models.GroupChats;
import com.example.fyp_app.Models.User;
import com.example.fyp_app.R;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MessageBoardFragment extends Fragment {

    private UserAdapter userAdapter;
    private RecyclerView recyclerV;
    private List<User> mUsers;
    private String mUserID;

    MessageBoardAdapter messageBoardAdapter;

    TextView nomessages;
    ImageButton sendMessage;
    RecyclerView recyclerView;
    TextView messagetosend;

    String currentgroupID;
    String sname;

    List<GroupChats> mchats;

    FirebaseUser fuser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_messageboard, container, false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        nomessages = view.findViewById(R.id.nomessages);
        // get the possible sender name
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User u = snapshot.getValue(User.class);
                sname = u.getFirstname();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        currentgroupID = Objects.requireNonNull(getActivity()).getIntent().getStringExtra("id");
        sendMessage = view.findViewById(R.id.btn_send);
        messagetosend = view.findViewById(R.id.text_to_send);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        sendMessage.setOnClickListener(v -> {
            String msg = messagetosend.getText().toString();
            if (!msg.equals("")){
                sendMessage(fuser.getUid(), sname, currentgroupID, msg);
            }else{
                Toast.makeText(getContext(), "No message to send", Toast.LENGTH_SHORT).show();
            }
            messagetosend.setText("");
        });

        readMessages();


        return view;




    }

    private void sendMessage(String senderid, String sendername,String groupid, String message) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", senderid);
        hashMap.put("sendername", sendername);
        hashMap.put("group", groupid);
        hashMap.put("message", message);

        reference.child("GroupChats").push().setValue(hashMap);
    }

    private void readMessages(){
        mchats = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("GroupChats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchats.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    GroupChats gc = dataSnapshot.getValue(GroupChats.class);
                    if (gc.getGroup().equals(currentgroupID)){
                        mchats.add(gc);
                    }

                    if(mchats.size() == 0){
                        nomessages.setVisibility(View.VISIBLE);
                    }else {
                        nomessages.setVisibility(View.INVISIBLE);
                        messageBoardAdapter = new MessageBoardAdapter(getContext(), mchats, "default");
                        recyclerView.setAdapter(messageBoardAdapter);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
