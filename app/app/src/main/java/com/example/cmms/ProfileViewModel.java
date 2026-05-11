package com.example.cmms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<User> user;

    public LiveData<User> getUser(){
        if(user == null){
            user = new MutableLiveData<>();
            loadUser();
        }
        return user;
    }

    private void loadUser() {
        List<User.Certificate> certificates = new ArrayList<>();
        certificates.add(new User.Certificate("Spawanie MIG", "2027-01-15"));
        certificates.add(new User.Certificate("BHP", "2026-12-31"));

        User user = new User("Admin", "Adminowski", "admin@cmms.pl", "admin", certificates);
        this.user.setValue(user);
    }

}
