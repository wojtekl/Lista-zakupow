package com.gmail.lesniakwojciech.listazakupowa;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class ActivityListaZakupow extends FragmentActivity implements ActionBar.TabListener, WspoldzielenieDanych
{
    
    private AdView mAdView;
    private ViewPager viewPager;
    private List<ModelProdukt> doKupienia;
    private List<ModelProdukt> produkty;
    private List<ModelProdukt> zabrane;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitylistazakupow);
        final Resources resources = this.getResources();
        
        MobileAds.initialize(this.getApplicationContext(), resources.getString(R.string.ad_app_id));
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder().build());
        mAdView.setAdListener(new AdListener()
        {
            @Override
            public void onAdLoaded()
            {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
        
        viewPager = (ViewPager)this.findViewById(R.id.pager);
        viewPager.setAdapter(new TabsPagerAdapter(this.getSupportFragmentManager()));
        final ActionBar actionBar = this.getActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText(resources.getString(R.string.ZABRANE)).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(resources.getString(R.string.DOKUPIENIA)).setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText(resources.getString(R.string.PRODUKTY)).setTabListener(this));
        
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
        
        });
        final SharedPreferences settings = this.getSharedPreferences("LISTA-ZAKUPOW", 0);
        String stringLista = settings.getString("LISTA", "[[],[],[]]");
        if(settings.getBoolean("PIERWSZE-URUCHOMIENIE", true))
        {
            final SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("PIERWSZE-URUCHOMIENIE", false);
            editor.commit();
            if(stringLista.equals("[[],[],[]]")){
                stringLista = resources.getString(R.string.PIERWSZALISTA);
            }
            Toast.makeText(getApplicationContext(), R.string.PIERWSZEURUCHOMIENIE, Toast.LENGTH_LONG).show();
        }
        produkty = new ArrayList<ModelProdukt>();
        doKupienia = new ArrayList<ModelProdukt>();
        zabrane = new ArrayList<ModelProdukt>();
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
                zabrane.add(ModelProdukt.fromJSON(jsonArray.getString(i)));
            }
        }
        catch(final JSONException exception)
        {
        }
        viewPager.setCurrentItem(1);
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
                stringBuilder.append(",");
                stringBuilder.append(produkty.get(i).toJSON());
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
                stringBuilder.append(",");
                stringBuilder.append(doKupienia.get(i).toJSON());
            }
        }
        stringBuilder.append("]");
        
        stringBuilder.append(",");
        
        stringBuilder.append("[");
        d = zabrane.size();
        if(0 < d)
        {
            stringBuilder.append(zabrane.get(0).toJSON());
            for(int i = 1; i < d; ++i)
            {
                stringBuilder.append(",");
                stringBuilder.append(zabrane.get(i).toJSON());
            }
        }
        stringBuilder.append("]");
        
        stringBuilder.append("]");
        
        final String stringLista = stringBuilder.toString();
        
        final SharedPreferences settings = this.getSharedPreferences("LISTA-ZAKUPOW", 0);
        final SharedPreferences.Editor editor = settings.edit();
        if(!settings.getString("LISTA", "").equals(stringLista))
        {
            editor.putString("LISTA", stringLista);
            editor.commit();
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
    protected void onDestroy()
    {
        mAdView.destroy();
        super.onDestroy();
    }
    
    @Override
    public List<ModelProdukt> doKupienia()
    {
        return doKupienia;
    }
    
    @Override
    public void doKupienia(final List<ModelProdukt> d)
    {
        doKupienia = d;
    }
    
    @Override
    public List<ModelProdukt> produkty()
    {
        return produkty;
    }

    @Override
    public void produkty(final List<ModelProdukt> p)
    {
        produkty = p;
    }
    
    @Override
    public List<ModelProdukt> zabrane()
    {
        return zabrane;
    }
    
    @Override
    public void zabrane(final List<ModelProdukt> z)
    {
        zabrane = z;
    }
    
}
