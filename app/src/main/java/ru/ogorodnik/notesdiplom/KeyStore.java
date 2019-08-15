package ru.ogorodnik.notesdiplom;

public interface KeyStore {

    void saveKey(String pin);
    boolean checkKey(String pin);
}
