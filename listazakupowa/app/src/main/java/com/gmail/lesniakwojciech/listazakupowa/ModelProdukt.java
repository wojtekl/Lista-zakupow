package com.gmail.lesniakwojciech.listazakupowa;

import org.json.JSONArray;
import org.json.JSONException;

public class ModelProdukt {
    private String nazwa;
    private String sklep;
    private double cena;
    private int popularnosc;

    public ModelProdukt() {
    }

    public ModelProdukt(final String nazwa, final String sklep, final double cena) {
        this.nazwa = nazwa;
        this.sklep = sklep;
        this.cena = cena;
        this.popularnosc = 0;
    }

    public static ModelProdukt fromJSON(final String p) throws JSONException {
        final ModelProdukt produkt = new ModelProdukt();
        final JSONArray jsonArray = new JSONArray(p);
        produkt.nazwa = jsonArray.getString(0);
        produkt.sklep = jsonArray.getString(1);
        produkt.cena = jsonArray.getDouble(2);
        produkt.popularnosc = jsonArray.optInt(3, 0);
        return produkt;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(final String nazwa) {
        this.nazwa = nazwa;
    }

    public String getSklep() {
        return sklep;
    }

    public void setSklep(final String sklep) {
        this.sklep = sklep;
    }

    public double getCena() {
        return cena;
    }

    public void setCena(final double cena) {
        this.cena = cena;
    }

    public int getPopularnosc() {
        return popularnosc;
    }

    public void setPopularnosc(final int popularnosc) {
        this.popularnosc = popularnosc;
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof ModelProdukt
                && nazwa.equals(((ModelProdukt) object).getNazwa());
    }

    public String toJSON() {
        return new StringBuilder()
                .append("[")
                .append("\"").append(nazwa).append("\"")
                .append(",").append("\"").append(sklep).append("\"")
                .append(",").append(cena)
                .append(",").append(popularnosc)
                .append("]")
                .toString();
    }

    public void podbijPopularnosc() {
        ++this.popularnosc;
    }
}
