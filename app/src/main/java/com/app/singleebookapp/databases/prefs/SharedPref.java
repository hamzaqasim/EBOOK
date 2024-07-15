package com.app.singleebookapp.databases.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("ebook_settings", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getIsFirstTimeLaunch() {
        return sharedPreferences.getBoolean("first_launch", true);
    }

    public void setIsFirstTimeLaunch(Boolean firstLaunch) {
        editor.putBoolean("first_launch", firstLaunch);
        editor.apply();
    }

    public Boolean getIsReadingPage() {
        return sharedPreferences.getBoolean("is_reading_page", false);
    }

    public void setIsReadingPage(Boolean isReadingPage) {
        editor.putBoolean("is_reading_page", isReadingPage);
        editor.apply();
    }

    public void setLastReadingPage(int lastPage) {
        editor.putInt("last_page", lastPage);
        editor.apply();
    }

    public int getLastReadingPage() {
        return sharedPreferences.getInt("last_page", 0);
    }

    public void saveConfig(String ebookDescription, String allowSavBook, String scrollHandle, String customTableOfContents, String moreAppsUrl, String privacyPolicyUrl) {
        editor.putString("ebook_description", ebookDescription);
        editor.putString("allow_save_book", allowSavBook);
        editor.putString("scroll_handle", scrollHandle);
        editor.putString("custom_table_of_contents", customTableOfContents);
        editor.putString("more_apps_url", moreAppsUrl);
        editor.putString("privacy_policy_url", privacyPolicyUrl);
        editor.apply();
    }

    public void setPdfUrl(String pdfUrl) {
        editor.putString("pdf_url", pdfUrl);
        editor.apply();
    }

    public String getPdfUrl() {
        return sharedPreferences.getString("pdf_url", "https://github.com/solodroid-id/sample-data/raw/master/404.pdf");
    }

    public String getBookDescription() {
        return sharedPreferences.getString("ebook_description", "");
    }

    public String allowSaveBook() {
        return sharedPreferences.getString("allow_save_book", "true");
    }

    public Boolean getSwipeHorizontal() {
        return sharedPreferences.getBoolean("swipe_horizontal", true);
    }

    public void setSwipeHorizontal(Boolean swipeHorizontal) {
        editor.putBoolean("swipe_horizontal", swipeHorizontal);
        editor.apply();
    }

//    public void setSwipeHorizontal(String pdfUrl) {
//        editor.putString("pdf_url", pdfUrl);
//        editor.apply();
//    }
//
//    public String getSwipeHorizontal() {
//        return sharedPreferences.getString("swipe_horizontal", "true");
//    }

    public String getScrollHandle() {
        return sharedPreferences.getString("scroll_handle", "true");
    }

    public String getCustomTableOfContents() {
        return sharedPreferences.getString("custom_table_of_contents", "true");
    }

    public String getMoreAppsUrl() {
        return sharedPreferences.getString("more_apps_url", "");
    }

    public String getPrivacyPolicyUrl() {
        return sharedPreferences.getString("privacy_policy_url", "");
    }

    public Integer getInAppReviewToken() {
        return sharedPreferences.getInt("in_app_review_token", 0);
    }

    public void updateInAppReviewToken(int value) {
        editor.putInt("in_app_review_token", value);
        editor.apply();
    }

    public Integer getInterstitialAdCounter() {
        return sharedPreferences.getInt("interstitial_counter", 1);
    }

    public void updateInterstitialAdCounter(int counter) {
        editor.putInt("interstitial_counter", counter);
        editor.apply();
    }

    public Integer getLastItemPosition() {
        return sharedPreferences.getInt("item_position", 0);
    }

    public void setLastItemPosition(int position) {
        editor.putInt("item_position", position);
        editor.apply();
    }

}
