package com.boscotec.crypyocompare.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boscotec.crypyocompare.activity.MainActivity;
import com.boscotec.crypyocompare.api.ApiClient;
import com.boscotec.crypyocompare.api.IApi;
import com.boscotec.crypyocompare.model.Crypto;
import com.boscotec.crypyocompare.R;
import com.boscotec.crypyocompare.utils.Jsonhelper;
import com.boscotec.crypyocompare.utils.Utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Johnbosco on 16-Oct-17.
 */

public class CurrencyCardAdapter extends RecyclerView.Adapter<CurrencyCardAdapter.ViewHolder> {

    private ItemClickListener mOnClickListener;
    private List<Crypto> items;
    private Context context;

    public interface ItemClickListener {
        void onCardCloseClick(Crypto crypto);
        void onCardClick(Crypto crypto);
    }

    public CurrencyCardAdapter(Context context, ItemClickListener mOnClickListener) {
        this.context = context;
        this.mOnClickListener = mOnClickListener;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void swapItems(List<Crypto> newItems) {
        if(newItems == null) return;

        if(items != null) items.clear();
        items = newItems;

        //Toast.makeText(context, String.valueOf(newItems.size()), Toast.LENGTH_SHORT).show();

       // Force the RecyclerView to refresh
        this.notifyDataSetChanged();
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Crypto item = items.get(position);
        if(item == null) return;
        holder.bindType(item);
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

        void bindType(final Crypto currency) {
            icon.setImageResource(currency.getImage());
            amount.setText(String.format("%s 0.00", currency.getToCurrency()));
            currency.setAmount("0.00");

            if (Utils.isOnline(context)) {
                IApi connectToApi = ApiClient.getClient().create(IApi.class);
                final Call<ResponseBody> call = connectToApi.grabConversion(currency.getBaseCurrency(), currency.getToCurrency());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            StringBuilder sb = new StringBuilder();
                            try {
                                BufferedReader br = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                                for (String temp; ((temp = br.readLine()) != null); ) {
                                    sb.append(temp);
                                }
                                HashMap<String, String> conv = Jsonhelper.getValue(sb.toString());
                                currency.setAmount(conv.get(currency.getToCurrency()));
                                amount.setText(String.format("%s %s", currency.getToCurrency(), conv.get(currency.getToCurrency())));
                            } catch (Exception io) {
                                io.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        }

        @Override
        public void onClick(View view) {
            Crypto card = items.get(getAdapterPosition());
            if(card == null) return;

            if (mOnClickListener != null){
                if (view.getId() == R.id.close) mOnClickListener.onCardCloseClick(card);
                else mOnClickListener.onCardClick(card);
           }
        }

    }

}
