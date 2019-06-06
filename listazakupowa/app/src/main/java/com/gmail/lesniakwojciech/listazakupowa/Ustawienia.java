package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class Ustawienia {
    private static final String SHARED_PREFERENCES = "ListaZakupow";
    private static final String SP_LISTY = "listy";
    private static final String SP_PIERWSZE_URUCHOMIENIE = "pierwszeUruchomienie";
    private static final String SP_PIERWSZY_PRODUKT = "pierwszyProdukt";
    private static final String SP_PIERWSZE_WYSLANIE = "pierwszeWyslanie";
    private static final String SP_WCZYTANIE_OSTATNIE = "wczytanieOstatnie";
    // private static final String SP_REKLAMA_NASTEPNA = "reklamaNastepna";
    private static final String SP_ADRES = "adres";
    private static final String SP_ZETONY = "zetony";
    private static final String SP_IDENTYFIKATOR = "identyfikator";
    private static final String SP_UDOSTEPNIAJ_CENY = "udostepniajCeny";
    private static final String SP_TRYB_NOCNY = "trybNocny";

    private final SharedPreferences sharedPreferences;

    private final SharedPreferences ustawienia;

    public Ustawienia(final Context context)
    {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES, 0);

        ustawienia = PreferenceManager.getDefaultSharedPreferences(context);
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
        return ustawienia.getBoolean(SP_PIERWSZE_URUCHOMIENIE, defValue);
    }

    public void setPierwszeUruchomienie(final boolean pierwszeUruchomienie)
    {
        ustawienia.edit().putBoolean(SP_PIERWSZE_URUCHOMIENIE, pierwszeUruchomienie).apply();
    }

    public boolean getPierwszyProdukt(final boolean defValue)
    {
        return ustawienia.getBoolean(SP_PIERWSZY_PRODUKT, defValue);
    }

    public void setPierwszyProdukt(final boolean pierwszyProdukt)
    {
        ustawienia.edit().putBoolean(SP_PIERWSZY_PRODUKT, pierwszyProdukt).apply();
    }

    public boolean getPierwszeWyslanie(final boolean defValue)
    {
        return ustawienia.getBoolean(SP_PIERWSZE_WYSLANIE, defValue);
    }

    public void setPierwszeWyslanie(final boolean pierwszeWyslanie)
    {
        ustawienia.edit().putBoolean(SP_PIERWSZE_WYSLANIE, pierwszeWyslanie).apply();
    }

    public long getWczytanieOstatnie(final long defValue)
    {
        return ustawienia.getLong(SP_WCZYTANIE_OSTATNIE, defValue);
    }

    public void setWczytanieOstatnie(final long wczytanieOstatnie)
    {
        ustawienia.edit().putLong(SP_WCZYTANIE_OSTATNIE, wczytanieOstatnie).apply();
    }

    /* public long getReklamaNastepna(final long defValue)
    {
        return ustawienia.getLong(SP_REKLAMA_NASTEPNA, defValue);
    }

    public void setReklamaNastepna(final long reklamaNastepna)
    {
        ustawienia.edit().putLong(SP_REKLAMA_NASTEPNA, reklamaNastepna).apply();
    } */

    public String getAdres(final String defValue)
    {
        return ustawienia.getString(SP_ADRES, defValue);
    }

    public void setAdres(final String adres)
    {
        ustawienia.edit().putString(SP_ADRES, adres).apply();
    }

    public String getIdentyfikator(final String defValue)
    {
        return ustawienia.getString(SP_IDENTYFIKATOR, defValue);
    }

    public void setIdentyfikator(final String identyfikator)
    {
        ustawienia.edit().putString(SP_IDENTYFIKATOR, identyfikator).apply();
    }

    public int getZetony(final int defValue)
    {
        return Integer.parseInt(ustawienia.getString(SP_ZETONY, String.valueOf(defValue)));
    }

    public void setZetony(final int zetony)
    {
        ustawienia.edit().putString(SP_ZETONY, String.valueOf(zetony)).apply();
    }

    public boolean getUdostepniajCeny(final boolean defValue)
    {
        return ustawienia.getBoolean(SP_UDOSTEPNIAJ_CENY, defValue);
    }

    public void setUdostepniajCeny(final boolean udostepniajCeny)
    {
        ustawienia.edit().putBoolean(SP_UDOSTEPNIAJ_CENY, udostepniajCeny).apply();
    }

    public boolean getTrybNocny(final boolean defValue)
    {
        return ustawienia.getBoolean(SP_TRYB_NOCNY, defValue);
    }

    public void setTrybNocny(final boolean trybNocny)
    {
        ustawienia.edit().putBoolean(SP_TRYB_NOCNY, trybNocny).apply();
    }
}
