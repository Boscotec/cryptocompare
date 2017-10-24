package com.boscotec.crypyocompare;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by Johnbosco on 16-Oct-17.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    ArrayList<String> currencies;
    private Context context;

    public CardAdapter(Context context, ArrayList<String> currencies) {
        this.context = context;
        this.currencies = currencies;
    }

    @Override
    public int getItemCount() { return currencies.size();}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
  //      final StudentPerformanceDataModel dataModel = dataModels.get(position);

        holder.amount.setText("$10000.900");
        ArrayAdapter<String> currenciesAdapter = new ArrayAdapter<>(context, R.layout.spinnerlayout, currencies);
        currenciesAdapter.setDropDownViewResource(R.layout.spinnerlayout);
        holder.spinner.setAdapter(currenciesAdapter);
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.getAdapterPosition();
                Timber.d("Clicked on: %s", holder.getAdapterPosition());
            }
        });

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView close;
        ImageView icon;
        TextView amount;
        Spinner spinner;

        public ViewHolder(View view) {
            super(view);
            amount = view.findViewById(R.id.amount);
            close = view.findViewById(R.id.close);
            icon = view.findViewById(R.id.icon);
            spinner = view.findViewById(R.id.spinner);
        }
    }
}