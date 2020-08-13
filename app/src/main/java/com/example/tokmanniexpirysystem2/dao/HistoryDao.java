package com.example.tokmanniexpirysystem2.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tokmanniexpirysystem2.entities.History;


import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM history")
    List<History> getAll();

    @Insert
    void insert(History history);

    @Delete
    void delete(History history);

    @Update
    void update(History history);
}
