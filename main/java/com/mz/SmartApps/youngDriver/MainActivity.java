package com.mz.SmartApps.youngDriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {
    //נהג צעיר - יומן שיעורי נהיגה, תקופת מלווה, מעקב הוצאות
    public static final String TAG ="outputmz";
    private int type = 0;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView mainNavBar;
    private AdTools adTools;
    private HomeFragment homeFragment;
    private AccompanyFragment accompanyFragment;
    private ProfileFragment profileFragment;
    private PaymentsFragment paymentsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(savedInstanceState != null)
            Log.d(TAG, "tttttttyppe: "+savedInstanceState.getInt("type"));


        sharedPreferences = getSharedPreferences("file1", 0);
        int newU = sharedPreferences.getInt("new", 0);
        if (newU == 0) {
            Intent intent = new Intent(MainActivity.this, WelcomeScreen.class);
            startActivity(intent);
        }
        else {

            type = 2;
            Bundle bundle = getIntent().getExtras();
            if (bundle != null){
                type = bundle.getInt("type",0);

            }
            Log.d(TAG, "type: "+ type);
            adTools = new AdTools(MainActivity.this);

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    if (type==1)
                        adTools.loadAndShowFullScreenAd(getResources().getString(R.string.after_widget));
                    else if (type == 2)
                        adTools.loadAndShowFullScreenAd(getResources().getString(R.string.entery_ad));

                    AdView adView = findViewById(R.id.mainAdBanner);
                    adTools.loadBannerAd(adView);

                }
            });

            //set the fragments
            homeFragment = new HomeFragment();
            accompanyFragment = new AccompanyFragment();
            profileFragment = new ProfileFragment();
            paymentsFragment = new PaymentsFragment();

            mainNavBar = findViewById(R.id.mainNavBar);

            mainNavBar.setOnNavigationItemSelectedListener(mainBNBlistener);
            Log.d(TAG, "type: "+type);
            if (type==1)
              mainNavBar.setSelectedItemId(R.id.accompanyF);
            else
                mainNavBar.setSelectedItemId(R.id.homeF);
        }


    }
    @Override
    public void onBackPressed() {
        if (mainNavBar.getSelectedItemId() ==  R.id.homeF){
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
        else{
            mainNavBar.setSelectedItemId(R.id.homeF);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: "+intent.getExtras().getInt("type",0));
    }

    //Listener for handling item selection events in the BottomNavigationView.
    BottomNavigationView.OnNavigationItemSelectedListener mainBNBlistener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.homeF:
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, homeFragment).commit();
                    return true;
                case R.id.paymentsF:
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, paymentsFragment).commit();
                    return true;
                case R.id.accompanyF:
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,accompanyFragment).commit();
                    return true;
                case R.id.profileF:
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,profileFragment).commit();
                    return true;
            }
            return false;
        }
    };

}
