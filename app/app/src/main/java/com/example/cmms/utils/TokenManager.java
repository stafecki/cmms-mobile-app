package com.example.cmms.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {

    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static String getToken(Context context){
        SharedPreferences prefs = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE);
        return prefs.getString("token", null);
    }

    public static void clearToken(Context context){
        SharedPreferences prefs = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("token");
        editor.apply();
    }


}
