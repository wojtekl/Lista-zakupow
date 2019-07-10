package com.gmail.lesniakwojciech.listazakupowa;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.snackbar.Snackbar;

public class ActivityUstawienia extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if(new Ustawienia(this).getTrybNocny(false)) {
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

            findPreference(Ustawienia.SP_TRYB_NOCNY).setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    Snackbar.make(requireView(), R.string.uruchomAplikacjePonownie, Snackbar.LENGTH_LONG).show();
                    return true;
                }
            });

            findPreference("pokazInstrukcje").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    new AsyncTaskRzadanie(new AsyncTaskRzadanie.Listener() {
                        @Override
                        public void onPostExecute(final AsyncTaskRzadanie.RzadanieResponse response) {
                            if(response.isOK(true)) {
                                final Intent intent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(response.getMessage()));
                                if(Permissions.canStart(intent, requireContext().getPackageManager())) {
                                    startActivity(intent);
                                }
                            }
                        }
                    }).execute(getString(R.string.adresInstrukcja));
                    return true;
                }
            });

            findPreference("wyczyscWszystko").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
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
                                            final Ustawienia ustawienia = new Ustawienia(requireContext());
                                            ustawienia.setListy("[[],[],[]]");
                                        }
                                    }
                            )
                            .create()
                            .show();
                    return true;
                }
            });

            findPreference("zetonyZaReklame").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final Context context = getContext();
                    Reklamy.rewardedVideoAd(context, new Reklamy.Listener() {
                        @Override
                        public void onRewarded(final int amount) {
                            ((EditTextPreference)findPreference("zetony"))
                                    .setText(String.valueOf(new Zetony(context)
                                            .dodajZetony(amount * Zetony.ZETONY_REWARDEDVIDEOAD, getView())));
                        }
                    });
                    return true;
                }
            });

            findPreference("udostepnijAplikacje").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final Intent intent = new Intent(Intent.ACTION_SENDTO)
                            .setData(Uri.parse("smsto:"))
                            .putExtra("sms_body", getString(R.string.udostepnijTekst)
                                    + getString(R.string.adresPlayAplikacja));
                    if(Permissions.canStart(intent, requireContext().getPackageManager())) {
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
                            .setData(Uri.parse(getString(R.string.adresPlayAplikacje)))
                            .setPackage("com.android.vending");
                    if(Permissions.canStart(intent, requireContext().getPackageManager())) {
                        startActivity(intent);
                    }
                    return true;
                }
            });

            findPreference("produktySpolecznosci").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    startActivity(new Intent(getContext(), ActivitySpolecznosc.class));
                    return true;
                }
            });
        }
    }
}