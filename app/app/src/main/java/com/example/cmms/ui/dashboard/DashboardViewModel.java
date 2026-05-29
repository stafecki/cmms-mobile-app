package com.example.cmms.ui.dashboard;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.cmms.data.local.AppDatabase;
import com.example.cmms.data.local.dao.WorkOrderDao;
import com.example.cmms.data.local.entities.WorkOrderEntity;
import com.example.cmms.data.remote.ApiClient;
import com.example.cmms.data.remote.ApiService;
import com.example.cmms.data.remote.models.DashboardResponse;
import com.example.cmms.data.remote.models.WorkOrderResponse;
import com.example.cmms.data.repository.AuthRepository;
import com.example.cmms.utils.NetworkUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends AndroidViewModel {

    private static final String TAG = "DashboardViewModel";

    private final AuthRepository authRepository;
    private final WorkOrderDao workOrderDao;
    private final Set<String> OPEN_STATUSES = new HashSet<>(Arrays.asList("NEW", "IN_PROGRESS", "WAITING_FOR_PARTS"));

    private final MutableLiveData<DashboardResponse> dashboardData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        workOrderDao = AppDatabase.getInstance(application).workOrderDao();
    }

    public void loadDashboard(@NonNull String period) {
        if (!NetworkUtils.isNetworkAvailable(getApplication())) {
            loadFromLocalDb();
            return;
        }

        String role = authRepository.getSavedUserRole();
        if ("ADMIN".equals(role) || "MANAGER".equals(role)) {
            loadFullDashboard(period);
        } else {
            loadBasicDashboard();
        }
    }

    private void loadFromLocalDb() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        LiveData<List<WorkOrderEntity>> localData = workOrderDao.getAll();
        localData.observeForever(new Observer<List<WorkOrderEntity>>() {
            @Override
            public void onChanged(List<WorkOrderEntity> entities) {
                localData.removeObserver(this);
                isLoading.postValue(false);
                if (entities != null && !entities.isEmpty()) {
                    int open = 0;
                    int critical = 0;
                    for (WorkOrderEntity wo : entities) {
                        if (OPEN_STATUSES.contains(wo.getStatus())) {
                            open++;
                            if ("CRITICAL".equals(wo.getPriority())) {
                                critical++;
                            }
                        }
                    }
                    dashboardData.postValue(DashboardResponse.fromBasicStats(open, critical));
                } else {
                    dashboardData.postValue(DashboardResponse.fromBasicStats(0, 0));
                }
            }
        });
    }

    private void loadFullDashboard(@NonNull String period) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        ApiService api = ApiClient.getApiService(getApplication());

        api.getDashboard(period).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(@NonNull Call<DashboardResponse> call, @NonNull Response<DashboardResponse> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    dashboardData.postValue(response.body());
                } else {
                    Log.e(TAG, "loadDashboard failed with code: " + response.code());
                    errorMessage.postValue(NetworkUtils.getErrorMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<DashboardResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "loadDashboard network error", t);
                isLoading.postValue(false);
                loadFromLocalDb();
            }
        });
    }

    private void loadBasicDashboard() {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        ApiService api = ApiClient.getApiService(getApplication());

        api.getWorkOrders().enqueue(new Callback<List<WorkOrderResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<WorkOrderResponse>> call, @NonNull Response<List<WorkOrderResponse>> response) {
                isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<WorkOrderResponse> orders = response.body();
                    int open = 0;
                    int critical = 0;
                    for (WorkOrderResponse wo : orders) {
                        if (OPEN_STATUSES.contains(wo.getStatus())) {
                            open++;
                            if ("CRITICAL".equals(wo.getPriority())) {
                                critical++;
                            }
                        }
                    }
                    DashboardResponse data = DashboardResponse.fromBasicStats(open, critical);
                    dashboardData.postValue(data);
                } else {
                    Log.e(TAG, "loadBasicDashboard failed with code: " + response.code());
                    errorMessage.postValue(NetworkUtils.getErrorMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WorkOrderResponse>> call, @NonNull Throwable t) {
                Log.e(TAG, "loadBasicDashboard network error", t);
                isLoading.postValue(false);
                loadFromLocalDb();
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
