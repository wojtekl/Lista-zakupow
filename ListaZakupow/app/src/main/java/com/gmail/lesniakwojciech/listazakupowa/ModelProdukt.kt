package com.gmail.lesniakwojciech.listazakupowa

import org.json.JSONArray
import java.text.DecimalFormat
import java.util.*

class ModelProdukt(
    var nazwa: String = "",
    var sklep: String = "",
    var cena: Double = 0.0,
    var popularnosc: Int = 0
) {
    companion object {
        fun fromJSON(string: String): ModelProdukt {
            val jsonArray = JSONArray(string)
            return ModelProdukt(
                jsonArray.getString(0),
                jsonArray.getString(1),
                jsonArray.getDouble(2),
                jsonArray.optInt(3, 0)
            )
        }

        private val FORMAT_CENA = DecimalFormat("#0.00")

        fun formatCena(cena: Double): String {
            return FORMAT_CENA.format(cena)
        }
    }

    override fun hashCode(): Int {
        return nazwa.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is ModelProdukt && nazwa == other.nazwa
    }

    fun toJSON(): String {
        return StringBuilder()
            .append("[")
            .append("\"").append(nazwa).append("\"")
            .append(",").append("\"").append(sklep).append("\"")
            .append(",").append(cena)
            .append(",").append(popularnosc)
            .append("]")
            .toString()
    }

    fun podbijPopularnosc() {
        ++popularnosc
    }

    class ComparatorCena : Comparator<ModelProdukt> {
        override fun compare(p1: ModelProdukt, p2: ModelProdukt): Int {
            val p = p2.cena - p1.cena
            if (0.0 == p) {
                return p1.nazwa.compareTo(p2.nazwa)
            }
            return if (0 < p) 1 else -1
        }
    }

    class ComparatorPopularnosc : Comparator<ModelProdukt> {
        override fun compare(p1: ModelProdukt, p2: ModelProdukt): Int {
            val p = p2.popularnosc - p1.popularnosc
            return if (0 == p) {
                p1.nazwa.compareTo(p2.nazwa)
            } else p
        }
    }

    class ComparatorSklep : Comparator<ModelProdukt> {
        override fun compare(p1: ModelProdukt, p2: ModelProdukt): Int {
            val p = p1.sklep.compareTo(p2.sklep)
            return if (0 == p) {
                p1.nazwa.compareTo(p2.nazwa)
            } else p
        }
    }
}
