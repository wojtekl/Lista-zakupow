package com.gmail.lesniakwojciech.listazakupowa

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.lesniakwojciech.commons.*
import com.gmail.lesniakwojciech.commons.AsyncTaskRzadanie.Response
import com.gmail.lesniakwojciech.listazakupowa.DialogFragmentProdukt.DialogListener
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentWKoszyku : Fragment(), DialogListener {
    companion object {
        const val title = R.string.w_koszyku
        const val color = R.color.amberA100
        const val icon = R.drawable.ic_shopping_cart

        private const val CONTEXT_UAKTUALNIJ = "fwcUaktualnij"
    }

    private lateinit var adapterListaZakupow: AdapterListaZakupow

    private val onItemClickListener: OnItemClickListener = object : OnItemClickListener() {
        override fun onItemClick(position: Int) {
            if (null != actionMode) {
                return
            }

            adapterListaZakupow.moveItem(position, RepositoryListaZakupow.Lista.DO_KUPIENIA)
            ustawKoszt()
        }

        override fun onItemLongClick(view: View, position: Int) {
            if (null != actionMode) {
                return
            }

            adapterListaZakupow.setSelection(view)
            actionMode = requireActivity().startActionMode(object : ActionMode.Callback {
                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    mode.menuInflater.inflate(R.menu.fragmentwkoszykucontext, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    val model = adapterListaZakupow.getItem(position)
                    val context = requireContext()
                    return when (item.itemId) {
                        R.id.fwcCena -> {
                            if (Permissions.hasInternet(context, view) && Zetony(context)
                                    .sprawdzZetony(Zetony.ZETONY_CENA_ZOBACZ, true, view)
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
                                                            model.nazwa + ":"
                                                                    + WebAPI.filtruj(response.message)
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
                        R.id.fwcUaktualnij -> {
                            val activity = requireActivity()
                            DialogFragmentProdukt.newInstance(
                                this@FragmentWKoszyku, position,
                                model, adapterListaZakupow.repository.sklepy()
                            )
                                .show(activity.supportFragmentManager, CONTEXT_UAKTUALNIJ)
                            mode.finish()
                            WebAPI.wlaczInternet(context, view)
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

    override fun onCreateView(li: LayoutInflater, vg: ViewGroup?, bundle: Bundle?): View {
        val view = li.inflate(R.layout.fragmentprodukty, vg, false)
        adapterListaZakupow = AdapterListaZakupow(
            (activity as RepositoryListaZakupow.IRepository)
                .getRepository()
        )
        adapterListaZakupow.setOnItemClickListener(onItemClickListener)
        adapterListaZakupow.setSelectedItemColor(
            ResourcesCompat
                .getColor(this.resources, R.color.colorAccent, null)
        )
        val recyclerView: RecyclerView = view.findViewById(R.id.fpListView)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapterListaZakupow
        recyclerView.layoutManager = LinearLayoutManager(context)
        val fab: FloatingActionButton = view.findViewById(R.id.fab)
        fab.setImageResource(R.drawable.ic_playlist_add_check)
        fab.setOnClickListener { zakonczZakupy() }
        setHasOptionsMenu(true)
        return view
    }

    override fun onResume() {
        super.onResume()

        adapterListaZakupow.get(RepositoryListaZakupow.Lista.W_KOSZYKU)
        ustawKoszt()
    }

    override fun onPause() {
        onItemClickListener.finish()

        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu, mi: MenuInflater) {
        mi.inflate(R.menu.fragmentwkoszykuoptions, menu)
    }

    override fun onOptionsItemSelected(mi: MenuItem): Boolean {
        return when (mi.itemId) {
            R.id.fwkoZakonczZakupy -> {
                zakonczZakupy()
                true
            }
            else -> super.onOptionsItemSelected(mi)
        }
    }

    private fun zakonczZakupy() {
        for (i in 0 until adapterListaZakupow.itemCount) {
            adapterListaZakupow.getItem(i).podbijPopularnosc()
        }
        adapterListaZakupow.moveAll(RepositoryListaZakupow.Lista.PRODUKTY)
        ustawKoszt()
    }

    private fun ustawKoszt() {
        requireActivity().title = getString(R.string.app_name) + ": $" + ModelProdukt.formatCena(
            adapterListaZakupow.repository.koszt(RepositoryListaZakupow.Lista.W_KOSZYKU)
        )
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {}
    override fun onDialogPositiveClick(dialog: DialogFragment, i: Int, model: ModelProdukt) {
        if (CONTEXT_UAKTUALNIJ == dialog.tag) {
            val context = requireContext()
            val ustawienia = Ustawienia(context)
            val view = requireView()
            if (adapterListaZakupow.updateItem(i, model)
                && ustawienia.getCenyUdostepniaj(false)
                && Permissions.hasInternet(context, view)
            ) {
                WebAPI.udostepnijCeny(ustawienia, model, object : AsyncTaskRzadanie.Listener {
                    override fun onPostExecute(response: Response) {
                        Zetony(context).dodajZetony(Zetony.ZETONY_CENA_UDOSTEPNIENIE, view)
                    }
                })
            }
            ustawKoszt()
        }
    }
}
