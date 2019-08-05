package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentWKoszyku
        extends Fragment
        implements DialogFragmentProdukt.DialogListener {
    protected static final int title = R.string.w_koszyku;
    protected static final int color = R.color.amberA100;
    protected static final int icon = R.drawable.ic_shopping_cart;

    private static final String CONTEXT_UAKTUALNIJ = "fwcUaktualnij";

    private AdapterListaZakupow adapterListaZakupow;
    private AdapterListaZakupow doKupienia, produkty;

    private ActionMode actionMode;
    private final AdapterListaZakupow.OnItemClickListener onItemClickListener =
            new AdapterListaZakupow.OnItemClickListener() {
                @Override
                public void onItemClick(final int position) {
                    if (null != actionMode) {
                        return;
                    }

                    doKupienia.addItem(adapterListaZakupow.getItem(position));
                    doKupienia.sort(new ComparatorProduktSklep());
                    adapterListaZakupow.removeItem(position);
                }

                @Override
                public void onItemLongClick(final View view, final int position) {
                    if (null != actionMode) {
                        return;
                    }

                    adapterListaZakupow.setSelection(view);
                    actionMode = requireActivity().startActionMode(new ActionMode.Callback() {

                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            mode.getMenuInflater().inflate(R.menu.fragmentwkoszykucontext, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            return false;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            final ModelProdukt model = adapterListaZakupow.getItem(position);
                            switch (item.getItemId()) {
                                case R.id.fwcCena:
                                    final Context context = requireContext();
                                    final View view = getView();
                                    if (Permissions.hasInternet(context, view) &&
                                            new Zetony(context).sprawdzZetony(Zetony.ZETONY_CENA_ZOBACZ,
                                                    true, view)) {
                                        final Ustawienia ustawienia = new Ustawienia(context);
                                        new AsyncTaskRzadanie(new AsyncTaskRzadanie.Listener() {
                                            @Override
                                            public void onPostExecute(final AsyncTaskRzadanie.RzadanieResponse response) {
                                                if (response.isOK(false)) {
                                                    startActivity(new Intent(context, ActivityKomunikat.class)
                                                            .putExtra(ActivityKomunikat.IE_KOMUNIKAT,
                                                                    model.getNazwa() + ":"
                                                                            + WebAPI.filtruj(response.getMessage())));
                                                }
                                            }
                                        }).execute(WebAPI.pobierzCeny(ustawienia.getAPIAdres(""), model.getNazwa(),
                                                ustawienia.getIdentyfikator("")));
                                    }
                                    mode.finish();
                                    return true;
                                case R.id.fwcUaktualnij:
                                    DialogFragmentProdukt
                                            .newInstance(FragmentWKoszyku.this, position, model.getNazwa(),
                                                    model.getSklep(), model.getCena(),
                                                    ((IWspoldzielenieDanych) requireActivity()).getSklepy())
                                            .show(requireActivity().getSupportFragmentManager(), CONTEXT_UAKTUALNIJ);
                                    mode.finish();
                                    new Zetony(getContext()).wlaczInternet(getView());
                                    return true;
                                default:
                                    return false;
                            }
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {
                            actionMode = null;
                            adapterListaZakupow.clearSelection();
                        }
                    });
                }
            };

    @Override
    public View onCreateView(@NonNull final LayoutInflater li, final ViewGroup vg, final Bundle bundle) {
        final View view = li.inflate(R.layout.fragmentprodukty, vg, false);

        final IWspoldzielenieDanych wspoldzielenieDanych = (IWspoldzielenieDanych) requireActivity();
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
    public void onPause() {
        if (null != actionMode) {
            actionMode.finish();
        }

        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater mi) {
        mi.inflate(R.menu.fragmentwkoszykuoptions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem mi) {
        switch (mi.getItemId()) {
            case R.id.fwkoZakonczZakupy:
                zakonczZakupy();
                return true;
            default:
                return super.onOptionsItemSelected(mi);
        }
    }

    private void zakonczZakupy() {
        for (int i = 0, d = adapterListaZakupow.getItemCount(); i < d; ++i) {
            adapterListaZakupow.getItem(i).podbijPopularnosc();
        }
        produkty.addAll(adapterListaZakupow.getDataset());
        produkty.sort(new ComparatorProduktPopularnosc());
        adapterListaZakupow.clear();
    }

    @Override
    public void onDialogNegativeClick(final DialogFragment dialog) {
    }

    @Override
    public void onDialogPositiveClick(final DialogFragment dialog, final int i, final String nazwa,
                                      final String sklep, final double cena) {
        if (FragmentWKoszyku.CONTEXT_UAKTUALNIJ.equals(dialog.getTag())) {
            final ModelProdukt model = adapterListaZakupow.getItem(i);
            final Context context = requireContext();
            final Ustawienia ustawienia = new Ustawienia(context);
            if (ustawienia.getCenyUdostepniaj(false) && model.getCena() != cena
                    && Permissions.hasInternet(context, null)) {
                new AsyncTaskRzadanie(new AsyncTaskRzadanie.Listener() {
                    @Override
                    public void onPostExecute(final AsyncTaskRzadanie.RzadanieResponse response) {
                        new Zetony(context).dodajZetony(Zetony.ZETONY_CENA_UDOSTEPNIENIE, getView());
                    }
                }).execute(ustawienia.getAPIAdres("") + WebAPI.produkt, AsyncTaskRzadanie.POST,
                        WebAPI.udostepnijCeny(ustawienia.getIdentyfikator(""), nazwa, sklep, cena));
            }
            model.setNazwa(nazwa);
            model.setSklep(sklep);
            model.setCena(cena);
            adapterListaZakupow.sort(new ComparatorProduktCena());
        }
    }
}
