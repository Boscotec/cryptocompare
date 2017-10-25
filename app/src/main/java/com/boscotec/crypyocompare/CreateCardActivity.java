package com.boscotec.crypyocompare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boscotec.crypyocompare.api.ApiClient;
import com.boscotec.crypyocompare.api.IApi;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Johnbosco on 16-Oct-17.
 */

public class CreateCardActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView amount;
    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor = null;
    RadioButton  rb_btc;
    RadioButton  rb_eth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createcard);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        amount = (TextView) findViewById(R.id.amount);
        rb_btc = (RadioButton)findViewById(R.id.rb_btc);
        rb_eth = (RadioButton)findViewById(R.id.rb_eth);
        final Spinner to = (Spinner) findViewById(R.id.spinnerto);
        Button save = (Button) findViewById(R.id.save);

        sharedPref = getSharedPreferences(getString(R.string.shared_pref_cryptocompare), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        ArrayAdapter<CharSequence> toCurrenciesAdapter = ArrayAdapter.createFromResource(this, R.array.to_array, R.layout.spinnerlayout);
        toCurrenciesAdapter.setDropDownViewResource(R.layout.spinnerlayout);
        to.setAdapter(toCurrenciesAdapter);
        to.setOnItemSelectedListener(this);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Timber.d("Onclick of button");
                String currency_selected = to.getSelectedItem().toString().trim();
                if(rb_btc.isChecked()) {
                    if(!checkIfExists("BTC", currency_selected)) {
                        addCard("BTC", currency_selected);
                    }else{
                        Toast.makeText(getBaseContext(), "This card already exists!", Toast.LENGTH_LONG).show();
                    }
                }else if(rb_eth.isChecked()){
                    if(!checkIfExists("ETH", currency_selected)) {
                        addCard("ETH", currency_selected);
                    }else{
                        Toast.makeText(getBaseContext(), "This card already exists!", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }


    public boolean checkIfExists(String from_currency, String to_currency) {
        int all_saved = sharedPref.getInt("num_saved", 0);
        for(int i=0; i<all_saved; i++){
            if(sharedPref.getString("saved_"+i, BuildConfig.FLAVOR).equals(from_currency+","+to_currency)){
                return true;
            }
        }
        return false;
    }

    private void addCard(String from_currency,  String to_currency) {
        int all_saved = sharedPref.getInt("num_saved", 0);
        editor.putInt("num_saved", all_saved+1);
        editor.putString("saved_"+all_saved, from_currency+","+to_currency);
        editor.commit();
        Toast.makeText(this, "save successful", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinnerto){
            Timber.d("Ready to go online");
            if(rb_btc.isChecked()) {

            }else if(rb_eth.isChecked()){

            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Timber.d("Nothing was selected");
    }

    public void convert(String from, String to) {
        IApi connectToApi = ApiClient.getClient().create(IApi.class);
        final Call<ResponseBody> call = connectToApi.grabConversion(from, to);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    amount.setText(response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
