package com.example.tokmanniexpirysystem2.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.tokmanniexpirysystem2.entities.User;
import com.example.tokmanniexpirysystem2.repositories.UserRepository;

import java.util.List;

public class LoginViewModel extends AndroidViewModel {

    private UserRepository repository;
    private LiveData<List<User>> getAllData;

    public LoginViewModel(@NonNull Application application) {
        super(application);

        repository = new UserRepository(application);
        getAllData = repository.getAllData();

    }

    public void insert(User user) {
        repository.insertData(user);
    }

    public LiveData<List<User>> getGetAllData() {
        return getAllData;
    }

}
