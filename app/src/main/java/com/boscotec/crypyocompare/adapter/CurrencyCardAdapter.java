package com.boscotec.crypyocompare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boscotec.crypyocompare.utils.Jsonhelper;
import com.boscotec.crypyocompare.R;
import com.boscotec.crypyocompare.api.ApiClient;
import com.boscotec.crypyocompare.api.IApi;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Johnbosco on 16-Oct-17.
 */

public class CurrencyCardAdapter extends RecyclerView.Adapter<CurrencyCardAdapter.ViewHolder> {
    ArrayList<String> currencies;
    Context context;

    private ItemClickListener mOnClickListener;
    public interface ItemClickListener {
        void onCloseClick(int close);
        void onCardClick(int card);
    }

    public CurrencyCardAdapter(Context context, ItemClickListener mOnClickListener) {
        this.context = context;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public int getItemCount() {
        if(currencies == null){
            return 0;
        }else{
            return currencies.size();
        }
    }

    public void swapItems(ArrayList<String> newItems) {
        if(currencies != null) currencies.clear();
        currencies = newItems;
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
        String currency = currencies.get(position);
        if(TextUtils.isEmpty(currency)) return;
        holder.bindType(currency);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView close;
        ImageView icon;
        TextView amount;

        public ViewHolder(View view) {
            super(view);
            amount = view.findViewById(R.id.amount);
            close = view.findViewById(R.id.close);
            icon = view.findViewById(R.id.icon);

            close.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bindType(String currency) {
            final String from = currency.split(",")[0];
            final String to = currency.split(",")[1];

            amount.setText(String.format("%s 0.00", to));

            if (from.contains("Bitcoin")) {icon.setImageResource(R.drawable.btc_logo);}
            else if (from.contains("Ethereum")) {icon.setImageResource(R.drawable.eth_logo);}

            IApi connectToApi = ApiClient.getClient().create(IApi.class);
            final Call<ResponseBody> call = connectToApi.grabConversion(from, to);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        StringBuilder sb = new StringBuilder();
                        try{
                            BufferedReader br = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                            for (String temp; ((temp = br.readLine()) != null); ) {
                                sb.append(temp);
                            }

                            JSONObject jsonObject = new JSONObject(sb.toString());
                            HashMap<String, String> conv = Jsonhelper.getValue(jsonObject);
                            if(conv == null){
                                amount.setText(String.format("%s 0.00", to));
                            }else {
                                amount.setText(String.format("%s %s", to, conv.get(to)));
                            }
                        }catch (Exception io){
                            io.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                   // Toast.makeText(context, "Error:  "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onClick(View view) {
            if(mOnClickListener == null) return;
            if(view.getId() == R.id.close){
                mOnClickListener.onCloseClick(getAdapterPosition());
            }else{
                mOnClickListener.onCardClick(getAdapterPosition());
            }
        }
    }
}
