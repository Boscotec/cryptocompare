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
    @GET("price")
    Call<ResponseBody> grabConversion(@Query("fsym") String from,  @Query("tsyms") String to);
}
