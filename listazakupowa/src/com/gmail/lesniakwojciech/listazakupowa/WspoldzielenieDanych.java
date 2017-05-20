package com.gmail.lesniakwojciech.listazakupowa;

import java.util.List;

public interface WspoldzielenieDanych
{
    
    public List<ModelProdukt> produkty();
    
    public void produkty(final List<ModelProdukt> p);
    
    public List<ModelProdukt> doKupienia();
    
    public void doKupienia(final List<ModelProdukt> d);
    
    public List<ModelProdukt> zabrane();
    
    public void zabrane(final List<ModelProdukt> z);

}
