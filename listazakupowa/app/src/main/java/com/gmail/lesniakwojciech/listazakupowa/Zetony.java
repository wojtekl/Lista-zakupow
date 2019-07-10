package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class Zetony {
    private static final int ZETONY_PREMIA = 9;
    public static final int ZETONY_CENA_UDOSTEPNIENIE = 1;
    public static final int ZETONY_CENA_ZOBACZ = 1;
    public static final int ZETONY_BANNER = 2;
    public static final int ZETON_BANNER_WYLACZ = 50;
    public static final int ZETONY_REWARDEDVIDEOAD = 3;
    public static final int ZETONY_WYSLIJLISTE = 2;
    public static final int ZETONY_PRODUKTY_SPOLECZNOSCI = 3;

    private final Context context;

    public Zetony(final Context context)
    {
        this.context = context;
    }

    public boolean sprawdzZetony(final int ile, final boolean pobierz, final View view) {
        final Ustawienia ustawienia = new Ustawienia(context);
        final int stan = ustawienia.getZetony(ZETONY_PREMIA);
        if(stan < ile) {
            if(null != view) {
                Snackbar.make(view, R.string.brakZetonow, Snackbar.LENGTH_LONG).show();
            }
            return false;
        }
        if(pobierz) {
            ustawienia.setZetony(stan - ile);
        }
        return true;
    }

    public int dodajZetony(final int ile, final View view) {
        final Ustawienia ustawienia = new Ustawienia(context);
        ustawienia.setZetony(ustawienia.getZetony(ZETONY_PREMIA) + ile);
        if(null != view) {
            Snackbar.make(view, context.getString(R.string.przyznanoZetony) + ile, Snackbar.LENGTH_LONG).show();
        }
        return ustawienia.getZetony(0);
    }
}
