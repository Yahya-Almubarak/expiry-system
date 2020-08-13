package com.example.tokmanniexpirysystem2.repositories;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.tokmanniexpirysystem2.dao.UserDao;
import com.example.tokmanniexpirysystem2.database.AppDatabase;
import com.example.tokmanniexpirysystem2.database.DatabaseClient;
import com.example.tokmanniexpirysystem2.entities.User;

import java.util.List;

public class UserRepository {

    private UserDao userDao;
    private LiveData<List<User>> allData;

    public UserRepository(Application application) {

        AppDatabase db = DatabaseClient.getInstance(application).getAppDatabase();
        userDao = db.userDao();
        allData = userDao.getAll();

    }

    public LiveData<List<User>> getAllData() {
        return allData;
    }



    public void insertData(User user) {
        new UserInsertion(userDao).execute(user);
    }

    private static class UserInsertion extends AsyncTask<User, Void, Void> {

        private UserDao userDao;

        private UserInsertion(UserDao userDao) {

            this.userDao = userDao;

        }

        @Override
        protected Void doInBackground(User... user) {

            userDao.getAll();
            userDao.insert(user[0]);
            return null;

        }

    }





}
