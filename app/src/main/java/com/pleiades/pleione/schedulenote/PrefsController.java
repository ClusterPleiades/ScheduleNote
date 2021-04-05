package com.pleiades.pleione.schedulenote;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class PrefsController {

    // notification request
    public static void setNotificationRequestListPrefs(Context context, String key, ArrayList<NotificationRequest> value) {
        SharedPreferences prefs = context.getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(value);

        editor.putString(key, json);
        editor.apply();
    }

    public static ArrayList<NotificationRequest> getNotificationRequestListPrefs(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences("prefs", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString(key, "");
        Type type = new TypeToken<ArrayList<NotificationRequest>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    // schedule
    public static void setScheduleListPrefs(Context context, String key, ArrayList<Schedule> value) {
        SharedPreferences prefs = context.getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(value);

        editor.putString(key, json);
        editor.apply();
    }

    public static ArrayList<Schedule> getScheduleListPrefs(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences("prefs", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString(key, "");
        Type type = new TypeToken<ArrayList<Schedule>>() {
        }.getType();

        return gson.fromJson(json, type);
    }
}