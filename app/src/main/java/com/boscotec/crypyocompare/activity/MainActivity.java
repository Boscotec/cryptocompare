package com.boscotec.crypyocompare.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.boscotec.crypyocompare.adapter.CurrencyCardAdapter;
import com.boscotec.crypyocompare.model.Crypto;
import com.boscotec.crypyocompare.utils.Jsonhelper;
import com.boscotec.crypyocompare.R;
import com.boscotec.crypyocompare.api.ApiClient;
import com.boscotec.crypyocompare.api.IApi;
import com.boscotec.crypyocompare.utils.Utils;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements CreateCard.CardClickListener, CurrencyCardAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<Crypto>> {

    private CurrencyCardAdapter adapter;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private SharedPreferences sharedPref = null;
    private static final int LOADER_ID = 1234;
    private static final String prefSaved = "prefSaved";
    private static final String prefFirstTime = "prefFirstTime";
    private CreateCard createCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createCard = findViewById(R.id.createcard);
        createCard.setOnCardClickListener(this);

        sharedPref = getSharedPreferences(getString(R.string.shared_pref_cryptocompare), Context.MODE_PRIVATE);

        adapter = new CurrencyCardAdapter(this, this);

        RecyclerView mRecyclerView =  findViewById(R.id.recycler);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setAdapter(adapter);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCardView();
            }
        });

        if(sharedPref.getBoolean(prefFirstTime, true)){
            teachApp();
            sharedPref.edit().putBoolean(prefFirstTime, false).apply();
        }
    }

    @Override
    public void onResume(){
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        hideCard();
    }

    private void toggleCardView(){
        if(createCard.getVisibility() == View.VISIBLE){
           hideCard();
        }else{
           createCard.setVisibility(View.VISIBLE);
           fab.setImageResource(R.drawable.ic_clear_white);
        }
    }

    private void hideCard(){
        createCard.setVisibility(View.GONE);
        fab.setImageResource(R.drawable.ic_add);
    }

    public boolean saveCardToDb(Crypto card) {
        Timber.d("saving perform on card: %s to %s", card.getBaseCurrency(), card.getToCurrency());

        String[] array = TextUtils.split(sharedPref.getString(prefSaved, ""), "=");
        ArrayList<String> arrayToList = new ArrayList<>(Arrays.asList(array));
        arrayToList.add(card.toString());
        String[] sarray =  arrayToList.toArray(new String[arrayToList.size()]);
        return sharedPref.edit().putString(prefSaved, TextUtils.join("=", sarray)).commit();
    }

    public ArrayList<Crypto> getCardFromDb(){
        String[] array = TextUtils.split(sharedPref.getString(prefSaved, ""), "=");
        ArrayList<String> arrayToList = new ArrayList<>(Arrays.asList(array));

        ArrayList<Crypto> items = new ArrayList<>();
        for (String s : arrayToList) {
            String currencies = s.split("%")[0];
            int image = Integer.valueOf(s.split("%")[1]);
            String baseCurrency = currencies.split(",")[0];
            String toCurrency = currencies.split(",")[1];
            items.add(new Crypto(baseCurrency, toCurrency, image));
        }

        return items;
    }

    public boolean deleteCardFromDb(Crypto card) {
        Timber.d("delete perform on card: %s to %s", card.getBaseCurrency(), card.getToCurrency());
        String[] array = TextUtils.split(sharedPref.getString(prefSaved, ""), "=");
        ArrayList<String> arrayToList = new ArrayList<>(Arrays.asList(array));

        if (!arrayToList.contains(card.toString())) return false;

        arrayToList.remove(card.toString());

        String[] darray =  arrayToList.toArray(new String[arrayToList.size()]);
        return sharedPref.edit().putString(prefSaved, TextUtils.join("=", darray)).commit();
    }

    public boolean checkIfExistsInDb(Crypto card) {
        String[] array = TextUtils.split(sharedPref.getString(prefSaved, ""), "=");
        ArrayList<String> arrayToList = new ArrayList<>(Arrays.asList(array));
        return arrayToList.contains(card.toString());
    }

    @Override
    public void onSaveClick(Crypto crypto) {
        if(checkIfExistsInDb(crypto)) {
            Toast.makeText(getBaseContext(), "This card already exists!", Toast.LENGTH_LONG).show();
            return;
        }

        if(saveCardToDb(crypto)){
            Toast.makeText(getBaseContext(), "save successful", Toast.LENGTH_LONG).show();
            toggleCardView();
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        }else{
            Toast.makeText(getBaseContext(), "failed to save", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCardCloseClick(Crypto card) {
        if(deleteCardFromDb(card)) {
            Toast.makeText(getBaseContext(), "delete successful", Toast.LENGTH_LONG).show();
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onCardClick(Crypto card) {
        Intent intent = new Intent(this, ConversionActivity.class);
        intent.putExtra("crypto", card);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        System.exit(0);
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

    @Override
    public void onRadioButtonClick(Crypto crypto) {
        convert(crypto.getBaseCurrency(), crypto.getToCurrency());
    }

    @NonNull
    @Override
    public Loader<ArrayList<Crypto>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<ArrayList<Crypto>>(this) {
            ArrayList<Crypto> mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) { deliverResult(mTaskData); } // Delivers any previously loaded data immediately
                else { forceLoad(); }  // Force a new load
            }

            @Nullable
            @Override
            public ArrayList<Crypto> loadInBackground() {
                return getCardFromDb();
            }

            public void deliverResult(ArrayList<Crypto> data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Crypto>> loader, ArrayList<Crypto> data) {
        adapter.swapItems(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Crypto>> loader) {
        adapter.swapItems(null);
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

    public void convert(String baseCurrency, final String toCurrency) {
        if(!Utils.isOnline(this)) {
            Toast.makeText(this,"Data connectivity is OFF", Toast.LENGTH_SHORT).show();
            createCard.setAmount(String.format("%s 0.00", toCurrency));
            return;
        }

        IApi connectToApi = ApiClient.getClient().create(IApi.class);
        final Call<ResponseBody> call = connectToApi.grabConversion(baseCurrency, toCurrency);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,@NonNull Response<ResponseBody> response) {
                 if(response.isSuccessful()){
                    StringBuilder sb = new StringBuilder();
                    try{
                        BufferedReader br = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                        for (String temp; ((temp = br.readLine()) != null); ) {
                            sb.append(temp);
                        }

                        HashMap<String, String> conv = Jsonhelper.getValue(sb.toString());
                        createCard.setAmount(conv.isEmpty()? String.format("%s 0.00", toCurrency) : String.format("%s %s", toCurrency, conv.get(toCurrency)));
                    }catch (Exception io){
                        io.printStackTrace();
                        createCard.setAmount(String.format("%s 0.00", toCurrency));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,@NonNull Throwable t) {
                t.printStackTrace();
                createCard.setAmount(String.format("%s 0.00", toCurrency));
            }
        });

    }

}
