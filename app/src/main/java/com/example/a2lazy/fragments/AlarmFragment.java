package com.example.a2lazy.fragments;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.a2lazy.AlarmManager.MyAlarm;
import com.example.a2lazy.R;

import java.util.Calendar;

public class AlarmFragment extends AppCompatDialogFragment {

    TimePicker timePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_alarm_fragment, null);

        timePicker = view.findViewById(R.id.timeclock);

        builder.setView(view)
                .setTitle("Set Alarm")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH),
                                    timePicker.getHour(),
                                    timePicker.getMinute(),
                                    0
                            );
                            setAlarm(calendar.getTimeInMillis());

                    }
                });

        return builder.create();
    }

    private void setAlarm(long timeInMillis){
        AlarmManager am = (AlarmManager) getContext().getSystemService(getContext().ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), MyAlarm.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),0, intent, 0);

        am.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(getActivity(), "Alarm has been set" , Toast.LENGTH_LONG).show();
    }
}
