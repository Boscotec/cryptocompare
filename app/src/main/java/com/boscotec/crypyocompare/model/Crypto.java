package com.boscotec.crypyocompare.model;

import android.support.annotation.IdRes;
import com.boscotec.crypyocompare.R;
import java.io.Serializable;

/**
 * Created by Johnbosco on 09-Feb-18.
 */

public class Crypto implements Serializable {
   private String currency;
   private String rate;
   private int image;

   public Crypto(String currency, String rate, int image){
       this.currency = currency;
       this.rate = rate;
       this.image = image;
   }

   public int getImage() {
       return image;
   }

   public String getCurrency() {
       return currency;
   }

   public String getRate() {
       return rate;
   }

   public void setCurrency(String currency) {
       this.currency = currency;
   }

   public void setImage(int image) {
       this.image = image;
   }

   public void setRate(String rate) {
      this.rate = rate;
   }
}
