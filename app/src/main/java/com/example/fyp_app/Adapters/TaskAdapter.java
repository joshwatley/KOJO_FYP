package com.example.fyp_app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_app.EditTaskActivity;
import com.example.fyp_app.Models.Task;
import com.example.fyp_app.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private Context currentContext;
    private List<Task> tasks;

    public TaskAdapter(Context currentContext, List<Task> tasks) {
        this.tasks = tasks;
        this.currentContext = currentContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(currentContext).inflate(R.layout.displaytitems, parent, false);
        return new TaskAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Task task = tasks.get(position);
        holder.t_title.setText(task.getTask_title());
        holder.t_img.setImageResource(R.mipmap.ic_launcher);
        holder.t_desc.setText(task.getTask_content());
        holder.t_head.setText(task.getHeader());


        switch (task.getPriority()){
            case "0":
                holder.flagpri.setImageResource(R.color.colorTODO);
                break;
            case "1":
                holder.flagpri.setImageResource(R.color.colorINPROGRESS);
                break;
            case "2":
                holder.flagpri.setImageResource(R.color.colorDONE);
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go the task page
                Intent intent = new Intent(currentContext, EditTaskActivity.class);
                intent.putExtra("taskid", task.getTask_id());
                currentContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView t_title;
        private ImageView t_img;
        private TextView t_head;
        private TextView t_desc;
        private RelativeLayout taskbackground;
        private CircleImageView flagpri;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            t_title = itemView.findViewById(R.id.f_title);
            t_desc = itemView.findViewById(R.id.t_desc);
            t_head = itemView.findViewById(R.id.t_head);
            t_img = itemView.findViewById(R.id.t_img);
            taskbackground = itemView.findViewById(R.id.taskflagcolour);
            flagpri = itemView.findViewById(R.id.t_img);
        }
    }

    // get last message

}
