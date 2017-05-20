package com.gmail.lesniakwojciech.listazakupowa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

public class FragmentProdukty extends Fragment implements DialogProdukt.DialogListener
{
    
    private InterstitialAd mInterstitialAd;
    private ListView listView;
    private WspoldzielenieDanych wspoldzielenieDanych;
    private List<ModelProdukt> produkty, doKupienia;
    private InteractiveArrayAdapter interactiveArrayAdapter;
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle icicle)
    {
        //super.onCreate(icicle);
        final View view = inflater.inflate(R.layout.fragmentprodukty, container, false);
        mInterstitialAd = new InterstitialAd(this.getContext());
        mInterstitialAd.setAdUnitId(this.getResources().getString(R.string.pelnoekranowa_ad_unit_id));
        listView = (ListView)view.findViewById(R.id.produktyLista);
        produkty = wspoldzielenieDanych.produkty();
        interactiveArrayAdapter = new InteractiveArrayAdapter(this.getActivity(), produkty);
        listView.setAdapter(interactiveArrayAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        this.registerForContextMenu(listView);
        this.setHasOptionsMenu(true);
        doKupienia = wspoldzielenieDanych.doKupienia();
        return view;
    }
    
    private final OnItemClickListener onItemClickListener = new OnItemClickListener()
    {
        
        public void onItemClick(final AdapterView<?> av, final View view, final int i, final long l)
        {
            doKupienia.add((ModelProdukt)av.getItemAtPosition(i));
            produkty.remove(i);
            interactiveArrayAdapter.notifyDataSetChanged();
        }
        
    };
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater)
    {
        menuInflater.inflate(R.menu.menuprodukty, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem menu)
    {
        switch(menu.getItemId())
        {
            case R.id.produktyStworz:
                final ModelProdukt model = new ModelProdukt();
                final DialogProdukt dialogProdukt = new DialogProdukt();
                dialogProdukt.ustaw(model, this);
                dialogProdukt.show(getActivity().getSupportFragmentManager(), "dialogProdukt");
                //interactiveArrayAdapter.notifyDataSetChanged();
                return true;
            case R.id.produktyWyczyscWszystko:
                new AlertDialog.Builder(this.getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.WYKONAC)
                        .setMessage(R.string.POTWIERDZCZYSZCZENIE)
                        .setNegativeButton(R.string.NIE, new DialogInterface.OnClickListener()
                        {
                            public void onClick(final DialogInterface di, final int i)
                            {
                            }
                        })
                        .setPositiveButton(R.string.TAK, new DialogInterface.OnClickListener()
                        {
                            public void onClick(final DialogInterface di, final int i)
                            {
                                interactiveArrayAdapter.clear();
                                doKupienia.clear();
                                wspoldzielenieDanych.zabrane().clear();
                            }
                        })
                        .create()
                        .show();
                return true;
            case R.id.produktyWesprzyjMnie:
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                mInterstitialAd.setAdListener(new AdListener()
                {
                    @Override
                    public void onAdLoaded()
                    {
                        if(mInterstitialAd.isLoaded())
                        {
                            mInterstitialAd.show();
                            Toast.makeText(getActivity().getApplicationContext(), R.string.DZIEKUJE, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                return true;
            case R.id.produktyPokazInstrukcje:
                final Activity activity = this.getActivity();
                final SharedPreferences.Editor editor = activity.getSharedPreferences("LISTA-ZAKUPOW", 0).edit();
                editor.putBoolean("PIERWSZE-URUCHOMIENIE", true);
                editor.putBoolean("PIERWSZY-PRODUKT", true);
                editor.commit();
                Toast.makeText(activity.getApplicationContext(), R.string.URUCHOMAPLIKACJEPONOWNIE, Toast.LENGTH_LONG).show();
                return true;
            case R.id.produktyPolecSMSem:
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("smsto:"));
                intent.setType("vnd.android-dir/mms-sms");
                intent.putExtra("sms_body", this.getResources().getString(R.string.ODNOSNIK));
                startActivity(intent);
                return true;
        }
        return true;
    }
    
    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, view, menuInfo);
        final MenuInflater inflater = this.getActivity().getMenuInflater();
        inflater.inflate(R.menu.menuproduktylista, menu);
    }
    
    @Override
    public boolean onContextItemSelected(final MenuItem item)
    {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
        switch(item.getItemId())
        {
            case R.id.produktyListaZmien:
                final DialogProdukt dialogProdukt = new DialogProdukt();
                dialogProdukt.ustaw(produkty.get(info.position));
                dialogProdukt.show(this.getActivity().getSupportFragmentManager(), "dialogProdukt");
                //interactiveArrayAdapter.notifyDataSetChanged();
                return true;
            case R.id.produktyListaUsun:
                produkty.remove(info.position);
                interactiveArrayAdapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    
    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if(null != listView)
        {
            interactiveArrayAdapter.notifyDataSetChanged();
        }
    }
    
    @Override
    public void onAttach(final Context c)
    {
        super.onAttach(c);
        wspoldzielenieDanych = (WspoldzielenieDanych)c;
    }

    public void onDialogPositiveClick(final ModelProdukt produkt)
    {
        interactiveArrayAdapter.add(produkt);
        final Activity activity = this.getActivity();
        final SharedPreferences settings = activity.getSharedPreferences("LISTA-ZAKUPOW", 0);
        if(settings.getBoolean("PIERWSZY-PRODUKT", true))
        {
            final SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("PIERWSZY-PRODUKT", false);
            editor.commit();
            Toast.makeText(activity.getApplicationContext(), R.string.PIERWSZYPRODUKT, Toast.LENGTH_LONG).show();
        }
    }

    public void onDialogNegativeClick(final DialogFragment dialog)
    {
    }
    
}
