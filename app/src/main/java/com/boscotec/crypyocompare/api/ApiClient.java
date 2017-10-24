package com.boscotec.crypyocompare.api;

import com.boscotec.crypyocompare.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Johnbosco on 16-Oct-17.
 */

public class ApiClient {
    private static Retrofit retrofit = null;

    public  static  Retrofit getClient(){
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  retrofit;
    }
}
