package com.gmail.lesniakwojciech.listazakupowa

import android.content.Context
import android.text.TextUtils
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList

class RepositoryListaZakupow(context: Context) {
    companion object {
        fun JSONToList(array: JSONArray, list: MutableList<ModelProdukt>) {
            list.clear()
            for (i in 0 until array.length()) {
                list.add(ModelProdukt.fromJSON(array.getString(i)))
            }
        }
    }

    private val database = Ustawienia(context)

    private val domyslne = context.getString(R.string.lista_poczatkowa)

    private val produkty = ArrayList<ModelProdukt>()
    private val doKupienia = ArrayList<ModelProdukt>()
    private val wKoszyku = ArrayList<ModelProdukt>()

    fun getLista(lista: Lista): MutableList<ModelProdukt> {
        return when (lista) {
            Lista.PRODUKTY -> produkty
            Lista.W_KOSZYKU -> wKoszyku
            else -> doKupienia
        }
    }

    fun getListaJSON(lista: Lista): String {
        return listToJSON(
            when (lista) {
                Lista.PRODUKTY -> produkty
                Lista.W_KOSZYKU -> wKoszyku
                else -> doKupienia
            }
        )
    }

    fun get() {
        val jsonLista = JSONArray(database.getListaZakupow(domyslne))
        JSONToList(jsonLista.getJSONArray(0), produkty)
        JSONToList(jsonLista.getJSONArray(1), doKupienia)
        JSONToList(jsonLista.getJSONArray(2), wKoszyku)
    }

    fun update() {
        database.setListaZakupow(
            StringBuilder()
                .append("[")
                .append(listToJSON(produkty))
                .append(",")
                .append(listToJSON(doKupienia))
                .append(",")
                .append(listToJSON(wKoszyku))
                .append("]")
                .toString()
        )
    }

    fun insert(lista: Lista, model: ModelProdukt) {
        val list = getLista(lista)
        list.add(model)
        Collections.sort(list, getComparator(lista))
        //update()
    }

    fun insertAll(lista: Lista, models: List<ModelProdukt>) {
        val list = getLista(lista)
        list.addAll(models)
        Collections.sort(list, getComparator(lista))
        //update()
    }

    fun sort(lista: Lista) {
        Collections.sort(getLista(lista), getComparator(lista))
    }

    fun merge(json: String) {
        val jsonArray = JSONArray(json)
        for (i in 0 until jsonArray.length()) {
            val model = ModelProdukt.fromJSON(jsonArray.getString(i))
            if (doKupienia.contains(model) || wKoszyku.contains(model)) {
                continue
            }
            var istnieje = false
            for (j in 0 until produkty.size) {
                val produkt = produkty[j]
                if (produkt == model) {
                    doKupienia.add(produkt)
                    produkty.removeAt(j)
                    istnieje = true
                    break
                }
            }
            if (!istnieje) {
                doKupienia.add(model)
            }
        }
        Collections.sort(doKupienia, getComparator(Lista.DO_KUPIENIA))
        update()
    }

    fun sklepy(): List<String> {
        val sklepy = ArrayList<String>()
        sklepy(sklepy, produkty)
        sklepy(sklepy, doKupienia)
        sklepy(sklepy, wKoszyku)
        return sklepy
    }

    fun doWyslania(wstep: String): String {
        var size = doKupienia.size
        if (1 > size) {
            return ""
        }
        val stringBuilder = StringBuilder()
        if (!TextUtils.isEmpty(wstep)) stringBuilder.append(wstep).append(":\n")
        --size
        for (i in 0 until size) {
            stringBuilder.append(" * ").append(doKupienia[i].nazwa).append(",\n")
        }
        return stringBuilder.append(" * ").append(doKupienia[size].nazwa).append(".").toString()
    }

    fun koszt(lista: Lista): Double {
        var koszt = 0.0
        for (produkt in getLista(lista)) {
            koszt += produkt.cena
        }
        return koszt
    }

    private fun listToJSON(list: List<ModelProdukt>): String {
        val stringBuilder = StringBuilder().append("[")
        val size = list.size
        if (0 < size) {
            stringBuilder.append(list[0].toJSON())
            for (i in 1 until size) {
                stringBuilder.append(",").append(list[i].toJSON())
            }
        }
        return stringBuilder.append("]").toString()
    }

    private fun sklepy(sklepy: MutableList<String>, list: List<ModelProdukt>) {
        for (element in list) {
            val sklep = element.sklep
            if (!sklepy.contains(sklep)) {
                sklepy.add(sklep)
            }
        }
    }

    enum class Lista {
        PRODUKTY,
        DO_KUPIENIA,
        W_KOSZYKU
    }

    interface IDatabaseListaZakupow {
        fun getListaZakupow(json: String): String
        fun setListaZakupow(json: String)
    }

    interface IRepository {
        fun getRepository(): RepositoryListaZakupow
    }

    private fun getComparator(lista: Lista): Comparator<ModelProdukt> {
        return when (lista) {
            Lista.PRODUKTY -> ModelProdukt.ComparatorPopularnosc()
            Lista.W_KOSZYKU -> ModelProdukt.ComparatorCena()
            else -> ModelProdukt.ComparatorSklep()
        }
    }
}
