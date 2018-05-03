package com.boscotec.crypyocompare.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.boscotec.crypyocompare.R;
import com.boscotec.crypyocompare.model.Crypto;

import timber.log.Timber;

/**
 * Created by Johnbosco on 26-Oct-17.
 */

public class CreateCard extends RelativeLayout implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private TextView textViewAmount;
    private Spinner spinner;
    private RadioGroup radioGroup;
    private CardClickListener mOnClickListener;

    public CreateCard(Context context) {
        super(context);
        init();
    }

    public CreateCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CreateCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        View view = inflate(getContext(), R.layout.activity_createcard, this);
        textViewAmount = view.findViewById(R.id.amount);
        radioGroup = view.findViewById(R.id.radioGroup);
        RadioButton radioButtonBtc = view.findViewById(R.id.rb_btc);
        radioButtonBtc.setOnClickListener(this);
        RadioButton radioButtonEth = view.findViewById(R.id.rb_eth);
        radioButtonEth.setOnClickListener(this);
        Button buttonSave = view.findViewById(R.id.save);
        buttonSave.setOnClickListener(this);
        spinner = view.findViewById(R.id.spinnerto);

        ArrayAdapter<CharSequence> toCurrenciesAdapter =
                ArrayAdapter.createFromResource(getContext(), R.array.to_array, R.layout.spinnerlayout);
        toCurrenciesAdapter.setDropDownViewResource(R.layout.spinnerlayout);
        spinner.setAdapter(toCurrenciesAdapter);
        //spinner.setSelection(1);
        spinner.setOnItemSelectedListener(this);
    }

    public String getAmount(){
        return textViewAmount.getText().toString();
    }

    public void setAmount(String amount){
        textViewAmount.setText(amount);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Timber.d("Ready to go online");
        if(adapterView.getId() == R.id.spinnerto){
            switcher(radioGroup.getCheckedRadioButtonId());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Timber.d("Nothing was selected");
    }

    @Override
    public void onClick(View view) {
        if(mOnClickListener == null) return;
        switcher(view.getId());
    }

    public void switcher(int id){
        Crypto crypto = new Crypto();
        crypto.setToCurrency(spinner.getSelectedItem().toString());

        switch (id){
            case R.id.rb_btc:
                crypto.setBaseCurrency("BTC");
                crypto.setImage(R.drawable.btc_logo);
                mOnClickListener.onRadioButtonClick(crypto);
                break;
            case R.id.rb_eth:
                crypto.setBaseCurrency("ETH");
                crypto.setImage(R.drawable.eth_logo);
                mOnClickListener.onRadioButtonClick(crypto);
                break;
            case R.id.save:

                if(radioGroup.getCheckedRadioButtonId() == R.id.rb_btc){
                    crypto.setBaseCurrency("BTC");
                    crypto.setImage(R.drawable.btc_logo);
                } else{
                    crypto.setBaseCurrency("ETH");
                    crypto.setImage(R.drawable.eth_logo);
                }
                mOnClickListener.onSaveClick(crypto);
                break;
            default:break;
        }
    }

    public void setOnCardClickListener(CardClickListener mOnClickListener){
        this.mOnClickListener = mOnClickListener;
    }

    public interface CardClickListener {
        void onRadioButtonClick(Crypto crypto);
        void onSaveClick(Crypto crypto);
    }
}
