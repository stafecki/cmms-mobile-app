package com.example.cmms.data.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cmms.utils.Constants;
import com.example.cmms.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class AuthInterceptor implements Interceptor {

    private static final String TAG = "AuthInterceptor";
    private final SharedPreferences prefs;

    public AuthInterceptor(Context context) {
        this.prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = prefs.getString(Constants.KEY_TOKEN, null);
        Request original = chain.request();

        Request request = original;
        if (token != null && !token.isEmpty()) {
            request = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
        }

        Response response = chain.proceed(request);

        if (response.code() == 401 && !original.url().encodedPath().contains("auth/")) {
            String refreshToken = prefs.getString(Constants.KEY_REFRESH_TOKEN, null);
            if (refreshToken != null) {
                String newToken = tryRefresh(refreshToken);
                if (newToken != null) {
                    response.close();
                    Request retry = original.newBuilder()
                            .header("Authorization", "Bearer " + newToken)
                            .build();
                    return chain.proceed(retry);
                } else {
                    prefs.edit().clear().apply();
                    SessionManager.notifySessionExpired();
                }
            } else {
                prefs.edit().clear().apply();
                SessionManager.notifySessionExpired();
            }
        }

        return response;
    }

    private String tryRefresh(String refreshToken) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("token", refreshToken);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(Constants.BASE_URL + "auth/refresh")
                    .post(RequestBody.create(body.toString(),
                            MediaType.parse("application/json")))
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String json = response.body().string();
                JsonObject obj = new Gson().fromJson(json, JsonObject.class);
                String newAccess = obj.has("accessToken") ? obj.get("accessToken").getAsString() : null;
                String newRefresh = obj.has("refreshToken") ? obj.get("refreshToken").getAsString() : null;

                if (newAccess != null) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(Constants.KEY_TOKEN, newAccess);
                    if (newRefresh != null) {
                        editor.putString(Constants.KEY_REFRESH_TOKEN, newRefresh);
                    }
                    editor.apply();
                    Log.d(TAG, "Token refreshed successfully");
                    return newAccess;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Token refresh failed", e);
        }
        return null;
    }
}
