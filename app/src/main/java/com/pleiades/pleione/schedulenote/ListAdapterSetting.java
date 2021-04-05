package com.pleiades.pleione.schedulenote;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapterSetting extends BaseAdapter {
    private final ArrayList<String> settingList;
    private final int listCount;

    public ListAdapterSetting(ArrayList<String> newSettingList) {
        settingList = newSettingList;
        listCount = settingList.size();
    }

    @Override
    public int getCount() {
        return listCount;
    }

    @Override
    public Object getItem(int position) {
        return settingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        SettingViewHolder holder;

        if (convertView == null) {
            final Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_setting, parent, false);

            // holder
            holder = new SettingViewHolder();
            holder.tv = convertView.findViewById(R.id.settingTitle);
            holder.settingSwitch = convertView.findViewById(R.id.settingSwitch);

            convertView.setTag(holder);
        } else {
            holder = (SettingViewHolder) convertView.getTag();
        }
        final SharedPreferences prefs = convertView.getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();
        holder.tv.setText(settingList.get(position));

        holder.settingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!LayoutSetting.settingErrorLock) {
                    if (isChecked) {
                        if (pos == 0) {
                            editor.putBoolean("receiveNotification", true);
                            editor.apply();
                            MainActivity.setScheduleNotifications();
                        }
                    } else {
                        if (pos == 0) {
                            editor.putBoolean("receiveNotification", false);
                            editor.apply();
                            MainActivity.releaseScheduleNotifications();
                        }
                    }
                }
            }
        });

        if (position < 1) {
            holder.settingSwitch.setVisibility(View.VISIBLE);
            LayoutSetting.settingErrorLock = true;
            if (position == 0)
                holder.settingSwitch.setChecked(prefs.getBoolean("receiveNotification", false));
            LayoutSetting.settingErrorLock = false;
        } else
            holder.settingSwitch.setVisibility(View.INVISIBLE);

        return convertView;
    }
}

class SettingViewHolder {
    TextView tv;
    Switch settingSwitch;
}