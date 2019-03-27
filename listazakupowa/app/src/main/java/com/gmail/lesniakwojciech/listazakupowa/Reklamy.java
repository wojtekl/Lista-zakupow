package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.Calendar;

public class Reklamy {
    private static final long MILIS_IN_DAY = 8 * 60 * 60 * 1000;

    private static RewardedVideoAd mRewardedVideoAd;

    public static void initialize(final Context context) {
        MobileAds.initialize(context, context.getString(R.string.ad_app_id));
    }

    public static void banner(final AdView mAdView, final View parentView, final Context context) {
        final Calendar calendar = Calendar.getInstance();
        final Ustawienia ustawienia = new Ustawienia(context);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mAdView.setVisibility(View.GONE);
                Snackbar.make(parentView, R.string.dziekujeCi, Snackbar.LENGTH_LONG).show();
            }
        });

        if ((ustawienia.getReklamaNastepna(0)) < calendar.getTimeInMillis()) {
            mAdView.loadAd(new AdRequest.Builder().build());
        }
    }

    public static void interstitialAd(final Context context) {
        final Calendar calendar = Calendar.getInstance();
        final Ustawienia ustawienia = new Ustawienia(context);

        final InterstitialAd mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.pelnoekranowa_ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });

        if (ustawienia.getReklamaNastepna(0) < calendar.getTimeInMillis()) {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    protected static void rewardedVideoAd(final Context context, final View parentView) {
        final Calendar calendar = Calendar.getInstance();
        final Ustawienia ustawienia = new Ustawienia(context);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            private boolean rewarded = false;

            @Override
            public void onRewardedVideoAdLoaded() {
                rewarded = false;
                if(mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                }
            }

            @Override
            public void onRewardedVideoAdOpened() {
            }

            @Override
            public void onRewardedVideoStarted() {
            }

            @Override
            public void onRewardedVideoAdClosed() {
                if(rewarded) {
                    Snackbar.make(parentView, R.string.otrzymujeszNagrode, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                final long teraz = calendar.getTimeInMillis();
                long reklamaNastepna = ustawienia.getReklamaNastepna(teraz);
                if(teraz > reklamaNastepna) {
                    reklamaNastepna = teraz;
                }
                ustawienia.setReklamaNastepna(reklamaNastepna
                        + MILIS_IN_DAY * rewardItem.getAmount());
                rewarded = true;
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
            }

            @Override
            public void onRewardedVideoCompleted() {
            }
        });

        mRewardedVideoAd.loadAd(context.getString(R.string.znagroda_ad_unit_id),
                new AdRequest.Builder().build());
    }

    public static void resume(final Context context) {
        if(null != mRewardedVideoAd) {
            mRewardedVideoAd.resume(context);
        }
    }

    public static void pause(final Context context) {
        if(null != mRewardedVideoAd) {
            mRewardedVideoAd.pause(context);
        }
    }

    public static void destroy(final Context context) {
        if(null != mRewardedVideoAd) {
            mRewardedVideoAd.destroy(context);
        }
    }
}
