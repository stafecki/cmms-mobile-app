package com.example.cmms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Random;

public class DashboardViewModel extends ViewModel {
    private final MutableLiveData<Integer> openJobsCount = new MutableLiveData<>();
    private final MutableLiveData<Integer> criticalJobsCount = new MutableLiveData<>();

    private final MutableLiveData<Integer> upcomingMaintenanceCount = new MutableLiveData<>();

    public LiveData<Integer> getOpenJobsCount() {
        return openJobsCount;
    }

    public LiveData<Integer> getCriticalJobsCount() {
        return criticalJobsCount;
    }

    public LiveData<Integer> getUpcomingMaintenanceCount() { return upcomingMaintenanceCount; }

    public void loadDashboardData() {
        Random random = new Random();
        openJobsCount.setValue(random.nextInt(15));
        criticalJobsCount.setValue(random.nextInt(12));
        upcomingMaintenanceCount.setValue(random.nextInt(25));
    }
}