package com.gmail.lesniakwojciech.listazakupowa;

import java.util.Comparator;

public class ComparatorProduktSklep implements Comparator<ModelProdukt> {
    @Override
    public int compare(final ModelProdukt p1, final ModelProdukt p2) {
        final int p = p1.getSklep().compareTo(p2.getSklep());
        if(0 == p) {
            return p1.getNazwa().compareTo(p2.getNazwa());
        }
        return p;
    }
}
