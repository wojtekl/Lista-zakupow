package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;

public class WebAPI {
    public static String udostepnijCeny(final Context context, final String nazwa,
                                        final String sklep, final double cena) {
        return "http://listazakupow.ugu.pl"
                + "/dodaj.php"
                + "?nazwa=" + nazwa
                + "&sklep=" + sklep
                + "&cena=" + cena
                + "&identyfikator=" + new Ustawienia(context).getIdentyfikator("");
    }

    public static String pobierzCeny(final String nazwa) {
        return "http://listazakupow.ugu.pl?nazwa=" + nazwa;
    }

    public static String filtruj(final String odpowiedz) {
        return odpowiedz.substring(odpowiedz.indexOf("<p>") + 3, odpowiedz.indexOf("</p>"));
    }
}
