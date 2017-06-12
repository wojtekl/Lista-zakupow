package com.gmail.lesniakwojciech.listazakupowa;

import org.json.JSONArray;
import org.json.JSONException;

public class ModelProdukt
{
  public ModelProdukt()
  {
  }
  
  private String nazwa;
  
  public String getNazwa()
  {
    return nazwa;
  }
  
  public void setNazwa(final String nazwa)
  {
    this.nazwa = nazwa;
  }
  
  private String sklep;
  
  public String getSklep()
  {
    return sklep;
  }
  
  public void setSklep(final String sklep)
  {
    this.sklep = sklep;
  }
  
  private float cena;
  
  public float getCena()
  {
    return cena;
  }
  
  public void setCena(final float cena)
  {
    this.cena = cena;
  }
  
  public ModelProdukt(final String nazwa, final String sklep, final float cena)
  {
    this.nazwa = nazwa;
    this.sklep = sklep;
    this.cena = cena;
  }
  
  public String toJSON()
  {
    return new StringBuilder()
      .append("[")
      .append("\"").append(nazwa).append("\"")
      .append(",").append("\"").append(sklep).append("\"")
      .append(",").append(cena)
      .append("]")
      .toString();
  }
  
  public static ModelProdukt fromJSON(final String p)
  {
    final ModelProdukt produkt = new ModelProdukt();
    try
    {
      final JSONArray jsonArray = new JSONArray(p);
      produkt.nazwa = jsonArray.getString(0);
      produkt.sklep = jsonArray.getString(1);
      produkt.cena = (float)jsonArray.getDouble(2);
    }
    catch(final JSONException exception)
    {
    }
    return produkt;
  }
}
