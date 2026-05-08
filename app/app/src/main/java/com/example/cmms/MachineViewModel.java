package com.example.cmms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MachineViewModel extends ViewModel {
    private MutableLiveData<List<String>> machines;

    public LiveData<List<String>> getMachines() {
        if (machines == null) {
            machines = new MutableLiveData<>();
            loadMachines();
        }
        return machines;
    }

    public void refreshMachines() {
        loadMachines();
    }

    private void loadMachines() {
        List<String> machineList = new ArrayList<>();
        machineList.add("Tokarka CNC Haas ST-10");
        machineList.add("Frezarka 3-osiowa");
        machineList.add("Wiertarka kolumnowa");
        machineList.add("Wiertlo lol");

        machines.setValue(machineList);
    }
}