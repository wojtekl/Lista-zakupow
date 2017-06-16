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
  protected interface DialogListener
  {
    public void onDialogNegativeClick(final DialogFragment dialog);
    public void onDialogPositiveClick(final DialogFragment dialog, final int i, final String nazwa, 
      final String sklep, final float cena);
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
  
  public static DialogFragmentProdukt newInstance(final int i, final String nazwa, final String sklep, 
    final float cena)
  {
    final Bundle bundle = new Bundle();
    bundle.putInt("i", i);
    bundle.putString("nazwa", nazwa);
    bundle.putString("sklep", sklep);
    bundle.putFloat("cena", cena);
    final DialogFragmentProdukt dialogFragment = new DialogFragmentProdukt();
    dialogFragment.setArguments(bundle);
    return dialogFragment;
  }
  
  private EditText etNazwa, etCena, etSklep;
  
  @Override
  public Dialog onCreateDialog(final Bundle bundle)
  {
    final Activity activity = getActivity();
    final View view = activity.getLayoutInflater().inflate(R.layout.dialogfragmentprodukt, null);
    etNazwa = (EditText)view.findViewById(R.id.dfpNazwa);
    etSklep = (EditText)view.findViewById(R.id.dfpSklep);
    etCena = (EditText)view.findViewById(R.id.dfpCena);
    final Bundle arguments = getArguments();
    etNazwa.setText(arguments.getString("nazwa", ""));
    etSklep.setText(arguments.getString("sklep", ""));
    etCena.setText((new DecimalFormat("#0.00")).format(arguments.getFloat("cena", 0.0f)));
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
            Float.parseFloat(etCena.getText().toString().replaceAll(",", ".")));
        }
      }
      )
      .create();
  }
}
