package com.example.student.smartmediagallery.core.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.student.smartmediagallery.core.constants.PurchaseConstants;
import com.example.student.smartmediagallery.core.listener.PurchaseObserver;
import com.example.student.smartmediagallery.core.policy.PurchaseMode;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by student on 23.12.2015.
 */
public class PurchaseManager {
    Context context;
    private ArrayList<PurchaseObserver> purchaseObservers;
    private SharedPreferences sharedPreferences;

    public PurchaseManager(Context context) {
        this.context = context;
        purchaseObservers = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(PurchaseConstants.PURCHASE_PREFERENCE.toString(), Context.MODE_PRIVATE);
    }

    public void subscribe(PurchaseObserver purchaseObserver) {
        purchaseObservers.add(purchaseObserver);
    }

    public void unsubscribe(PurchaseObserver purchaseObserver) {
        purchaseObservers.remove(purchaseObserver);
    }

    public void purchase() {
        Purchaser purchaser = new Purchaser();
        purchaser.execute();
    }

    private void notifyPrepare() {
        for (PurchaseObserver purchaseObserver: purchaseObservers) {
            purchaseObserver.onPreparePurchasing();
        }
    }

    private void notifyPurchased() {
        for (PurchaseObserver purchaseObserver: purchaseObservers) {
            purchaseObserver.onPurchased();
        }
    }

    private void savePurchaseInSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PurchaseConstants.PURCHASED_FLAG.toString(), PurchaseConstants.PURCHASED.toString());
        editor.apply();
    }

    public boolean isPurchased() {
        String purchaseStatus = sharedPreferences.getString(PurchaseConstants.PURCHASED_FLAG.toString(), PurchaseConstants.NOT_PURCHASED.toString());
        Log.d("mylog", "is purchased: " + purchaseStatus.equals(PurchaseConstants.PURCHASED.toString()));
        return purchaseStatus.equals(PurchaseConstants.PURCHASED.toString());
    }

    class Purchaser extends AsyncTask {
        @Override
        protected void onPreExecute() {
            notifyPrepare();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            savePurchaseInSharedPreferences();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            notifyPurchased();
        }
    }
}
