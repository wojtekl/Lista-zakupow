package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ActivitySpolecznosc extends AppCompatActivity {
    private AdapterListaZakupow adapterListaZakupow;
    private ActionMode actionMode;

    private Context context;
    private final AdapterListaZakupow.OnItemClickListener onItemClickListener =
            new AdapterListaZakupow.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    /* if (null != actionMode) {
                        return;
                    } */
                }

                @Override
                public void onItemLongClick(final View view, final int position) {
                    if (null != actionMode) {
                        return;
                    }

                    adapterListaZakupow.setSelection(view);
                    actionMode = startActionMode(new ActionMode.Callback() {

                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            mode.getMenuInflater().inflate(R.menu.activityspolecznosccontext, menu);
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
                                case R.id.ascCena:
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
                                case R.id.ascDodajProdukt:
                                    final Ustawienia ustawienia = new Ustawienia(context);
                                    final List<ModelProdukt> listProdukty = new ArrayList<>();
                                    final List<ModelProdukt> listDoKupienia = new ArrayList<>();
                                    final List<ModelProdukt> listWKoszyku = new ArrayList<>();
                                    ParserProdukt.parse(ustawienia.getListy("[[],[],[]]"),
                                            listProdukty, listDoKupienia, listWKoszyku);
                                    final String nazwa = model.getNazwa();
                                    boolean istnieje = false;
                                    for (int i = 0, d = listWKoszyku.size(); d > i; ++i) {
                                        istnieje = istnieje || nazwa.equals(listWKoszyku.get(i).getNazwa());
                                    }
                                    for (int i = 0, d = listDoKupienia.size(); d > i; ++i) {
                                        istnieje = istnieje || nazwa.equals(listDoKupienia.get(i).getNazwa());
                                    }
                                    for (int i = 0, d = listProdukty.size(); d > i; ++i) {
                                        istnieje = istnieje || nazwa.equals(listProdukty.get(i).getNazwa());
                                    }
                                    if (!istnieje) {
                                        listProdukty.add(model);
                                    }
                                    ustawienia.setListy(ParserProdukt.toString(listProdukty,
                                            listDoKupienia, listWKoszyku));
                                    mode.finish();
                                    return true;
                                default:
                                    return false;
                            }
                        }

                        @Override
                        public void onDestroyActionMode(final ActionMode mode) {
                            actionMode = null;
                            adapterListaZakupow.clearSelection();
                        }
                    });
                }
            };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (new Ustawienia(this).getSkorkaCiemna(false)) {
            setTheme(R.style.AppThemeNight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityspolecznosc);
        context = this;

        adapterListaZakupow = new AdapterListaZakupow(new ArrayList<ModelProdukt>());
        adapterListaZakupow.setOnItemClickListener(onItemClickListener);
        pobierzProdukty(adapterListaZakupow, findViewById(R.id.activityspolecznosc));

        final RecyclerView recyclerView = findViewById(R.id.asListView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterListaZakupow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void pobierzProdukty(final AdapterListaZakupow adapterListaZakupow, final View view) {
        if (Permissions.hasInternet(this, view)) {
            final Ustawienia ustawienia = new Ustawienia(this);
            new AsyncTaskRzadanie(new AsyncTaskRzadanie.Listener() {
                @Override
                public void onPostExecute(final AsyncTaskRzadanie.RzadanieResponse response) {
                    if (response.isOK(true)) {
                        ParserProdukt.list(response.getMessage(), adapterListaZakupow.getDataset());
                        adapterListaZakupow.notifyDataSetChanged();
                    }
                }
            }).execute(WebAPI.pobierzListe(ustawienia.getAPIAdres("")
                    , ustawienia.getIdentyfikator("")));
        }
    }
}
