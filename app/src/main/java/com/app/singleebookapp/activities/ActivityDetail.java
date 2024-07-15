package com.app.singleebookapp.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.app.singleebookapp.R;
import com.app.singleebookapp.databases.prefs.SharedPref;
import com.app.singleebookapp.utils.Tools;


public class ActivityDetail extends AppCompatActivity {

    SharedPref sharedPref;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Tools.setNavigation(this);
        sharedPref = new SharedPref(this);
        webView = findViewById(R.id.webView);
        Tools.displayPostDescription(webView, sharedPref.getBookDescription());
        setupToolbar();
    }

    public void setupToolbar() {
        Tools.setupToolbar(this, findViewById(R.id.toolbar), getString(R.string.app_name), true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
