package com.gmail.lesniakwojciech.listazakupowa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import java.text.DecimalFormat;

public class DialogFragmentProdukt 
  extends DialogFragment
{
  private static final String NAZWA = "nazwa";
  private static final String SKLEP = "sklep";
  private static final String CENA = "cena";
  private static final String CENA_FORMAT = "#0.00";
  
  protected interface DialogListener
  {
    public void onDialogNegativeClick(final DialogFragment dialog);
    public void onDialogPositiveClick(final DialogFragment dialog, final int i, 
      final String nazwa, final String sklep, final double cena);
  }
  
  private DialogListener listener;
  
  @Override
  public void onAttach(final Activity actvt)
  {
    super.onAttach(actvt);
    
    try
    {
      listener = (DialogListener)actvt;
    }
    catch(final ClassCastException exception)
    {
    }
  }
  
  public static DialogFragmentProdukt newInstance(final int i, final String nazwa, 
    final String sklep, final double cena)
  {
    final Bundle bundle = new Bundle();
    bundle.putInt("i", i);
    bundle.putString(NAZWA, nazwa);
    bundle.putString(SKLEP, sklep);
    bundle.putDouble(CENA, cena);
    final DialogFragmentProdukt dialogFragment = new DialogFragmentProdukt();
    dialogFragment.setArguments(bundle);
    return dialogFragment;
  }
  
  private EditText etNazwa, etCena, etSklep;
  
  @Override
  public Dialog onCreateDialog(final Bundle bundle)
  {
    final Activity activity = getActivity();
    final View view = activity.getLayoutInflater().inflate(R.layout.dialogfragmentprodukt, 
      null);
    etNazwa = (EditText)view.findViewById(R.id.dfpEtNazwa);
    etSklep = (EditText)view.findViewById(R.id.dfpEtSklep);
    etCena = (EditText)view.findViewById(R.id.dfpEtCena);
    final Bundle arguments = getArguments();
    etNazwa.setText(arguments.getString(NAZWA, ""));
    etSklep.setText(arguments.getString(SKLEP, ""));
    etCena.setText((new DecimalFormat(CENA_FORMAT)).format(arguments.getDouble(CENA, 0.0f)));
    return new AlertDialog
      .Builder(activity)
      .setView(view)
      .setNegativeButton(R.string.zaniechaj, new DialogInterface.OnClickListener()
      {
        public void onClick(final DialogInterface di, final int i)
        {
          listener.onDialogNegativeClick(DialogFragmentProdukt.this);
        }
      }
      )
      .setPositiveButton(R.string.zachowaj, new DialogInterface.OnClickListener()
      {
        public void onClick(final DialogInterface di, final int i)
        {
          listener.onDialogPositiveClick(DialogFragmentProdukt.this, arguments.getInt("i", -1), 
            etNazwa.getText().toString(), etSklep.getText().toString(), 
            Double.parseDouble(etCena.getText().toString().replaceAll(",", ".")));
        }
      }
      )
      .create();
  }
}
