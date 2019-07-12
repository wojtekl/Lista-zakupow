package com.gmail.lesniakwojciech.listazakupowa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.snackbar.Snackbar;

public class ActivityUstawienia extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (new Ustawienia(this).getSkorkaCiemna(false)) {
            setTheme(R.style.AppThemeNight);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityustawienia);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new FragmentUstawienia())
                .commit();
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class FragmentUstawienia extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            findPreference(Ustawienia.SP_SKORKA_CIEMNA).setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    Snackbar.make(requireView(), R.string.uruchom_aplikacje_ponownie, Snackbar.LENGTH_LONG).show();
                    return true;
                }
            });

            findPreference("pokazInstrukcje").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final Context context = requireContext();
                    if (Permissions.hasInternet(context, getView())) {
                        new AsyncTaskRzadanie(new AsyncTaskRzadanie.Listener() {
                            @Override
                            public void onPostExecute(final AsyncTaskRzadanie.RzadanieResponse response) {
                                if (response.isOK(true)) {
                                    final String message = response.getMessage();
                                    if (Patterns.WEB_URL.matcher(message).matches()) {
                                        final Intent intent = new Intent(Intent.ACTION_VIEW,
                                                Uri.parse(message));
                                        if (Permissions.canStart(intent, context.getPackageManager())) {
                                            startActivity(intent);
                                        }
                                    }
                                }
                            }
                        }).execute(getString(R.string.ADRES_INSTRUKCJA));
                    }
                    return true;
                }
            });

            findPreference("wyczyscWszystko").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final Ustawienia ustawienia = new Ustawienia(requireContext());
                    new AlertDialog.Builder(getActivity(), ustawienia.getSkorkaCiemna(false)
                            ? R.style.AppThemeNight_AlertOverlay : R.style.AppTheme_AlertOverlay)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.wyczysc_wszystko)
                            .setMessage(R.string.potwierdz_czyszczenie)
                            .setNegativeButton(R.string.nie, new DialogInterface.OnClickListener() {
                                        public void onClick(final DialogInterface di, final int i) {
                                        }
                                    }
                            )
                            .setPositiveButton(R.string.tak, new DialogInterface.OnClickListener() {
                                        public void onClick(final DialogInterface di, final int i) {
                                            ustawienia.setListy("[[],[],[]]");
                                        }
                                    }
                            )
                            .create()
                            .show();
                    return true;
                }
            });

            findPreference("zdobadzZetony").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final Context context = requireContext();
                    final View view = getView();
                    if (Permissions.hasInternet(context, view)) {
                        Reklamy.rewardedVideoAd(context, new Reklamy.Listener() {
                            @Override
                            public void onRewarded(final int amount) {
                                ((EditTextPreference) findPreference("zetony"))
                                        .setText(String.valueOf(new Zetony(context)
                                                .dodajZetony(amount * Zetony.ZETONY_REWARDEDVIDEOAD, view)));
                            }
                        });
                    }
                    return true;
                }
            });

            findPreference("udostepnijAplikacje").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final Intent intent = new Intent(Intent.ACTION_SENDTO)
                            .setData(Uri.parse("smsto:"))
                            .putExtra("sms_body", getString(R.string.udostepnij_tekst)
                                    + getString(R.string.ADRES_PLAY_APLIKACJA));
                    if (Permissions.canStart(intent, requireContext().getPackageManager())) {
                        startActivity(intent);
                    }
                    return true;
                }
            });

            findPreference("inneAplikacje").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse(getString(R.string.ADRES_PLAY_APLIKACJE)))
                            .setPackage("com.android.vending");
                    if (Permissions.canStart(intent, requireContext().getPackageManager())) {
                        startActivity(intent);
                    }
                    return true;
                }
            });

            findPreference("produktySpolecznosci").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final Context context = requireContext();
                    final View view = getView();
                    if (Permissions.hasInternet(context, view) && new Zetony(context)
                            .sprawdzZetony(Zetony.ZETONY_PRODUKTY_SPOLECZNOSCI, true, view)) {
                        startActivity(new Intent(context, ActivitySpolecznosc.class));
                    }
                    return true;
                }
            });
        }
    }
}