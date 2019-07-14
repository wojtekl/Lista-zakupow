package com.gmail.lesniakwojciech.listazakupowa;

import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class Reklamy {
    private static AdView mAdView;
    private static RewardedVideoAd mRewardedVideoAd;

    public static void initialize(final Context context) {
        MobileAds.initialize(context, context.getString(R.string.AD_APP_ID));
    }

    public static void banner(final AdView adView, final boolean show, final Listener listener) {
        mAdView = adView;
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mAdView.setVisibility(View.GONE);
                listener.onRewarded(1);
            }
        });

        if (show) {
            mAdView.loadAd(new AdRequest.Builder().build());
        }
    }

    protected static void rewardedVideoAd(final Context context, final Listener listener) {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            private int amount = 0;

            @Override
            public void onRewardedVideoAdLoaded() {
                amount = 0;
                if (mRewardedVideoAd.isLoaded()) {
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
                if (0 < amount) {
                    listener.onRewarded(amount);
                }
            }

            @Override
            public void onRewarded(final RewardItem rewardItem) {
                amount = rewardItem.getAmount();
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

        mRewardedVideoAd.loadAd(context.getString(R.string.AD_UNIT_ID_REWARDED),
                new AdRequest.Builder().build());
    }

    public static void resume(final Context context) {
        if (null != mAdView) {
            mAdView.resume();
        }
        if (null != mRewardedVideoAd) {
            mRewardedVideoAd.resume(context);
        }
    }

    public static void pause(final Context context) {
        if (null != mAdView) {
            mAdView.pause();
        }
        if (null != mRewardedVideoAd) {
            mRewardedVideoAd.pause(context);
        }
    }

    public static void destroy(final Context context) {
        if (null != mAdView) {
            mAdView.destroy();
        }
        if (null != mRewardedVideoAd) {
            mRewardedVideoAd.destroy(context);
        }
    }

    protected interface Listener {
        void onRewarded(final int amount);
    }
}
