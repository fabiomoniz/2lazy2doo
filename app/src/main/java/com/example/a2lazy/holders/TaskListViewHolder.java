package com.example.a2lazy.holders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.example.a2lazy.R;
import com.example.a2lazy.TaskListActivity;
import com.example.a2lazy.fragments.ImageFragment;
import com.example.a2lazy.fragments.MenuFragment;
import com.example.a2lazy.models.TaskListModel;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TaskListViewHolder extends RecyclerView.ViewHolder {

    private TextView taskListNameTextView, createdByTextView, dateTextView;
    private ImageView imageV;

    public TaskListViewHolder(@NonNull final View itemView) {
        super(itemView);
        taskListNameTextView = itemView.findViewById(R.id.task_list_name_text_view);
        createdByTextView = itemView.findViewById(R.id.created_by_text_view);
        dateTextView = itemView.findViewById(R.id.date_text_view);
        imageV = itemView.findViewById(R.id.image_view);

        imageV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(itemView.getContext(), "clicked" , Toast.LENGTH_LONG).show();



                FragmentManager fm = ((AppCompatActivity) itemView.getContext()).getSupportFragmentManager();

                ImageFragment imageFragment = new ImageFragment();
                imageFragment.show(fm, "menu alarm");
            }
        });
    }

    public void setTaskList(final Context context, final String userEmail, final TaskListModel taskModel) {
        final String taskListId = taskModel.getTaskListId();

        final String taskListName = taskModel.getTaskListName();
        taskListNameTextView.setText(taskListName);

        String createdBy = "Created by: " + taskModel.getCreatedBy();
        createdByTextView.setText(createdBy);

        if(taskModel.getImageUrl() != ""){
            String imageUrl = taskModel.getImageUrl();
            Picasso.with(context)
                    .load(imageUrl)
                    .resize(50, 50)
                    .centerCrop()
                    .into(imageV);
        }


        Date date = taskModel.getDate();
        if (date != null) {
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
            String shoppingListCreationDate = dateFormat.format(date);
            dateTextView.setText(shoppingListCreationDate);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), TaskListActivity.class);
                intent.putExtra("taskModel", taskModel);
                view.getContext().startActivity(intent);
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();

                MenuFragment menuFragment = new MenuFragment();
                menuFragment.userEmail = userEmail;
                menuFragment.taskListId = taskListId;
                menuFragment.taskListName = taskListName;
                menuFragment.show(fm, "menu dialog");

                return true;
            }
        });
    }
}
