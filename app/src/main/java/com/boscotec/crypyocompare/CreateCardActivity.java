package com.boscotec.crypyocompare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.boscotec.crypyocompare.api.ApiClient;
import com.boscotec.crypyocompare.api.IApi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by Johnbosco on 16-Oct-17.
 */

public class CreateCardActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Boolean ready= false;
    TextView amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createcard);

        amount = findViewById(R.id.amount);
        Spinner from = findViewById(R.id.spinnerfrom);
        Spinner to = findViewById(R.id.spinnerto);
        Button save = findViewById(R.id.save);

        //   ArrayList<String> fromCurrencies = new ArrayList<>();
        // ArrayAdapter<String> fromCurrenciesAdapter = new ArrayAdapter<>(this, R.layout.spinnerlayout, fromCurrencies);
        ArrayAdapter<CharSequence> fromCurrenciesAdapter = ArrayAdapter.createFromResource(this, R.array.fro_array, R.layout.spinnerlayout);
        fromCurrenciesAdapter.setDropDownViewResource(R.layout.spinnerlayout);
        from.setAdapter(fromCurrenciesAdapter);
        from.setOnItemSelectedListener(this);

       // ArrayAdapter<String> toCurrenciesAdapter = new ArrayAdapter<>(this, R.layout.spinnerlayout, fromCurrencies);
        ArrayAdapter<CharSequence> toCurrenciesAdapter = ArrayAdapter.createFromResource(this, R.array.to_array, R.layout.spinnerlayout);
        toCurrenciesAdapter.setDropDownViewResource(R.layout.spinnerlayout);
        to.setAdapter(toCurrenciesAdapter);
        to.setOnItemSelectedListener(this);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("Onclick of button");
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinnerfrom){
            ready = true;
        } else if(parent.getId() == R.id.spinnerto){
            if(ready){
                Timber.d("Ready to go online");
                //amount.setText(BuildConfig.API_BASE_URL);
                //convert("BTC", "ETH");

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
