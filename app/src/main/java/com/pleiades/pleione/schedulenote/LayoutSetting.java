package com.pleiades.pleione.schedulenote;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class LayoutSetting extends AppCompatActivity {

    public static boolean settingErrorLock = false; // prevent listener accumulate scripts repeatedly

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<String> settingList = new ArrayList<>();

        settingList.add(getString(R.string.setting_receive_notification));

        ListAdapterSetting adapterSetting = new ListAdapterSetting(settingList);
        ListView settingListView = findViewById(R.id.settingList);
        settingListView.setAdapter(adapterSetting);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule_add_action_bar, menu);

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
}