package com.ssmnd.studentintellect.activities;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowMetrics;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public final class ActivitiesMethods {

    public static AdRequest adRequest = new AdRequest.Builder().build();

    /**
     * Initialize Ads.
     * @param activity Activity
     * @param adView AdView */
    public static void initializeAds(Activity activity, AdView adView) {
        MobileAds.initialize(activity, initializationStatus -> {
            adView.loadAd(adRequest);
        });
    }

    /**
     * Initialize Ads.
     * @param activity Activity
     * @param adViewContainer AdViewContainer
     * @param resBannerAdUnitId ResBannerAdUnitId  */
    public static void initializeAds(Activity activity, FrameLayout adViewContainer, @StringRes int resBannerAdUnitId) {
        MobileAds.initialize(activity, initializationStatus -> {
            AdView adView = new AdView(activity);
            adView.setAdUnitId(activity.getString(resBannerAdUnitId));
            adViewContainer.addView(adView);
            // Since we're loading the banner based on the adContainerView size, we need
            // to wait until this view is laid out before we can get the width.
            final boolean[] initialLayoutComplete = {false};
            adViewContainer.getViewTreeObserver().addOnGlobalLayoutListener(()-> {
                if (!initialLayoutComplete[0]) {
                    initialLayoutComplete[0] = true;
                    adView.setAdSize(getAdSize(activity, adViewContainer));
                    adView.loadAd(adRequest);
                }
            });
        });
    }

    /**
     * Determine the screen width (less decorations) to use for the ad width.
     * @param activity Activity
     * @param adViewContainer AdViewContainer
     * @return {@link AdSize}*/
    @NonNull
    private static AdSize getAdSize(Activity activity, FrameLayout adViewContainer) {
        int width;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Rect bounds = windowMetrics.getBounds();
            width = bounds.width();
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
        }
        int adWidthPixels = adViewContainer.getWidth();
        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = width;
        }

        float density = activity.getResources().getDisplayMetrics().density;
        int adWidth = (int) ((float) adWidthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }



}
