package com.gmail.lesniakwojciech.listazakupowa;

public class WebAPI {
    public static final String produkt = "/produkt";

    public static String udostepnijCeny(final String identyfikator, final String nazwa,
                                        final String sklep, final double cena) {
        return "nazwa=" + nazwa
                + "&sklep=" + sklep
                + "&cena=" + cena
                + "&identyfikator=" + identyfikator;
    }

    public static String pobierzCeny(final String adresAPI, final String nazwa, final String identyfikator) {
        return adresAPI
                + produkt
                + "?nazwa=" + nazwa
                + "&identyfikator=" + identyfikator;
    }

    public static String pobierzListe(final String adresAPI, final String identyfikator) {
        return adresAPI
                + "/produkty?identyfikator=" + identyfikator;
    }

    public static String filtruj(final String odpowiedz) {
        final int poczatek = odpowiedz.indexOf("<p>");
        if(0 < poczatek) {
            return odpowiedz.substring(poczatek + 3, odpowiedz.indexOf("</p>"));
        }
        else {
            return odpowiedz;
        }
    }
}
