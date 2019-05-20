package com.example.a2lazy.holders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.a2lazy.R;
import com.example.a2lazy.TaskListActivity;
import com.example.a2lazy.models.TaskListModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TaskListViewHolder extends RecyclerView.ViewHolder {

    private TextView taskListNameTextView, createdByTextView, dateTextView;

    public TaskListViewHolder(@NonNull View itemView) {
        super(itemView);
        taskListNameTextView = itemView.findViewById(R.id.task_list_name_text_view);
        createdByTextView = itemView.findViewById(R.id.created_by_text_view);
        dateTextView = itemView.findViewById(R.id.date_text_view);
    }

    public void setTaskList(final Context context, final String userEmail, final TaskListModel taskModel) {
        final String taskListId = taskModel.getTaskListId();

        final String taskListName = taskModel.getTaskListName();
        taskListNameTextView.setText(taskListName);

        String createdBy = "Created by: " + taskModel.getCreatedBy();
        createdByTextView.setText(createdBy);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit Shopping List Name");

                final EditText editText = new EditText(context);
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                editText.setText(taskListName);
                editText.setSelection(editText.getText().length());
                editText.setHint("Type a name");
                editText.setHintTextColor(Color.GRAY);
                builder.setView(editText);

                final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                final Map<String, Object> map = new HashMap<>();

                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newTaskListName = editText.getText().toString().trim();
                        map.put("taskListName", newTaskListName);
                        rootRef.collection("taskLists").document(userEmail).collection("userTaskLists").document(taskListId).update(map);

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });


                /**BASTIAN!!! GOOD NOW THAT I GOT YOUR ATTENTION*/



                  /**THEN !!!! uncomment this and deleted the top one*/
//                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
                      /** create a reference to the tasklistid in database  called "tasklistIdRef"*/
                      /**then over here create a new query (i think)the path must associated with all the products  with this task and then delete it, its 6 am and i cant sleep or think proporlly i cant even spell ... */
                      /**rootRef.collection("taskLists").document(userEmail).collection("userTaskLists").document(taskListId).update(map); maybe like this*/
                      /**i cant think straight anymore*/
//
//                        taskListIdRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Snackbar.make(taskListViewFragment, "Task Deleted!", Snackbar.LENGTH_LONG).show();
//                                }
//                        });
//                    }
//                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
    }


}
