package com.example.findmyphone.DB_VM;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 1,entities = {TrustedPerson.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase ourInstance;

    public static AppDatabase getInstance(Context context) {
        if(ourInstance == null){
            synchronized (new Object()){
                ourInstance = Room.databaseBuilder(context,
                        AppDatabase.class,
                        "TRUSTED_NUMBERS"
                        ).build();
            }
        }
        return ourInstance;
    }

    public abstract TrustedDao getDao();

}
