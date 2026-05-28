package com.example.cmms.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SessionManager {

    private static final MutableLiveData<Boolean> sessionExpired = new MutableLiveData<>();

    public static void notifySessionExpired() {
        sessionExpired.postValue(true);
    }

    public static LiveData<Boolean> getSessionExpired() {
        return sessionExpired;
    }

    public static void reset() {
        sessionExpired.postValue(false);
    }

    private SessionManager() {}
}
