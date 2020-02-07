package com.example.findmyphone;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.example.findmyphone.DB_VM.AppDatabase;
import com.example.findmyphone.DB_VM.TrustedPerson;

public class CustomAsyncTask extends AsyncTask<Void, Void, Void> {

    AppDatabase database;
    Uri contact;
    String operation,name,number;
    TrustedPerson person;

    public CustomAsyncTask(Context context, Uri contact, String operation, String name, String number) {
        database = AppDatabase.getInstance(context);
        this.contact = contact;
        this.operation = operation;
        this.name = name;
        this.number = number;
    }

    public CustomAsyncTask(Context context, String operation, TrustedPerson person) {
        database = AppDatabase.getInstance(context);
        this.operation = operation;
        this.person = person;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        if(operation.equalsIgnoreCase("INSERT")){
            database.getDao().insertTrustNumber(new TrustedPerson(name, number, contact.toString()));
        }
        else if(operation.equalsIgnoreCase("DELETE")){
            database.getDao().deleteTrustedNumber(person);
        }


        return null;
    }
}
