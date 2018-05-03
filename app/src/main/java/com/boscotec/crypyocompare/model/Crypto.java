package com.boscotec.crypyocompare.model;

import android.support.annotation.IdRes;
import com.boscotec.crypyocompare.R;
import java.io.Serializable;

/**
 * Created by Johnbosco on 09-Feb-18.
 */

public class Crypto implements Serializable {
   private int image;
   private String baseCurrency;
   private String toCurrency;
   private String amount;

   public Crypto(){ }

   public Crypto(String  baseCurrency, String  toCurrency, int image){
       this.baseCurrency =  baseCurrency;
       this.toCurrency =  toCurrency;
       this.image = image;
   }

   public int getImage() {
        return image;
    }

   public void setImage(int image) {
        this.image = image;
    }

   public String getBaseCurrency() {
        return baseCurrency;
    }

   public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

   public String getToCurrency() {
        return toCurrency;
    }

   public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

   public String getAmount() { return amount; }

   public void setAmount(String amount) { this.amount = amount; }

   @Override
   public String toString() { return baseCurrency + ',' + toCurrency + '%' + image; }

}
