package com.gmail.lesniakwojciech.listazakupowa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.text.DecimalFormat;

public class DialogFragmentProdukt
        extends DialogFragment {
    private static final String NAZWA = "nazwa";
    private static final String SKLEP = "sklep";
    private static final String CENA = "cena";
    private static final String CENA_FORMAT = "#0.00";

    protected interface DialogListener {
        void onDialogNegativeClick(final DialogFragment dialog);

        void onDialogPositiveClick(final DialogFragment dialog, final int i, final String nazwa,
                                   final String sklep, final double cena);
    }

    private DialogListener listener;
    private ArrayAdapter<String> sklepy;

    public static DialogFragmentProdukt newInstance(final DialogListener listener, final int i,
                                                    final String nazwa, final String sklep,
                                                    final double cena,
                                                    final ArrayAdapter<String> sklepy) {
        final Bundle bundle = new Bundle();
        bundle.putInt("i", i);
        bundle.putString(NAZWA, nazwa);
        bundle.putString(SKLEP, sklep);
        bundle.putDouble(CENA, cena);
        final DialogFragmentProdukt dialogFragment = new DialogFragmentProdukt();
        dialogFragment.setArguments(bundle);
        dialogFragment.listener = listener;
        dialogFragment.sklepy = sklepy;
        return dialogFragment;
    }

    private EditText etNazwa, etCena;
    private AppCompatAutoCompleteTextView etSklep;

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle bundle) {
        final View view = requireActivity().getLayoutInflater().inflate(R.layout.dialogfragmentprodukt,
                null);
        etNazwa = view.findViewById(R.id.dfpEtNazwa);
        etSklep = view.findViewById(R.id.dfpEtSklep);
        etCena = view.findViewById(R.id.dfpEtCena);
        final Bundle arguments = getArguments();
        etNazwa.setText(arguments.getString(NAZWA, ""));
        etSklep.setText(arguments.getString(SKLEP, ""));
        etCena.setText((new DecimalFormat(CENA_FORMAT)).format(arguments.getDouble(CENA, 0.0f)));
        etSklep.setAdapter(sklepy);
        return new AlertDialog
                .Builder(getActivity())
                .setView(view)
                .setNegativeButton(R.string.zaniechaj, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface di, final int i) {
                        listener.onDialogNegativeClick(DialogFragmentProdukt.this);
                    }
                }
                )
                .setPositiveButton(R.string.zachowaj, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface di, final int i) {
                        listener.onDialogPositiveClick(DialogFragmentProdukt.this,
                                arguments.getInt("i", -1),
                                etNazwa.getText().toString().trim(),
                                etSklep.getText().toString().trim(),
                                Double.parseDouble(etCena.getText().toString().trim()
                                        .replaceAll(",", ".")));
                    }
                }
                )
                .create();
    }
}
