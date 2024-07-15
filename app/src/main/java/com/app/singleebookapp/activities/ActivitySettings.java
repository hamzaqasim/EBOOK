package com.app.singleebookapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.singleebookapp.BuildConfig;
import com.app.singleebookapp.Config;
import com.app.singleebookapp.R;
import com.app.singleebookapp.databases.prefs.SharedPref;
import com.app.singleebookapp.utils.Tools;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.DecimalFormat;

public class ActivitySettings extends AppCompatActivity {

    private static final String TAG = "ActivitySettings";
    SharedPref sharedPref;
    LinearLayout parentView;
    MaterialSwitch materialSwitch;
    RelativeLayout btnSwitchTheme;
    ImageView btnClearCache;
    TextView txtCacheSize;
    private String singleChoiceSelected;
    TextView txtReadingMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedPref = new SharedPref(this);
        Tools.setNavigation(this);
        initView();
        setupToolbar();
    }

    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        Tools.setupToolbar(this, toolbar, getString(R.string.txt_title_settings), true);
    }

    private void initView() {
        parentView = findViewById(R.id.parent_view);
        txtReadingMode = findViewById(R.id.txt_reading_mode);
        materialSwitch = findViewById(R.id.switch_last_reading);
        btnSwitchTheme = findViewById(R.id.btn_switch_last_reading);

        materialSwitch.setChecked(sharedPref.getIsReadingPage());
        materialSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> sharedPref.setIsReadingPage(isChecked));

        btnSwitchTheme.setOnClickListener(v -> {
            if (materialSwitch.isChecked()) {
                sharedPref.setIsReadingPage(false);
                materialSwitch.setChecked(false);
            } else {
                sharedPref.setIsReadingPage(true);
                materialSwitch.setChecked(true);
            }
        });

        findViewById(R.id.btn_notification).setOnClickListener(v -> {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID);
            } else {
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", BuildConfig.APPLICATION_ID);
                intent.putExtra("app_uid", getApplicationInfo().uid);
            }
            startActivity(intent);
        });

        if (sharedPref.getSwipeHorizontal().equals(true)) {
            txtReadingMode.setText(R.string.txt_reading_horizontal);
        } else {
            txtReadingMode.setText(R.string.txt_reading_vertical);
        }

        if (Config.ENABLE_RTL_MODE) {
            findViewById(R.id.btn_reading_mode).setVisibility(View.GONE);
        }

        findViewById(R.id.btn_reading_mode).setOnClickListener(v -> {
            String[] items = getResources().getStringArray(R.array.dialog_book_columns);

            int itemSelected;
            if (sharedPref.getSwipeHorizontal().equals(true)) {
                itemSelected = 0;
                singleChoiceSelected = items[0];
            } else {
                itemSelected = 1;
                singleChoiceSelected = items[1];
            }

            new MaterialAlertDialogBuilder(ActivitySettings.this)
                    .setTitle(getString(R.string.title_setting_reading_mode))
                    .setSingleChoiceItems(items, itemSelected, (dialogInterface, i) -> singleChoiceSelected = items[i])
                    .setPositiveButton(R.string.option_ok, (dialogInterface, i) -> {
                        if (singleChoiceSelected.equals(getResources().getString(R.string.txt_reading_horizontal))) {
                            if (sharedPref.getSwipeHorizontal().equals(false)) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                sharedPref.setSwipeHorizontal(true);
                                txtReadingMode.setText(R.string.txt_reading_horizontal);
                            }
                        } else if (singleChoiceSelected.equals(getResources().getString(R.string.txt_reading_vertical))) {
                            if (sharedPref.getSwipeHorizontal().equals(true)) {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                sharedPref.setSwipeHorizontal(false);
                                txtReadingMode.setText(R.string.txt_reading_vertical);
                            }
                        }
                        dialogInterface.dismiss();
                    })
                    .setNegativeButton(R.string.option_cancel, null)
                    .show();
        });

        txtCacheSize = findViewById(R.id.txt_cache_size);
        initializeCache();

        btnClearCache = findViewById(R.id.btn_clear_cache);
        btnClearCache.setOnClickListener(view -> clearCache());

        findViewById(R.id.lyt_clear_cache).setOnClickListener(v -> clearCache());

        findViewById(R.id.btn_privacy_policy).setOnClickListener(v -> Tools.openWebPage(this,
                getString(R.string.title_setting_privacy), sharedPref.getPrivacyPolicyUrl()
        ));

        findViewById(R.id.btn_share).setOnClickListener(v -> Tools.shareApp(this));

        findViewById(R.id.btn_rate).setOnClickListener(v -> Tools.rateApp(this));

        findViewById(R.id.btn_more).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sharedPref.getMoreAppsUrl()))));

        findViewById(R.id.btn_about).setOnClickListener(v -> {
            LayoutInflater layoutInflater = LayoutInflater.from(ActivitySettings.this);
            View view = layoutInflater.inflate(R.layout.dialog_about, null);
            TextView txtAppVersion = view.findViewById(R.id.txt_app_version);
            txtAppVersion.setText(getString(R.string.msg_about_version) + " " + BuildConfig.VERSION_CODE + " (" + BuildConfig.VERSION_NAME + ")");
            final MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(ActivitySettings.this);
            alert.setView(view);
            alert.setPositiveButton(R.string.option_ok, (dialog, which) -> dialog.dismiss());
            alert.show();
        });

    }

    private void clearCache() {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(ActivitySettings.this);
        dialog.setMessage(R.string.msg_clear_cache);
        dialog.setPositiveButton(R.string.option_yes, (dialogInterface, i) -> {
            FileUtils.deleteQuietly(getCacheDir());
            FileUtils.deleteQuietly(getExternalCacheDir());
            txtCacheSize.setText(getString(R.string.sub_setting_clear_cache_start) + " 0 Bytes " + getString(R.string.sub_setting_clear_cache_end));
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.msg_cache_cleared), Snackbar.LENGTH_SHORT).show();
        });
        dialog.setNegativeButton(R.string.option_cancel, null);
        dialog.show();
    }

    private void initializeCache() {
        txtCacheSize.setText(getString(R.string.sub_setting_clear_cache_start) + " " + readableFileSize((0 + getDirSize(getCacheDir())) + getDirSize(getExternalCacheDir())) + " " + getString(R.string.sub_setting_clear_cache_end));
    }

    public long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0 Bytes";
        }
        String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10((double) size) / Math.log10(1024.0d));
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        double d = (double) size;
        double pow = Math.pow(1024.0d, (double) digitGroups);
        Double.isNaN(d);
        stringBuilder.append(decimalFormat.format(d / pow));
        stringBuilder.append(" ");
        stringBuilder.append(units[digitGroups]);
        return stringBuilder.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
