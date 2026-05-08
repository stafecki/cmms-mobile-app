package com.example.cmms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WorkOrderDetailsViewModel extends ViewModel {
    private final MutableLiveData<String> title = new MutableLiveData<>();
    private final MutableLiveData<String> description = new MutableLiveData<>();
    private final MutableLiveData<String> priority = new MutableLiveData<>();
    private final MutableLiveData<String> status = new MutableLiveData<>();
    private final MutableLiveData<String> technicianName = new MutableLiveData<>();
    private final MutableLiveData<String> machine = new MutableLiveData<>();
    private final MutableLiveData<String> parts = new MutableLiveData<>();


    public LiveData<String> getTitle() { return title; }
    public LiveData<String> getDescription() { return description; }
    public LiveData<String> getPriority() { return priority; }
    public LiveData<String> getStatus() { return status; }
    public LiveData<String> getTechnicianName() { return technicianName; }
    public LiveData<String> getMachine() { return machine; }
    public LiveData<String> getParts() { return parts; }
    public void loadWorkOrderDetails() {
        title.setValue("Zamówienie 1");
        description.setValue("Opis zamówienia 1");
        priority.setValue("HIGH");
        status.setValue("IN_PROGRESS");
        technicianName.setValue("Jan Kowalski");
        machine.setValue("Maszyna A");
        parts.setValue("Części A, B");
    }

}
