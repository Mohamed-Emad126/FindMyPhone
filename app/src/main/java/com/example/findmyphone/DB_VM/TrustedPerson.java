package com.example.findmyphone.DB_VM;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class TrustedPerson {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String number;
    private String uri;

    public TrustedPerson(int id, String name, String number, String uri) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.uri = uri;
    }

    @Ignore
    public TrustedPerson(String name, String number, String uri) {
        this.name = name;
        this.number = number;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
