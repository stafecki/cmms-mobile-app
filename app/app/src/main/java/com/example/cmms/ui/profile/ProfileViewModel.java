package com.example.cmms.ui.profile;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.cmms.data.remote.ApiClient;
import com.example.cmms.data.remote.ApiService;
import com.example.cmms.data.remote.models.UserResponse;
import com.example.cmms.data.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {

    private static final String TAG = "ProfileViewModel";

    private final AuthRepository authRepository;

    private final MutableLiveData<UserResponse> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> logoutSuccess = new MutableLiveData<>(false);

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public void loadProfile() {
        isLoading.setValue(true);

        String token = authRepository.getToken();
        ApiService api = ApiClient.getApiService(token);

        api.getMe().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    user.postValue(response.body());
                } else {
                    Log.e(TAG, "loadProfile failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "loadProfile network error", t);
                isLoading.postValue(false);
            }
        });
    }

    public void logout() {
        authRepository.logout();
        logoutSuccess.setValue(true);
    }

    @NonNull
    public MutableLiveData<UserResponse> getUser() {
        return user;
    }

    @NonNull
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @NonNull
    public MutableLiveData<Boolean> getLogoutSuccess() {
        return logoutSuccess;
    }
}
