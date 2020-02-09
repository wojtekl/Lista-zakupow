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

        val isActionSend = Intent.ACTION_SEND == intent.action
        if (isActionSend || Intent.ACTION_ATTACH_DATA == intent.action) {
            wczytajListe(
                (if (isActionSend) intent.extras?.get(Intent.EXTRA_STREAM)
                else intent.data) as Uri, ustawienia
            )
        }
    }

    private fun wczytajListe(uri: Uri, ustawienia: Ustawienia) {
        if (Build.VERSION_CODES.M <= Build.VERSION.SDK_INT && !Permissions.requestPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission_group.STORAGE,
                true
            )
        ) {
            return
        }
        val wiadomosc = UkrytaWiadomosc(this)
        if (!wiadomosc.odczytaj(uri, 3331)) {
            finish()
            return
        }
        val repository = RepositoryListaZakupow(this)
        repository.get()
        if (ustawienia.getWczytanieOstatnie(0) < wiadomosc.data) {
            ustawienia.setWczytanieOstatnie(wiadomosc.data)
            aktualizujListe(repository, wiadomosc.tresc)
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
            .setOnCancelListener { finish() }
            .setNegativeButton(R.string.nie) { dialog, which -> finish() }
            .setPositiveButton(R.string.tak) { di, i ->
                aktualizujListe(
                    repository,
                    wiadomosc.tresc
                )
            }
            .create()
            .show()
    }

    private fun aktualizujListe(repository: RepositoryListaZakupow, lista: String) {
        repository.merge(lista)
        startActivity(
            Intent(this, ActivityMain::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
        finish()
    }
}
