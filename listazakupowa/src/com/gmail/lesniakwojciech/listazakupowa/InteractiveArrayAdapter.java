package com.gmail.lesniakwojciech.listazakupowa;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class InteractiveArrayAdapter extends ArrayAdapter<ModelProdukt>
{
    
    private final Activity activity;
    private final List<ModelProdukt> list;
    
    public InteractiveArrayAdapter(final Activity a, final List<ModelProdukt> l)
    {
        super(a, R.layout.listitemprodukt, l);
        activity = a;
        list = l;
    }
    
    private static class ViewHolder
    {
        private TextView textViewNazwa;
        private TextView textViewSklepCena;
    }
    
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent)
    {
        View view = convertView;
        ViewHolder viewHolder;
        if(null == view)
        {
            view = activity.getLayoutInflater().inflate(R.layout.listitemprodukt, null);
            viewHolder = new ViewHolder();
            viewHolder.textViewNazwa = (TextView)view.findViewById(R.id.LPRODUKTYNAZWA);
            viewHolder.textViewSklepCena = (TextView)view.findViewById(R.id.LPRODUKTSKLEPCENA);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)view.getTag();
        }
        final ModelProdukt produkt = list.get(position);
        viewHolder.textViewNazwa.setText(produkt.getNazwa());
        final NumberFormat numberFormat = new DecimalFormat("#0.00");
        viewHolder.textViewSklepCena.setText(produkt.getSklep() + ": " + numberFormat.format(produkt.getCena()));
        /*view.setOnClickListener(new OnClickListener()
        {
            private final InteractiveArrayAdapter interactiveArrayAdapter = iAA;
            public void onClick(View view1)
            {
                final String wybrana = ((ViewHolder)view1.getTag()).textView.getText().toString();
                int w = -1;
                for(int i = 0; i < list.size(); ++i)
                {
                    if(!wybrana.equals(list.get(i).getName()))
                    {
                        list.get(i).setCheked(false);
                    }
                    else
                    {
                        w = list.get(i).getIdentyfikator();
                        list.get(i).setCheked(true);
                    }
                }
                ((WspoldzielenieDanych)activity).wspoldziel(w);
                interactiveArrayAdapter.notifyDataSetChanged();
                Toast.makeText(context, wybrana, Toast.LENGTH_LONG).show();
            }
            
        });*/
        /*holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton cb, boolean bln) {
                ((Model)holder.checkBox.getTag()).setCheked(cb.isSelected());
            }
            
        });*/
        return view;
    }
    
}
