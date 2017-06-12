package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import java.util.List;

public class FragmentWKoszyku 
  extends Fragment 
  implements DialogFragmentProdukt.DialogListener
{
  private InterstitialAd mInterstitialAd;
  private ListView listView;
  private IWspoldzielenieDanych wspoldzielenieDanych;
  private List<ModelProdukt> wKoszyku, doKupienia, produkty;
  private AAdapterListaZakupow aAdapterListaZakupow;
  
  @Override
  public View onCreateView(final LayoutInflater li, final ViewGroup vg, final Bundle bundle)
  {
    final View view = li.inflate(R.layout.fragmentprodukty, vg, false);
    
    mInterstitialAd = new InterstitialAd(getContext());
    mInterstitialAd.setAdUnitId(getResources().getString(R.string.pelnoekranowa_ad_unit_id));
    
    wKoszyku = wspoldzielenieDanych.getWKoszyku();
    doKupienia = wspoldzielenieDanych.getDoKupienia();
    produkty = wspoldzielenieDanych.getProdukty();
    
    aAdapterListaZakupow = new AAdapterListaZakupow(getActivity(), wKoszyku);
    listView = (ListView)view.findViewById(R.id.fpListView);
    listView.setAdapter(aAdapterListaZakupow);
    listView.setOnItemClickListener(onItemClickListener);
    listView.setOnItemLongClickListener(onItemLongClickListener);
    listView.setBackgroundColor(Color.parseColor("#10ff8000"));
    
    setHasOptionsMenu(true);
    return view;
  }
  
  @Override
  public void onAttach(final Context cntxt)
  {
    super.onAttach(cntxt);
    
    wspoldzielenieDanych = (IWspoldzielenieDanych)cntxt;
  }
  
  @Override
  public void onCreateOptionsMenu(final Menu menu, final MenuInflater mi)
  {
    mi.inflate(R.menu.fragmentwkoszykuoptions, menu);
  }
  
  @Override
  public boolean onOptionsItemSelected(final MenuItem mi)
  {
    switch(mi.getItemId())
    {
      case R.id.fwkoZakonczZakupy:
        produkty.addAll(wKoszyku);
        aAdapterListaZakupow.clear();
        return true;
      case R.id.fwkoWspomozAutora:
        mInterstitialAd.setAdListener(new AdListener()
        {
          @Override
          public void onAdLoaded()
          {
            if(mInterstitialAd.isLoaded())
            {
              mInterstitialAd.show();
              Toast.makeText(getActivity().getApplicationContext(), R.string.dziekuje, Toast.LENGTH_LONG).show();
            }
          }
        }
        );
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        return true;
      default:
        return super.onOptionsItemSelected(mi);
    }
  }
  
  private final OnItemClickListener onItemClickListener = new OnItemClickListener()
  {
    public void onItemClick(final AdapterView<?> av, final View view, final int i, final long l)
    {
      ModelProdukt model = (ModelProdukt)av.getItemAtPosition(i);
      doKupienia.add(model);
      aAdapterListaZakupow.remove(model);
    }
  };
  
  private final OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener()
  {
    public boolean onItemLongClick(final AdapterView<?> av, final View view, final int i, final long l)
    {
      new DialogFragmentProdukt(FragmentWKoszyku.this, (ModelProdukt)av.getItemAtPosition(i))
        .show(getActivity().getSupportFragmentManager(), "fwkItemLongClick");
      return true;
    }
  };
  
  @Override
  public void setUserVisibleHint(final boolean isVisibleToUser)
  {
    super.setUserVisibleHint(isVisibleToUser);
    
    if(
      (null != listView) && 
      (listView.getCount() != aAdapterListaZakupow.getCount()) 
    )
    {
      aAdapterListaZakupow.notifyDataSetChanged();
    }
  }
  
  public void onDialogPositiveClick(ModelProdukt model)
  {
  }
  
  public void onDialogNegativeClick(DialogFragment dialog)
  {
  }
}
