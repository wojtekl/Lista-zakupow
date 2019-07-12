package com.gmail.lesniakwojciech.listazakupowa;

import java.util.Comparator;

public class ComparatorProduktPopularnosc implements Comparator<ModelProdukt> {
    @Override
    public int compare(final ModelProdukt p1, final ModelProdukt p2) {
        final int p = p2.getPopularnosc() - p1.getPopularnosc();
        if (0 == p) {
            return p1.getNazwa().compareTo(p2.getNazwa());
        }
        return p;
    }
}
