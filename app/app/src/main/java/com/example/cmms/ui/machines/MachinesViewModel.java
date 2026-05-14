package com.example.cmms.ui.machines;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cmms.data.local.entities.MachineEntity;
import com.example.cmms.data.repository.AuthRepository;
import com.example.cmms.data.repository.MachineRepository;

import java.util.List;

public class MachinesViewModel extends AndroidViewModel {

    private final MachineRepository machineRepository;
    private final AuthRepository authRepository;

    private final MediatorLiveData<List<MachineEntity>> machines = new MediatorLiveData<>();
    private LiveData<List<MachineEntity>> machinesSource;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public MachinesViewModel(@NonNull Application application) {
        super(application);
        machineRepository = new MachineRepository(application);
        authRepository = new AuthRepository(application);
    }

    public void loadMachines() {
        isLoading.setValue(true);
        errorMessage.setValue(null);
        String token = authRepository.getToken();
        setMachinesSource(machineRepository.getMachines(token));
    }

    public void search(@NonNull String query) {
        setMachinesSource(machineRepository.searchMachines(query));
    }

    private void setMachinesSource(@NonNull LiveData<List<MachineEntity>> newSource) {
        if (machinesSource != null) {
            machines.removeSource(machinesSource);
        }
        machinesSource = newSource;
        machines.addSource(machinesSource, data -> {
            machines.setValue(data);
            isLoading.setValue(false);
        });
    }

    @NonNull
    public LiveData<List<MachineEntity>> getMachines() {
        return machines;
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
