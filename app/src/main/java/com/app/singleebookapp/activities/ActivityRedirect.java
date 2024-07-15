package com.app.singleebookapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.app.singleebookapp.R;
import com.app.singleebookapp.utils.Constant;
import com.app.singleebookapp.utils.Tools;
import com.google.android.material.snackbar.Snackbar;

public class ActivityRedirect extends AppCompatActivity {

    ImageButton btnClose;
    Button btnRedirect;
    String redirectUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);
        Tools.setNavigation(this);
        if (getIntent() != null) {
            redirectUrl = getIntent().getStringExtra("redirect_url");
        }

        initView();
    }

    private void initView() {
        btnClose = findViewById(R.id.btn_close);
        btnRedirect = findViewById(R.id.btn_redirect);

        btnClose.setOnClickListener(view -> {
            finish();
            Constant.IS_APP_OPEN = false;
        });

        btnRedirect.setOnClickListener(view -> {
            if (redirectUrl.equals("")) {
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.redirect_error), Snackbar.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl)));
                finish();
                Constant.IS_APP_OPEN = false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Constant.IS_APP_OPEN = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.IS_APP_OPEN = false;
    }

}
