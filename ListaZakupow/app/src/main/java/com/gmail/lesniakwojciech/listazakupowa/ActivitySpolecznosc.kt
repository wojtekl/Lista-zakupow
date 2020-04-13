package com.gmail.lesniakwojciech.listazakupowa

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.lesniakwojciech.commons.*
import com.gmail.lesniakwojciech.commons.AsyncTaskRzadanie.Response
import org.json.JSONArray

class ActivitySpolecznosc : AppCompatActivity() {
    private lateinit var adapterListaZakupow: AdapterListaZakupow

    private lateinit var context: Context

    private val onItemClickListener: OnItemClickListener = object : OnItemClickListener() {
        override fun onItemLongClick(view: View, position: Int) {
            if (null != actionMode) {
                return
            }

            adapterListaZakupow.setSelection(view)
            actionMode = startActionMode(object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    mode.menuInflater.inflate(R.menu.activityspolecznosccontext, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    val model = adapterListaZakupow.getItem(position)
                    return when (item.itemId) {
                        R.id.ascCena -> {
                            if (Permissions.hasInternet(context, view)
                                && Zetony(context).sprawdzZetony(
                                    Zetony.ZETONY_CENA_ZOBACZ,
                                    true,
                                    view
                                )
                            ) {
                                val ustawienia = Ustawienia(context)
                                WebAPI.pobierzCeny(
                                    ustawienia,
                                    model.nazwa,
                                    object : AsyncTaskRzadanie.Listener {
                                        override fun onPostExecute(response: Response) {
                                            if (response.isOK(false)) {
                                                startActivity(
                                                    Intent(context, ActivityKomunikat::class.java)
                                                        .putExtra(
                                                            ActivityKomunikat.IE_SKORKA,
                                                            if (ustawienia.getSkorkaCiemna(false)) R.style.AppThemeNight else null
                                                        )
                                                        .putExtra(
                                                            ActivityKomunikat.IE_IKONA,
                                                            R.drawable.ic_launcher
                                                        )
                                                        .putExtra(
                                                            ActivityKomunikat.IE_TYTUL,
                                                            getString(R.string.app_name)
                                                        )
                                                        .putExtra(
                                                            ActivityKomunikat.IE_KOMUNIKAT,
                                                            model.nazwa + ":\n" + WebAPI.filtruj(
                                                                response.message
                                                            )
                                                        )
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                            mode.finish()
                            true
                        }
                        R.id.ascDodajProdukt -> {
                            val repository = adapterListaZakupow.repository
                            val nazwa = model.nazwa
                            var istnieje = false
                            val listWKoszyku =
                                repository.getLista(RepositoryListaZakupow.Lista.W_KOSZYKU)
                            for (i in 0 until listWKoszyku.size) {
                                istnieje = istnieje || nazwa == listWKoszyku[i].nazwa
                            }
                            val listDoKupienia =
                                repository.getLista(RepositoryListaZakupow.Lista.DO_KUPIENIA)
                            for (i in 0 until listDoKupienia.size) {
                                istnieje = istnieje || nazwa == listDoKupienia[i].nazwa
                            }
                            val listProdukty =
                                repository.getLista(RepositoryListaZakupow.Lista.PRODUKTY)
                            for (i in 0 until listProdukty.size) {
                                istnieje = istnieje || nazwa == listProdukty[i].nazwa
                            }
                            if (!istnieje) {
                                listProdukty.add(model)
                                repository.update()
                            }
                            mode.finish()
                            true
                        }
                        else -> false
                    }
                }

                override fun onDestroyActionMode(mode: ActionMode) {
                    actionMode = null
                    adapterListaZakupow.clearSelection()
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Ustawienia(this).getSkorkaCiemna(false)) {
            setTheme(R.style.AppThemeNight)
        }
        super.onCreate(savedInstanceState)
        if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
            Utils.ustawAnimacje(window, Explode(), Explode(), true)
        }
        setContentView(R.layout.activityspolecznosc)
        context = this

        adapterListaZakupow = AdapterListaZakupow(RepositoryListaZakupow(this))
        adapterListaZakupow.setOnItemClickListener(onItemClickListener)
        adapterListaZakupow.setSelectedItemColor(
            ResourcesCompat
                .getColor(context.resources, R.color.colorAccent, null)
        )
        pobierzProdukty(adapterListaZakupow, findViewById(R.id.activityspolecznosc))

        val recyclerView = findViewById<RecyclerView>(R.id.asListView)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapterListaZakupow
        recyclerView.layoutManager = LinearLayoutManager(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun pobierzProdukty(adapterListaZakupow: AdapterListaZakupow, view: View) {
        adapterListaZakupow.repository.get()
        if (Permissions.hasInternet(this, view)) {
            val ustawienia = Ustawienia(this)
            WebAPI.pobierzListe(
                ustawienia,
                object : AsyncTaskRzadanie.Listener {
                    override fun onPostExecute(response: Response) {
                        val jsonArray = try {
                            JSONArray(response.message)
                        } catch (exception: Exception) {
                            JSONArray()
                        }
                        if (response.isOK(true)) {
                            RepositoryListaZakupow.JSONToList(
                                jsonArray,
                                adapterListaZakupow.getDataset()
                            )
                            adapterListaZakupow.notifyDataSetChanged()
                        }
                    }
                }
            )
        }
    }
}
