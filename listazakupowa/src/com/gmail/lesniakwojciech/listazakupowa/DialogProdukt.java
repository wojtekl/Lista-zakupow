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
import java.text.NumberFormat;

public class DialogProdukt extends DialogFragment
{
    
    protected interface DialogListener
    {
        
        public void onDialogPositiveClick(final ModelProdukt produkt);
        public void onDialogNegativeClick(final DialogFragment dialog);
        
    }
    
    private DialogListener listener;
    
    /*@Override
    public void onAttach(final Activity activity)
    {
        super.onAttach(activity);
        try
        {
            listener = (DialogListener)activity;
        }
        catch(final ClassCastException exception)
        {
        }
    }*/
    
    private ModelProdukt produkt;
    
    private EditText dProduktNazwa, dProduktCena, dProduktSklep;
    
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        final Activity activity = this.getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View view = activity.getLayoutInflater().inflate(R.layout.dialogprodukt, null);
        dProduktNazwa = (EditText)view.findViewById(R.id.DPRODUKTNAZWA);
        dProduktCena = (EditText)view.findViewById(R.id.DPRODUKTCENA);
        dProduktSklep = (EditText)view.findViewById(R.id.DPRODUKTSKLEP);
        if(null != produkt)
        {
            dProduktNazwa.setText(produkt.getNazwa());
            final NumberFormat numberFormat = new DecimalFormat("#0.00");
            dProduktCena.setText(numberFormat.format(produkt.getCena()));
            dProduktSklep.setText((produkt.getSklep()));
        }
        builder.setView(view)
                .setPositiveButton(R.string.ZAPISZ, new DialogInterface.OnClickListener()
        {
            
            public void onClick(final DialogInterface dialog, int id)
            {
                produkt.setNazwa(dProduktNazwa.getText().toString());
                produkt.setCena(Float.parseFloat(dProduktCena.getText().toString().replaceAll(",", ".")));
                produkt.setSklep(dProduktSklep.getText().toString());
                if(null != listener)
                {
                listener.onDialogPositiveClick(produkt);
                }
                /**/
            }
            
        })
                .setNegativeButton(R.string.ZANIECHAJ, new DialogInterface.OnClickListener()
        {
            
            public void onClick(final DialogInterface dialog, final int id)
            {
                //listener.onDialogNegativeClick(DialogProdukt.this);
            }
            
        });
        return builder.create();
    }
    
    protected void ustaw(final ModelProdukt p)
    {
        this.produkt = p;
    }
    
    protected void ustaw(final ModelProdukt p, final Fragment a)
    {
        this.produkt = p;
        listener = (DialogListener)a;
    }
    
}
