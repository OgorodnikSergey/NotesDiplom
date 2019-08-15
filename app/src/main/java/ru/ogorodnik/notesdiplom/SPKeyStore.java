package ru.ogorodnik.notesdiplom;

import android.content.Context;
import android.content.SharedPreferences;

public class SPKeyStore implements KeyStore{

    Context context;

    SPKeyStore(Context context){
        this.context = context;
    }

    public void saveKey(String pin){
        int hash = pin.hashCode();
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.password), Context.MODE_PRIVATE);
        sp.edit().putString(context.getString(R.string.PIN), String.valueOf(hash)).commit();
    }

    private String getKey(){
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.password), Context.MODE_PRIVATE);
        return sp.getString(context.getString(R.string.PIN), "");
    }

    public boolean checkKey(String pin){
        if (String.valueOf(pin.hashCode()).equals(getKey()))
            return true;
        else
            return false;
    }
}
