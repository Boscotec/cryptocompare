package com.boscotec.crypyocompare.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.boscotec.crypyocompare.BuildConfig;
import com.boscotec.crypyocompare.adapter.CurrencyCardAdapter;
import com.boscotec.crypyocompare.model.Crypto;
import com.boscotec.crypyocompare.utils.Jsonhelper;
import com.boscotec.crypyocompare.R;
import com.boscotec.crypyocompare.api.ApiClient;
import com.boscotec.crypyocompare.api.IApi;
import com.boscotec.crypyocompare.utils.Utils;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

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

public class MainActivity extends AppCompatActivity implements CreateCard.CardClickListener,
        View.OnClickListener, CurrencyCardAdapter.ItemClickListener {

    RecyclerView recyclerView;
    CurrencyCardAdapter adapter;
    SharedPreferences sharedPref = null;
    SharedPreferences.Editor editor = null;
    ArrayList<Crypto> currencies;
    CreateCard createCard;
    FloatingActionButton fab;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        createCard = findViewById(R.id.createcard);
        createCard.setOnCardClickListener(this);

        sharedPref = getSharedPreferences(getString(R.string.shared_pref_cryptocompare), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        currencies = new ArrayList<>();
        adapter = new CurrencyCardAdapter(this, this);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        recyclerView =  findViewById(R.id.recycler);
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
    }

    @Override
    public void onCloseClick(int close) {
    /*    Timber.d("Clicked on: %s", close);
        int all_saved = sharedPref.getInt("num_saved", 0);
        for (int i = 0; i < all_saved; i++) {
            if (sharedPref.getString("saved_" + i, BuildConfig.FLAVOR).equals(currencies.get(close))) {
                currencies.remove(close);
                editor.remove("saved_" + i);
                editor.putInt("num_saved", all_saved - 1);
                editor.commit();
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                refresh();
                break;
            }
        }
        */
    }

    @Override
    public void onCardClick(Crypto card) {
        Intent intent = new Intent(this, ConversionActivity.class);
        intent.putExtra("crypto", card);
        startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
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

    private void refresh(){
        currencies.clear();
        int all_saved = sharedPref.getInt("num_saved", 0);
        for(int i=0; i<all_saved; i++){
           String s = sharedPref.getString("saved_"+i, BuildConfig.FLAVOR);
           String from = s.split(",")[0];
           String to = s.split(",")[1];
           currencies.add(new Crypto(to, "1000", Integer.valueOf(from)));
        }
        adapter.swapItems(currencies);

        if(currencies.size() == 0){
            teachApp();
        }
    }

    @TargetApi(17)
    private void teachApp() {
        final SpannableString sassyDesc = new SpannableString("It allows you to close the app");
        sassyDesc.setSpan(new StyleSpan(Typeface.ITALIC), sassyDesc.length(), sassyDesc.length(), 0);

        // We have a sequence of targets, so lets build it!
        final TapTargetSequence sequence = new TapTargetSequence(this)
                .continueOnCancel(true)
                .targets(
                    // This tap target will target the back button, we just need to pass its containing toolbar
                    TapTarget.forToolbarNavigationIcon(toolbar, "This is the back button", sassyDesc).id(1),
                   // Likewise, this tap target will target the toolbar overflow button
                    TapTarget.forToolbarOverflow(toolbar, "This will show more options", "See more option").id(3),
                    TapTarget.forView(findViewById(R.id.fab), "Fab", "Click here to create a card").id(4)
                            .icon(getResources().getDrawable(R.drawable.ic_add_24dp))
                            .outerCircleColor(R.color.colorPrimary).targetCircleColor(R.color.white)
                            .textColor(R.color.white).textTypeface(Typeface.SANS_SERIF)
                            .dimColor(android.R.color.black).targetRadius(40)
                )
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards to the sequence
                    @Override
                    public void onSequenceFinish() {
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        Log.d("TapTargetView: ", "Clicked on " + lastTarget.id());
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        Log.d("TapTargetView: ", "Cancel at " + lastTarget.id());
                    }
                });

        sequence.start();
    }

    private void showCard(){
        createCard.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_clear_white);
    }

    private void hideCard(){
        createCard.setVisibility(View.GONE);
        fab.setImageResource(R.drawable.ic_add);
    }

    @Override
    public void onRadioButtonClick(String from, String currency) {
      convert(from, currency);
    }

    @Override
    public void onSaveClick(String from, String to, String amount) {
        Timber.d("Onclick of button");
        if(!checkIfExists(from, to)){
            addCard(from, to, amount);
        }else {
            Toast.makeText(getBaseContext(), "This card already exists!", Toast.LENGTH_LONG).show();
        }
    }

    public void update(String from, String to, String amount){
        int all_saved = sharedPref.getInt("num_saved", 0);
        for(int i=0; i<all_saved; i++){
            String json = sharedPref.getString("saved_"+i, BuildConfig.FLAVOR);
            try {
                JSONObject jsonObject = new JSONObject(json);
                HashMap<String, String> conv = Jsonhelper.getValue(jsonObject);
                if(conv.get("crypto").equals(from+to)){

                    //return true;
                    //update

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                if(createCard.getVisibility()==View.VISIBLE){hideCard();}
                else{showCard();}
                break;
            default:break;
        }
    }

    public boolean checkIfExists(String from, String to) {
        int all_saved = sharedPref.getInt("num_saved", 0);
        for(int i=0; i<all_saved; i++){
            String json = sharedPref.getString("saved_"+i, BuildConfig.FLAVOR);
            try {
                JSONObject jsonObject = new JSONObject(json);
                HashMap<String, String> conv = Jsonhelper.getValue(jsonObject);
                if(conv.get("crypto").equals(from+to)){
                    return true;
                }
            }catch (Exception e){ e.printStackTrace();}
        }
        return false;
    }

    public void addCard(String from,  String to, String amount) {
        String json = String.format("{\"crypto\":\"%s\", \"amount\":\"%s\"}", from+to, amount);

        int all_saved = sharedPref.getInt("num_saved", 0);
        editor.putInt("num_saved", all_saved+1);
        editor.putString("saved_"+all_saved, json);
        editor.commit();
        Toast.makeText(getBaseContext(), "save successful", Toast.LENGTH_LONG).show();
        hideCard();
        refresh();
    }

    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        private GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
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
       return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
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
            startActivity(new Intent(this, AboutUs.class));
            return true;
        }else if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    public void convert(String from, final String to) {
        if(!Utils.isOnline(this)) {
            Toast.makeText(this,"Data connectivity is OFF", Toast.LENGTH_SHORT).show();
            return;
        }

        if (from.contains("Bitcoin")) { from = "BTC";}
        else if (from.contains("Ethereum")) { from = "ETH";}

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

                        Toast.makeText(MainActivity.this, to+" "+ conv.get(to), Toast.LENGTH_LONG).show();

                        createCard.setAmount(conv.isEmpty()? String.format("%s 0.00", to) : conv.get(to));
                                /* String.format("%s %s", to, conv.get(to)) */
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
