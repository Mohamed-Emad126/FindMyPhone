package com.example.findmyphone.DB_VM;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TrustedDao {

    @Query("SELECT * FROM TrustedPerson")
    LiveData<List<TrustedPerson>> getAllTrustedNumbers();

    @Query("SELECT * FROM TrustedPerson WHERE number =:num")
    List<TrustedPerson> isTrusted(String num);

    @Insert
    void insertTrustNumber(TrustedPerson person);

    @Delete
    void deleteTrustedNumber(TrustedPerson person);
}
