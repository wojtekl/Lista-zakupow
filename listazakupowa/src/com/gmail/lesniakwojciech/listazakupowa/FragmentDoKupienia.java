package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.List;

public class FragmentDoKupienia 
  extends Fragment
{
  private ListView listView;
  private AAdapterListaZakupow aAdapterListaZakupow;
  private IWspoldzielenieDanych wspoldzielenieDanych;
  private List<ModelProdukt> wKoszyku, doKupienia, produkty;
  
  @Override
  public View onCreateView(final LayoutInflater li, final ViewGroup vg, final Bundle bundle)
  {
    final View view = li.inflate(R.layout.fragmentprodukty, vg, false);
    
    wKoszyku = wspoldzielenieDanych.getWKoszyku();
    doKupienia = wspoldzielenieDanych.getDoKupienia();
    produkty = wspoldzielenieDanych.getProdukty();
    
    aAdapterListaZakupow = new AAdapterListaZakupow(getActivity(), doKupienia);
    listView = (ListView)view.findViewById(R.id.fpListView);
    listView.setAdapter(aAdapterListaZakupow);
    listView.setOnItemClickListener(onItemClickListener);
    listView.setOnItemLongClickListener(onItemLongClickListener);
    listView.setBackgroundColor(getResources().getColor(R.color.red50));
    
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
    mi.inflate(R.menu.fragmentdokupieniaoptions, menu);
  }
  
  @Override
  public boolean onOptionsItemSelected(final MenuItem mi)
  {
    switch(mi.getItemId())
    {
      case R.id.fdkoWyslijSMSem:
        int l = doKupienia.size();
        if(0 < l)
        {
          final StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append(getResources().getString(R.string.doKupienia)).append(":\n");
          --l;
          for(int i = 0; i < l; ++i)
          {
            stringBuilder.append(doKupienia.get(i).getNazwa()).append(",\n");
          }
          stringBuilder.append(doKupienia.get(l).getNazwa()).append(".");
          startActivity(new Intent(Intent.ACTION_VIEW)
            .setData(Uri.parse("smsto:"))
            .setType("vnd.android-dir/mms-sms")
            .putExtra("sms_body", stringBuilder.toString())
          );
        }
        return true;
      case R.id.fdkoWyczysc:
        produkty.addAll(doKupienia);
        aAdapterListaZakupow.clear();
        return true;
      default:
        return super.onOptionsItemSelected(mi);
    }
  }
  
  private final OnItemClickListener onItemClickListener = new OnItemClickListener()
  {
    public void onItemClick(final AdapterView<?> av, final View view, final int i, final long l)
    {
      final ModelProdukt model = (ModelProdukt)av.getItemAtPosition(i);
      wKoszyku.add(model);
      aAdapterListaZakupow.remove(model);
    }
  };
  
  private final OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener()
  {
    public boolean onItemLongClick(final AdapterView<?> av, final View view, final int i, final long l)
    {
      final ModelProdukt model = (ModelProdukt)av.getItemAtPosition(i);
      DialogFragmentProdukt
        .newInstance(i, model.getNazwa(), model.getSklep(), model.getCena())
        .show(getActivity().getSupportFragmentManager(), "fdkItemLongClick");
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
}
