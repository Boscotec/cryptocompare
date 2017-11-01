package com.boscotec.crypyocompare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.boscotec.crypyocompare.api.ApiClient;
import com.boscotec.crypyocompare.api.IApi;
import com.boscotec.crypyocompare.utils.Jsonhelper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversionActivity extends AppCompatActivity {

//    private String CRYPTO_TYPE = "BTC";
//    private int CRYPTO_POSITION =0;
    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor = null;
    EditText from_currency, to_currency;
//    public String BASE_CURRENCY = "BTC";
//    public String QUOTE_CURRENCY = "USD";
//    private String CURRENCY_SYMBOL = "";
//    public String CRYPTO_URL = "https://min-api.cryptocompare.com/data/price?fsym="+BASE_CURRENCY+"&tsyms="+QUOTE_CURRENCY;
    Resources res;


    @Override
    public void onBackPressed(){
    super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        from_currency = (EditText)findViewById(R.id.from_currency);
        to_currency = (EditText)findViewById(R.id.to_currency);
        ImageView imageView = (ImageView) findViewById(R.id.currency_img);

        sharedPref = getSharedPreferences(getString(R.string.shared_pref_cryptocompare), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Bundle extras = getIntent().getExtras();

        res = getResources();
       // String[] currArry = res.getStringArray(R.array.currency_array);

        if (extras != null) {

            String from = extras.getString("from");
            String to = extras.getString("to");

            if(from!=null && to!=null){
               if (from.equals("BTC")) {
                    imageView.setImageResource(R.drawable.btc_logo);
               } else if (from.equals("ETH")) {
                    imageView.setImageResource(R.drawable.eth_logo);
                 }
            }
        }

        from_currency.addTextChangedListener(new EditTextListener());
    }


    private class EditTextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public void convert(String from, final String to) {
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
                           // createCard.amount.setText(to.concat(" 0.00"));
                        }else {
                           // createCard.amount.setText(to+" "+conv.get(to));
                        }
                    }catch (Exception io){
                        io.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getBaseContext(), "error:  "+t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
