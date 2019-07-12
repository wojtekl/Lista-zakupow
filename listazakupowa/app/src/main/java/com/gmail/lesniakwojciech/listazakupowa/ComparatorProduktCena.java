package com.gmail.lesniakwojciech.listazakupowa;

import java.util.Comparator;

public class ComparatorProduktCena implements Comparator<ModelProdukt> {
    @Override
    public int compare(final ModelProdukt p1, final ModelProdukt p2) {
        final double p = p2.getCena() - p1.getCena();
        if (0 == p) {
            return p1.getNazwa().compareTo(p2.getNazwa());
        }
        return 0 < p ? 1 : -1;
    }
}
