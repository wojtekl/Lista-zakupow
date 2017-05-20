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
import android.widget.ListView;

import java.util.List;

public class FragmentZabrane extends Fragment /*implements DialogProdukt.DialogListener*/
{
    
    private ListView listView;
    private WspoldzielenieDanych wspoldzielenieDanych;
    private List<ModelProdukt> zabrane, doKupienia, produkty;
    private InteractiveArrayAdapter interactiveArrayAdapter;
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle icicle)
    {
        //super.onCreate(icicle);
        final View view = inflater.inflate(R.layout.fragmentprodukty, container, false);
        listView = (ListView)view.findViewById(R.id.produktyLista);
        zabrane = wspoldzielenieDanych.zabrane();
        interactiveArrayAdapter = new InteractiveArrayAdapter(this.getActivity(), zabrane);
        listView.setAdapter(interactiveArrayAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        setHasOptionsMenu(true);
        doKupienia = wspoldzielenieDanych.doKupienia();
        produkty = wspoldzielenieDanych.produkty();
        return view;
    }
    
    private final OnItemClickListener onItemClickListener = new OnItemClickListener()
    {
        
        public void onItemClick(final AdapterView<?> av, final View view, final int i, final long l)
        {
            doKupienia.add(((ModelProdukt)av.getItemAtPosition(i)));
            zabrane.remove(i);
            interactiveArrayAdapter.notifyDataSetChanged();
        }
        
    };
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater)
    {
        menuInflater.inflate(R.menu.menudokupienia, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem menu)
    {
        switch(menu.getItemId())
        {
            case R.id.doKupieniaWyczysc:
                produkty.addAll(zabrane);
                interactiveArrayAdapter.clear();
                return true;
            case R.id.doKupieniaWyslijSMSem:
                int l = zabrane.size();
                if(0 < l)
                {
                    final StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(this.getResources().getString(R.string.ZABRANE));
                    stringBuilder.append(":\n");
                    --l;
                    for(int i = 0; i < l; ++i)
                    {
                        stringBuilder.append(zabrane.get(i).getNazwa());
                        stringBuilder.append(",\n");
                    }
                    stringBuilder.append(zabrane.get(l).getNazwa());
                    stringBuilder.append(".");
                    final Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("smsto:"));
                    intent.setType("vnd.android-dir/mms-sms");
                    intent.putExtra("sms_body", stringBuilder.toString());
                    startActivity(intent);
                }
                return true;
        }
        return true;
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

    /*
    public void onDialogPositiveClick(final DialogFragment dialog)
    {
    }

    public void onDialogNegativeClick(final DialogFragment dialog)
    {
    }
    */
    
}
