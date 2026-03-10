package com.calculator.ads;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.google.android.gms.ads.*;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends Activity {

    EditText num1, num2;
    Button addBtn, rewardBtn;
    TextView result;
    AdView adView;

    InterstitialAd interstitialAd;
    RewardedAd rewardedAd;

    int calcCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        MobileAds.initialize(this);

        num1 = findViewById(R.id.num1);
        num2 = findViewById(R.id.num2);
        addBtn = findViewById(R.id.addBtn);
        rewardBtn = findViewById(R.id.rewardBtn);
        result = findViewById(R.id.result);
        adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        loadInterstitialAd();
        loadRewardedAd();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = num1.getText().toString();
                String s2 = num2.getText().toString();
                if (s1.isEmpty() || s2.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter both numbers", Toast.LENGTH_SHORT).show();
                    return;
                }
                double a = Double.parseDouble(s1);
                double b = Double.parseDouble(s2);
                result.setText("Result: " + (a + b));
                calcCount++;
                if (calcCount % 3 == 0) showInterstitialAd();
            }
        });

        rewardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRewardedAd();
            }
        });
    }

    private void loadInterstitialAd() {
        InterstitialAd.load(this, "ca-app-pub-5367408521620850/8760358930",
            new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(InterstitialAd ad) {
                    interstitialAd = ad;
                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            interstitialAd = null;
                            loadInterstitialAd();
                        }
                    });
                }
                @Override
                public void onAdFailedToLoad(LoadAdError e) { interstitialAd = null; }
            });
    }

    private void showInterstitialAd() {
        if (interstitialAd != null) interstitialAd.show(this);
        else loadInterstitialAd();
    }

    private void loadRewardedAd() {
        RewardedAd.load(this, "ca-app-pub-5367408521620850/7151288435",
            new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
                @Override
                public void onAdLoaded(RewardedAd ad) {
                    rewardedAd = ad;
                    rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            rewardedAd = null;
                            loadRewardedAd();
                        }
                    });
                }
                @Override
                public void onAdFailedToLoad(LoadAdError e) { rewardedAd = null; }
            });
    }

    private void showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd.show(this, rewardItem ->
                Toast.makeText(this, "Reward: " + rewardItem.getAmount() + " " + rewardItem.getType(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Ad not ready yet!", Toast.LENGTH_SHORT).show();
            loadRewardedAd();
        }
    }

    @Override protected void onResume() { super.onResume(); adView.resume(); }
    @Override protected void onPause() { adView.pause(); super.onPause(); }
    @Override protected void onDestroy() { adView.destroy(); super.onDestroy(); }
}
