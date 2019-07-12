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

public class FragmentProdukty
        extends Fragment
        implements DialogFragmentProdukt.DialogListener {
    protected static final int title = R.string.produkty;
    protected static final int color = R.color.lightBlueA100;
    protected static final int icon = R.drawable.ic_kitchen;

    private static final String OPTIONS_DODAJ_PRODUKT = "fpoDodajProdukt";
    private static final String CONTEXT_UAKTUALNIJ = "fpcUaktualnij";

    private IWspoldzielenieDanych wspoldzielenieDanych;
    private AdapterListaZakupow adapterListaZakupow;
    private AdapterListaZakupow doKupienia;

    private ActionMode actionMode;
    private final AdapterListaZakupow.OnItemClickListener onItemClickListener =
            new AdapterListaZakupow.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
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
                            mode.getMenuInflater().inflate(R.menu.fragmentproduktycontext, menu);
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
                                case R.id.fpcCena:
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
                                                                    model.getNazwa() + ":\n"
                                                                            + WebAPI.filtruj(response.getMessage())));
                                                }
                                            }
                                        }).execute(WebAPI.pobierzCeny(ustawienia.getAPIAdres(""),
                                                model.getNazwa(), ustawienia.getIdentyfikator("")));
                                    }
                                    mode.finish();
                                    return true;
                                case R.id.fpcUaktualnij:
                                    DialogFragmentProdukt
                                            .newInstance(FragmentProdukty.this, position,
                                                    model.getNazwa(), model.getSklep(), model.getCena(),
                                                    wspoldzielenieDanych.getSklepy())
                                            .show(requireActivity().getSupportFragmentManager(),
                                                    CONTEXT_UAKTUALNIJ);
                                    mode.finish();
                                    return true;
                                case R.id.fpcUsun:
                                    adapterListaZakupow.removeItem(position);
                                    mode.finish();
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

        wspoldzielenieDanych = (IWspoldzielenieDanych) requireActivity();
        doKupienia = wspoldzielenieDanych.getDoKupienia();

        adapterListaZakupow = wspoldzielenieDanych.getProdukty();
        adapterListaZakupow.setOnItemClickListener(onItemClickListener);

        final RecyclerView recyclerView = view.findViewById(R.id.fpListView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterListaZakupow);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                DialogFragmentProdukt
                        .newInstance(FragmentProdukty.this, -1, "", "",
                                0.0f, wspoldzielenieDanych.getSklepy())
                        .show(requireActivity().getSupportFragmentManager(), OPTIONS_DODAJ_PRODUKT);
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
        mi.inflate(R.menu.fragmentproduktyoptions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem mi) {
        switch (mi.getItemId()) {
            case R.id.fpoDodajProdukt:
                DialogFragmentProdukt
                        .newInstance(FragmentProdukty.this, -1, "", "",
                                0.0f, wspoldzielenieDanych.getSklepy())
                        .show(requireActivity().getSupportFragmentManager(), OPTIONS_DODAJ_PRODUKT);
                return true;
            case R.id.fpoUstawienia:
                startActivity(new Intent(getContext(), ActivityUstawienia.class));
                return true;
            default:
                return super.onOptionsItemSelected(mi);
        }
    }

    @Override
    public void onDialogNegativeClick(final DialogFragment dialog) {
    }

    @Override
    public void onDialogPositiveClick(final DialogFragment dialog, final int i, final String nazwa,
                                      final String sklep, final double cena) {
        final String tag = dialog.getTag();
        if (FragmentProdukty.OPTIONS_DODAJ_PRODUKT.equals(tag)) {
            final ModelProdukt model = new ModelProdukt();
            model.setNazwa(nazwa);
            model.setSklep(sklep);
            model.setCena(cena);
            adapterListaZakupow.addItem(model);
            adapterListaZakupow.sort(new ComparatorProduktPopularnosc());
            final Context context = requireContext();
            final Ustawienia ustawienia = new Ustawienia(context);
            if (ustawienia.getCenyUdostepniaj(false) && Permissions.hasInternet(context, null)) {
                new AsyncTaskRzadanie(new AsyncTaskRzadanie.Listener() {
                    @Override
                    public void onPostExecute(final AsyncTaskRzadanie.RzadanieResponse response) {
                        new Zetony(context).dodajZetony(Zetony.ZETONY_CENA_UDOSTEPNIENIE, getView());
                    }
                }).execute(ustawienia.getAPIAdres("") + WebAPI.produkt, AsyncTaskRzadanie.POST,
                        WebAPI.udostepnijCeny(ustawienia.getIdentyfikator(""), nazwa, sklep, cena));
            }
            return;
        }
        if (FragmentProdukty.CONTEXT_UAKTUALNIJ.equals(tag)) {
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
            adapterListaZakupow.sort(new ComparatorProduktPopularnosc());
        }
    }
}
