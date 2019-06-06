package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class FragmentDoKupienia
        extends Fragment
        implements DialogFragmentProdukt.DialogListener {
    private static final String CONTEXT_UAKTUALNIJ = "fdcUaktualnij";

    private AdapterListaZakupow adapterListaZakupow;
    private AdapterListaZakupow wKoszyku, produkty;

    private ActionMode actionMode;

    @Override
    public View onCreateView(@NonNull final LayoutInflater li, final ViewGroup vg, final Bundle bundle) {
        final View view = li.inflate(R.layout.fragmentprodukty, vg, false);

        final IWspoldzielenieDanych wspoldzielenieDanych = (IWspoldzielenieDanych) requireActivity();
        wKoszyku = wspoldzielenieDanych.getWKoszyku();
        produkty = wspoldzielenieDanych.getProdukty();

        adapterListaZakupow = wspoldzielenieDanych.getDoKupienia();
        adapterListaZakupow.setOnItemClickListener(onItemClickListener);

        final RecyclerView recyclerView = view.findViewById(R.id.fpListView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterListaZakupow);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_send);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                wyslijListeSMSem();
            }
        });

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onPause() {
        if(null != actionMode) {
            actionMode.finish();
        }

        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull final MenuInflater mi) {
        mi.inflate(R.menu.fragmentdokupieniaoptions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem mi) {
        switch (mi.getItemId()) {
            case R.id.fdkoWyslijListeSMSem:
                wyslijListeSMSem();
                return true;
            case R.id.fdkoWyslijListe:
                wyslijListe(getContext());
                return true;
            case R.id.fdkoWyczysc:
                produkty.addAll(adapterListaZakupow.getDataset());
                produkty.sort(new ComparatorProduktPopularnosc());
                adapterListaZakupow.clear();
                return true;
            default:
                return super.onOptionsItemSelected(mi);
        }
    }

    private final AdapterListaZakupow.OnItemClickListener onItemClickListener =
            new AdapterListaZakupow.OnItemClickListener() {
        @Override
        public void onItemClick(final int position) {
            if(null != actionMode){
                return;
            }

            wKoszyku.addItem(adapterListaZakupow.getItem(position));
            wKoszyku.sort(new ComparatorProduktCena());
            adapterListaZakupow.removeItem(position);
        }

        @Override
        public void onItemLongClick(final View view, final int position) {
            if(null != actionMode){
                return;
            }

            adapterListaZakupow.setSelection(view);
            actionMode = requireActivity().startActionMode(new ActionMode.Callback() {

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.fragmentdokupieniacontext, menu);
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
                        case R.id.fdcCena:
                            if(new Zetony(getContext()).sprawdzZetony(Zetony.ZETONY_CENA_ZOBACZ,
                                    true, getView())) {
                                new AsyncTaskRzadanie(new AsyncTaskRzadanie.Gotowe() {
                                    @Override
                                    public void wykonaj(final String odpowiedz) {
                                        if(!TextUtils.isEmpty(odpowiedz)) {
                                            startActivity(new Intent(getContext(), ActivityKomunikat.class)
                                                    .putExtra(ActivityKomunikat.IE_KOMUNIKAT,
                                                            model.getNazwa() + ":\n"
                                                                    + WebAPI.filtruj(odpowiedz)));
                                        }
                                    }
                                }).execute(WebAPI.pobierzCeny(model.getNazwa()));
                            }
                            mode.finish();
                            return true;
                        case R.id.fdcUaktualnij:
                            DialogFragmentProdukt
                                    .newInstance(FragmentDoKupienia.this, position, model.getNazwa(),
                                            model.getSklep(), model.getCena(),
                                            ((IWspoldzielenieDanych)requireActivity()).getSklepy())
                                    .show(requireActivity().getSupportFragmentManager(), CONTEXT_UAKTUALNIJ);
                            mode.finish();
                            return true;
                        case R.id.fdcUsun:
                            produkty.addItem(adapterListaZakupow.getItem(position));
                            produkty.sort(new ComparatorProduktPopularnosc());
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

    private void wyslijListeSMSem() {
        int l = adapterListaZakupow.getItemCount();
        if (0 < l) {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getResources().getString(R.string.doKupienia)).append(":\n");
            --l;
            for (int i = 0; i < l; ++i) {
                stringBuilder.append(adapterListaZakupow.getItem(i).getNazwa()).append(",\n");
            }
            stringBuilder.append(adapterListaZakupow.getItem(l).getNazwa()).append(".");
            Wiadomosci.tekst(requireContext(), stringBuilder.toString());
        }
    }

    private void wyslijListe(final Context context) {
        if(1 > adapterListaZakupow.getItemCount()
                || !new Zetony(getContext()).sprawdzZetony(Zetony.ZETONY_WYSLIJLISTE,
                true, getView())) {
            return;
        }

        final UkrytaWiadomosc wiadomosc = new UkrytaWiadomosc();
        wiadomosc.setTresc(ParserProdukt.listToJSON(adapterListaZakupow.getDataset()));

        if(Wiadomosci.obraz(context, wiadomosc.przygotuj(context))) {
            final Ustawienia ustawienia = new Ustawienia(context);
            if(ustawienia.getPierwszeWyslanie(true)) {
                NotificationMain.show(context, context.getString(R.string.pierwszeWyslanie));
                ustawienia.setPierwszeWyslanie(false);
            }
        }
    }

    @Override
    public void onDialogNegativeClick(final DialogFragment dialog) {}

    @Override
    public void onDialogPositiveClick(final DialogFragment dialog, final int i, final String nazwa,
                                      final String sklep, final double cena) {
        if (FragmentDoKupienia.CONTEXT_UAKTUALNIJ.equals(dialog.getTag())) {
            final ModelProdukt model = adapterListaZakupow.getItem(i);
            if(new Ustawienia(requireContext()).getUdostepniajCeny(false) && model.getCena() != cena) {
                new AsyncTaskRzadanie(new AsyncTaskRzadanie.Gotowe() {
                    @Override
                    public void wykonaj(final String odpowiedz) {
                        new Zetony(getContext()).dodajZetony(Zetony.ZETONY_CENA_UDOSTEPNIENIE, null);
                    }
                }).execute(WebAPI.udostepnijCeny(getContext(), nazwa, sklep, cena));
            }
            model.setNazwa(nazwa);
            model.setSklep(sklep);
            model.setCena(cena);
            adapterListaZakupow.sort(new ComparatorProduktSklep());
        }
    }
}
