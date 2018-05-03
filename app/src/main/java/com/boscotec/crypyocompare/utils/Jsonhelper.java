package com.boscotec.crypyocompare.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Johnbosco on 31-Oct-17.
 */

public class Jsonhelper {
    public static HashMap<String, String> getValue(Object content) throws /* IOException,*/ JSONException {
        HashMap<String, String> hashMap = null;

         if (content instanceof String) {
            JSONObject jsonObject = new JSONObject(content.toString());
            hashMap = new HashMap<>();

            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = jsonObject.get(key);
                hashMap.put(key, value.toString());
            }
         }
         return hashMap;
    }
}