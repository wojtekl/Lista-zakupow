package com.gmail.lesniakwojciech.listazakupowa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
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
  implements DialogFragmentProdukt.DialogListener
{
  private ListView listView;
  private IWspoldzielenieDanych wspoldzielenieDanych;
  private List<ModelProdukt> doKupienia, produkty;
  private AAdapterListaZakupow aAdapterListaZakupow;
  
  @Override
  public View onCreateView(final LayoutInflater li, final ViewGroup vg, final Bundle bundle)
  {
    final View view = li.inflate(R.layout.fragmentprodukty, vg, false);
    
    doKupienia = wspoldzielenieDanych.getDoKupienia();
    produkty = wspoldzielenieDanych.getProdukty();
    
    aAdapterListaZakupow = new AAdapterListaZakupow(getActivity(), produkty);
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
        new DialogFragmentProdukt(this, new ModelProdukt())
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
          .setTitle(R.string.wykonac)
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
        new DialogFragmentProdukt(this, produkty.get(menuInfo.position))
          .show(getActivity().getSupportFragmentManager(), "fpcUaktualnij");
        return true;
      case R.id.fpcUsun:
        aAdapterListaZakupow.remove(produkty.get(menuInfo.position));
        return true;
      default:
        return super.onContextItemSelected(mi);
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
  
  public void onDialogPositiveClick(final ModelProdukt model)
  {
    if(!produkty.contains(model))
    {
      aAdapterListaZakupow.add(model);
      final Activity activity = getActivity();
      final SharedPreferences settings = activity.getSharedPreferences("LISTA-ZAKUPOW", 0);
      if(settings.getBoolean("PIERWSZY-PRODUKT", true))
      {
        settings.edit().putBoolean("PIERWSZY-PRODUKT", false).commit();
        final Resources resources = getResources();
        final String string = resources.getString(R.string.pierwszyProdukt);
        ((NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE))
          .notify(0, new NotificationCompat.Builder(activity)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(string)
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(
              activity, 
              0, 
              new Intent(activity, ActivityInstrukcje.class).putExtra("INSTRUKCJE", string), 
              PendingIntent.FLAG_UPDATE_CURRENT
            )
            )
            .build()
          );
      }
    }
  }
  
  public void onDialogNegativeClick(final DialogFragment dialog)
  {
  }
}
