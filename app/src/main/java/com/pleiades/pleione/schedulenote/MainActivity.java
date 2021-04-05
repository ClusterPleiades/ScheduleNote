package com.pleiades.pleione.schedulenote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    public static Context applicationContext;
    public static ArrayList<Schedule> scheduleList;

    private static ListAdapterSchedule adapter;
    private static ListView scheduleListView;
    private static int editPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_bar_title);

        // application context
        applicationContext = getApplicationContext();

        // schedule list initialize
        scheduleList = PrefsController.getScheduleListPrefs(applicationContext, "scheduleList");
        if (scheduleList == null) {
            scheduleList = new ArrayList<>();
            PrefsController.setScheduleListPrefs(applicationContext, "scheduleList", scheduleList);
        }

        // checkbox initialize
        for (int i = 0; i < scheduleList.size(); i++) {
            scheduleList.get(i).checked = false;
        }

        // list view initialize
        scheduleListView = (ListView) findViewById(R.id.scheduleList);

        // sort schedule list
        sortSchedule();

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Schedule item = (Schedule) parent.getAdapter().getItem(position);

                Intent intent = new Intent(applicationContext, LayoutScheduleEdit.class);

                intent.putExtra("title", item.title);
                intent.putExtra("date", item.date);
                intent.putExtra("time", item.time);
                intent.putExtra("memo", item.memo);
                intent.putExtra("postMeridiem", item.postMeridiem);

                // remember position to edit
                editPos = position;

                startActivity(intent);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(applicationContext, LayoutScheduleAdd.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        for (int i = 0; i < menu.size(); i++) {
//            Drawable drawable = menu.getItem(i).getIcon();
//            if (drawable != null) {
//                drawable.mutate();
//                drawable.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
//            }
//        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.deleteSchedule) {
            deleteDialog();
        } else if (id == R.id.completeSchedule) {
            completeDialog();
        } else if (id == R.id.setting) {
            Intent intent = new Intent(applicationContext, LayoutSetting.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public static void sortSchedule() {
        Collections.sort(scheduleList);

        adapter = new ListAdapterSchedule(scheduleList);
        scheduleListView.setAdapter(adapter);
    }

    public static void saveSchedule(String title, String date, String time, String memo, boolean postMeridiem) {
        Schedule newSchedule = new Schedule();

        newSchedule.title = title;
        newSchedule.date = date;
        newSchedule.time = time;
        newSchedule.memo = memo;
        newSchedule.postMeridiem = postMeridiem;

        scheduleList.add(newSchedule);

        sortSchedule();

        scheduleListView.setAdapter(adapter);

        // prefs control
        PrefsController.setScheduleListPrefs(applicationContext, "scheduleList", scheduleList);

        releaseScheduleNotifications();
        setScheduleNotifications();
    }

    public static void editSchedule(String title, String date, String time, String memo, boolean postMeridiem) {
        scheduleList.get(editPos).title = title;
        scheduleList.get(editPos).date = date;
        scheduleList.get(editPos).time = time;
        scheduleList.get(editPos).memo = memo;
        scheduleList.get(editPos).postMeridiem = postMeridiem;

        sortSchedule();

        // prefs control
        PrefsController.setScheduleListPrefs(applicationContext, "scheduleList", scheduleList);

        releaseScheduleNotifications();
        setScheduleNotifications();
    }

    // delete dialog
    public void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppTheme_AlertDialogOverlay);

        // cannot cancel
        builder.setCancelable(false);

        // builder.setTitle
        builder.setMessage(R.string.dialog_ask_delete);
        builder.setPositiveButton(R.string.dialog_confirm,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        releaseScheduleNotifications();

                        Iterator<Schedule> iterator = scheduleList.iterator();
                        while (iterator.hasNext()) {
                            Schedule item = iterator.next();

                            if (item.checked) {
                                iterator.remove();
                            }
                        }

                        sortSchedule();

                        // prefs control
                        PrefsController.setScheduleListPrefs(applicationContext, "scheduleList", scheduleList);

                        setScheduleNotifications();
                    }
                });
        builder.setNegativeButton(R.string.dialog_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        TextView textView = dialog.findViewById(android.R.id.message);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(R.color.colorDrawText));

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
    }

    // complete dialog
    public void completeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppTheme_AlertDialogOverlay);

        // cannot cancel
        builder.setCancelable(false);

        // builder.setTitle
        builder.setMessage(R.string.dialog_ask_complete);
        builder.setPositiveButton(R.string.dialog_confirm,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        releaseScheduleNotifications();

                        for (int i = 0; i < scheduleList.size(); i++) {
                            if (scheduleList.get(i).checked) {
                                scheduleList.get(i).completed = true;
                                scheduleList.get(i).checked = false;
                            }
                        }

                        sortSchedule();

                        // prefs control
                        PrefsController.setScheduleListPrefs(applicationContext, "scheduleList", scheduleList);

                        setScheduleNotifications();
                    }
                });
        builder.setNegativeButton(R.string.dialog_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

        TextView textView = dialog.findViewById(android.R.id.message);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(R.color.colorDrawText));

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
    }

    // add all notification reservation
    public static void setScheduleNotifications() {
        SharedPreferences prefs = applicationContext.getSharedPreferences("prefs", MODE_PRIVATE);
        String schedule_time_default = MainActivity.applicationContext.getString(R.string.schedule_time_default);

        if (prefs.getBoolean("receiveNotification", false)) {
            for (int i = 0; i < scheduleList.size(); i++) {
                if (!scheduleList.get(i).completed && !scheduleList.get(i).time.equals(schedule_time_default)) {
                    String date = scheduleList.get(i).date;
                    String time = scheduleList.get(i).time;

                    int year = Integer.parseInt(date.substring(0, 4));
                    int month = Integer.parseInt(date.substring(5, 7));
                    int day = Integer.parseInt(date.substring(8, 10));
                    int hour = Integer.parseInt(time.substring(0, 2));
                    int minute = Integer.parseInt(time.substring(3, 5));

                    if (hour == 12)
                        hour = 0;
                    if (scheduleList.get(i).postMeridiem)
                        hour = hour + 12;
                    month--;

                    Calendar cal = Calendar.getInstance();

                    // get time now
                    int yearNow = cal.get(Calendar.YEAR);
                    int monthNow = cal.get(Calendar.MONTH);
                    int dayNow = cal.get(Calendar.DATE);
                    int hourNow = cal.get(Calendar.HOUR_OF_DAY);
                    int minuteNow = cal.get(Calendar.MINUTE);

                    if (isEnteredTimeIsLater(year, month, day, hour, minute, yearNow, monthNow, dayNow, hourNow, minuteNow) == 1) {
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DATE, day);
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);

                        ArrayList<NotificationRequest> notificationRequestList = new ArrayList<>();
                        NotificationRequest notificationRequest = new NotificationRequest();
                        notificationRequest.position = i;
                        notificationRequest.completed = false;
                        notificationRequestList.add(notificationRequest);
                        PrefsController.setNotificationRequestListPrefs(applicationContext, "notificationRequestList", notificationRequestList);

                        NotificationAlarm.setAlarm(applicationContext, cal, i); // request code is position
                    }

                }
            }
        }
    }

    public static int isEnteredTimeIsLater(int year, int month, int day, int hour, int minute, int yearNow, int monthNow, int dayNow, int hourNow, int minuteNow) {
        int result = compareInt(year, yearNow);

        if (result == 0)
            result = compareInt(month, monthNow);

        if (result == 0)
            result = compareInt(day, dayNow);

        if (result == 0)
            result = compareInt(hour, hourNow);

        if (result == 0)
            result = compareInt(minute, minuteNow);

        return result;
    }

    public static int compareInt(int x, int y) {
        return Integer.compare(x, y);
    }

    // remove all notification reservation
    public static void releaseScheduleNotifications() {
        SharedPreferences prefs = applicationContext.getSharedPreferences("prefs", MODE_PRIVATE);
        if (prefs.getBoolean("receiveNotification", false)) {
            for (int i = 0; i < scheduleList.size(); i++) {
                if (!scheduleList.get(i).completed) {
                    NotificationAlarm.releaseAlarm(applicationContext, i);
                }
            }
        }
    }
}
