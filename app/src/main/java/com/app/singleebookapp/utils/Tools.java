package com.app.singleebookapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.app.singleebookapp.BuildConfig;
import com.app.singleebookapp.Config;
import com.app.singleebookapp.R;
import com.app.singleebookapp.activities.ActivityWebView;
import com.app.singleebookapp.databases.prefs.SharedPref;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.solodroid.push.sdk.provider.OneSignalPush;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Tools {

    public static void setNavigation(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity, R.color.color_white));
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.color_primary_dark));
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(0);
        }
        if (Config.ENABLE_RTL_MODE) {
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public static void setupToolbar(AppCompatActivity activity, Toolbar toolbar, String title, boolean backButton) {
        activity.setSupportActionBar(toolbar);
        final ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(backButton);
            activity.getSupportActionBar().setHomeButtonEnabled(backButton);
            activity.getSupportActionBar().setTitle(title);
        }
    }

    public static void notificationOpenHandler(Context context, Intent getIntent) {
        SharedPref sharedPref = new SharedPref(context);
        String id = getIntent.getStringExtra(OneSignalPush.EXTRA_ID);
        String title = getIntent.getStringExtra(OneSignalPush.EXTRA_TITLE);
        String message = getIntent.getStringExtra(OneSignalPush.EXTRA_MESSAGE);
        String bigImage = getIntent.getStringExtra(OneSignalPush.EXTRA_IMAGE);
        String launchUrl = getIntent.getStringExtra(OneSignalPush.EXTRA_LAUNCH_URL);
        String uniqueId = getIntent.getStringExtra(OneSignalPush.EXTRA_UNIQUE_ID);
        String postId = getIntent.getStringExtra(OneSignalPush.EXTRA_POST_ID);
        String link = getIntent.getStringExtra(OneSignalPush.EXTRA_LINK);
        if (getIntent.hasExtra("unique_id")) {
            if (link != null && !link.equals("")) {
                if (!link.equals("0")) {
                    if (link.contains("play.google.com") || link.contains("?target=external")) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
                    } else {
                        Intent intent = new Intent(context, ActivityWebView.class);
                        intent.putExtra("title", title);
                        intent.putExtra("url", link);
                        context.startActivity(intent);
                    }
                }
            }
        }
    }

    public static String decode(String code) {
        return decodeBase64(decodeBase64(decodeBase64(code)));
    }

    public static String decodeBase64(String code) {
        byte[] valueDecoded = Base64.decode(code.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        return new String(valueDecoded);
    }

    public static String getFileNameFromUrl(String pdfUrl) {
        String pdfName;
        if (pdfUrl.startsWith("https://") || pdfUrl.startsWith("http://")) {
            if (pdfUrl.contains("drive.google.com")) {
                String name = pdfUrl.replace("https://", "").replace("http://", "");
                List<String> urls = Arrays.asList(name.split("/"));
                pdfName = urls.get(3);
            } else {
                String fileName = pdfUrl.substring(pdfUrl.lastIndexOf('/') + 1);
                pdfName = fileName.substring(0, fileName.lastIndexOf('.'));
            }
        } else {
            pdfName = pdfUrl;
        }
        return pdfName;
    }

    public static String reformatFileName(String fileName) {
        return fileName.replace(":", "").replace("'", "");
    }

    public static void showKeyboard(Activity activity, boolean show) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (show) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        } else {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    public static void fullScreenMode(Activity activity, View lytTop, View lytBottom, boolean on) {
        if (on) {
            lytTop.setVisibility(View.GONE);
            lytTop.animate().translationY(-lytTop.getHeight());
            lytBottom.setVisibility(View.GONE);
            lytBottom.animate().translationY(lytBottom.getHeight());
            hideSystemUI(activity);
        } else {
            lytTop.setVisibility(View.VISIBLE);
            lytTop.animate().translationY(0);
            lytBottom.setVisibility(View.VISIBLE);
            lytBottom.animate().translationY(0);
            showNavigation(activity);
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public static void fullScreenMode(AppCompatActivity activity, boolean show) {
        SharedPref sharedPref = new SharedPref(activity);
        if (show) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            int resultColor = ColorUtils.blendARGB(activity.getResources().getColor(R.color.color_primary_dark), Color.BLACK, 0.35f);
            activity.getWindow().setStatusBarColor(resultColor);
            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void hideSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public static void showNavigation(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(0);
        }
    }

    public static void openWebPage(Activity context, String title, String url) {
        Intent intent = new Intent(context, ActivityWebView.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static void displayPostDescription(WebView webView, String htmlData) {
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.setFocusableInTouchMode(false);
        webView.setFocusable(false);
        webView.getSettings().setJavaScriptEnabled(true);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String bg_paragraph = "<style type=\"text/css\">body{color: #000000; padding: 0.75em;} a{color:#1e88e5; font-weight:bold;}";

        String font_style_default = "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/font/custom_font.ttf\")}body {font-family: MyFont; font-size: medium; overflow-wrap: break-word; word-wrap: break-word; -ms-word-break: break-all; word-break: break-all; word-break: break-word; -ms-hyphens: auto; -moz-hyphens: auto; -webkit-hyphens: auto; hyphens: auto;}</style>";

        String text_default = "<html><head>"
                + font_style_default
                + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                + bg_paragraph
                + "</style></head>"
                + "<body>"
                + htmlData
                + "</body></html>";

        String text_rtl = "<html dir='rtl'><head>"
                + font_style_default
                + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                + bg_paragraph
                + "</style></head>"
                + "<body>"
                + htmlData
                + "</body></html>";

        if (Config.ENABLE_RTL_MODE) {
            webView.loadDataWithBaseURL(null, text_rtl, mimeType, encoding, null);
        } else {
            webView.loadDataWithBaseURL(null, text_default, mimeType, encoding, null);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });

    }

    public static void rateApp(Activity activity) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
    }

    public static void shareApp(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.share_text) + "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        intent.setType("text/plain");
        activity.startActivity(intent);
    }

    public static void inAppReview(Activity activity) {
        SharedPref sharedPref = new SharedPref(activity);
        if (sharedPref.getInAppReviewToken() <= 3) {
            sharedPref.updateInAppReviewToken(sharedPref.getInAppReviewToken() + 1);
        } else {
            ReviewManager manager = ReviewManagerFactory.create(activity);
            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    manager.launchReviewFlow(activity, reviewInfo).addOnFailureListener(e -> {
                    }).addOnCompleteListener(complete -> {
                            }
                    ).addOnFailureListener(failure -> {
                    });
                }
            }).addOnFailureListener(failure -> {
            });
        }

    }

    public static void setNativeAdStyle(Activity activity, LinearLayout nativeAdView, String style) {
        switch (style) {
            case "small":
            case "radio":
                nativeAdView.addView(View.inflate(activity, com.solodroid.ads.sdk.R.layout.view_native_ad_radio, null));
                break;
            case "news":
            case "medium":
                nativeAdView.addView(View.inflate(activity, com.solodroid.ads.sdk.R.layout.view_native_ad_news, null));
                break;
            default:
                nativeAdView.addView(View.inflate(activity, com.solodroid.ads.sdk.R.layout.view_native_ad_medium, null));
                break;
        }
    }

}
