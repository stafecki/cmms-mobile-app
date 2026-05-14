package com.example.cmms.ui.login;

import android.app.Application;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.cmms.data.repository.AuthRepository;

public class LoginViewModel extends AndroidViewModel {

    private static final String TAG = "LoginViewModel";

    private final AuthRepository authRepository;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>(false);

    public LoginViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public void login(@NonNull String email, @NonNull String password) {
        if (email.trim().isEmpty()) {
            errorMessage.setValue("Email is required");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            errorMessage.setValue("Invalid email format");
            return;
        }
        if (password.trim().isEmpty()) {
            errorMessage.setValue("Password is required");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        authRepository.login(email.trim(), password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess() {
                isLoading.postValue(false);
                loginSuccess.postValue(true);
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Login error: " + message);
                isLoading.postValue(false);
                errorMessage.postValue(message);
            }
        });
    }

    @NonNull
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    @NonNull
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    @NonNull
    public MutableLiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }
}
