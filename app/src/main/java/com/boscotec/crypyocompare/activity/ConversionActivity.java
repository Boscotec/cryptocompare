package com.boscotec.crypyocompare.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.boscotec.crypyocompare.R;
import com.boscotec.crypyocompare.api.ApiClient;
import com.boscotec.crypyocompare.api.IApi;
import com.boscotec.crypyocompare.utils.Jsonhelper;
import com.boscotec.crypyocompare.utils.Util;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversionActivity extends AppCompatActivity {
    EditText from_currency, to_currency;
    Float xchng_rate;
    String to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        from_currency = findViewById(R.id.from_currency);
        to_currency = findViewById(R.id.to_currency);
        ImageView imageView = findViewById(R.id.currency_img);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String xchange = extras.getString("currencies");
            final String from = xchange.split(",")[0];
            final String to = xchange.split(",")[1];

            if(TextUtils.isEmpty(from) && TextUtils.isEmpty(to)){
                if (from.contains("Bitcoin")) {
                    imageView.setImageResource(R.drawable.btc_logo);
                    from_currency.setHint("1 BTC");
                } else if (from.equals("Ethereum")) {
                    imageView.setImageResource(R.drawable.eth_logo);
                    from_currency.setHint("1 ETH");
                }
                convert(from, to);
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
            if(!Util.isOnline(ConversionActivity.this)) {
                Toast.makeText(getBaseContext(), "Data connectivity is OFF", Toast.LENGTH_SHORT).show();
                return;
             }

            //String input = from_currency.getText().toString().trim();
            // if (!input.isEmpty()) {
            //    if (xchng_rate != null) {
            //        to_currency.setText(to + " " + String.valueOf(xchng_rate * Float.valueOf(input)));
            //    }
            // } else {
            //    if (xchng_rate != null) {
            //        to_currency.setText(to + " " + String.valueOf(xchng_rate * 0));
            //    }
           // }

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    public void convert(String from, final String to) {
        if(!Util.isOnline(this)) {
            Toast.makeText(this,"Data connectivity is OFF", Toast.LENGTH_SHORT).show();
            return;
        }

        IApi connectToApi = ApiClient.getClient().create(IApi.class);
        final Call<ResponseBody> call = connectToApi.grabConversion(from, to);
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

                        JSONObject jsonObject = new JSONObject(sb.toString());
                        HashMap<String, String> conv = Jsonhelper.getValue(jsonObject);
                        xchng_rate = Float.valueOf(conv.get(to));
                        if (conv.isEmpty()) {
                            to_currency.setText(to.concat(" 0.00"));
                        } else {
                            to_currency.setText(to + " " + conv.get(to));
                        }
                    } catch (Exception io) {
                        io.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getBaseContext(), "error:  " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
