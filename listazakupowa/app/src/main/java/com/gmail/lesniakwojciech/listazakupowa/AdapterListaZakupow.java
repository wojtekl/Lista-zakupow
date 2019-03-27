package com.gmail.lesniakwojciech.listazakupowa;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdapterListaZakupow
        extends RecyclerView.Adapter<AdapterListaZakupow.ViewHolderListaZakupow> {
    private static final String CENA_FORMAT = "#0.00";

    private final List<ModelProdukt> dataset;
    private OnItemClickListener onItemClickListener;
    private View selectedItem = null;
    private Drawable holderBackground = null;

    public AdapterListaZakupow(final List<ModelProdukt> dataset)
    {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public ViewHolderListaZakupow onCreateViewHolder(@NonNull final ViewGroup parent,
                                                     final int viewType) {
        return new ViewHolderListaZakupow(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapterlistazakupow, parent, false), onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderListaZakupow holder, final int position) {
        final ModelProdukt model = dataset.get(position);
        holder.nazwa.setText(model.getNazwa());
        holder.sklep.setText(model.getSklep());
        holder.cena.setText((new DecimalFormat(CENA_FORMAT)).format(model.getCena()));
    }

    @Override
    public int getItemCount() {
        return this.dataset.size();
    }

    public ModelProdukt getItem(final int position) {
        return this.dataset.get(position);
    }

    public void addItem(final ModelProdukt model) {
        this.dataset.add(model);
        this.notifyItemInserted(dataset.size() - 1);
    }

    public void removeItem(final int position) {
        this.dataset.remove(position);
        this.notifyItemRemoved(position);
    }

    public List<ModelProdukt> getDataset() {
        return this.dataset;
    }

    public void addAll(final List<ModelProdukt> list) {
        this.dataset.addAll(list);
        this.notifyDataSetChanged();
    }

    public void clear() {
        dataset.clear();
        this.notifyDataSetChanged();
    }

    public void sort(final Comparator<ModelProdukt> comparator) {
        Collections.sort(dataset, comparator);
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelection(final View view) {
        if(null == selectedItem) {
            selectedItem = view;
            holderBackground = selectedItem.getBackground();
            selectedItem.setBackgroundColor(Color.LTGRAY);
        }
    }

    public void clearSelection() {
        ViewCompat.setBackground(selectedItem, holderBackground);
        holderBackground = null;
        selectedItem = null;
    }

    protected class ViewHolderListaZakupow
            extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        public final TextView nazwa, sklep, cena;

        private final OnItemClickListener onItemClickListener;

        public ViewHolderListaZakupow(final View view,
                                      final OnItemClickListener onItemClickListener) {
            super(view);
            this.nazwa = view.findViewById(R.id.aalzTvNazwa);
            this.sklep = view.findViewById(R.id.aalzTvSklep);
            this.cena = view.findViewById(R.id.aalzTvCena);
            this.onItemClickListener = onItemClickListener;
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            if(null != this.onItemClickListener) {
                this.onItemClickListener.onItemClick(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(final View view) {
            if(null != this.onItemClickListener) {
                this.onItemClickListener.onItemLongClick(view, getAdapterPosition());
                return true;
            }

            return false;
        }
    }

    protected interface OnItemClickListener {
        void onItemClick(final int position);
        void onItemLongClick(final View view, final int position);
    }
}
