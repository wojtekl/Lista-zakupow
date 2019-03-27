package com.gmail.lesniakwojciech.listazakupowa;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class FragmentWKoszyku
        extends Fragment
        implements DialogFragmentProdukt.DialogListener {
    public static final String ITEM_LONG_CLICK = "fwkItemLongClick";

    private View view;
    private AdapterListaZakupow adapterListaZakupow;
    private AdapterListaZakupow doKupienia, produkty;

    @Override
    public View onCreateView(@NonNull final LayoutInflater li, final ViewGroup vg, final Bundle bundle) {
        view = li.inflate(R.layout.fragmentprodukty, vg, false);

        final IWspoldzielenieDanych wspoldzielenieDanych = (IWspoldzielenieDanych) getActivity();
        doKupienia = wspoldzielenieDanych.getDoKupienia();
        produkty = wspoldzielenieDanych.getProdukty();

        adapterListaZakupow = wspoldzielenieDanych.getWKoszyku();
        adapterListaZakupow.setOnItemClickListener(onItemClickListener);

        final RecyclerView recyclerView = view.findViewById(R.id.fpListView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterListaZakupow);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_playlist_add_check);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                zakonczZakupy();
            }
        });

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater mi) {
        mi.inflate(R.menu.fragmentwkoszykuoptions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem mi) {
        switch (mi.getItemId()) {
            case R.id.fwkoZakonczZakupy:
                zakonczZakupy();
                return true;
            case R.id.fwkoWyswietlReklame:
                Reklamy.rewardedVideoAd(getContext(), view);
                return true;
            default:
                return super.onOptionsItemSelected(mi);
        }
    }

    private final AdapterListaZakupow.OnItemClickListener onItemClickListener =
            new AdapterListaZakupow.OnItemClickListener() {
        @Override
        public void onItemClick(final int position) {
            doKupienia.addItem(adapterListaZakupow.getItem(position));
            doKupienia.sort(new ComparatorProduktSklep());
            adapterListaZakupow.removeItem(position);
        }

        @Override
        public void onItemLongClick(final View view, final int position) {
            final ModelProdukt model = adapterListaZakupow.getItem(position);
            DialogFragmentProdukt
                    .newInstance(FragmentWKoszyku.this, position, model.getNazwa(),
                            model.getSklep(), model.getCena(),
                            ((IWspoldzielenieDanych)getActivity()).getSklepy())
                    .show(getActivity().getSupportFragmentManager(), ITEM_LONG_CLICK);
        }
    };

    private void zakonczZakupy() {
        for(int i = 0, d = adapterListaZakupow.getItemCount(); i < d; ++i) {
            adapterListaZakupow.getItem(i).podbijPopularnosc();
        }
        produkty.addAll(adapterListaZakupow.getDataset());
        produkty.sort(new ComparatorProduktPopularnosc());
        adapterListaZakupow.clear();
    }

    @Override
    public void onDialogNegativeClick(final DialogFragment dialog) {}

    @Override
    public void onDialogPositiveClick(final DialogFragment dialog, final int i, final String nazwa,
                                      final String sklep, final double cena) {
        if (FragmentWKoszyku.ITEM_LONG_CLICK.equals(dialog.getTag())) {
            final ModelProdukt model = adapterListaZakupow.getItem(i);
            model.setNazwa(nazwa);
            model.setSklep(sklep);
            model.setCena(cena);
            adapterListaZakupow.sort(new ComparatorProduktCena());
        }
    }
}
