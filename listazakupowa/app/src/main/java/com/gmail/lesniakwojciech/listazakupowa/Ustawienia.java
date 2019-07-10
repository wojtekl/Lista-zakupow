package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class Ustawienia {
    private static final String SP_LISTY = "listy";
    private static final String SP_WCZYTANIE_OSTATNIE = "wczytanieOstatnie";
    public static final String SP_TRYB_NOCNY = "trybNocny";
    private static final String SP_IDENTYFIKATOR = "identyfikator";
    private static final String SP_ZETONY = "zetony";
    private static final String SP_UDOSTEPNIAJ_CENY = "udostepniajCeny";
    private static final String SP_ADRES_API = "adresAPI";

    private final SharedPreferences sharedPreferences;

    public Ustawienia(final Context context)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
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

    public long getWczytanieOstatnie(final long defValue)
    {
        return sharedPreferences.getLong(SP_WCZYTANIE_OSTATNIE, defValue);
    }

    public void setWczytanieOstatnie(final long wczytanieOstatnie)
    {
        sharedPreferences.edit().putLong(SP_WCZYTANIE_OSTATNIE, wczytanieOstatnie).apply();
    }

    public boolean getTrybNocny(final boolean defValue)
    {
        return sharedPreferences.getBoolean(SP_TRYB_NOCNY, defValue);
    }

    public void setTrybNocny(final boolean trybNocny)
    {
        sharedPreferences.edit().putBoolean(SP_TRYB_NOCNY, trybNocny).apply();
    }

    public String getIdentyfikator(final String defValue)
    {
        return sharedPreferences.getString(SP_IDENTYFIKATOR, defValue);
    }

    public void setIdentyfikator(final String identyfikator)
    {
        sharedPreferences.edit().putString(SP_IDENTYFIKATOR, identyfikator).apply();
    }

    public int getZetony(final int defValue)
    {
        return Integer.parseInt(sharedPreferences.getString(SP_ZETONY, String.valueOf(defValue)));
    }

    public void setZetony(final int zetony)
    {
        sharedPreferences.edit().putString(SP_ZETONY, String.valueOf(zetony)).apply();
    }

    public boolean getUdostepniajCeny(final boolean defValue)
    {
        return sharedPreferences.getBoolean(SP_UDOSTEPNIAJ_CENY, defValue);
    }

    public void setUdostepniajCeny(final boolean udostepniajCeny)
    {
        sharedPreferences.edit().putBoolean(SP_UDOSTEPNIAJ_CENY, udostepniajCeny).apply();
    }

    public String getAdresAPI(final String defValue)
    {
        return sharedPreferences.getString(SP_ADRES_API, defValue);
    }

    public void setAdresAPI(final String adresAPI)
    {
        sharedPreferences.edit().putString(SP_ADRES_API, adresAPI).apply();
    }
}
