package com.example.cmms.ui.dashboard;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.cmms.data.remote.ApiClient;
import com.example.cmms.data.remote.ApiService;
import com.example.cmms.data.remote.models.DashboardResponse;
import com.example.cmms.data.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends AndroidViewModel {

    private static final String TAG = "DashboardViewModel";

    private final AuthRepository authRepository;

    private final MutableLiveData<DashboardResponse> dashboardData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public void loadDashboard(@NonNull String period) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        String token = authRepository.getToken();
        ApiService api = ApiClient.getApiService(token);

        api.getDashboard(period).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    dashboardData.postValue(response.body());
                } else {
                    Log.e(TAG, "loadDashboard failed with code: " + response.code());
                    errorMessage.postValue("Failed to load dashboard: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "loadDashboard network error", t);
                isLoading.postValue(false);
                errorMessage.postValue(t.getMessage());
            }
        });
    }

    public void refresh() {
        loadDashboard("month");
    }

    @NonNull
    public MutableLiveData<DashboardResponse> getDashboardData() {
        return dashboardData;
    }

    @NonNull
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @NonNull
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
