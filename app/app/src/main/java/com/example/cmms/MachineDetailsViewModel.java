package com.example.cmms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class MachineDetailsViewModel extends ViewModel {

    private final MutableLiveData<String> machineName = new MutableLiveData<>();
    private final MutableLiveData<String> machineSerial = new MutableLiveData<>();
    private final MutableLiveData<String> machineLocation = new MutableLiveData<>();
    private final MutableLiveData<String> machineHours = new MutableLiveData<>();
    private final MutableLiveData<List<String>> recentJobs = new MutableLiveData<>();

    public LiveData<String> getMachineName() { return machineName; }
    public LiveData<String> getMachineSerial() { return machineSerial; }
    public LiveData<String> getMachineLocation() { return machineLocation; }
    public LiveData<String> getMachineHours() { return machineHours; }
    public LiveData<List<String>> getRecentJobs() { return recentJobs; }

    public void loadMachineDetails() {
        machineName.setValue("Tokarka CNC Haas ST-10");
        machineSerial.setValue("SN: 123456789");
        machineLocation.setValue("Hala A, Sektor 3");
        machineHours.setValue("1250 h");

        List<String> jobs = new ArrayList<>();
        jobs.add("Wymiana chłodziwa - 12.10.2023");
        jobs.add("Kalibracja wrzeciona - 05.09.2023");
        recentJobs.setValue(jobs);
    }
}