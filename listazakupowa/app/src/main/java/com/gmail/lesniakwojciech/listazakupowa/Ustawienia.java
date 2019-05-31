package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.content.SharedPreferences;

public class Ustawienia {
    private static final String SHARED_PREFERENCES = "ListaZakupow";
    private static final String SP_LISTY = "listy";
    private static final String SP_PIERWSZE_URUCHOMIENIE = "pierwszeUruchomienie";
    private static final String SP_PIERWSZY_PRODUKT = "pierwszyProdukt";
    private static final String SP_PIERWSZE_WYSLANIE = "pierwszeWyslanie";
    private static final String SP_WCZYTANIE_OSTATNIE = "wczytanieOstatnie";
    private static final String SP_REKLAMA_NASTEPNA = "reklamaNastepna";
    private static final String SP_ADRES = "adres";

    private final SharedPreferences sharedPreferences;

    public Ustawienia(final Context context)
    {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, 0);
    }

    public String getListy(final String defValue)
    {
        return sharedPreferences.getString(SP_LISTY, defValue);
    }

    public void setListy(final String listy)
    {
        if (!sharedPreferences.getString(SP_LISTY, "[[],[],[]]").equals(listy)) {
            sharedPreferences.edit().putString(SP_LISTY, listy).apply();
        }
    }

    public boolean getPierwszeUruchomienie(final boolean defValue)
    {
        return sharedPreferences.getBoolean(SP_PIERWSZE_URUCHOMIENIE, defValue);
    }

    public void setPierwszeUruchomienie(final boolean pierwszeUruchomienie)
    {
        sharedPreferences.edit().putBoolean(SP_PIERWSZE_URUCHOMIENIE, pierwszeUruchomienie).apply();
    }

    public boolean getPierwszyProdukt(final boolean defValue)
    {
        return sharedPreferences.getBoolean(SP_PIERWSZY_PRODUKT, defValue);
    }

    public void setPierwszyProdukt(final boolean pierwszyProdukt)
    {
        sharedPreferences.edit().putBoolean(SP_PIERWSZY_PRODUKT, pierwszyProdukt).apply();
    }

    public boolean getPierwszeWyslanie(final boolean defValue)
    {
        return sharedPreferences.getBoolean(SP_PIERWSZE_WYSLANIE, defValue);
    }

    public void setPierwszeWyslanie(final boolean pierwszeWyslanie)
    {
        sharedPreferences.edit().putBoolean(SP_PIERWSZE_WYSLANIE, pierwszeWyslanie).apply();
    }

    public long getWczytanieOstatnie(final long defValue)
    {
        return sharedPreferences.getLong(SP_WCZYTANIE_OSTATNIE, defValue);
    }

    public void setWczytanieOstatnie(final long wczytanieOstatnie)
    {
        sharedPreferences.edit().putLong(SP_WCZYTANIE_OSTATNIE, wczytanieOstatnie).apply();
    }

    public long getReklamaNastepna(final long defValue)
    {
        return sharedPreferences.getLong(SP_REKLAMA_NASTEPNA, defValue);
    }

    public void setReklamaNastepna(final long reklamaNastepna)
    {
        sharedPreferences.edit().putLong(SP_REKLAMA_NASTEPNA, reklamaNastepna).apply();
    }

    public String getAdres(final String defValue)
    {
        return sharedPreferences.getString(SP_ADRES, defValue);
    }

    public void setAdres(final String adres)
    {
        sharedPreferences.edit().putString(SP_ADRES, adres).apply();
    }
}
