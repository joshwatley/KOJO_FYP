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

import com.example.fyp_app.InMessageActivity;
import com.example.fyp_app.InsideGroupActivity;
import com.example.fyp_app.Models.Chat;
import com.example.fyp_app.Models.Groups;
import com.example.fyp_app.Models.User;
import com.example.fyp_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.security.acl.Group;
import java.text.BreakIterator;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private Context currentContext;
    private List<Groups> groups;

    public GroupAdapter(Context currentContext, List<Groups> groups) {
        this.groups = groups;
        this.currentContext = currentContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(currentContext).inflate(R.layout.displaygitems, parent, false);
        return new GroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Groups group = groups.get(position);
        holder.sgroup_name.setText(group.getName());
        holder.itemView.setOnClickListener(v -> {
            // go the group home page
            Intent intent = new Intent(currentContext, InsideGroupActivity.class);
            intent.putExtra("id", group.getGroupid());
            currentContext.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView sgroup_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sgroup_name = itemView.findViewById(R.id.sgroup_name);
        }
    }

    // get last message

}
