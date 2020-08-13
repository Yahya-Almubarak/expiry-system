package com.example.tokmanniexpirysystem2.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.tokmanniexpirysystem2.dao.HistoryDao;
import com.example.tokmanniexpirysystem2.dao.ProductDao;
import com.example.tokmanniexpirysystem2.dao.UserDao;
import com.example.tokmanniexpirysystem2.entities.History;
import com.example.tokmanniexpirysystem2.entities.Product;
import com.example.tokmanniexpirysystem2.entities.User;

@Database(entities = {Product.class, User.class, History.class}, version = 2, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
    public abstract UserDao userDao();
    public abstract HistoryDao historyDao();
}
