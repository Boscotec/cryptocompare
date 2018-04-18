package com.boscotec.crypyocompare.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import com.boscotec.crypyocompare.R;
import com.boscotec.crypyocompare.model.Crypto;

public class ConversionActivity extends AppCompatActivity {
    EditText to_currency;
    Float xchng_rate;
    String to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EditText from_currency = findViewById(R.id.from_currency);
        to_currency = findViewById(R.id.to_currency);
        ImageView imageView = findViewById(R.id.currency_img);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Crypto crypto = (Crypto) extras.getSerializable("crypto");
           // String xchange = extras.getString("crypto");
            //String amount = extras.getString("amount");
            
            //String from = xchange.split(",")[0];
            //final String to = xchange.split(",")[1];

          //  if(!TextUtils.isEmpty(from) && !TextUtils.isEmpty(to)){
          //      if (from.contains("Bitcoin")) {
          //          imageView.setImageResource(R.drawable.btc_logo);
          //          from_currency.setHint("1 BTC");
          //      } else if (from.contains("Ethereum")) {
          //          imageView.setImageResource(R.drawable.eth_logo);
          //          from_currency.setHint("1 ETH");
          //      }

          //      xchng_rate = Float.valueOf(amount);
          //      if (TextUtils.isEmpty(amount)) {
          //          to_currency.setText(to.concat(" 0.00"));
          //      } else {
          //          to_currency.setText(String.format("%s %s", to, amount));
          //      }
          //  }
        }

        from_currency.addTextChangedListener(new EditTextListener());
    }

    private class EditTextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String input = s.toString();
            //if(xchng_rate != null){
            //    if (!TextUtils.isEmpty(input)) {
            //        to_currency.setText(String.format("%s %s", to, String.valueOf(xchng_rate * Float.valueOf(input))));
            //    } else {
            //        to_currency.setText(String.format("%s %s", to, String.valueOf(xchng_rate * 0)));
            //    }
            //}
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
