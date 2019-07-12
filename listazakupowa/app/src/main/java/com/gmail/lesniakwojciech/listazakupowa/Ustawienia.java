package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class Ustawienia {
    public static final String SP_SKORKA_CIEMNA = "skorkaCiemna";
    private static final String SP_LISTY = "listy";
    private static final String SP_WCZYTANIE_OSTATNIE = "wczytanieOstatnie";
    private static final String SP_IDENTYFIKATOR = "identyfikator";
    private static final String SP_ZETONY = "zetony";
    private static final String SP_CENY_UDOSTEPNIAJ = "cenyUdostepniaj";
    private static final String SP_API_ADRES = "APIAdres";

    private final SharedPreferences sharedPreferences;

    public Ustawienia(final Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getListy(final String defValue) {
        return sharedPreferences.getString(SP_LISTY, defValue);
    }

    public void setListy(final String listy) {
        if (!sharedPreferences.getString(SP_LISTY, "[[],[],[]]").equals(listy)) {
            sharedPreferences.edit().putString(SP_LISTY, listy).apply();
        }
    }

    public long getWczytanieOstatnie(final long defValue) {
        return sharedPreferences.getLong(SP_WCZYTANIE_OSTATNIE, defValue);
    }

    public void setWczytanieOstatnie(final long wczytanieOstatnie) {
        sharedPreferences.edit().putLong(SP_WCZYTANIE_OSTATNIE, wczytanieOstatnie).apply();
    }

    public boolean getSkorkaCiemna(final boolean defValue) {
        return sharedPreferences.getBoolean(SP_SKORKA_CIEMNA, defValue);
    }

    public void setSkorkaCiemna(final boolean skorkaCiemna) {
        sharedPreferences.edit().putBoolean(SP_SKORKA_CIEMNA, skorkaCiemna).apply();
    }

    public String getIdentyfikator(final String defValue) {
        return sharedPreferences.getString(SP_IDENTYFIKATOR, defValue);
    }

    public void setIdentyfikator(final String identyfikator) {
        sharedPreferences.edit().putString(SP_IDENTYFIKATOR, identyfikator).apply();
    }

    public int getZetony(final int defValue) {
        return Integer.parseInt(sharedPreferences.getString(SP_ZETONY, String.valueOf(defValue)));
    }

    public void setZetony(final int zetony) {
        sharedPreferences.edit().putString(SP_ZETONY, String.valueOf(zetony)).apply();
    }

    public boolean getCenyUdostepniaj(final boolean defValue) {
        return sharedPreferences.getBoolean(SP_CENY_UDOSTEPNIAJ, defValue);
    }

    public void setCenyUdostepniaj(final boolean cenyUdostepniaj) {
        sharedPreferences.edit().putBoolean(SP_CENY_UDOSTEPNIAJ, cenyUdostepniaj).apply();
    }

    public String getAPIAdres(final String defValue) {
        return sharedPreferences.getString(SP_API_ADRES, defValue);
    }

    public void setAPIAdres(final String APIAdres) {
        sharedPreferences.edit().putString(SP_API_ADRES, APIAdres).apply();
    }
}
