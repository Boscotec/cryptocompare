package com.boscotec.crypyocompare.activity;

import android.content.Context;
import android.support.annotation.IdRes;
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

import timber.log.Timber;

/**
 * Created by Johnbosco on 26-Oct-17.
 */

public class CreateCard extends RelativeLayout implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    TextView amount;
    private Spinner spinner;
    private RadioGroup radioGroup;
    private RadioButton radioButtonBtc;
    private RadioButton radioButtonEth;
    private Button buttonSave;
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
        amount = view.findViewById(R.id.amount);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioButtonBtc = view.findViewById(R.id.rb_btc);
        radioButtonBtc.setOnClickListener(this);
        radioButtonEth = view.findViewById(R.id.rb_eth);
        radioButtonEth.setOnClickListener(this);
        buttonSave = view.findViewById(R.id.save);
        buttonSave.setOnClickListener(this);
        spinner = view.findViewById(R.id.spinnerto);

        ArrayAdapter<CharSequence> toCurrenciesAdapter =
                ArrayAdapter.createFromResource(getContext(), R.array.to_array, R.layout.spinnerlayout);
        toCurrenciesAdapter.setDropDownViewResource(R.layout.spinnerlayout);
        spinner.setAdapter(toCurrenciesAdapter);
        //spinner.setSelection(1);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Timber.d("Ready to go online");
        if(adapterView.getId() == R.id.spinnerto){
            switch (radioGroup.getCheckedRadioButtonId()){
                case R.id.rb_btc:
                    mOnClickListener.onRadioButtonClick(radioButtonBtc.getText().toString(), spinner.getSelectedItem().toString());
                    break;
                case R.id.rb_eth:
                    mOnClickListener.onRadioButtonClick(radioButtonEth.getText().toString(), spinner.getSelectedItem().toString());
                    break;
                default:break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Timber.d("Nothing was selected");
    }

    @Override
    public void onClick(View view) {
        if(mOnClickListener == null) return;

        switch (view.getId()){
            case R.id.rb_btc:
                mOnClickListener.onRadioButtonClick(radioButtonBtc.getText().toString(), spinner.getSelectedItem().toString());
                break;
            case R.id.rb_eth:
                mOnClickListener.onRadioButtonClick(radioButtonEth.getText().toString(), spinner.getSelectedItem().toString());
                break;
            case R.id.save:
                mOnClickListener.onSaveClick(((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString(),
                        spinner.getSelectedItem().toString());
                break;
            default:break;
        }
    }

    public void setOnCardClickListener(CardClickListener mOnClickListener){
        this.mOnClickListener = mOnClickListener;
    }

    public interface CardClickListener {
        void onRadioButtonClick(String from, String currency);
        void onSaveClick(String from, String currency);
    }
}
