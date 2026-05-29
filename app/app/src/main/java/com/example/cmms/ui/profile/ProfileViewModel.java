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
import com.example.cmms.utils.NetworkUtils;

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
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            loadFromLocal();
            return;
        }

        isLoading.setValue(true);

        ApiService api = ApiClient.getApiService(getApplication());

        api.getMe().enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserResponse> call, @NonNull Response<UserResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    user.postValue(response.body());
                } else {
                    Log.e(TAG, "loadProfile failed with code: " + response.code());
                    loadFromLocal();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "loadProfile network error", t);
                isLoading.postValue(false);
                loadFromLocal();
            }
        });
    }

    private void loadFromLocal() {
        isLoading.setValue(false);
        UserResponse localUser = UserResponse.fromLocal(
                authRepository.getSavedUserName(),
                authRepository.getSavedUserEmail(),
                authRepository.getSavedUserRole()
        );
        user.setValue(localUser);
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
