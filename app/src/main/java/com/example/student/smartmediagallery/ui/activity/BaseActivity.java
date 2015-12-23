package com.example.student.smartmediagallery.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.student.smartmediagallery.core.container.Container;
import com.example.student.smartmediagallery.core.container.ParserContainer;
import com.example.student.smartmediagallery.core.listener.PurchaseObserver;
import com.example.student.smartmediagallery.core.manager.PurchaseManager;
import com.example.student.smartmediagallery.core.policy.PurchaseModeProxy;

/**
 * Created by student on 23.12.2015.
 */
public abstract class BaseActivity extends AppCompatActivity implements PurchaseObserver {

    private ProgressDialog progressDialog;
    protected Container container;
    protected ParserContainer parserContainer;
    protected PurchaseManager purchaseManager;
    protected PurchaseModeProxy purchaseModeProxy;
    protected View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        container = Container.getInstance(this);
        parserContainer = container.getParserContainer();
        purchaseManager = container.getPurchaseManager();
        purchaseModeProxy = new PurchaseModeProxy(purchaseManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        purchaseManager.subscribe(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        purchaseManager.unsubscribe(this);
    }

    @Override
    public void onPreparePurchasing() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Purchase operation");
        progressDialog.setMessage("Buying...");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d("mylog", "progress dialog is showing");
    }

    @Override
    public void onPurchased() {
        Toast.makeText(this, "Congratulations! You bought application successfully!", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }
}
