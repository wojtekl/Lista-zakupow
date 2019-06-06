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
import android.text.TextUtils;
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
        if(new Ustawienia(this).getTrybNocny(false)) {
            setTheme(R.style.AppThemeNight);
        }
        super.onCreate(bundle);
        setContentView(R.layout.activitymain);
        NotificationMain.createNotificationChannel(this);

        Reklamy.initialize(this);
        Reklamy.banner((AdView)findViewById(R.id.adView),
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
        viewPager.setAdapter(new FPagerAdapterMain(getSupportFragmentManager(), this));
        final TabLayout tabLayout = findViewById(R.id.alzTabLayout);
        FPagerAdapterMain.tabLayout(viewPager, tabLayout, getResources());

        viewPager.setCurrentItem(1);

        if(Build.VERSION_CODES.M <= Build.VERSION.SDK_INT) {
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

        Reklamy.resume(this);

        final Ustawienia ustawienia = new Ustawienia(this);

        String stringLista = ustawienia.getListy("[[],[],[]]");
        if (ustawienia.getPierwszeUruchomienie(true)) {
            ustawienia.setPierwszeUruchomienie(false);
            if(TextUtils.isEmpty(ustawienia.getIdentyfikator(""))) {
                ustawienia.setIdentyfikator(UUID.randomUUID().toString());
            }
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
        produkty.notifyDataSetChanged();
        doKupienia.notifyDataSetChanged();
        wKoszyku.notifyDataSetChanged();
        sklepy = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                ParserProdukt.sklepy(listProdukty, listDoKupienia, listWKoszyku));

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
