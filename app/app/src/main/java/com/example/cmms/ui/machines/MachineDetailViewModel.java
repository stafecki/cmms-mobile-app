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

public class MachineDetailViewModel extends AndroidViewModel {

    private final MachineRepository machineRepository;
    private final AuthRepository authRepository;

    private final MediatorLiveData<MachineEntity> machine = new MediatorLiveData<>();
    private LiveData<MachineEntity> machineSource;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public MachineDetailViewModel(@NonNull Application application) {
        super(application);
        machineRepository = new MachineRepository(application);
        authRepository = new AuthRepository(application);
    }

    public void loadMachine(@NonNull String id) {
        isLoading.setValue(true);
        String token = authRepository.getToken();

        if (machineSource != null) {
            machine.removeSource(machineSource);
        }
        machineSource = machineRepository.getMachineById(id, token);
        machine.addSource(machineSource, data -> {
            machine.setValue(data);
            isLoading.setValue(false);
        });
    }

    @NonNull
    public LiveData<MachineEntity> getMachine() {
        return machine;
    }

    @NonNull
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
