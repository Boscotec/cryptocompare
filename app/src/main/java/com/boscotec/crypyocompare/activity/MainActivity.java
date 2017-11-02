package com.boscotec.crypyocompare.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Toast;

import com.boscotec.crypyocompare.BuildConfig;
import com.boscotec.crypyocompare.adapter.CurrencyCardAdapter;
import com.boscotec.crypyocompare.utils.Jsonhelper;
import com.boscotec.crypyocompare.R;
import com.boscotec.crypyocompare.api.ApiClient;
import com.boscotec.crypyocompare.api.IApi;
import com.boscotec.crypyocompare.utils.Util;

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

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    RecyclerView recyclerView;
    CurrencyCardAdapter adapter;
    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor = null;
    ArrayList<String> currencies;
    CreateCard createCard;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createCard = (CreateCard) findViewById(R.id.createcard);
        createCard.to.setOnItemSelectedListener(this);
        createCard.save.setOnClickListener(this);
        createCard.rb_btc.setOnClickListener(this);
        createCard.rb_eth.setOnClickListener(this);

        sharedPref = getSharedPreferences(getString(R.string.shared_pref_cryptocompare), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        currencies = new ArrayList<>();
        adapter = new CurrencyCardAdapter(this, currencies, sharedPref);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        if(getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else{
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setAdapter(adapter);

        refresh();
        if(sharedPref.getAll().isEmpty() || sharedPref.getInt("num_saved", 0)==0) {
            Toast.makeText(this, "No card, click on the add button", Toast.LENGTH_LONG).show();
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                if(createCard.getVisibility()==View.VISIBLE){
                    hideCard();
                }else{
                    showCard();
                }
                break;
            case R.id.save:
                Timber.d("Onclick of button");
                String currency_selected = createCard.to.getSelectedItem().toString().trim();
                if(createCard.rb_btc.isChecked()) {
                    if(!checkIfExists("BTC", currency_selected)) {
                        addCard("BTC", currency_selected);
                    }else{
                        Toast.makeText(getBaseContext(), "This card already exists!", Toast.LENGTH_LONG).show();
                    }
                }else if(createCard.rb_eth.isChecked()){
                    if(!checkIfExists("ETH", currency_selected)) {
                        addCard("ETH", currency_selected);
                    }else{
                        Toast.makeText(getBaseContext(), "This card already exists!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.rb_btc:
                convert("BTC", createCard.to.getSelectedItem().toString());
                break;
            case R.id.rb_eth:
                convert("ETH", createCard.to.getSelectedItem().toString());
                break;
            default:
                break;
        }
    }


    private void showCard(){
        createCard.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_clear_white);
    }

    private void hideCard(){
        createCard.setVisibility(View.GONE);
        fab.setImageResource(R.drawable.ic_add);
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


    public void addCard(String from_currency,  String to_currency) {
        int all_saved = sharedPref.getInt("num_saved", 0);
        editor.putInt("num_saved", all_saved+1);
        editor.putString("saved_"+all_saved, from_currency+","+to_currency);
        editor.commit();
        Toast.makeText(getBaseContext(), "save successful", Toast.LENGTH_LONG).show();
        hideCard();
        refresh();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinnerto){
            Timber.d("Ready to go online");
            if(createCard.rb_btc.isChecked()) {
                convert("BTC", createCard.to.getSelectedItem().toString());
            }else if(createCard.rb_eth.isChecked()){
                convert("ETH", createCard.to.getSelectedItem().toString());
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Timber.d("Nothing was selected");
    }

    private void refresh(){
        currencies.clear();
        int all_saved = sharedPref.getInt("num_saved", 0);
        for(int i=0; i<all_saved; i++){
            currencies.add(sharedPref.getString("saved_"+i, BuildConfig.FLAVOR));
        }
        adapter.notifyDataSetChanged();
    }

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onResume(){
        super.onResume();
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_about) {
            Intent intent = new Intent(this, AboutUs.class);
            startActivity(intent);
            return true;
        }else if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();
        hideCard();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        System.exit(0);
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
                            createCard.amount.setText(to.concat(" 0.00"));
                        }else {
                            createCard.amount.setText(to+" "+conv.get(to));
                        }
                    }catch (Exception io){
                        io.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

}
