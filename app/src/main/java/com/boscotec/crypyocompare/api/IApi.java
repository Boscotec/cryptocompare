package com.boscotec.crypyocompare.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Johnbosco on 16-Oct-17.
 */

public interface IApi {

    //@POST("")
    @GET("pricemulti?fsyms={from}&tsyms={to}")
    Call<ResponseBody> grabConversion(@Query("from") String from,  @Query("to") String to);

}
