package com.gmail.lesniakwojciech.listazakupowa;

import android.widget.ArrayAdapter;

public interface IWspoldzielenieDanych {
    AdapterListaZakupow getProdukty();

    void setProdukty(final AdapterListaZakupow produkty);

    AdapterListaZakupow getDoKupienia();

    void setDoKupienia(final AdapterListaZakupow doKupienia);

    AdapterListaZakupow getWKoszyku();

    void setWKoszyku(final AdapterListaZakupow wKoszyku);

    ArrayAdapter<String> getSklepy();

    void setSklepy(final ArrayAdapter<String> sklepy);
}
