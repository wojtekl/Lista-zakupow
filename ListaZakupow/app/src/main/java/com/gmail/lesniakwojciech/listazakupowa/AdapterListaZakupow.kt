package com.gmail.lesniakwojciech.listazakupowa

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.gmail.lesniakwojciech.commons.OnItemClickListener
import com.gmail.lesniakwojciech.commons.ViewHolderBase

class AdapterListaZakupow(val repository: RepositoryListaZakupow) :
    RecyclerView.Adapter<AdapterListaZakupow.ViewHolderListaZakupow>() {
    private var onItemClickListener: OnItemClickListener? = null
    private var selectedItem: View? = null
    private var selectedItemColor = Color.GRAY
    private var holderBackground: Drawable? = null

    private var dataset: MutableList<ModelProdukt> = ArrayList()
    //val repository = RepositoryListaZakupow(context)
    private lateinit var lista: RepositoryListaZakupow.Lista

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderListaZakupow {
        return ViewHolderListaZakupow(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapterlistazakupow, parent, false), onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: ViewHolderListaZakupow, position: Int) {
        val model = dataset[position]
        holder.nazwa.text = model.nazwa
        holder.sklep.text = model.sklep
        holder.cena.text = ModelProdukt.formatCena(model.cena)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    fun getItem(position: Int): ModelProdukt {
        return dataset[position]
    }

    fun removeItem(position: Int) {
        dataset.removeAt(position)
        notifyItemRemoved(position)
        //repository.update()
    }

    fun addItem(model: ModelProdukt, lista: RepositoryListaZakupow.Lista) {
        repository.insert(lista, model)
        if (this.lista == lista) {
            notifyItemInserted(dataset.size - 1)
        }
    }

    fun updateItem(position: Int, model: ModelProdukt): Boolean {
        val produkt = dataset[position]
        val updated = produkt.nazwa != model.nazwa
                || produkt.sklep != model.sklep
                || produkt.cena != model.cena
        if (!updated) {
            return false
        }
        produkt.nazwa = model.nazwa
        produkt.sklep = model.sklep
        produkt.cena = model.cena
        repository.sort(lista)
        notifyDataSetChanged()
        //repository.update()
        return true
    }

    fun moveItem(position: Int, lista: RepositoryListaZakupow.Lista) {
        repository.insert(lista, dataset[position])
        dataset.removeAt(position)
        //repository.update()
        notifyDataSetChanged()
    }

    fun moveAll(lista: RepositoryListaZakupow.Lista) {
        repository.insertAll(lista, dataset)
        dataset.clear()
        //repository.update()
        notifyDataSetChanged()
    }

    fun getDataset(): MutableList<ModelProdukt> {
        return dataset
    }

    fun getDatasetJSON(): String {
        return repository.getListaJSON(lista)
    }

    fun get(lista: RepositoryListaZakupow.Lista) {
        this.lista = lista
        //repository.get()
        dataset = repository.getLista(lista)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setSelectedItemColor(selectedItemColor: Int) {
        this.selectedItemColor = selectedItemColor
    }

    fun setSelection(view: View) {
        if (null == selectedItem) {
            selectedItem = view
            holderBackground = selectedItem!!.background
            selectedItem?.setBackgroundColor(selectedItemColor)
        }
    }

    fun clearSelection() {
        ViewCompat.setBackground(selectedItem!!, holderBackground)
        holderBackground = null
        selectedItem = null
    }

    class ViewHolderListaZakupow(view: View, onItemClickListener: OnItemClickListener?) :
        ViewHolderBase(view, onItemClickListener) {
        val nazwa: TextView = view.findViewById(R.id.aalzTvNazwa)
        val sklep: TextView = view.findViewById(R.id.aalzTvSklep)
        val cena: TextView = view.findViewById(R.id.aalzTvCena)
    }
}
