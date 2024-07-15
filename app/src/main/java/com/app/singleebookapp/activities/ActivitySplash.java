package com.app.singleebookapp.activities;

import static com.solodroid.ads.sdk.util.Constant.ADMOB;
import static com.solodroid.ads.sdk.util.Constant.AD_STATUS_ON;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN;
import static com.solodroid.ads.sdk.util.Constant.APPLOVIN_MAX;
import static com.solodroid.ads.sdk.util.Constant.GOOGLE_AD_MANAGER;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.singleebookapp.BuildConfig;
import com.app.singleebookapp.Config;
import com.app.singleebookapp.R;
import com.app.singleebookapp.callbacks.CallbackConfig;
import com.app.singleebookapp.databases.prefs.AdsPref;
import com.app.singleebookapp.databases.prefs.SharedPref;
import com.app.singleebookapp.databases.sqlite.DbChapter;
import com.app.singleebookapp.models.Ads;
import com.app.singleebookapp.models.App;
import com.app.singleebookapp.models.Url;
import com.app.singleebookapp.rests.RestAdapter;
import com.app.singleebookapp.utils.Constant;
import com.app.singleebookapp.utils.Tools;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySplash extends AppCompatActivity {

    public static final String TAG = "SplashActivity";
    Call<CallbackConfig> callbackConfigCall = null;
    SharedPref sharedPref;
    AdsPref adsPref;
    DbChapter dbChapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Tools.setNavigation(this);
        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);
        dbChapter = new DbChapter(this);
        new Handler(Looper.getMainLooper()).postDelayed(this::requestConfig, Constant.DELAY_SPLASH);
    }

    private void requestConfig() {
        String data = Tools.decode(Config.ACCESS_KEY);
        String[] results = data.split("_applicationId_");
        String remoteUrl = results[0];
        String applicationId = results[1];

        if (applicationId.equals(BuildConfig.APPLICATION_ID)) {
            requestAPI(remoteUrl);
        } else {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Error")
                    .setMessage("Whoops! invalid access key or applicationId, please check your configuration")
                    .setPositiveButton("Ok", (dialog, which) -> {
                        finish();
                        Constant.IS_APP_OPEN = false;
                    })
                    .setCancelable(false)
                    .show();
        }
        Log.d(TAG, "Start request config");
    }

    private void requestAPI(String remoteUrl) {
        if (remoteUrl.startsWith("http://") || remoteUrl.startsWith("https://")) {
            if (remoteUrl.contains("https://drive.google.com")) {
                String driveUrl = remoteUrl.replace("https://", "").replace("http://", "");
                List<String> data = Arrays.asList(driveUrl.split("/"));
                String googleDriveFileId = data.get(3);
                callbackConfigCall = RestAdapter.createApi().getDriveJsonFileId(googleDriveFileId);
            } else {
                callbackConfigCall = RestAdapter.createApi().getJsonUrl(remoteUrl);
            }
        } else {
            callbackConfigCall = RestAdapter.createApi().getDriveJsonFileId(remoteUrl);
        }
        callbackConfigCall.enqueue(new Callback<CallbackConfig>() {
            public void onResponse(@NonNull Call<CallbackConfig> call, @NonNull Response<CallbackConfig> response) {
                CallbackConfig resp = response.body();
                displayApiResults(resp);
                Log.d(TAG, "request config success");
            }

            public void onFailure(@NonNull Call<CallbackConfig> call, @NonNull Throwable th) {
                showOpenAdsIfAvailable(false);
                Log.d(TAG, "request config failed : " + th.getMessage());
            }
        });
    }

    private void displayApiResults(CallbackConfig resp) {

        if (resp != null) {
            App app = resp.app;
            Url url = resp.url;
            Ads ads = resp.ads;

            if (sharedPref.getIsFirstTimeLaunch()) {
                sharedPref.setSwipeHorizontal(app.swipe_horizontal_reading_book.equals("true"));
                sharedPref.setIsFirstTimeLaunch(false);
            }

            if (app.custom_table_of_contents.equals("true")) {
                if (resp.table_of_contents.size() > 0) {
                    dbChapter.truncateTableChapter(DbChapter.TABLE_LABEL);
                    dbChapter.addListChapter(resp.table_of_contents, DbChapter.TABLE_LABEL);
                } else {
                    Log.d(TAG, "table of content is empty!");
                }
            }

            sharedPref.saveConfig(
                    app.ebook_description,
                    app.allow_save_book_in_storage,
                    app.show_scroll_handle_reading_book,
                    app.custom_table_of_contents,
                    url.more_apps_url,
                    url.privacy_policy_url
            );

            sharedPref.setPdfUrl(app.ebook_pdf_url);

            adsPref.saveAds(
                    ads.ad_status,
                    ads.main_ads,
                    ads.backup_ads,
                    ads.admob_publisher_id,
                    ads.admob_banner_ad_unit_id,
                    ads.admob_interstitial_ad_unit_id,
                    ads.admob_native_ad_unit_id,
                    ads.admob_app_open_ad_unit_id,
                    ads.ad_manager_banner_ad_unit_id,
                    ads.ad_manager_interstitial_ad_unit_id,
                    ads.ad_manager_native_ad_unit_id,
                    ads.ad_manager_app_open_ad_unit_id,
                    ads.fan_banner_ad_unit_id,
                    ads.fan_interstitial_ad_unit_id,
                    ads.fan_native_ad_unit_id,
                    ads.startapp_app_id,
                    ads.unity_game_id,
                    ads.unity_banner_placement_id,
                    ads.unity_interstitial_placement_id,
                    ads.applovin_banner_ad_unit_id,
                    ads.applovin_interstitial_ad_unit_id,
                    ads.applovin_native_ad_manual_unit_id,
                    ads.applovin_banner_zone_id,
                    ads.applovin_interstitial_zone_id,
                    ads.applovin_app_open_ad_unit_id,
                    ads.ironsource_app_key,
                    ads.ironsource_banner_placement_name,
                    ads.ironsource_interstitial_placement_name,
                    ads.interstitial_ad_interval
            );

            if (app.status.equals("1")) {
                showOpenAdsIfAvailable(true);
                Log.d(TAG, "App status on");
            } else {
                Intent intent = new Intent(getApplicationContext(), ActivityRedirect.class);
                intent.putExtra("redirect_url", url.redirect_url);
                startActivity(intent);
                finish();
                Log.d(TAG, "App status off");
            }
        } else {
            showOpenAdsIfAvailable(false);
        }

    }

    private void showOpenAdsIfAvailable(boolean show) {
        if (show) {
            if (adsPref.getAdStatus().equals(AD_STATUS_ON)) {
                Application application = getApplication();
                switch (adsPref.getMainAds()) {
                    case ADMOB:
                        if (!adsPref.getAdMobAppOpenAdId().equals("0")) {
                            ((MyApplication) application).showAdIfAvailable(ActivitySplash.this, this::startMainActivity);
                        } else {
                            startMainActivity();
                        }
                        break;
                    case GOOGLE_AD_MANAGER:
                        if (!adsPref.getAdManagerAppOpenAdId().equals("0")) {
                            ((MyApplication) application).showAdIfAvailable(ActivitySplash.this, this::startMainActivity);
                        } else {
                            startMainActivity();
                        }
                        break;
                    case APPLOVIN:
                    case APPLOVIN_MAX:
                        if (!adsPref.getAppLovinAppOpenAdUnitId().equals("0")) {
                            ((MyApplication) application).showAdIfAvailable(ActivitySplash.this, this::startMainActivity);
                        } else {
                            startMainActivity();
                        }
                        break;
                    default:
                        startMainActivity();
                        break;
                }
            } else {
                startMainActivity();
            }
        } else {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}
