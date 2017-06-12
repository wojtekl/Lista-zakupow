package com.gmail.lesniakwojciech.listazakupowa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import java.text.DecimalFormat;

public class DialogFragmentProdukt 
  extends DialogFragment
{
  protected interface DialogListener
  {
    public void onDialogPositiveClick(final ModelProdukt model);
    public void onDialogNegativeClick(final DialogFragment dialog);
  }
  
  private final DialogListener listener;
  
  private final ModelProdukt model;
  
  public DialogFragmentProdukt(final Fragment fragment, final ModelProdukt model)
  {
    listener = (DialogListener)fragment;
    
    if(null == model)
    {
      this.model = new ModelProdukt();
    }
    else
    {
      this.model = model;
    }
  }
  
  /* @Override
  public void onAttach(final Activity actvt)
  {
    super.onAttach(actvt);
    
    try
    {
      listener = (DialogListener)getParentFragment();
    }
    catch(final ClassCastException exception)
    {
    }
  } */
  
  private EditText etNazwa, etCena, etSklep;
  
  @Override
  public Dialog onCreateDialog(final Bundle bundle)
  {
    final Activity activity = getActivity();
    final View view = activity.getLayoutInflater().inflate(R.layout.dialogfragmentprodukt, null);
    etNazwa = (EditText)view.findViewById(R.id.dfpNazwa);
    etSklep = (EditText)view.findViewById(R.id.dfpSklep);
    etCena = (EditText)view.findViewById(R.id.dfpCena);
    etNazwa.setText(model.getNazwa());
    etSklep.setText((model.getSklep()));
    etCena.setText((new DecimalFormat("#0.00")).format(model.getCena()));
    return new AlertDialog
      .Builder(activity)
      .setView(view)
      .setPositiveButton(R.string.zachowaj, new DialogInterface.OnClickListener()
      {
        public void onClick(final DialogInterface di, int i)
        {
          model.setNazwa(etNazwa.getText().toString());
          model.setSklep(etSklep.getText().toString());
          model.setCena(Float.parseFloat(etCena.getText().toString().replaceAll(",", ".")));
          listener.onDialogPositiveClick(model);
        }
      }
      )
      .setNegativeButton(R.string.zaniechaj, new DialogInterface.OnClickListener()
      {
        public void onClick(final DialogInterface di, final int i)
        {
          listener.onDialogNegativeClick(DialogFragmentProdukt.this);
        }
      }
      )
      .create();
  }
}
