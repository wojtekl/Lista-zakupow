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
    
    public void setNazwa(final String n)
    {
        nazwa = n;
    }
    
    private String sklep;
    
    public String getSklep()
    {
        return sklep;
    }
    
    public void setSklep(final String s)
    {
        sklep = s;
    }
    
    private float cena;
    
    public float getCena()
    {
        return cena;
    }
    
    public void setCena(final float c)
    {
        cena = c;
    }
    
    public ModelProdukt(final String n, final String s, final float c)
    {
        nazwa = n;
        sklep = s;
        cena = c;
    }
    
    public String toJSON()
    {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[\"");
        stringBuilder.append(nazwa);
        stringBuilder.append("\",\"");
        stringBuilder.append(sklep);
        stringBuilder.append("\",\"");
        stringBuilder.append(cena);
        stringBuilder.append("\"]");
        return stringBuilder.toString();
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
