package com.gmail.lesniakwojciech.listazakupowa;

import java.util.List;

public interface IWspoldzielenieDanych
{
  public List<ModelProdukt> getProdukty();
  
  public void setProdukty(final List<ModelProdukt> produkty);
  
  public List<ModelProdukt> getDoKupienia();
  
  public void setDoKupienia(final List<ModelProdukt> doKupienia);
  
  public List<ModelProdukt> getWKoszyku();
  
  public void setWKoszyku(final List<ModelProdukt> wKoszyku);
}
