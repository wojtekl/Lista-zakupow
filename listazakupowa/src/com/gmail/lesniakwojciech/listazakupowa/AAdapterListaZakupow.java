package com.gmail.lesniakwojciech.listazakupowa;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.List;

public class AAdapterListaZakupow 
  extends ArrayAdapter<ModelProdukt>
{
  private final Activity activity;
  private final List<ModelProdukt> list;
  
  public AAdapterListaZakupow(final Activity activity, final List<ModelProdukt> list)
  {
    super(activity, R.layout.aadapterlistazakupow, list);
    
    this.activity = activity;
    this.list = list;
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
      view = activity.getLayoutInflater().inflate(R.layout.aadapterlistazakupow, null);
      viewHolder = new ViewHolder();
      viewHolder.textViewNazwa = (TextView)view.findViewById(R.id.LPRODUKTYNAZWA);
      viewHolder.textViewSklepCena = (TextView)view.findViewById(R.id.LPRODUKTSKLEPCENA);
      view.setTag(viewHolder);
    }
    else
    {
      viewHolder = (ViewHolder)view.getTag();
    }
    final ModelProdukt model = list.get(position);
    viewHolder.textViewNazwa.setText(model.getNazwa());
    viewHolder.textViewSklepCena.setText(new StringBuilder()
      .append(model.getSklep())
      .append(": ")
      .append((new DecimalFormat("#0.00")).format(model.getCena()))
      .toString()
    );
    
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
    
    return view;
  }
}
