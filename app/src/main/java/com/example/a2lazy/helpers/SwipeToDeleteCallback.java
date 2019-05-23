package com.example.a2lazy.helpers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import com.example.a2lazy.holders.TaskListViewHolder;
import com.example.a2lazy.models.TaskListModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private FirestoreRecyclerAdapter<TaskListModel, TaskListViewHolder> mAdapter;
    private Drawable icon;
    private final ColorDrawable background;
    private swipeListener listener;
    private String UserEmail;

    public SwipeToDeleteCallback(String userEmail, FirestoreRecyclerAdapter<TaskListModel, TaskListViewHolder> adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        UserEmail = userEmail;
        background = new ColorDrawable(Color.RED);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();
        String taskListID = mAdapter.getItem(position).getTaskListId(); // returns null and i need to get tasklistid another way
        listener.deleteItem(UserEmail, taskListID);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        if (dX > 0) { // Swiping to the right
            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());

        } else if (dX < 0) { // Swiping to the left
            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }
        background.draw(c);
    }

    public interface swipeListener {
        void deleteItem(String userEmail , String TaskListId);
    }
}