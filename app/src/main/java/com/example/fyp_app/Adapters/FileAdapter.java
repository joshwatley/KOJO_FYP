package com.example.fyp_app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_app.EditTaskActivity;
import com.example.fyp_app.Models.File;
import com.example.fyp_app.Models.Task;
import com.example.fyp_app.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private Context currentContext;
    private List<File> files;

    public FileAdapter(Context currentContext, List<File> files) {
        this.files = files;
        this.currentContext = currentContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(currentContext).inflate(R.layout.displayfitems, parent, false);
        return new FileAdapter.ViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final File file = files.get(position);
        holder.f_title.setText(file.getFile_title());
//        holder.t_img.setImageResource(R.mipmap.ic_launcher);
//        holder.t_desc.setText(task.getTask_content());
//        holder.t_head.setText(task.getHeader());

//
//        switch (task.getPriority()){
//            case "0":
//                holder.flagpri.setImageResource(R.color.colorTODO);
//                break;
//            case "1":
//                holder.flagpri.setImageResource(R.color.colorINPROGRESS);
//                break;
//            case "2":
//                holder.flagpri.setImageResource(R.color.colorDONE);
//                break;
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(file.getFile_link()));
                currentContext.startActivity(browserIntent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView f_title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            f_title = itemView.findViewById(R.id.f_title);
        }
    }

    // get last message

}
