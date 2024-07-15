package com.app.singleebookapp.databases.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class AdsPref {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public AdsPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("ads_setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveAds(String adStatus, String mainAds, String backupAds, String adMobPublisherId, String admobBannerAdUnitId, String admobInterstitialAdUnitId, String admobNativeAdUnitId, String admobAppOpenAdUnitId, String adManagerBannerAdUnitId, String adManagerInterstitialAdUnitId, String adManagerNativeAdUnitId, String adManagerAppOpenAdUnitId, String fanBannerAdUnitId, String fanInterstitialAdUnitId, String fanNativeAdUnitId, String startAppId, String unityGameId, String unityBannerId, String unityInterstitialId, String appLovinBannerId, String appLovinInterstitialId, String appLovinNativeManualId, String applovinAppOpenAdUnitId, String applovinBannerZoneId, String applovinInterstitialZoneId, String ironsourceAppKey, String ironsourceBannerPlacementName, String ironsourceInterstitialPlacementName, int interstitialAdInterval) {
        editor.putString("ad_status", adStatus);
        editor.putString("main_ads", mainAds);
        editor.putString("backup_ads", backupAds);
        editor.putString("admob_publisher_id", adMobPublisherId);;
        editor.putString("admob_banner_ad_unit_id", admobBannerAdUnitId);
        editor.putString("admob_interstitial_ad_unit_id", admobInterstitialAdUnitId);
        editor.putString("admob_native_ad_unit_id", admobNativeAdUnitId);
        editor.putString("admob_app_open_ad_unit_id", admobAppOpenAdUnitId);
        editor.putString("ad_manager_banner_ad_unit_id", adManagerBannerAdUnitId);
        editor.putString("ad_manager_interstitial_ad_unit_id", adManagerInterstitialAdUnitId);
        editor.putString("ad_manager_native_ad_unit_id", adManagerNativeAdUnitId);
        editor.putString("ad_manager_app_open_ad_unit_id", adManagerAppOpenAdUnitId);
        editor.putString("fan_banner_ad_unit_id", fanBannerAdUnitId);
        editor.putString("fan_interstitial_ad_unit_id", fanInterstitialAdUnitId);
        editor.putString("fan_native_ad_unit_id", fanNativeAdUnitId);
        editor.putString("startapp_app_id", startAppId);
        editor.putString("unity_game_id", unityGameId);
        editor.putString("unity_banner_placement_id", unityBannerId);
        editor.putString("unity_interstitial_placement_id", unityInterstitialId);
        editor.putString("applovin_banner_ad_unit_id", appLovinBannerId);
        editor.putString("applovin_interstitial_ad_unit_id", appLovinInterstitialId);
        editor.putString("applovin_native_ad_manual_unit_id", appLovinNativeManualId);
        editor.putString("applovin_app_open_ad_unit_id", applovinAppOpenAdUnitId);
        editor.putString("applovin_banner_zone_id", applovinBannerZoneId);
        editor.putString("applovin_interstitial_zone_id", applovinInterstitialZoneId);
        editor.putString("ironsource_app_key", ironsourceAppKey);
        editor.putString("ironsource_banner_placement_name", ironsourceBannerPlacementName);
        editor.putString("ironsource_interstitial_placement_name", ironsourceInterstitialPlacementName);
        editor.putInt("interstitial_ad_interval", interstitialAdInterval);
        editor.apply();
    }

    public String getAdStatus() {
        return sharedPreferences.getString("ad_status", "0");
    }

    public String getMainAds() {
        return sharedPreferences.getString("main_ads", "0");
    }

    public String getBackupAds() {
        return sharedPreferences.getString("backup_ads", "none");
    }

    public String getAdMobPublisherId() {
        return sharedPreferences.getString("admob_publisher_id", "0");
    }

    public String getAdMobBannerId() {
        return sharedPreferences.getString("admob_banner_ad_unit_id", "0");
    }

    public String getAdMobInterstitialId() {
        return sharedPreferences.getString("admob_interstitial_ad_unit_id", "0");
    }

    public String getAdMobNativeId() {
        return sharedPreferences.getString("admob_native_ad_unit_id", "0");
    }

    public String getAdMobAppOpenAdId() {
        return sharedPreferences.getString("admob_app_open_ad_unit_id", "0");
    }

    public String getAdManagerBannerId() {
        return sharedPreferences.getString("ad_manager_banner_ad_unit_id", "0");
    }

    public String getAdManagerInterstitialId() {
        return sharedPreferences.getString("ad_manager_interstitial_ad_unit_id", "0");
    }

    public String getAdManagerNativeId() {
        return sharedPreferences.getString("ad_manager_native_ad_unit_id", "0");
    }

    public String getAdManagerAppOpenAdId() {
        return sharedPreferences.getString("ad_manager_app_open_ad_unit_id", "0");
    }

    public String getFanBannerId() {
        return sharedPreferences.getString("fan_banner_ad_unit_id", "0");
    }

    public String getFanInterstitialId() {
        return sharedPreferences.getString("fan_interstitial_ad_unit_id", "0");
    }

    public String getFanNativeId() {
        return sharedPreferences.getString("fan_native_ad_unit_id", "0");
    }

    public String getStartappAppId() {
        return sharedPreferences.getString("startapp_app_id", "0");
    }

    public String getUnityGameId() {
        return sharedPreferences.getString("unity_game_id", "0");
    }

    public String getUnityBannerPlacementId() {
        return sharedPreferences.getString("unity_banner_placement_id", "banner");
    }

    public String getUnityInterstitialPlacementId() {
        return sharedPreferences.getString("unity_interstitial_placement_id", "video");
    }

    public String getAppLovinBannerAdUnitId() {
        return sharedPreferences.getString("applovin_banner_ad_unit_id", "0");
    }

    public String getAppLovinInterstitialAdUnitId() {
        return sharedPreferences.getString("applovin_interstitial_ad_unit_id", "0");
    }

    public String getAppLovinNativeAdManualUnitId() {
        return sharedPreferences.getString("applovin_native_ad_manual_unit_id", "0");
    }

    public String getAppLovinAppOpenAdUnitId() {
        return sharedPreferences.getString("applovin_app_open_ad_unit_id", "0");
    }

    public String getAppLovinBannerZoneId() {
        return sharedPreferences.getString("applovin_banner_zone_id", "0");
    }

    public String getAppLovinInterstitialZoneId() {
        return sharedPreferences.getString("applovin_interstitial_zone_id", "0");
    }

    public String getIronSourceAppKey() {
        return sharedPreferences.getString("ironsource_app_key", "0");
    }

    public String getIronSourceBannerPlacementName() {
        return sharedPreferences.getString("ironsource_banner_placement_name", "0");
    }

    public String getIronSourceInterstitialPlacementName() {
        return sharedPreferences.getString("ironsource_interstitial_placement_name", "0");
    }

    public int getInterstitialAdInterval() {
        return sharedPreferences.getInt("interstitial_ad_interval", 0);
    }

    public Integer getInterstitialAdCounter() {
        return sharedPreferences.getInt("interstitial_counter", 1);
    }

    public void updateInterstitialAdCounter(int counter) {
        editor.putInt("interstitial_counter", counter);
        editor.apply();
    }

}
