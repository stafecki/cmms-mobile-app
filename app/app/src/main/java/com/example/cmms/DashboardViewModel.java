package com.example.cmms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {
    private final MutableLiveData<Integer> openJobsCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> criticalJobsCount = new MutableLiveData<>();

    public LiveData<Integer> getOpenJobsCount() {
        return openJobsCount;
    }

    public LiveData<Integer> getCriticalJobsCount() {
        return criticalJobsCount;
    }

    public void loadDashboardData() {
        openJobsCount.setValue(12);
        criticalJobsCount.setValue(3);
    }
}