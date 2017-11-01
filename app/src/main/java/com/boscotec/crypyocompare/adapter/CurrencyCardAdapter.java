package com.boscotec.crypyocompare.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boscotec.crypyocompare.BuildConfig;
import com.boscotec.crypyocompare.ConversionActivity;
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
import timber.log.Timber;

/**
 * Created by Johnbosco on 16-Oct-17.
 */

public class CurrencyCardAdapter extends RecyclerView.Adapter<CurrencyCardAdapter.ViewHolder> {

    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor = null;
    ArrayList<String> currencies;
    private Context context;

    public CurrencyCardAdapter(Context context, ArrayList<String> currencies, SharedPreferences sharedPref) {
        this.context = context;
        this.currencies = currencies;
        this.sharedPref = sharedPref;
        this.editor = sharedPref.edit();
    }

    @Override
    public int getItemCount() { return currencies.size();}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if(currencies.size()!=0) {
            String xchange = currencies.get(position);
            final String from = xchange.split(",")[0];
            final String to = xchange.split(",")[1];

            if (from.equals("BTC")) {
                holder.icon.setImageResource(R.drawable.btc_logo);
            } else if (from.equals("ETH")) {
                holder.icon.setImageResource(R.drawable.eth_logo);
            }

            holder.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Timber.d("Clicked on: %s", holder.getAdapterPosition());
                    int all_saved = sharedPref.getInt("num_saved", 0);
                    for (int i = 0; i < all_saved; i++) {
                        if (sharedPref.getString("saved_" + i, BuildConfig.FLAVOR).equals(currencies.get(holder.getAdapterPosition()))) {
                            currencies.remove(holder.getAdapterPosition());
                            editor.remove("saved_" + i);
                            editor.putInt("num_saved", all_saved - 1);
                            editor.commit();
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                            return;
                        }
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String _xchange = currencies.get(holder.getAdapterPosition());
                    final String _from = _xchange.split(",")[0];
                    final String _to = _xchange.split(",")[1];

                    Intent intent = new Intent(context, ConversionActivity.class);
                    intent.putExtra("from", _from);
                    intent.putExtra("to", _to);
                    context.startActivity(intent);
                }
            });

            holder.amount.setText(to.concat(" 0.00"));

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
                                holder.amount.setText(to.concat(" 0.00"));
                            }else {
                                holder.amount.setText(to+" "+conv.get(to));
                            }
                        }catch (Exception io){
                            io.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(context, "Error:  "+t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView close;
        ImageView icon;
        TextView amount;

        public ViewHolder(View view) {
            super(view);
            amount = view.findViewById(R.id.amount);
            close = view.findViewById(R.id.close);
            icon = view.findViewById(R.id.icon);
        }
    }
}
