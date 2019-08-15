package com.example.test.notes;

public interface KeyStore {

    void saveKey(String pin);
    boolean checkKey(String pin);
}
