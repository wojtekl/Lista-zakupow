package com.gmail.lesniakwojciech.listazakupowa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import java.util.List;

public class FragmentProdukty 
  extends Fragment
{
  private ListView listView;
  private AAdapterListaZakupow aAdapterListaZakupow;
  private IWspoldzielenieDanych wspoldzielenieDanych;
  private List<ModelProdukt> doKupienia;
  
  @Override
  public View onCreateView(final LayoutInflater li, final ViewGroup vg, final Bundle bundle)
  {
    final View view = li.inflate(R.layout.fragmentprodukty, vg, false);
    
    doKupienia = wspoldzielenieDanych.getDoKupienia();
    
    aAdapterListaZakupow = new AAdapterListaZakupow(getActivity(), R.layout.aadapterlistazakupow, 
      wspoldzielenieDanych.getProdukty());
    listView = (ListView)view.findViewById(R.id.fpListView);
    listView.setAdapter(aAdapterListaZakupow);
    listView.setOnItemClickListener(onItemClickListener);
    registerForContextMenu(listView);
    
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
    mi.inflate(R.menu.fragmentproduktyoptions, menu);
  }
  
  @Override
  public boolean onOptionsItemSelected(final MenuItem mi)
  {
    switch(mi.getItemId())
    {
      case R.id.fpoNowy:
        DialogFragmentProdukt
          .newInstance(-1, "", "", 0.0f)
          .show(getActivity().getSupportFragmentManager(), "fpoNowy");
        return true;
      case R.id.fpoPokazInstrukcje:
        final Activity activity = getActivity();
        activity.getSharedPreferences("LISTA-ZAKUPOW", 0).edit()
          .putBoolean("PIERWSZE-URUCHOMIENIE", true)
          .putBoolean("PIERWSZY-PRODUKT", true)
          .commit();
        Toast.makeText(activity.getApplicationContext(), R.string.uruchomAplikacjePonownie, Toast.LENGTH_LONG)
          .show();
        return true;
      case R.id.fpoWyczyscWszystko:
        new AlertDialog.Builder(getActivity())
          .setIcon(android.R.drawable.ic_dialog_alert)
          .setTitle(R.string.wyczyscWszystko)
          .setMessage(R.string.potwierdzCzyszczenie)
          .setNegativeButton(R.string.nie, new DialogInterface.OnClickListener()
          {
            public void onClick(final DialogInterface di, final int i)
            {
            }
          }
          )
          .setPositiveButton(R.string.tak, new DialogInterface.OnClickListener()
          {
            public void onClick(final DialogInterface di, final int i)
            {
              wspoldzielenieDanych.getWKoszyku().clear();
              doKupienia.clear();
              aAdapterListaZakupow.clear();
            }
          }
          )
          .create()
          .show();
        return true;
      default:
        return super.onOptionsItemSelected(mi);
    }
  }
  
  @Override
  public void onCreateContextMenu(final ContextMenu cm, final View view, final ContextMenu.ContextMenuInfo cmi)
  {
    super.onCreateContextMenu(cm, view, cmi);
    getActivity().getMenuInflater().inflate(R.menu.fragmentproduktycontext, cm);
  }
  
  @Override
  public boolean onContextItemSelected(final MenuItem mi)
  {
    final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo)mi.getMenuInfo();
    switch(mi.getItemId())
    {
      case R.id.fpcUaktualnij:
        final int i = menuInfo.position;
        final ModelProdukt model = aAdapterListaZakupow.getItem(i);
        DialogFragmentProdukt
          .newInstance(i, model.getNazwa(), model.getSklep(), model.getCena())
          .show(getActivity().getSupportFragmentManager(), "fpcUaktualnij");
        return true;
      case R.id.fpcUsun:
        aAdapterListaZakupow.remove(aAdapterListaZakupow.getItem(menuInfo.position));
        return true;
      default:
        return super.onContextItemSelected(mi);
    }
  }
  
  private final OnItemClickListener onItemClickListener = new OnItemClickListener()
  {
    public void onItemClick(final AdapterView<?> av, final View view, final int i, final long l)
    {
      final ModelProdukt model = (ModelProdukt)av.getItemAtPosition(i);
      doKupienia.add(model);
      aAdapterListaZakupow.remove(model);
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
