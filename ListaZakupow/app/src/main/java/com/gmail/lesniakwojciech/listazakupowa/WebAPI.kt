package com.gmail.lesniakwojciech.listazakupowa

import android.content.Context
import android.util.Patterns
import android.view.View
import com.gmail.lesniakwojciech.commons.AsyncTaskRzadanie
import com.gmail.lesniakwojciech.commons.Permissions
import com.google.android.material.snackbar.Snackbar

class WebAPI {
    companion object {
        private const val KONCOWKA_PRODUKT = "/produkt?"
        private const val KONCOWKA_PRODUKTY = "/produkty?"
        private const val PARAMETER_IDENTYFIKATOR = "identyfikator="
        private const val PARAMETER_NAZWA = "nazwa="
        private const val PARAMETER_SKLEP = "sklep="
        private const val PARAMETER_CENA = "cena="

        fun ustawAPIAdress(context: Context) {
            AsyncTaskRzadanie(object : AsyncTaskRzadanie.Listener {
                override fun onPostExecute(response: AsyncTaskRzadanie.Response) {
                    val message = response.message
                    if (response.isOK(true) && Patterns.WEB_URL.matcher(message).matches()) {
                        Ustawienia(context).setAPIAdres(message)
                    }
                }
            }).execute(context.getString(R.string.ADRES_POSREDNIK))
        }

        fun udostepnijCeny(
            ustawienia: Ustawienia,
            model: ModelProdukt,
            listener: AsyncTaskRzadanie.Listener
        ) {
            AsyncTaskRzadanie(listener).execute(
                ustawienia.getAPIAdres("") + KONCOWKA_PRODUKT,
                AsyncTaskRzadanie.POST,
                ustawienia.getKraj("en"),
                PARAMETER_NAZWA + model.nazwa +
                        "&" + PARAMETER_SKLEP + model.sklep +
                        "&" + PARAMETER_CENA + model.cena +
                        "&" + PARAMETER_IDENTYFIKATOR + ustawienia.getIdentyfikator("")
            )
        }

        fun pobierzCeny(
            ustawienia: Ustawienia,
            nazwa: String,
            listener: AsyncTaskRzadanie.Listener
        ) {
            AsyncTaskRzadanie(listener).execute(
                ustawienia.getAPIAdres("") + KONCOWKA_PRODUKT +
                        PARAMETER_NAZWA + nazwa +
                        "&" + PARAMETER_IDENTYFIKATOR + ustawienia.getIdentyfikator("")
            )
        }

        fun pobierzListe(
            ustawienia: Ustawienia,
            listener: AsyncTaskRzadanie.Listener
        ) {
            AsyncTaskRzadanie(listener).execute(
                ustawienia.getAPIAdres("") + KONCOWKA_PRODUKTY +
                        PARAMETER_IDENTYFIKATOR + ustawienia.getIdentyfikator(""),
                AsyncTaskRzadanie.GET,
                ustawienia.getKraj("en")
            )
        }

        fun filtruj(odpowiedz: String): String {
            val poczatek = odpowiedz.indexOf("<p>")
            return if (0 < poczatek) {
                odpowiedz.substring(poczatek + 3, odpowiedz.indexOf("</p>"))
            } else {
                odpowiedz
            }
        }

        fun wlaczInternet(context: Context, view: View) {
            if (Ustawienia(context).getCenyUdostepniaj(false)
                && !Permissions.hasInternet(context, view)
            ) {
                Snackbar.make(view, R.string.wlacz_internet, Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
