package com.boscotec.crypyocompare;

import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Johnbosco on 08-Oct-17.
 */

public class NetworkCalls {
    private static final String TAG = "NetworkCalls";
    private static final String baseUrl = "https://min-api.cryptocompare.com/data/pricemulti";
    //private String[] f = {"BTC","USD"};

    public void call(String from, String to)throws IOException, JSONException{
        HashMap<String, String> query = new HashMap<>();
        query.put("fsyms", from); //"BTC"
        query.put("tsyms", to); //"USD"

        Timber.d("Fetching data from api server: " + baseUrl);
        String jsonReport = getDataFrom(baseUrl, "get", query);
        JSONArray jsonArray = new JSONArray(jsonReport);

        for(int i =0; i<jsonArray.length(); i++){

//            if (jsonArray.optJSONObject(0) != null) {
  //              jsonArray.optJSONObject(0);
    //        }

        }
    }

    private String getDataFrom(String url, String method, HashMap<String, String> params)
            throws IOException {
        String response = BuildConfig.FLAVOR;
        HttpURLConnection client=null;
        InputStream is=null;
        Log.i(TAG, "fetching data from "+ url + " using method "+ method);

        if (method.toLowerCase().equals("post") || method.toLowerCase().equals("get")) {
            try {
                String query = buildPostParameters(params);
                URL server = new URL(url + "?" + query);
                client = (HttpURLConnection) server.openConnection();
                client.setDoOutput(true);
                client.setRequestMethod(method.toUpperCase());
                client.setRequestProperty("Connection", "Keep-Alive");
                client.setRequestProperty("Cache-Control", "no-cache");
                client.setRequestProperty("Accept-Charset", "UTF-8");
                //client.setRequestProperty("Content-Type", "");
                client.setReadTimeout(25000);
                client.setConnectTimeout(25000);
                client.connect();

                //check for valid respond code from server
                int status = client.getResponseCode();
                is = (status == HttpURLConnection.HTTP_OK) ? client.getInputStream() : client.getErrorStream();

                //Build the response or error as a string
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                for (String temp; ((temp = br.readLine()) != null); ) {
                    sb.append(temp);
                }

                response = sb.toString();

            } finally {
                if (is != null) is.close();
                if (client != null) client.disconnect();
            }
        }
        Log.i(TAG, "done fetching data from: "+ url + " with response: "+ response);

        return response;
    }

    private static String buildPostParameters(Object content){
        String output = null;

        if((content instanceof String) || (content instanceof JSONObject) || (content instanceof JSONArray)){
            output = content.toString();
        }else if(content instanceof Map){
            Uri.Builder builder = new Uri.Builder();
            HashMap hashMap = (HashMap)content;
            Iterator entries = hashMap.entrySet().iterator();
            while (entries.hasNext()){
              Map.Entry entry = (Map.Entry) entries.next();
              builder.appendQueryParameter(entry.getKey().toString(), entry.getValue().toString());
              entries.remove();
            }
              output = builder.build().getEncodedQuery();
        }
        return output;
    }

}
