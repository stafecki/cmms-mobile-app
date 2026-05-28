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

    private final AuthRepository authRepository;
    private final MachineRepository machineRepository;

    private final MediatorLiveData<MachineEntity> machine = new MediatorLiveData<>();
    private LiveData<MachineEntity> machineSource;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hoursUpdateSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public MachineDetailViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        machineRepository = new MachineRepository(application);
    }

    public void loadMachine(@NonNull String id) {
        isLoading.setValue(true);
        String token = authRepository.getToken();

        if (machineSource != null) {
            machine.removeSource(machineSource);
        }
        machineSource = machineRepository.getMachineById(id, token, message -> {
            isLoading.postValue(false);
            errorMessage.postValue(message);
        });
        machine.addSource(machineSource, entity -> {
            if (entity != null) {
                machine.setValue(entity);
                isLoading.setValue(false);
            }
        });
    }

    public void updateOperatingHours(@NonNull String id, double hours) {
        isLoading.setValue(true);
        String token = authRepository.getToken();
        machineRepository.updateOperatingHours(token, id, hours, new MachineRepository.CreateCallback() {
            @Override
            public void onSuccess() {
                isLoading.postValue(false);
                hoursUpdateSuccess.postValue(true);
                loadMachine(id);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    public void deleteMachine(@NonNull String id) {
        isLoading.setValue(true);
        String token = authRepository.getToken();
        machineRepository.deleteMachine(token, id, new MachineRepository.CreateCallback() {
            @Override
            public void onSuccess() {
                isLoading.postValue(false);
                deleteSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
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

    @NonNull
    public MutableLiveData<Boolean> getDeleteSuccess() {
        return deleteSuccess;
    }

    @NonNull
    public MutableLiveData<Boolean> getHoursUpdateSuccess() {
        return hoursUpdateSuccess;
    }

    @NonNull
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
