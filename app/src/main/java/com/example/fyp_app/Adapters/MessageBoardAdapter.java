package com.example.fyp_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_app.InMessageActivity;
import com.example.fyp_app.Models.Chat;
import com.example.fyp_app.Models.GroupChats;
import com.example.fyp_app.Models.User;
import com.example.fyp_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageBoardAdapter extends RecyclerView.Adapter<MessageBoardAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context currentContext;
    private List<GroupChats> gChats;
    private String imageurl;

    FirebaseUser fuser;

    public MessageBoardAdapter(Context currentContext, List<GroupChats> gChats, String imageurl) {
        this.gChats = gChats;
        this.currentContext = currentContext;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageBoardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(currentContext).inflate(R.layout.group_chat_item_right, parent, false);
            return new MessageBoardAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(currentContext).inflate(R.layout.group_chat_item_left, parent, false);
            return new MessageBoardAdapter.ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull MessageBoardAdapter.ViewHolder holder, int position) {

        GroupChats chat = gChats.get(position);
        holder.show_message.setText(chat.getMessage());
        holder.sendername.setText(chat.getSendername());

        if (imageurl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{

        }

    }


    @Override
    public int getItemCount() {
        return gChats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView show_message;
        private ImageView profile_image;
        private TextView sendername;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.profile_image);
            sendername = itemView.findViewById(R.id.uname);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (gChats.get(position).getSender().equals(fuser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
