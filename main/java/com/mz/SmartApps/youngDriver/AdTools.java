package com.mz.SmartApps.youngDriver;

import static com.mz.SmartApps.youngDriver.MainActivity.TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.text.SimpleDateFormat;
//AdTools is a utility class for managing ads in an Android application
public class AdTools {

    private Context context;
    AdRequest adRequest;
    InterstitialAd myInterstitialAd;
    
    // Constructs an instance of AdTools with the given context.
    public AdTools(Context context) {
        this.context = context;
        adRequest = new AdRequest.Builder().build();

    }
    
    // Checks if the interstitial ad is null.
    public boolean isInterstitialAdNull(){
        return myInterstitialAd == null;
    }
    //Loads a banner ad into the provided AdView
    public void loadBannerAd(AdView adView) {
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }
        });
    }
    // Shows the interstitial ad if it is not null.
    public void showInterstitialAd(){
        if (myInterstitialAd != null){
            myInterstitialAd.show((Activity)context);
            myInterstitialAd = null;
        }

    }

    //Loads an interstitial ad with the specified ad unit ID.
    public void loadInterstitialAd(String adUnitId) {
        InterstitialAd.load(context, adUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
               myInterstitialAd= interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        });
    }
    
    //Loads and shows a fullscreen ad with the specified unit ID
    public void loadAndShowFullScreenAd(String unitId){
        InterstitialAd.load(context, unitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                interstitialAd.show((Activity)context);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                //Log.d(TAG, "onAdFailedToLoad: "+loadAdError.toString());
            }
        });
    }
}

