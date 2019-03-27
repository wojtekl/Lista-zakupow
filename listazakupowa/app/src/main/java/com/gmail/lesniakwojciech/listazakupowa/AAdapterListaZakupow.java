package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class AAdapterListaZakupow
        extends ArrayAdapter<ModelProdukt> {
    private static final String CENA_FORMAT = "#0.00";

    private final int resource;

    private final LayoutInflater inflater;

    public AAdapterListaZakupow(final Context context, final int resource,
                                final List<ModelProdukt> objects) {
        super(context, resource, objects);
        this.resource = resource;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = inflater.inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textViewNazwa = convertView.findViewById(R.id.aalzTvNazwa);
            viewHolder.textViewSklep = convertView.findViewById(R.id.aalzTvSklep);
            viewHolder.textViewCena = convertView.findViewById(R.id.aalzTvCena);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ModelProdukt model = getItem(position);
        viewHolder.textViewNazwa.setText(model.getNazwa());
        viewHolder.textViewSklep.setText(model.getSklep());
        viewHolder.textViewCena.setText((new DecimalFormat(CENA_FORMAT)).format(model.getCena()));

    /* view.setOnClickListener(new OnClickListener()
    {
      private final InteractiveArrayAdapter interactiveArrayAdapter = iAA;
      public void onClick(final View view1)
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
    }
    ); */

    /* viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
    {
      public void onCheckedChanged(final CompoundButton cb, final boolean bln)
      {
        ((Model)viewHolder.checkBox.getTag()).setCheked(cb.isSelected());
      }
    }
    ); */

        return convertView;
    }

    private static class ViewHolder {
        private TextView textViewNazwa, textViewSklep, textViewCena;
    }
}
