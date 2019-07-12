package com.gmail.lesniakwojciech.listazakupowa;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityKomunikat
        extends AppCompatActivity {
    public static final String IE_KOMUNIKAT = "komunikat";

    @Override
    protected void onCreate(final Bundle bundle) {
        if (new Ustawienia(this).getSkorkaCiemna(false)) {
            setTheme(R.style.AppThemeNight);
        }
        super.onCreate(bundle);
        /*setContentView(R.layout.activityinstrukcje);

        ((TextView) findViewById(R.id.aiTextView)).setText(getIntent().getStringExtra(
                NotificationMain.IE_KOMUNIKAT));

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        new AlertDialog
                .Builder(this)
                .setIcon(R.drawable.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage(getIntent().getStringExtra(IE_KOMUNIKAT))
                .setNegativeButton(R.string.powroc, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(final DialogInterface dialog) {
                        finish();
                    }
                })
                .create()
                .show();
    }

    /*@Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activitykomunikatoptions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem mi) {
        switch (mi.getItemId()) {
            case R.id.akPowroc:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(mi);
        }
    }*/
}
