package com.gmail.lesniakwojciech.listazakupowa;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

public class AWProviderListaZakupow 
  extends AppWidgetProvider
{
  @Override
  public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, 
    final int []appWidgetIds)
  {
    final List<ModelProdukt> doKupienia = new ArrayList<ModelProdukt>();
    try
    {
      final JSONArray jsonArray = new JSONArray(context
        .getSharedPreferences(ActivityMain.SHARED_PREFERENCES, 0)
        .getString(ActivityMain.SP_LISTY, "[[],[],[]]")
      ).getJSONArray(1);
      for(int i = 0, d = jsonArray.length(); i < d; ++i)
      {
        doKupienia.add(ModelProdukt.fromJSON(jsonArray.getString(i)));
      }
    }
    catch(final JSONException exception)
    {
    }
    String string = "";
    int l = doKupienia.size();
    if(0 < l)
    {
      final StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(context.getResources().getString(R.string.doKupienia)).append(":\n");
      --l;
      for(int i = 0; i < l; ++i)
      {
        stringBuilder.append(" > ").append(doKupienia.get(i).getNazwa()).append(",\n");
      }
      stringBuilder.append(" > ").append(doKupienia.get(l).getNazwa()).append(".");
      string = stringBuilder.toString();
    }
    
    for(int i = 0, d = appWidgetIds.length; i < d; ++i)
    {
      final RemoteViews views = new RemoteViews(context.getPackageName(), 
        R.layout.awproviderlistazakupow);
      views.setTextViewText(R.id.awplzTextView, string);
      views.setOnClickPendingIntent(R.id.awplzTextView, PendingIntent.getActivity(
        context, 
        0, 
        new Intent(context, ActivityMain.class), 
        0
      )
      );
      appWidgetManager.updateAppWidget(appWidgetIds[i], views);
    }
  }
}
