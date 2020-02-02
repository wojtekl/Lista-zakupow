package com.gmail.lesniakwojciech.listazakupowa

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.fragment.app.DialogFragment

class DialogFragmentProdukt : DialogFragment() {
    companion object {
        private const val NAZWA = "nazwa"
        private const val SKLEP = "sklep"
        private const val CENA = "cena"

        fun newInstance(
            listener: DialogListener,
            i: Int,
            model: ModelProdukt,
            sklepy: List<String>
        ): DialogFragmentProdukt {
            val bundle = Bundle()
            bundle.putInt("i", i)
            bundle.putString(NAZWA, model.nazwa)
            bundle.putString(SKLEP, model.sklep)
            bundle.putDouble(CENA, model.cena)
            val dialogFragment = DialogFragmentProdukt()
            dialogFragment.arguments = bundle
            dialogFragment.listener = listener
            dialogFragment.sklepy = sklepy
            return dialogFragment
        }
    }

    private lateinit var listener: DialogListener
    private lateinit var sklepy: List<String>
    private lateinit var etNazwa: EditText
    private lateinit var etCena: EditText
    private lateinit var etSklep: AppCompatAutoCompleteTextView

    override fun onCreateDialog(bundle: Bundle?): Dialog {
        val context = requireContext()
        val view = requireActivity().layoutInflater.inflate(R.layout.dialogfragmentprodukt, null)
        etNazwa = view.findViewById(R.id.dfpEtNazwa)
        etSklep = view.findViewById(R.id.dfpEtSklep)
        etCena = view.findViewById(R.id.dfpEtCena)
        val arguments = arguments!!
        etNazwa.setText(arguments.getString(NAZWA, ""))
        etSklep.setText(arguments.getString(SKLEP, ""))
        etCena.setText(ModelProdukt.formatCena(arguments.getDouble(CENA, 0.0)))
        etSklep.setAdapter(
            ArrayAdapter(
                context,
                android.R.layout.simple_dropdown_item_1line,
                sklepy
            )
        )
        return AlertDialog.Builder(
            activity,
            if (Ustawienia(context).getSkorkaCiemna(false))
                R.style.AppThemeNight_AlertOverlay
            else R.style.AppTheme_AlertOverlay
        )
            .setView(view)
            .setNegativeButton(R.string.zaniechaj) { di, i -> listener.onDialogNegativeClick(this@DialogFragmentProdukt) }
            .setPositiveButton(R.string.zachowaj) { di, i ->
                listener.onDialogPositiveClick(
                    this@DialogFragmentProdukt,
                    arguments.getInt("i", -1),
                    ModelProdukt(etNazwa.text.toString().trim { it <= ' ' },
                        etSklep.text.toString().trim { it <= ' ' },
                        etCena.text.toString().trim { it <= ' ' }
                            .replace(",".toRegex(), ".").toDouble())
                )
            }
            .create()
    }

    interface DialogListener {
        fun onDialogNegativeClick(dialog: DialogFragment)
        fun onDialogPositiveClick(dialog: DialogFragment, i: Int, model: ModelProdukt)
    }
}
