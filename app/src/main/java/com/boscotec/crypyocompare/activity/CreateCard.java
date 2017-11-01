package com.boscotec.crypyocompare.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.boscotec.crypyocompare.R;

/**
 * Created by Johnbosco on 26-Oct-17.
 */

public class CreateCard extends RelativeLayout {
    TextView amount;
    RadioButton rb_btc;
    RadioButton  rb_eth;
    Spinner to;
    Button save;

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
        inflate(getContext(), R.layout.activity_createcard, this);
        this.amount = (TextView) findViewById(R.id.amount);
        this.rb_btc = (RadioButton)findViewById(R.id.rb_btc);
        this.rb_eth = (RadioButton)findViewById(R.id.rb_eth);
        this.to = (Spinner) findViewById(R.id.spinnerto);
        this.save = (Button) findViewById(R.id.save);

        ArrayAdapter<CharSequence> toCurrenciesAdapter =
                ArrayAdapter.createFromResource(getContext(), R.array.to_array, R.layout.spinnerlayout);
        toCurrenciesAdapter.setDropDownViewResource(R.layout.spinnerlayout);
        to.setAdapter(toCurrenciesAdapter);
    }
}
