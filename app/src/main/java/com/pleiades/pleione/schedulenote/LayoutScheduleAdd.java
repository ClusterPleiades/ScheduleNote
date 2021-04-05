package com.pleiades.pleione.schedulenote;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LayoutScheduleAdd extends AppCompatActivity {
    private TextView tv;
    private boolean postMeridiem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_add);

        Toolbar toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        datePickerDialogInitialize();
        timePickerDialogInitialize();

        cancelButtonInitialize();
        saveButtonInitialize();

        // select title at start up
        findViewById(R.id.editTitle).requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule_add_action_bar, menu);

//        for(int i = 0; i < menu.size(); i++){
//            Drawable drawable = menu.getItem(i).getIcon();
//            if(drawable != null) {
//                drawable.mutate();
//                drawable.setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
//            }
//        }

        return true;
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.back) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void datePickerDialogInitialize() {
        final Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date = cal.get(Calendar.DATE);

        String dayOfWeek = getDayOfWeek(year, month, date);

//        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
//        Date selectedDate = new Date(year, month, date - 1);
//        String dayOfWeek = simpledateformat.format(selectedDate);
//        dayOfWeek = cutDayOfWeek(dayOfWeek);

        month++;
        tv = findViewById(R.id.editDate);
        String str = String.format(Locale.ENGLISH, "%d-%02d-%02d (%s)", year, month, date, dayOfWeek);
        tv.setText(str);

        findViewById(R.id.editDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(LayoutScheduleAdd.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
//                        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
//                        Date selectedDate = new Date(year, month, date - 1);
//                        String dayOfWeek = simpledateformat.format(selectedDate);
//                        dayOfWeek = cutDayOfWeek(dayOfWeek);
                            String dayOfWeek = getDayOfWeek(year, month, date);

                        month++;
                        tv = findViewById(R.id.editDate);
                        String str = String.format(Locale.ENGLISH, "%d-%02d-%02d (%s)", year, month, date, dayOfWeek);
                        tv.setText(str);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
                dialog.show();

            }
        });
    }

    private String getDayOfWeek(int year, int month, int date) {
        Date selectedDate = new Date(year, month, date - 1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);

        String dayOfWeek = "";
        switch(cal.get(Calendar.DAY_OF_WEEK)){
            case 1:
                dayOfWeek = getString(R.string.Sun);
                break;
            case 2:
                dayOfWeek = getString(R.string.Mon);
                break;
            case 3:
                dayOfWeek = getString(R.string.Tue);
                break;
            case 4:
                dayOfWeek = getString(R.string.Wed);
                break;
            case 5:
                dayOfWeek = getString(R.string.Thu);
                break;
            case 6:
                dayOfWeek = getString(R.string.Fri);
                break;
            case 7:
                dayOfWeek = getString(R.string.Sat);
                break;
        }
        return dayOfWeek;
    }

    private void timePickerDialogInitialize() {
        final Calendar cal = Calendar.getInstance();

        tv = findViewById(R.id.editTime);
        tv.setText(R.string.schedule_time_default);

        findViewById(R.id.editTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog dialog = new TimePickerDialog(LayoutScheduleAdd.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
                        tv = findViewById(R.id.editTime);

                        if (hour < 12) {
                            postMeridiem = false;
                            if (hour == 0)
                                hour = 12;
                            String str = String.format(Locale.ENGLISH, "%02d:%02d (%s)", hour, min, getString(R.string.am));
                            tv.setText(str);
                        } else {
                            postMeridiem = true;
                            if (hour != 12)
                                hour = hour - 12;
                            String str = String.format(Locale.ENGLISH, "%02d:%02d (%s)", hour, min, getString(R.string.pm));
                            tv.setText(str);
                        }
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false); // last parameter whether 24hour or not

                dialog.show();
            }
        });
    }

    private void cancelButtonInitialize() {
        tv = findViewById(R.id.editCancel);

        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void saveButtonInitialize() {
        tv = findViewById(R.id.editSave);

        tv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText newTitle = findViewById(R.id.editTitle);
                String title = newTitle.getText().toString();

                TextView newDate = findViewById(R.id.editDate);
                String date = newDate.getText().toString();

                TextView newTime = findViewById(R.id.editTime);
                String time = newTime.getText().toString();

                EditText newMemo = findViewById(R.id.editMemo);
                String memo = newMemo.getText().toString();

                MainActivity.saveSchedule(title, date, time, memo, postMeridiem);

                finish();
            }
        });
    }
}
