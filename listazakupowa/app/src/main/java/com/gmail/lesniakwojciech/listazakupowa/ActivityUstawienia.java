package com.gmail.lesniakwojciech.listazakupowa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

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

            findPreference("pokazInstrukcje").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final Ustawienia ustawienia = new Ustawienia(requireContext());
                    ustawienia.setPierwszeUruchomienie(true);
                    ustawienia.setPierwszyProdukt(true);
                    ustawienia.setPierwszeWyslanie(true);
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
                    Reklamy.rewardedVideoAd(getContext(), new Reklamy.Listener() {
                        @Override
                        public void onRewarded(final int amount) {
                            new Zetony(getContext()).dodajZetony(
                                    amount * Zetony.ZETONY_REWARDEDVIDEOAD, getView());
                            ((EditTextPreference)findPreference("zetony"))
                                    .setText(String.valueOf(amount * Zetony.ZETONY_REWARDEDVIDEOAD));
                        }
                    });
                    return true;
                }
            });

            findPreference("udostepnij").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final Intent intent = new Intent(Intent.ACTION_SENDTO)
                            .setData(Uri.parse("smsto:"))
                            .putExtra("sms_body", getString(R.string.udostepnijTekst)
                                    + getString(R.string.udostepnijAdres));
                    final ActivityInfo activityInfo = intent.resolveActivityInfo
                            (requireContext().getPackageManager(), intent.getFlags());
                    if(null != activityInfo && activityInfo.exported) {
                        startActivity(intent);
                    }
                    return true;
                }
            });

            findPreference("googlePlay").setOnPreferenceClickListener(new Preference
                    .OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final Intent intent = new Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse(getString(R.string.googlePlayLink)))
                            .setPackage("com.android.vending");
                    final ActivityInfo activityInfo = intent.resolveActivityInfo
                            (requireContext().getPackageManager(), intent.getFlags());
                    if(null != activityInfo && activityInfo.exported) {
                        startActivity(intent);
                    }
                    return true;
                }
            });
        }
    }
}