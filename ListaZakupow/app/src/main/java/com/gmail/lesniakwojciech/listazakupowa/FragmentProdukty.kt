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

class FragmentProdukty : Fragment(), DialogListener {
    companion object {
        const val title = R.string.produkty
        const val color = R.color.lightBlueA100
        const val icon = R.drawable.ic_kitchen

        private const val OPTIONS_DODAJ_PRODUKT = "fpoDodajProdukt"
        private const val CONTEXT_UAKTUALNIJ = "fpcUaktualnij"
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
                    mode.menuInflater.inflate(R.menu.fragmentproduktycontext, menu)
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }

                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    val model = adapterListaZakupow.getItem(position)
                    val context = requireContext()
                    return when (item.itemId) {
                        R.id.fpcCena -> {
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
                                                            if (ustawienia.getSkorkaCiemna(false))
                                                                R.style.AppThemeNight
                                                            else null
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
                                                            model.nazwa
                                                                    + ":\n" + WebAPI.filtruj(
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
                        R.id.fpcUaktualnij -> {
                            DialogFragmentProdukt.newInstance(
                                this@FragmentProdukty,
                                position, model, adapterListaZakupow.repository.sklepy()
                            )
                                .show(requireActivity().supportFragmentManager, CONTEXT_UAKTUALNIJ)
                            mode.finish()
                            WebAPI.wlaczInternet(context, view)
                            true
                        }
                        R.id.fpcUsun -> {
                            adapterListaZakupow.removeItem(position)
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
        fab.setImageResource(com.gmail.lesniakwojciech.commons.R.drawable.ic_add)
        fab.setOnClickListener {
            val context = requireContext()
            DialogFragmentProdukt
                .newInstance(
                    this@FragmentProdukty, -1, ModelProdukt(),
                    adapterListaZakupow.repository.sklepy()
                )
                .show(requireActivity().supportFragmentManager, OPTIONS_DODAJ_PRODUKT)
            WebAPI.wlaczInternet(context, requireView())
        }
        setHasOptionsMenu(true)
        return view
    }

    override fun onResume() {
        super.onResume()

        adapterListaZakupow.get(RepositoryListaZakupow.Lista.PRODUKTY)
        ustawKoszt()
    }

    override fun onPause() {
        onItemClickListener.finish()

        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu, mi: MenuInflater) {
        mi.inflate(R.menu.fragmentproduktyoptions, menu)
    }

    override fun onOptionsItemSelected(mi: MenuItem): Boolean {
        val context = requireContext()
        return when (mi.itemId) {
            R.id.fpoDodajProdukt -> {
                DialogFragmentProdukt
                    .newInstance(
                        this@FragmentProdukty, -1, ModelProdukt(),
                        adapterListaZakupow.repository.sklepy()
                    )
                    .show(requireActivity().supportFragmentManager, OPTIONS_DODAJ_PRODUKT)
                WebAPI.wlaczInternet(context, requireView())
                true
            }
            R.id.fpoUstawienia -> {
                startActivity(Intent(context, ActivityUstawienia::class.java))
                true
            }
            else -> super.onOptionsItemSelected(mi)
        }
    }

    private fun ustawKoszt() {
        requireActivity().title = getString(R.string.app_name) + ": $" + ModelProdukt.formatCena(
            adapterListaZakupow.repository.koszt(RepositoryListaZakupow.Lista.DO_KUPIENIA)
                    + adapterListaZakupow.repository.koszt(RepositoryListaZakupow.Lista.W_KOSZYKU)
        )
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {}
    override fun onDialogPositiveClick(dialog: DialogFragment, i: Int, model: ModelProdukt) {
        val tag = dialog.tag
        val context = requireContext()
        val ustawienia = Ustawienia(context)
        val view = requireView()
        if (OPTIONS_DODAJ_PRODUKT == tag) {
            adapterListaZakupow.addItem(model, RepositoryListaZakupow.Lista.PRODUKTY)
            if (ustawienia.getCenyUdostepniaj(false)
                && Permissions.hasInternet(context, view)
            ) {
                WebAPI.udostepnijCeny(ustawienia, model, object : AsyncTaskRzadanie.Listener {
                    override fun onPostExecute(response: Response) {
                        Zetony(context).dodajZetony(Zetony.ZETONY_CENA_UDOSTEPNIENIE, view)
                    }
                })
            }
            return
        }
        if (CONTEXT_UAKTUALNIJ == tag) {
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
        }
    }
}
