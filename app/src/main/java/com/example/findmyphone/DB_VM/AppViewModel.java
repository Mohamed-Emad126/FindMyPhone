package com.example.findmyphone.DB_VM;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class AppViewModel extends AndroidViewModel {

    LiveData<List<TrustedPerson>> allTrusted;

    public AppViewModel(@NonNull Application application) {
        super(application);

        allTrusted = AppDatabase.getInstance(this.getApplication()).getDao().getAllTrustedNumbers();

    }

    public LiveData<List<TrustedPerson>> getAllTrusted() {
        return allTrusted;
    }
}
