package com.gmail.lesniakwojciech.listazakupowa;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class ParserProdukt {
    public static void parse(final String stringLista, List<ModelProdukt> produkty,
                             List<ModelProdukt> doKupienia, List<ModelProdukt> wKoszyku) {
        try {
            JSONArray jsonArray;
            final JSONArray jsonLista = new JSONArray(stringLista);

            jsonArray = jsonLista.getJSONArray(0);
            for (int i = 0, d = jsonArray.length(); i < d; ++i) {
                produkty.add(ModelProdukt.fromJSON(jsonArray.getString(i)));
            }

            jsonArray = jsonLista.getJSONArray(1);
            for (int i = 0, d = jsonArray.length(); i < d; ++i) {
                doKupienia.add(ModelProdukt.fromJSON(jsonArray.getString(i)));
            }

            jsonArray = jsonLista.getJSONArray(2);
            for (int i = 0, d = jsonArray.length(); i < d; ++i) {
                wKoszyku.add(ModelProdukt.fromJSON(jsonArray.getString(i)));
            }
        } catch (final JSONException exception) {
        }
    }

    public static String toString(final List<ModelProdukt> produkty,
                                  final List<ModelProdukt> doKupienia, final List<ModelProdukt> wKoszyku) {
        final StringBuilder stringBuilder = new StringBuilder();
        int d;

        stringBuilder.append("[");

        stringBuilder.append("[");
        d = produkty.size();
        if (0 < d) {
            stringBuilder.append(produkty.get(0).toJSON());
            for (int i = 1; i < d; ++i) {
                stringBuilder.append(",").append(produkty.get(i).toJSON());
            }
        }
        stringBuilder.append("]");

        stringBuilder.append(",");

        stringBuilder.append("[");
        d = doKupienia.size();
        if (0 < d) {
            stringBuilder.append(doKupienia.get(0).toJSON());
            for (int i = 1; i < d; ++i) {
                stringBuilder.append(",").append(doKupienia.get(i).toJSON());
            }
        }
        stringBuilder.append("]");

        stringBuilder.append(",");

        stringBuilder.append("[");
        d = wKoszyku.size();
        if (0 < d) {
            stringBuilder.append(wKoszyku.get(0).toJSON());
            for (int i = 1; i < d; ++i) {
                stringBuilder.append(",").append(wKoszyku.get(i).toJSON());
            }
        }
        stringBuilder.append("]");

        stringBuilder.append("]");

        return stringBuilder.toString();
    }

    public static String listToJSON(final List<ModelProdukt> list)
    {
        final StringBuilder stringBuilder = new StringBuilder().append("[");

        int l = list.size();
        if (0 < l) {
            --l;
            for (int i = 0; i < l; ++i) {
                stringBuilder.append(list.get(i).toJSON()).append(",");
            }
            stringBuilder.append(list.get(l).toJSON());
        }

        return stringBuilder.append("]").toString();
    }

    public static void merge(final String lista, List<ModelProdukt> wKoszyku,
                            List<ModelProdukt> doKupienia, List<ModelProdukt> produkty)
    {
        try {
            final JSONArray jsonArray = new JSONArray(lista);
            for (int i = 0, d = jsonArray.length(); i < d; ++i) {
                final ModelProdukt produkt = ModelProdukt.fromJSON(jsonArray.getString(i));
                if(wKoszyku.contains(produkt) || doKupienia.contains(produkt)){
                    continue;
                }
                boolean istnieje = false;
                for(int j = 0, e = produkty.size(); j < e; ++j)
                {
                    final ModelProdukt model = produkty.get(j);
                    if(produkt.equals(model)){
                        doKupienia.add(model);
                        produkty.remove(j);
                        istnieje = true;
                        break;
                    }
                }
                if(!istnieje){
                    doKupienia.add(produkt);
                }
            }
        }
        catch(final JSONException exception){
        }
    }
}
