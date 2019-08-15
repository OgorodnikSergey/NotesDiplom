package com.example.test.notes;

import android.app.Application;

public class App extends Application {

    private static NoteRepository noteRepository;
    private static KeyStore keyStore;

    @Override
    public void onCreate(){

        super.onCreate();
        noteRepository = new SQLLiteNoteRepository(this);
        keyStore = new SPKeyStore(this);
    }

    public static NoteRepository getNoteRepository(){
        return noteRepository;
    }

    public static KeyStore getKeyStore(){
        return keyStore;
    }
}
