package com.securityconfirm.layout.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class SecurityContext {
    private static final String USERLIST_TAG = "USERLIST";
    /**
     * full list of users, analog db, stored in shared preferences
     */
    private static List<Account> userList= new ArrayList<Account>();

    public static void save(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(userList);
        editor.putString(USERLIST_TAG, json);
        editor.apply();//.commit();
    }

    public static void init(Context context){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPrefs.getString(USERLIST_TAG , null);
        Type type = new TypeToken<List<Account>>() {}.getType();
        userList = gson.fromJson(json, type);
    }

    public static List<Account> getUserList() {
        return userList;
    }

    public static void add(Account account){
        userList.add(account);
    }

    public static void remove(Account account){
        userList.remove(account);
    }
}
