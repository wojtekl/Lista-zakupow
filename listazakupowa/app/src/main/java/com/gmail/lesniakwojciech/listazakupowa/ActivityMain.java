package com.gmail.lesniakwojciech.listazakupowa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityMain
        extends AppCompatActivity
        implements IWspoldzielenieDanych {
    private AdView mAdView;
    private AdapterListaZakupow wKoszyku, doKupienia, produkty;
    private ArrayAdapter<String> sklepy;

    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activitymain);
        NotificationMain.createNotificationChannel(this);

        Reklamy.initialize(this);
        mAdView = findViewById(R.id.adView);
        final View main = findViewById(R.id.main);
        Reklamy.banner(mAdView, main, this);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarNavigation(toolbar);

        final ViewPager viewPager = findViewById(R.id.alzViewPager);
        viewPager.setAdapter(new FPagerAdapterMain(getSupportFragmentManager(), this));
        final TabLayout tabLayout = findViewById(R.id.alzTabLayout);
        FPagerAdapterMain.tabLayout(viewPager, tabLayout, getResources());

        viewPager.setCurrentItem(1);

        if(Build.VERSION_CODES.M <= Build.VERSION.SDK_INT &&
                PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Permissions.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.uprawnieniaDoWczytaniaListy) + " "
                            + Permissions.getPermissionDescription(getPackageManager(),
                            Manifest.permission_group.STORAGE));
        }

        produkty = new AdapterListaZakupow(new ArrayList<ModelProdukt>());
        doKupienia = new AdapterListaZakupow(new ArrayList<ModelProdukt>());
        wKoszyku = new AdapterListaZakupow(new ArrayList<ModelProdukt>());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdView.resume();
        Reklamy.resume(this);

        final Ustawienia ustawienia = new Ustawienia(this);

        String stringLista = ustawienia.getListy("[[],[],[]]");
        if (ustawienia.getPierwszeUruchomienie(true)) {
            ustawienia.setPierwszeUruchomienie(false);
            if ("[[],[],[]]".equals(stringLista)) {
                stringLista = getString(R.string.pierwszaLista);
            }
            NotificationMain.show(this, getString(R.string.pierwszeUruchomienie));
        }
        final List<ModelProdukt> listProdukty = produkty.getDataset();
        final List<ModelProdukt> listDoKupienia = doKupienia.getDataset();
        final List<ModelProdukt> listWKoszyku = wKoszyku.getDataset();
        listProdukty.clear();
        listDoKupienia.clear();
        listWKoszyku.clear();
        ParserProdukt.parse(stringLista, listProdukty, listDoKupienia, listWKoszyku);
        przygotujSklepy(listProdukty, listDoKupienia, listWKoszyku);

        new AsyncTaskRzadanie(new AsyncTaskRzadanie.Gotowe() {
            @Override
            public void wykonaj(String odpowiedz) {
                ustawienia.setAdres(odpowiedz);
            }
        }).execute(getString(R.string.adres));
    }

    @Override
    protected void onPause() {
        new Ustawienia(this).setListy(ParserProdukt.toString(produkty.getDataset(),
                doKupienia.getDataset(), wKoszyku.getDataset()));

        AWProviderListaZakupow.appWidgetUpdate(this);
        createShortcuts();

        mAdView.pause();
        Reklamy.pause(this);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mAdView.destroy();
        Reklamy.destroy(this);

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        Reklamy.interstitialAd(this);
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

    private void przygotujSklepy(final List<ModelProdukt> produkty, final List<ModelProdukt> doKupienia,
                                 final List<ModelProdukt> wKoszyku) {
        final List<String> listSklepy = new ArrayList<>();
        for(int i = 0, d = produkty.size(); i < d; ++i) {
            final String sklep = produkty.get(i).getSklep();
            if(!listSklepy.contains(sklep)) {
                listSklepy.add(sklep);
            }
        }
        for(int i = 0, d = doKupienia.size(); i < d; ++i) {
            final String sklep = doKupienia.get(i).getSklep();
            if(!listSklepy.contains(sklep)) {
                listSklepy.add(sklep);
            }
        }
        for(int i = 0, d = wKoszyku.size(); i < d; ++i) {
            final String sklep = wKoszyku.get(i).getSklep();
            if(!listSklepy.contains(sklep)) {
                listSklepy.add(sklep);
            }
        }
        sklepy = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                listSklepy);
    }

    private void toolbarNavigation(final Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_launcher);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final PackageManager packageManager = getPackageManager();
                final Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(getString(R.string.googlePlayLink)))
                        .setPackage("com.android.vending");
                final ActivityInfo activityInfo = intent.resolveActivityInfo(packageManager,
                        intent.getFlags());
                if(null != activityInfo && activityInfo.exported) {
                    startActivity(intent);
                }
            }
        });
    }

    private void createShortcuts() {
        if (Build.VERSION_CODES.N_MR1 <= Build.VERSION.SDK_INT) {
            int l = doKupienia.getItemCount();
            if (0 < l) {
                final StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(getResources().getString(R.string.doKupienia)).append(":\n");
                --l;
                for (int i = 0; i < l; ++i) {
                    stringBuilder.append(doKupienia.getItem(i).getNazwa()).append(",\n");
                }
                stringBuilder.append(doKupienia.getItem(l).getNazwa()).append(".");

                final PackageManager packageManager = this.getPackageManager();
                final Intent intent = new Intent(Intent.ACTION_SENDTO)
                        .setData(Uri.parse("smsto:"))
                        .putExtra("sms_body", stringBuilder.toString());
                final ActivityInfo activityInfo = intent.resolveActivityInfo(packageManager,
                        intent.getFlags());
                if (null != activityInfo && activityInfo.exported) {
                    getSystemService(ShortcutManager.class).setDynamicShortcuts(Arrays.asList(
                            new ShortcutInfo
                                    .Builder(this, getString(R.string.app_name))
                                    .setShortLabel(getString(R.string.wyslijListeSMSem))
                                    .setLongLabel(getString(R.string.wyslijListeSMSem))
                                    .setIcon(Icon.createWithResource(this,
                                            R.drawable.ic_launcher))
                                    .setIntent(intent)
                                    .build()));
                }
            }
        }
    }
}
