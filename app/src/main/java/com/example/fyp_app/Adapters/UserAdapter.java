package com.example.fyp_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fyp_app.InMessageActivity;
import com.example.fyp_app.Models.Chat;
import com.example.fyp_app.Models.User;
import com.example.fyp_app.R;
import com.example.fyp_app.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.BreakIterator;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context currentContext;
    private List<User> users;
    private String lastMessage;

    public UserAdapter(Context currentContext, List<User> users) {
        this.users = users;
        this.currentContext = currentContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(currentContext).inflate(R.layout.displayuitems, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.user_firstname.setText(user.getFirstname() + " " + user.getLastname());

        if (user.getImageLoc().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(currentContext)
                    .load(user.getImageLoc())
                    .into(holder.profile_image);
        }

        lastMessage(user.getId(), holder.lastsentmessage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(currentContext, InMessageActivity.class);
                intent.putExtra("userid", user.getId());
                currentContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView user_firstname;
        private ImageView profile_image;
        private TextView lastsentmessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_firstname = itemView.findViewById(R.id.user_firstname);
            profile_image = itemView.findViewById(R.id.profile_img);
            lastsentmessage = itemView.findViewById(R.id.lastsentmessage);
        }
    }

    // get last message

    private void lastMessage(final String userID, final TextView lastmsg){
        lastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userID) ||
                            chat.getReceiver().equals(userID) && chat.getSender().equals(firebaseUser.getUid())){
                        lastMessage = chat.getMessage();
                    }
                }

                switch (lastMessage){
                    case "default":
                        lastmsg.setText("No Message");
                        break;

                    default:
                        lastmsg.setText(lastMessage);
                        break;
                }

                lastMessage = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
