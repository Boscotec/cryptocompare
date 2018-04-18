package com.boscotec.crypyocompare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.boscotec.crypyocompare.model.Crypto;
import com.boscotec.crypyocompare.R;
import java.util.ArrayList;

/**
 * Created by Johnbosco on 16-Oct-17.
 */

public class CurrencyCardAdapter extends RecyclerView.Adapter<CurrencyCardAdapter.ViewHolder> {
    private ArrayList<Crypto> currencies;
    private Context context;

    private ItemClickListener mOnClickListener;
    public interface ItemClickListener {
        void onCloseClick(int close);
        void onCardClick(Crypto crypto);
    }

    public CurrencyCardAdapter(Context context, ItemClickListener mOnClickListener) {
        this.context = context;
        this.mOnClickListener = mOnClickListener;
        currencies = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        if(currencies == null){
            return 0;
        }else{
            return currencies.size();
        }
    }

    public void swapItems(ArrayList<Crypto> newItems) {
        if(currencies != null) currencies.clear();
        currencies.addAll(newItems);
        if(newItems != null){
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Crypto currency = currencies.get(position);
        if(currency == null) return;
        holder.bindType(currency);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView close;
        ImageView icon;
        TextView amount;

         ViewHolder(View view) {
            super(view);
            amount = view.findViewById(R.id.amount);
            close = view.findViewById(R.id.close);
            icon = view.findViewById(R.id.icon);

            close.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

         void bindType(Crypto currency) {
            String rate = currency.getRate();
            amount.setText(String.format("%s %s", currency.getCurrency(), (TextUtils.isEmpty(rate)? "0.00" : rate)));
          //  icon.setImageResource(currency.getImage());


            //if (from.contains("Bitcoin")) { icon.setImageResource(R.drawable.btc_logo); from = "BTC";}
            //else if (from.contains("Ethereum")) {icon.setImageResource(R.drawable.eth_logo); from = "ETH";}
        }

        @Override
        public void onClick(View view) {
            Crypto card = currencies.get(getAdapterPosition());
            if(card == null) return;

            if (mOnClickListener != null){
                if (view.getId() == R.id.close) {
                    mOnClickListener.onCloseClick(getAdapterPosition());
                } else {
                    mOnClickListener.onCardClick(card);
                }
           }

        }
    }
}
