package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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

public class FragmentDoKupienia
        extends Fragment
        implements DialogFragmentProdukt.DialogListener {
    private static final String ITEM_LONG_CLICK = "fdkItemLongClick";

    private AdapterListaZakupow adapterListaZakupow;
    private AdapterListaZakupow wKoszyku, produkty;

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
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater mi) {
        mi.inflate(R.menu.fragmentdokupieniaoptions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem mi) {
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
            wKoszyku.addItem(adapterListaZakupow.getItem(position));
            wKoszyku.sort(new ComparatorProduktCena());
            adapterListaZakupow.removeItem(position);
        }

        @Override
        public void onItemLongClick(final View view, final int position) {
            final ModelProdukt model = adapterListaZakupow.getItem(position);
            DialogFragmentProdukt
                    .newInstance(FragmentDoKupienia.this, position, model.getNazwa(),
                            model.getSklep(), model.getCena(),
                            ((IWspoldzielenieDanych)requireActivity()).getSklepy())
                    .show(requireActivity().getSupportFragmentManager(), ITEM_LONG_CLICK);
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
            final PackageManager packageManager = requireContext().getPackageManager();
            final Intent intent = new Intent(Intent.ACTION_SENDTO)
                    .setData(Uri.parse("smsto:"))
                    .putExtra("sms_body", stringBuilder.toString());
            final ActivityInfo activityInfo = intent.resolveActivityInfo(packageManager,
                    intent.getFlags());
            if(null != activityInfo && activityInfo.exported) {
                startActivity(intent);
            }
        }
    }

    private void wyslijListe(final Context context) {
        if(1 > adapterListaZakupow.getItemCount()) {
            return;
        }

        final UkrytaWiadomosc wiadomosc = new UkrytaWiadomosc();
        wiadomosc.setTresc(ParserProdukt.listToJSON(adapterListaZakupow.getDataset()));

        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_SEND)
                .setData(Uri.parse("smsto:"))
                .putExtra(Intent.EXTRA_STREAM, wiadomosc.przygotuj(context))
                .setType("image/jpeg");
        final ActivityInfo activityInfo = intent.resolveActivityInfo(packageManager,
                intent.getFlags());
        if(null != activityInfo && activityInfo.exported) {
            startActivityForResult(intent, 0);
            final Ustawienia ustawienia = new Ustawienia(context);
            if(ustawienia.getPierwszeWyslanie(true)) {
                NotificationMain.show(context, getString(R.string.pierwszeWyslanie));
                ustawienia.setPierwszeWyslanie(false);
            }
        }
    }

    @Override
    public void onDialogNegativeClick(final DialogFragment dialog) {}

    @Override
    public void onDialogPositiveClick(final DialogFragment dialog, final int i, final String nazwa,
                                      final String sklep, final double cena) {
        if (FragmentDoKupienia.ITEM_LONG_CLICK.equals(dialog.getTag())) {
            final ModelProdukt model = adapterListaZakupow.getItem(i);
            model.setNazwa(nazwa);
            model.setSklep(sklep);
            model.setCena(cena);
            adapterListaZakupow.sort(new ComparatorProduktSklep());
        }
    }
}
