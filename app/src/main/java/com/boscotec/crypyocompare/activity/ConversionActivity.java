package com.boscotec.crypyocompare.activity;

import android.content.Intent;
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
    private EditText mToCurrency, mBaseCurrency;
    private String toCurrency, baseCurrency;
    private Float rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBaseCurrency = findViewById(R.id.from_currency);
        mToCurrency = findViewById(R.id.to_currency);
        ImageView imageView = findViewById(R.id.currency_img);

        Intent intent = getIntent();
        if (intent.hasExtra("crypto") ) {
            Crypto crypto = (Crypto) intent.getSerializableExtra("crypto");

            int image = crypto.getImage();
            baseCurrency = crypto.getBaseCurrency();
            toCurrency = crypto.getToCurrency();
            String amount = crypto.getAmount();

            if(!TextUtils.isEmpty(baseCurrency) && !TextUtils.isEmpty(toCurrency)){
                imageView.setImageResource(image);
                mBaseCurrency.setHint("1 "+baseCurrency);
                mToCurrency.setText(toCurrency.concat(amount));
                rate = Float.valueOf(amount);
            }
        }

        mBaseCurrency.addTextChangedListener(new EditTextListener());
    }

    private class EditTextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String input = s.toString();

            if(rate != null){
                if (!TextUtils.isEmpty(input)) {
                    mToCurrency.setText(String.format("%s %s", toCurrency, String.valueOf(rate * Float.valueOf(input))));
                } else {
                    mBaseCurrency.setHint("1 "+baseCurrency);
                    mToCurrency.setText(String.format("%s %s", toCurrency, String.valueOf(rate)));
                }
            }
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
