package com.example.cmms.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.cmms.data.remote.ApiClient;
import com.example.cmms.data.remote.ApiService;
import com.example.cmms.data.remote.models.LoginRequest;
import com.example.cmms.data.remote.models.LoginResponse;
import com.example.cmms.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private static final String TAG = "AuthRepository";

    private final SharedPreferences prefs;

    public AuthRepository(@NonNull Context context) {
        this.prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public interface AuthCallback {
        void onSuccess();
        void onError(String message);
    }

    public void login(@NonNull String email, @NonNull String password, @NonNull AuthCallback callback) {
        ApiService api = ApiClient.getApiService(null);
        api.login(new LoginRequest(email, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse body = response.body();
                    if (body.getTokens() == null || body.getUser() == null) {
                        Log.e(TAG, "Login response missing tokens or user data");
                        callback.onError("Invalid server response");
                        return;
                    }
                    prefs.edit()
                            .putString(Constants.KEY_TOKEN, body.getTokens().getAccessToken())
                            .putString(Constants.KEY_USER_ID, body.getUser().getId())
                            .putString(Constants.KEY_USER_ROLE, body.getUser().getRole())
                            .apply();
                    Log.d(TAG, "Login successful for user: " + body.getUser().getId());
                    callback.onSuccess();
                } else {
                    Log.e(TAG, "Login failed with code: " + response.code());
                    callback.onError("Login failed: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Login network error", t);
                callback.onError(t.getMessage());
            }
        });
    }

    public void logout() {
        ApiService api = ApiClient.getApiService(getToken());
        api.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Log.d(TAG, "Logout response: " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e(TAG, "Logout network error", t);
            }
        });
        prefs.edit().clear().apply();
    }

    public String getToken() {
        return prefs.getString(Constants.KEY_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return getToken() != null;
    }

    public String getSavedUserId() {
        return prefs.getString(Constants.KEY_USER_ID, null);
    }

    public String getSavedUserRole() {
        return prefs.getString(Constants.KEY_USER_ROLE, null);
    }
}
