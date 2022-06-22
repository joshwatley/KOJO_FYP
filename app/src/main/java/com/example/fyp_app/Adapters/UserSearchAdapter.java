package com.example.fyp_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.icu.text.UFormat;
import android.media.AudioFocusRequest;
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
import com.example.fyp_app.CreateGroupActivity;
import com.example.fyp_app.InMessageActivity;
import com.example.fyp_app.Models.Chat;
import com.example.fyp_app.Models.User;
import com.example.fyp_app.R;
import com.example.fyp_app.UserForGroupInterface;
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

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {

    UserForGroupInterface ufgInterface;

    private Context currentContext;
    private List<User> users;

    public UserSearchAdapter(Context currentContext, List<User> users, UserForGroupInterface ufgInterface) {
        this.users = users;
        this.currentContext = currentContext;
        try{
            this.ufgInterface = (UserForGroupInterface) currentContext;
        } catch (ClassCastException e){
            throw new ClassCastException("Calling context must implement UserForGroupInterface");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(currentContext).inflate(R.layout.displayuitems, parent, false);
        return new UserSearchAdapter.ViewHolder(view);
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


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // pass user to activity
                ufgInterface.setValue(user.getId());
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_firstname = itemView.findViewById(R.id.user_firstname);
            profile_image = itemView.findViewById(R.id.profile_img);
        }
    }

}
