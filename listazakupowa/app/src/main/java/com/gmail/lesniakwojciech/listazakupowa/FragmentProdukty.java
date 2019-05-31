package com.gmail.lesniakwojciech.listazakupowa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class FragmentProdukty
        extends Fragment
        implements DialogFragmentProdukt.DialogListener {
    public static final String OPTIONS_DODAJ_PRODUKT = "fpoDodajProdukt";
    public static final String CONTEXT_UAKTUALNIJ = "fpcUaktualnij";

    private View view;
    private IWspoldzielenieDanych wspoldzielenieDanych;
    private AdapterListaZakupow adapterListaZakupow;
    private AdapterListaZakupow doKupienia;

    private ActionMode actionMode;

    @Override
    public View onCreateView(@NonNull final LayoutInflater li, final ViewGroup vg, final Bundle bundle) {
        view = li.inflate(R.layout.fragmentprodukty, vg, false);

        wspoldzielenieDanych = (IWspoldzielenieDanych) getActivity();
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
                        .show(getActivity().getSupportFragmentManager(), OPTIONS_DODAJ_PRODUKT);
            }
        });

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(null != actionMode && !isVisibleToUser) {
            actionMode.finish();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater mi) {
        mi.inflate(R.menu.fragmentproduktyoptions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem mi) {
        switch (mi.getItemId()) {
            case R.id.fpoDodajProdukt:
                DialogFragmentProdukt
                        .newInstance(FragmentProdukty.this, -1, "", "",
                                0.0f, wspoldzielenieDanych.getSklepy())
                        .show(getActivity().getSupportFragmentManager(), OPTIONS_DODAJ_PRODUKT);
                return true;
            case R.id.fpoPokazInstrukcje:
                final Ustawienia ustawienia = new Ustawienia(getContext());
                ustawienia.setPierwszeUruchomienie(true);
                ustawienia.setPierwszyProdukt(true);
                ustawienia.setPierwszeWyslanie(true);
                Snackbar.make(view, R.string.uruchomAplikacjePonownie, Snackbar.LENGTH_LONG).show();
                return true;
            case R.id.fpoWyczyscWszystko:
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.wyczyscWszystko)
                        .setMessage(R.string.potwierdzCzyszczenie)
                        .setNegativeButton(R.string.nie, new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface di, final int i) {}
                                }
                        )
                        .setPositiveButton(R.string.tak, new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface di, final int i) {
                                        wspoldzielenieDanych.getWKoszyku().clear();
                                        doKupienia.clear();
                                        adapterListaZakupow.clear();
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

    private final AdapterListaZakupow.OnItemClickListener onItemClickListener =
            new AdapterListaZakupow.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            if(null != actionMode){
                return;
            }

            doKupienia.addItem(adapterListaZakupow.getItem(position));
            doKupienia.sort(new ComparatorProduktSklep());
            adapterListaZakupow.removeItem(position);
        }

        @Override
        public void onItemLongClick(final View view, final int position) {
            if(null != actionMode){
                return;
            }

            adapterListaZakupow.setSelection(view);
            actionMode = getActivity().startActionMode(new ActionMode.Callback() {

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
                    switch (item.getItemId()) {
                        case R.id.fpcUaktualnij:
                            final ModelProdukt model = adapterListaZakupow.getItem(position);
                            DialogFragmentProdukt
                                    .newInstance(FragmentProdukty.this, position,
                                            model.getNazwa(), model.getSklep(), model.getCena(),
                                            wspoldzielenieDanych.getSklepy())
                                    .show(getActivity().getSupportFragmentManager(),
                                            CONTEXT_UAKTUALNIJ);
                            mode.finish();
                            return true;
                        case R.id.fpcPokazCeny:
                            new AsyncTaskRzadanie(new AsyncTaskRzadanie.Gotowe() {
                                @Override
                                public void wykonaj(String odpowiedz) {
                                    Log.d("ziutek", odpowiedz);
                                    startActivity(new Intent(getContext(), ActivityKomunikat.class)
                                            .putExtra(ActivityKomunikat.IE_KOMUNIKAT, odpowiedz.substring(0, 20)));
                                }
                            }).execute(new Ustawienia(requireContext()).getAdres(""));
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
    public void onDialogNegativeClick(final DialogFragment dialog) {}

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
            final Ustawienia ustawienia = new Ustawienia(getContext());
            if (ustawienia.getPierwszyProdukt(true)) {
                NotificationMain.show(getContext(), getString(R.string.pierwszyProdukt));
                ustawienia.setPierwszyProdukt(false);
            }
            return;
        }
        if (FragmentProdukty.CONTEXT_UAKTUALNIJ.equals(tag)) {
            final ModelProdukt model = adapterListaZakupow.getItem(i);
            model.setNazwa(nazwa);
            model.setSklep(sklep);
            model.setCena(cena);
            adapterListaZakupow.sort(new ComparatorProduktPopularnosc());
        }
    }
}
