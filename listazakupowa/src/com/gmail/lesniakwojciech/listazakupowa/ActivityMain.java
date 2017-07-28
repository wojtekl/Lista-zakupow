package com.gmail.lesniakwojciech.listazakupowa;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

public class ActivityMain 
  extends FragmentActivity 
  implements IWspoldzielenieDanych, DialogFragmentProdukt.DialogListener
{
  private AdView mAdView;
  private List<ModelProdukt> wKoszyku, doKupienia, produkty;
  
  @Override
  protected void onCreate(final Bundle bundle)
  {
    super.onCreate(bundle);
    setContentView(R.layout.activitymain);
    final Resources resources = getResources();
    
    MobileAds.initialize(getApplicationContext(), resources.getString(R.string.ad_app_id));
    mAdView = (AdView) findViewById(R.id.adView);
    mAdView.setAdListener(new AdListener()
    {
      @Override
      public void onAdLoaded()
      {
        super.onAdLoaded();
        mAdView.setVisibility(View.VISIBLE);
      }
    }
    );
    mAdView.loadAd(new AdRequest.Builder().build());
    
    final SharedPreferences settings = getSharedPreferences("LISTA-ZAKUPOW", 0);
    String stringLista = settings.getString("LISTA", "[[],[],[]]");
    if(settings.getBoolean("PIERWSZE-URUCHOMIENIE", true))
    {
      settings.edit().putBoolean("PIERWSZE-URUCHOMIENIE", false).commit();
      if(stringLista.equals("[[],[],[]]"))
      {
        stringLista = resources.getString(R.string.pierwszaLista);
      }
      final String string = resources.getString(R.string.pierwszeUruchomienie);
      ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE))
        .notify(0, new NotificationCompat.Builder(this)
          .setSmallIcon(R.drawable.ic_launcher)
          .setContentTitle(resources.getString(R.string.app_name))
          .setContentText(string)
          .setAutoCancel(true)
          .setContentIntent(PendingIntent.getActivity(
            this, 
            0, 
            new Intent(this, ActivityInstrukcje.class).putExtra("INSTRUKCJE", string), 
            PendingIntent.FLAG_UPDATE_CURRENT
          )
          )
          .build()
        );
    }
    produkty = new ArrayList<ModelProdukt>();
    doKupienia = new ArrayList<ModelProdukt>();
    wKoszyku = new ArrayList<ModelProdukt>();
    try
    {
      JSONArray jsonArray;
      final JSONArray jsonLista = new JSONArray(stringLista);
      
      jsonArray = jsonLista.getJSONArray(0);
      for(int i = 0, d = jsonArray.length(); i < d; ++i)
      {
        produkty.add(ModelProdukt.fromJSON(jsonArray.getString(i)));
      }
      
      jsonArray = jsonLista.getJSONArray(1);
      for(int i = 0, d = jsonArray.length(); i < d; ++i)
      {
        doKupienia.add(ModelProdukt.fromJSON(jsonArray.getString(i)));
      }
      
      jsonArray = jsonLista.getJSONArray(2);
      for(int i = 0, d = jsonArray.length(); i < d; ++i)
      {
        wKoszyku.add(ModelProdukt.fromJSON(jsonArray.getString(i)));
      }
    }
    catch(final JSONException exception)
    {
    }
    
    final ViewPager viewPager = (ViewPager)findViewById(R.id.alzViewPager);
    viewPager.setAdapter(new FPagerAdapterMain(getSupportFragmentManager(), this));
    viewPager.setCurrentItem(1);
    
    if(PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, 
      Manifest.permission.SEND_SMS))
    {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
    }
  }
  
  @Override
  protected void onResume()
  {
    super.onResume();
    
    mAdView.resume();
  }
  
  @Override
  protected void onPause()
  {
    mAdView.pause();
    
    super.onPause();
  }
  
  @Override
  protected void onStop()
  {
    final StringBuilder stringBuilder = new StringBuilder();
    int d;
    
    stringBuilder.append("[");
    
    stringBuilder.append("[");
    d = produkty.size();
    if(0 < d)
    {
      stringBuilder.append(produkty.get(0).toJSON());
      for(int i = 1; i < d; ++i)
      {
        stringBuilder.append(",").append(produkty.get(i).toJSON());
      }
    }
    stringBuilder.append("]");
    
    stringBuilder.append(",");
    
    stringBuilder.append("[");
    d = doKupienia.size();
    if(0 < d)
    {
      stringBuilder.append(doKupienia.get(0).toJSON());
      for(int i = 1; i < d; ++i)
      {
        stringBuilder.append(",").append(doKupienia.get(i).toJSON());
      }
    }
    stringBuilder.append("]");
    
    stringBuilder.append(",");
    
    stringBuilder.append("[");
    d = wKoszyku.size();
    if(0 < d)
    {
      stringBuilder.append(wKoszyku.get(0).toJSON());
      for(int i = 1; i < d; ++i)
      {
        stringBuilder.append(",").append(wKoszyku.get(i).toJSON());
      }
    }
    stringBuilder.append("]");
    
    stringBuilder.append("]");
    
    final String stringLista = stringBuilder.toString();
    
    final SharedPreferences settings = getSharedPreferences("LISTA-ZAKUPOW", 0);
    if(!settings.getString("LISTA", "[[],[],[]]").equals(stringLista))
    {
      settings.edit().putString("LISTA", stringLista).commit();
    }
    
    final Intent intent = new Intent(this, AWProviderListaZakupow.class);
    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager
      .getInstance(this)
      .getAppWidgetIds(new ComponentName(this, AWProviderListaZakupow.class))
    );
    sendBroadcast(intent);
    
    super.onStop();
  }
  
  @Override
  protected void onDestroy()
  {
    mAdView.destroy();
    
    super.onDestroy();
  }
  
  @Override
  public List<ModelProdukt> getProdukty()
  {
    return produkty;
  }

  @Override
  public void setProdukty(final List<ModelProdukt> produkty)
  {
    this.produkty = produkty;
  }
  
  @Override
  public List<ModelProdukt> getDoKupienia()
  {
    return doKupienia;
  }
  
  @Override
  public void setDoKupienia(final List<ModelProdukt> doKupienia)
  {
    this.doKupienia = doKupienia;
  }
  
  @Override
  public List<ModelProdukt> getWKoszyku()
  {
    return wKoszyku;
  }
  
  @Override
  public void setWKoszyku(final List<ModelProdukt> wKoszyku)
  {
    this.wKoszyku = wKoszyku;
  }
  
  public void onDialogNegativeClick(final DialogFragment dialog)
  {
  }
  
  public void onDialogPositiveClick(final DialogFragment dialog, final int i, final String nazwa, 
    final String sklep, final double cena)
  {
    final String tag = dialog.getTag();
    if(tag.equals("fwkItemLongClick"))
    {
      ModelProdukt model = wKoszyku.get(i);
      model.setNazwa(nazwa);
      model.setSklep(sklep);
      model.setCena(cena);
      return ;
    }
    if(tag.equals("fdkItemLongClick"))
    {
      ModelProdukt model = doKupienia.get(i);
      model.setNazwa(nazwa);
      model.setSklep(sklep);
      model.setCena(cena);
      return ;
    }
    if(tag.equals("fpoNowy"))
    {
      final ModelProdukt model = new ModelProdukt();
      model.setNazwa(nazwa);
      model.setSklep(sklep);
      model.setCena(cena);
      produkty.add(model);
      final SharedPreferences settings = getSharedPreferences("LISTA-ZAKUPOW", 0);
      if(settings.getBoolean("PIERWSZY-PRODUKT", true))
      {
        settings.edit().putBoolean("PIERWSZY-PRODUKT", false).commit();
        final Resources resources = getResources();
        final String string = resources.getString(R.string.pierwszyProdukt);
        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE))
          .notify(0, new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(string)
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(
              this, 
              0, 
              new Intent(this, ActivityInstrukcje.class).putExtra("INSTRUKCJE", string), 
              PendingIntent.FLAG_UPDATE_CURRENT
            )
            )
            .build()
          );
      }
      return ;
    }
    if(tag.equals("fpcUaktualnij"))
    {
      ModelProdukt model = produkty.get(i);
      model.setNazwa(nazwa);
      model.setSklep(sklep);
      model.setCena(cena);
      return ;
    }
  }
}