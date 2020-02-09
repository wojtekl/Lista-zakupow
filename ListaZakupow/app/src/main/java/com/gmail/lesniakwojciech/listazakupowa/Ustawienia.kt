package com.gmail.lesniakwojciech.listazakupowa

import android.content.Context
import com.gmail.lesniakwojciech.commons.Ustawienia

class Ustawienia(context: Context) : Ustawienia(context), RepositoryListaZakupow.IDatabaseListaZakupow {
    private val PREFERENCJE_LISTY = context.getString(R.string.PREFERENCJE_LISTY)
    private val PREFERENCJE_WCZYTANIE_OSTATNIE =
        context.getString(R.string.PREFERENCJE_WCZYTANIE_OSTATNIE)
    private val PREFERENCJE_CENY_UDOSTEPNIAJ =
        context.getString(R.string.PREFERENCJE_CENY_UDOSTEPNIAJ)
    private val PREFERENCJE_API_ADRES = context.getString(R.string.PREFERENCJE_API_ADRES)
    val PREFERENCJE_POKAZ_INSTRUKCJE = context.getString(R.string.PREFERENCJE_POKAZ_INSTRUKCJE)
    val PREFERENCJE_WYCZYSC_WSZYSTKO = context.getString(R.string.PREFERENCJE_WYCZYSC_WSZYSTKO)
    val PREFERENCJE_PRODUKTY_SPOLECZNOSCI =
        context.getString(R.string.PREFERENCJE_PRODUKTY_SPOLECZNOSCI)

    fun getListy(defValue: String): String {
        return sharedPreferences.getString(PREFERENCJE_LISTY, defValue)!!
    }

    fun setListy(listy: String) {
        if (sharedPreferences.getString(PREFERENCJE_LISTY, "[[],[],[]]") != listy) {
            sharedPreferences.edit().putString(PREFERENCJE_LISTY, listy).apply()
        }
    }

    fun getWczytanieOstatnie(defValue: Long): Long {
        return sharedPreferences.getLong(PREFERENCJE_WCZYTANIE_OSTATNIE, defValue)
    }

    fun setWczytanieOstatnie(wczytanieOstatnie: Long) {
        sharedPreferences.edit().putLong(PREFERENCJE_WCZYTANIE_OSTATNIE, wczytanieOstatnie).apply()
    }

    fun getCenyUdostepniaj(defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(PREFERENCJE_CENY_UDOSTEPNIAJ, defValue)
    }

    fun setCenyUdostepniaj(cenyUdostepniaj: Boolean) {
        sharedPreferences.edit().putBoolean(PREFERENCJE_CENY_UDOSTEPNIAJ, cenyUdostepniaj).apply()
    }

    fun getAPIAdres(defValue: String): String {
        return sharedPreferences.getString(PREFERENCJE_API_ADRES, defValue)!!
    }

    fun setAPIAdres(APIAdres: String) {
        sharedPreferences.edit().putString(PREFERENCJE_API_ADRES, APIAdres).apply()
    }

    override fun getListaZakupow(json: String): String {
        return sharedPreferences.getString(PREFERENCJE_LISTY, json)!!
    }

    override fun setListaZakupow(json: String) {
        if (sharedPreferences.getString(PREFERENCJE_LISTY, "[[],[],[]]") != json) {
            sharedPreferences.edit().putString(PREFERENCJE_LISTY, json).apply()
        }
    }
}
