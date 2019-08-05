package com.gmail.lesniakwojciech.listazakupowa;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityWczytaj extends Activity {
    private List<ModelProdukt> wKoszyku, doKupienia, produkty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Ustawienia ustawienia = new Ustawienia(this);
        if (ustawienia.getSkorkaCiemna(false)) {
            setTheme(R.style.AppThemeNight);
        }
        super.onCreate(savedInstanceState);

        produkty = new ArrayList<>();
        doKupienia = new ArrayList<>();
        wKoszyku = new ArrayList<>();
        ParserProdukt.parse(ustawienia.getListy("[[],[],[]]"), produkty, doKupienia, wKoszyku);

        final Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            wczytajListe(intent.getData(), ustawienia);
        }
    }

    private void wczytajListe(final Uri uri, final Ustawienia ustawienia) {
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT
                && PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, getString(R.string.uprawnienia_do_wczytania_listy) + " "
                    + Permissions.getPermissionDescription(getPackageManager(),
                    Manifest.permission_group.STORAGE), Toast.LENGTH_LONG).show();
            this.finish();
            return;
        }

        final UkrytaWiadomosc wiadomosc = new UkrytaWiadomosc();
        if (!wiadomosc.odczytaj(this, uri)) {
            this.finish();
            return;
        }

        if (ustawienia.getWczytanieOstatnie(0) < wiadomosc.getData()) {
            ParserProdukt.merge(wiadomosc.getTresc(), wKoszyku, doKupienia, produkty);
            Collections.sort(doKupienia, new ComparatorProduktSklep());
            ustawienia.setListy(ParserProdukt.toString(produkty, doKupienia, wKoszyku));
            ustawienia.setWczytanieOstatnie(wiadomosc.getData());
            startActivity(new Intent(this, ActivityMain.class).setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            this.finish();
            return;
        }

        final Activity activity = this;
        new AlertDialog
                .Builder(activity, ustawienia.getSkorkaCiemna(false)
                ? R.style.AppThemeNight_AlertOverlay : R.style.AppTheme_AlertOverlay)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.zaktualizuj_liste)
                .setMessage(R.string.potwierdz_aktualizacje)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(final DialogInterface dialog) {
                        activity.finish();
                    }
                })
                .setNegativeButton(R.string.nie, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        activity.finish();
                    }
                })
                .setPositiveButton(R.string.tak, new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface di, final int i) {
                                ParserProdukt.merge(wiadomosc.getTresc(), wKoszyku, doKupienia, produkty);
                                Collections.sort(doKupienia, new ComparatorProduktSklep());
                                ustawienia.setListy(ParserProdukt.toString(produkty, doKupienia, wKoszyku));
                                startActivity(new Intent(getApplicationContext(), ActivityMain.class).setFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                activity.finish();
                            }
                        }
                )
                .create()
                .show();
    }
}
