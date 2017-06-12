package com.gmail.lesniakwojciech.listazakupowa;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
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

public class ActivityListaZakupow 
  extends FragmentActivity 
  implements ActionBar.TabListener, IWspoldzielenieDanych
{
  private AdView mAdView;
  
  private ViewPager viewPager;
  
  private List<ModelProdukt> produkty;
  private List<ModelProdukt> doKupienia;
  private List<ModelProdukt> wKoszyku;
  
  @Override
  protected void onCreate(final Bundle bundle)
  {
    super.onCreate(bundle);
    setContentView(R.layout.activitylistazakupow);
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
    
    viewPager = (ViewPager)findViewById(R.id.alzViewPager);
    viewPager.setAdapter(new FPAdapterListaZakupow(getSupportFragmentManager()));
    final ActionBar actionBar = getActionBar();
    actionBar.setHomeButtonEnabled(false);
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    actionBar.addTab(actionBar.newTab().setText(resources.getString(R.string.wKoszyku)).setTabListener(this));
    actionBar.addTab(actionBar.newTab().setText(resources.getString(R.string.doKupienia)).setTabListener(this));
    actionBar.addTab(actionBar.newTab().setText(resources.getString(R.string.produkty)).setTabListener(this));
    
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
    {
      @Override
      public void onPageScrolled(final int i, final float f, final int i1)
      {
      }
      
      @Override
      public void onPageSelected(final int i)
      {
        actionBar.setSelectedNavigationItem(i);
      }
      
      @Override
      public void onPageScrollStateChanged(final int i)
      {
      }
    }
    );
    
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
    
    viewPager.setCurrentItem(1);
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
    super.onStop();
    
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
  }
  
  @Override
  protected void onDestroy()
  {
    mAdView.destroy();
    
    super.onDestroy();
  }
  
  public void onTabSelected(final ActionBar.Tab tab, final FragmentTransaction ft)
  {
    viewPager.setCurrentItem(tab.getPosition());
  }

  public void onTabUnselected(final ActionBar.Tab tab, final FragmentTransaction ft)
  {
  }

  public void onTabReselected(final ActionBar.Tab tab, final FragmentTransaction ft)
  {
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
}
