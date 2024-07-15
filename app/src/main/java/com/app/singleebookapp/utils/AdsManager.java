package com.app.singleebookapp.utils;

import static com.solodroid.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.solodroid.ads.sdk.util.Constant.IRONSOURCE;

import android.app.Activity;

import com.app.singleebookapp.BuildConfig;
import com.app.singleebookapp.R;
import com.app.singleebookapp.databases.prefs.AdsPref;
import com.app.singleebookapp.databases.prefs.SharedPref;
import com.solodroid.ads.sdk.format.AdNetwork;
import com.solodroid.ads.sdk.format.BannerAd;
import com.solodroid.ads.sdk.format.InterstitialAd;
import com.solodroid.ads.sdk.format.NativeAd;
import com.solodroid.ads.sdk.gdpr.GDPR;
import com.solodroid.ads.sdk.gdpr.LegacyGDPR;

public class AdsManager {

    Activity activity;
    AdNetwork.Initialize adNetwork;
    BannerAd.Builder bannerAd;
    InterstitialAd.Builder interstitialAd;
    NativeAd.Builder nativeAd;
    SharedPref sharedPref;
    AdsPref adsPref;
    LegacyGDPR legacyGDPR;
    GDPR gdpr;

    public AdsManager(Activity activity) {
        this.activity = activity;
        this.sharedPref = new SharedPref(activity);
        this.adsPref = new AdsPref(activity);
        this.legacyGDPR = new LegacyGDPR(activity);
        this.gdpr = new GDPR(activity);
        adNetwork = new AdNetwork.Initialize(activity);
        bannerAd = new BannerAd.Builder(activity);
        interstitialAd = new InterstitialAd.Builder(activity);
        nativeAd = new NativeAd.Builder(activity);
    }

    public void initializeAd() {
        adNetwork.setAdStatus(adsPref.getAdStatus())
                .setAdNetwork(adsPref.getMainAds())
                .setBackupAdNetwork(adsPref.getBackupAds())
                .setStartappAppId(adsPref.getStartappAppId())
                .setUnityGameId(adsPref.getUnityGameId())
                .setIronSourceAppKey(adsPref.getIronSourceAppKey())
                .setDebug(BuildConfig.DEBUG)
                .build();
    }

    public void loadBannerAd(int placement) {
        bannerAd.setAdStatus(adsPref.getAdStatus())
                .setAdNetwork(adsPref.getMainAds())
                .setBackupAdNetwork(adsPref.getBackupAds())
                .setAdMobBannerId(adsPref.getAdMobBannerId())
                .setGoogleAdManagerBannerId(adsPref.getAdManagerBannerId())
                .setFanBannerId(adsPref.getFanBannerId())
                .setUnityBannerId(adsPref.getUnityBannerPlacementId())
                .setAppLovinBannerId(adsPref.getAppLovinBannerAdUnitId())
                .setAppLovinBannerZoneId(adsPref.getAppLovinBannerZoneId())
                .setIronSourceBannerId(adsPref.getIronSourceBannerPlacementName())
                .setDarkTheme(false)
                .setPlacementStatus(placement)
                .build();
    }

    public void loadInterstitialAd(int placement, int interval) {
        interstitialAd.setAdStatus(adsPref.getAdStatus())
                .setAdNetwork(adsPref.getMainAds())
                .setBackupAdNetwork(adsPref.getBackupAds())
                .setAdMobInterstitialId(adsPref.getAdMobInterstitialId())
                .setGoogleAdManagerInterstitialId(adsPref.getAdManagerInterstitialId())
                .setFanInterstitialId(adsPref.getFanInterstitialId())
                .setUnityInterstitialId(adsPref.getUnityInterstitialPlacementId())
                .setAppLovinInterstitialId(adsPref.getAppLovinInterstitialAdUnitId())
                .setAppLovinInterstitialZoneId(adsPref.getAppLovinInterstitialZoneId())
                .setIronSourceInterstitialId(adsPref.getIronSourceInterstitialPlacementName())
                .setInterval(interval)
                .setPlacementStatus(placement)
                .build();
    }

    public void loadNativeAd(int placement, String style) {
        nativeAd.setAdStatus(adsPref.getAdStatus())
                .setAdNetwork(adsPref.getMainAds())
                .setBackupAdNetwork(adsPref.getBackupAds())
                .setAdMobNativeId(adsPref.getAdMobNativeId())
                .setAdManagerNativeId(adsPref.getAdManagerNativeId())
                .setFanNativeId(adsPref.getFanNativeId())
                .setAppLovinNativeId(adsPref.getAppLovinNativeAdManualUnitId())
                .setAppLovinDiscoveryMrecZoneId(adsPref.getAppLovinBannerZoneId())
                .setPlacementStatus(placement)
                .setNativeAdStyle(style)
                .setNativeAdBackgroundColor(android.R.color.transparent, android.R.color.transparent)
                .build();
        nativeAd.setNativeAdBackgroundResource(R.drawable.bg_native_ad);
    }

    public void showInterstitialAd() {
        interstitialAd.show();
    }

    public void updateConsentStatus() {
        if (Constant.ENABLE_GDPR_EU_CONSENT_UMP_SDK) {
            gdpr.updateGDPRConsentStatus();
        }
    }

    public void destroyBannerAd() {
        bannerAd.destroyAndDetachBanner();
    }

    public void resumeBannerAd(int placement) {
        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && !adsPref.getIronSourceBannerPlacementName().equals("0")) {
            if (adsPref.getMainAds().equals(IRONSOURCE) || adsPref.getBackupAds().equals(IRONSOURCE)) {
                loadBannerAd(placement);
            }
        }
    }

}
