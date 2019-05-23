package com.example.a2lazy.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.a2lazy.R;

import java.util.Calendar;

public class MenuFragment extends AppCompatDialogFragment {

    private MenuFragmentListener listener;
    private Button editButton;
    private Button cameraButton;
    private Button delete;
    private Button alarm;
    public String taskListId,taskListName,userEmail;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.customedit_dialog_fragment, null);

        builder.setView(view);

        editButton = view.findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.editTask(taskListId, taskListName);
                dismiss();
            }
        });

        cameraButton = view.findViewById(R.id.camera_open);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.openCamera(taskListId);
                dismiss();
            }
        });

        delete = view.findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.delete(userEmail,taskListId);
                dismiss();
            }
        });

        alarm = view.findViewById(R.id.alarm_button);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();

                AlarmFragment alarmFragment = new AlarmFragment();
                alarmFragment.show(fm, "menu alarm");
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (MenuFragmentListener) context;
        } catch (ClassCastException e) {
            throw  new ClassCastException(context.toString() +
                    "must implement MenuFragmentListener into menuholder");
        }
    }

    public interface MenuFragmentListener {
        void editTask(String taskListId, String taskListName);
        void openCamera(String taskListId);
        void delete(String userEmail , String TaskListId);
    }
}
