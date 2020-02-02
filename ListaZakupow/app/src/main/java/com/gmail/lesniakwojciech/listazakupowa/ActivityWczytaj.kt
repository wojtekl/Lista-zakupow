package com.gmail.lesniakwojciech.listazakupowa

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import androidx.appcompat.app.AppCompatActivity
import com.gmail.lesniakwojciech.commons.Permissions
import com.gmail.lesniakwojciech.commons.UkrytaWiadomosc
import com.gmail.lesniakwojciech.commons.Utils

class ActivityWczytaj : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val ustawienia = Ustawienia(this)
        if (ustawienia.getSkorkaCiemna(false)) {
            setTheme(R.style.AppThemeNight)
        }
        super.onCreate(savedInstanceState)
        if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
            Utils.ustawAnimacje(window, Explode(), Explode(), true)
        }

        if (Intent.ACTION_VIEW == intent.action) {
            wczytajListe(intent.data!!, ustawienia)
        }
    }

    private fun wczytajListe(uri: Uri, ustawienia: Ustawienia) {
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT && !Permissions.requestPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission_group.STORAGE
            )
        ) {
            finish()
            return
        }
        val wiadomosc = UkrytaWiadomosc(this)
        if (!wiadomosc.odczytaj(uri, 3331)) {
            finish()
            return
        }
        val repository = RepositoryProdukty(this)
        repository.get()
        if (ustawienia.getWczytanieOstatnie(0) < wiadomosc.data) {
            repository.merge(wiadomosc.tresc)
            ustawienia.setWczytanieOstatnie(wiadomosc.data)
            startActivity(
                Intent(this, ActivityMain::class.java).setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
            )
            finish()
            return
        }
        AlertDialog.Builder(
            this,
            if (ustawienia.getSkorkaCiemna(false)) R.style.AppThemeNight_AlertOverlay
            else R.style.AppTheme_AlertOverlay
        )
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.zaktualizuj_liste)
            .setMessage(R.string.potwierdz_aktualizacje)
            .setOnCancelListener { this.finish() }
            .setNegativeButton(R.string.nie) { dialog, which -> this.finish() }
            .setPositiveButton(R.string.tak) { di, i ->
                repository.merge(wiadomosc.tresc)
                startActivity(
                    Intent(applicationContext, ActivityMain::class.java).setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
                this.finish()
            }
            .create()
            .show()
    }
}
