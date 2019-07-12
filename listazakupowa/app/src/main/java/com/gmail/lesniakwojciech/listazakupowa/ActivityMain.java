package com.gmail.lesniakwojciech.listazakupowa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ActivityMain
        extends AppCompatActivity
        implements IWspoldzielenieDanych {
    private AdapterListaZakupow wKoszyku, doKupienia, produkty;
    private ArrayAdapter<String> sklepy;

    @Override
    protected void onCreate(final Bundle bundle) {
        final Ustawienia ustawienia = new Ustawienia(this);
        if (ustawienia.getSkorkaCiemna(false)) {
            setTheme(R.style.AppThemeNight);
        }
        super.onCreate(bundle);
        setContentView(R.layout.activitymain);
        NotificationMain.createNotificationChannel(this);

        Reklamy.initialize(this);
        Reklamy.banner((AdView) findViewById(R.id.adView),
                !new Zetony(this).sprawdzZetony(Zetony.ZETON_BANNER_WYLACZ, false, null),
                new Reklamy.Listener() {
                    @Override
                    public void onRewarded(final int amount) {
                        new Zetony(getApplicationContext()).dodajZetony(Zetony.ZETONY_BANNER, findViewById(R.id.main));
                    }
                });

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewPager viewPager = findViewById(R.id.alzViewPager);
        viewPager.setAdapter(new FPagerAdapterMain(getSupportFragmentManager(), getResources()));
        final TabLayout tabLayout = findViewById(R.id.alzTabLayout);
        FPagerAdapterMain.tabLayout(viewPager, tabLayout, getResources());

        viewPager.setCurrentItem(1);

        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
            Permissions.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.uprawnienia_do_wczytania_listy) + " "
                            + Permissions.getPermissionDescription(getPackageManager(),
                            Manifest.permission_group.STORAGE));
        }

        produkty = new AdapterListaZakupow(new ArrayList<ModelProdukt>());
        doKupienia = new AdapterListaZakupow(new ArrayList<ModelProdukt>());
        wKoszyku = new AdapterListaZakupow(new ArrayList<ModelProdukt>());

        if (TextUtils.isEmpty(ustawienia.getIdentyfikator(""))) {
            ustawienia.setIdentyfikator(UUID.randomUUID().toString());
        }
        new AsyncTaskRzadanie(new AsyncTaskRzadanie.Listener() {
            @Override
            public void onPostExecute(final AsyncTaskRzadanie.RzadanieResponse response) {
                if (response.isOK(true)) {
                    final String message = response.getMessage();
                    if (Patterns.WEB_URL.matcher(message).matches()) {
                        ustawienia.setAPIAdres(message);
                    }
                }
            }
        }).execute(getString(R.string.ADRES_POSREDNIK));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Reklamy.resume(this);

        final List<ModelProdukt> listProdukty = produkty.getDataset();
        final List<ModelProdukt> listDoKupienia = doKupienia.getDataset();
        final List<ModelProdukt> listWKoszyku = wKoszyku.getDataset();
        listProdukty.clear();
        listDoKupienia.clear();
        listWKoszyku.clear();
        ParserProdukt.parse(new Ustawienia(this).getListy(getString(R.string.lista_poczatkowa)),
                listProdukty, listDoKupienia, listWKoszyku);
        produkty.notifyDataSetChanged();
        doKupienia.notifyDataSetChanged();
        wKoszyku.notifyDataSetChanged();
        sklepy = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                ParserProdukt.sklepy(listProdukty, listDoKupienia, listWKoszyku));
    }

    @Override
    protected void onPause() {
        new Ustawienia(this).setListy(ParserProdukt.toString(produkty.getDataset(),
                doKupienia.getDataset(), wKoszyku.getDataset()));

        AWProviderListaZakupow.update(this);
        createShortcuts();

        Reklamy.pause(this);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Reklamy.destroy(this);

        super.onDestroy();
    }

    @Override
    public AdapterListaZakupow getProdukty() {
        return produkty;
    }

    @Override
    public void setProdukty(final AdapterListaZakupow produkty) {
        this.produkty = produkty;
    }

    @Override
    public AdapterListaZakupow getDoKupienia() {
        return doKupienia;
    }

    @Override
    public void setDoKupienia(final AdapterListaZakupow doKupienia) {
        this.doKupienia = doKupienia;
    }

    @Override
    public AdapterListaZakupow getWKoszyku() {
        return wKoszyku;
    }

    @Override
    public void setWKoszyku(final AdapterListaZakupow wKoszyku) {
        this.wKoszyku = wKoszyku;
    }

    @Override
    public ArrayAdapter<String> getSklepy() {
        return sklepy;
    }

    @Override
    public void setSklepy(ArrayAdapter<String> sklepy) {
        this.sklepy = sklepy;
    }

    private void createShortcuts() {
        if (Build.VERSION_CODES.N_MR1 <= Build.VERSION.SDK_INT) {
            final String lista = ParserProdukt.prepare(doKupienia.getDataset(), getString(R.string.do_kupienia));
            if (null != lista) {
                final Intent intent = Wiadomosci.tekst(this.getPackageManager(), lista);
                if (null != intent) {
                    getSystemService(ShortcutManager.class).setDynamicShortcuts(Arrays.asList(
                            new ShortcutInfo
                                    .Builder(this, getString(R.string.app_name))
                                    .setShortLabel(getString(R.string.wyslij_liste_SMSem))
                                    .setLongLabel(getString(R.string.wyslij_liste_SMSem))
                                    .setIcon(Icon.createWithResource(this,
                                            R.drawable.ic_launcher))
                                    .setIntent(intent)
                                    .build()));
                }
            }
        }
    }
}
