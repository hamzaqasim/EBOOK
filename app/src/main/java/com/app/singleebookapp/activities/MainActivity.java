package com.app.singleebookapp.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.singleebookapp.BuildConfig;
import com.app.singleebookapp.Config;
import com.app.singleebookapp.R;
import com.app.singleebookapp.adapters.AdapterChapter;
import com.app.singleebookapp.adapters.AdapterTree;
import com.app.singleebookapp.databases.prefs.AdsPref;
import com.app.singleebookapp.databases.prefs.SharedPref;
import com.app.singleebookapp.databases.sqlite.DbChapter;
import com.app.singleebookapp.models.Chapter;
import com.app.singleebookapp.utils.AdsManager;
import com.app.singleebookapp.utils.Constant;
import com.app.singleebookapp.utils.InputFilterIntRange;
import com.app.singleebookapp.utils.Tools;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.link.DefaultLinkHandler;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.scroll.ScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.shockwave.pdfium.PdfDocument;
import com.solodroid.push.sdk.provider.OneSignalPush;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {

    private static final String TAG = "loadPDF";
    private long exitTime = 0;
    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private PDFView pdfView;
    Toolbar toolbar;
    TextView toolbarTitle;
    TextView toolbarSubTitle;
    boolean flag = true;
    InputStream inputStream = null;
    OutputStream outputStream = null;
    private ShimmerFrameLayout lytShimmer;
    CoordinatorLayout parentView;
    AppBarLayout lytTop;
    LinearLayout lytBottom;
    File folderPath;
    File pdfPath;
    String fileName;
    String fileExtension = "pdf";
    View lytFailed;
    Button btnRetry;
    TextView txtPercentage;
    TextView txtLoading;
    CircularProgressIndicator progressBar;
    private final Handler handler = new Handler();
    SharedPref sharedPref;
    AdsPref adsPref;
    AdsManager adsManager;
    int savedReadingPages = 0;
    int lastReadPage = 0;
    String pdfUrl;
    List<File> list = null;
    String bookTitle;
    NavigationView navigationView;
    RecyclerView recyclerView;
    DbChapter dbChapter;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private AppUpdateManager appUpdateManager;
    View lytExitDialog;
    LinearLayout lytPanelView;
    LinearLayout lytPanelDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbChapter = new DbChapter(this);
        sharedPref = new SharedPref(this);
        adsPref = new AdsPref(this);
        Tools.setNavigation(this);
        bookTitle = Tools.getFileNameFromUrl(sharedPref.getPdfUrl());

        if (Config.ENABLE_RTL_MODE) {
            sharedPref.setSwipeHorizontal(false);
        }

        if (sharedPref.getIsReadingPage()) {
            savedReadingPages = sharedPref.getLastReadingPage();
        }

        initViews();
        initAds();
        setOnFullScreen();
        setFolderPath();
        setupToolbar();

        if (sharedPref.allowSaveBook().equals("false")) {
            loadFile(false);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                loadPdf();
            } else {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
            }
        }

        if (!BuildConfig.DEBUG) {
            appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
            inAppUpdate();
            inAppReview();
        }

        Tools.notificationOpenHandler(this, getIntent());
        new OneSignalPush.Builder(this).requestNotificationPermission();
        initExitDialog();

    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarSubTitle = findViewById(R.id.toolbar_subtitle);
        lytBottom = findViewById(R.id.lytBottom);
        lytTop = findViewById(R.id.appBarLayout);
        lytShimmer = findViewById(R.id.shimmerViewContainer);
        parentView = findViewById(R.id.coordinatorLayout);
        txtPercentage = findViewById(R.id.txtPercentage);
        txtLoading = findViewById(R.id.txtLoading);
        progressBar = findViewById(R.id.progressBar);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        pdfView = findViewById(R.id.pdfView);
        pdfView.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void initAds() {
        adsManager = new AdsManager(this);
        adsManager.initializeAd();
        adsManager.updateConsentStatus();
        adsManager.loadBannerAd(1);
        adsManager.loadInterstitialAd(1, adsPref.getInterstitialAdInterval());
    }

    private void showInterstitialAd() {
        adsManager.showInterstitialAd();
    }

    private void setOnFullScreen() {
        pdfView.setOnClickListener(v -> {
            if (flag) {
                Tools.fullScreenMode(this, lytTop, lytBottom, true);
                flag = false;
            } else {
                Tools.fullScreenMode(this, lytTop, lytBottom, false);
                flag = true;
            }
        });
    }

    private void setFolderPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            folderPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/." + getString(R.string.app_name) + "/");
        } else {
            folderPath = new File(Environment.getExternalStorageDirectory() + "/." + getString(R.string.app_name) + "/");
        }
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        } else {
            loadPdf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPdf();
            } else {
                loadFile(false);
            }
        }
    }

    public void showSnackBar(String msg) {
        Snackbar.make(parentView, msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        lastReadPage = page;
        toolbarSubTitle.setText(String.format("%s %s %s %s", getString(R.string.txt_page), page + 1, getString(R.string.txt_of), pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        swipeProgress(false);
        toolbarSubTitle.setVisibility(View.VISIBLE);
        findViewById(R.id.btnJumpPage).setOnClickListener(v -> showPageDialog(pdfView.getPageCount()));
        findViewById(R.id.btnPopup).setOnClickListener(this::showPopupMenu);
        if (sharedPref.getCustomTableOfContents().equals("true")) {
            setAdapterChapters(dbChapter.getChapters(DbChapter.TABLE_LABEL));
        } else {
            setAdapterTableOfContents(pdfView.getTableOfContents());
        }
    }

    private void setAdapterTableOfContents(List<PdfDocument.Bookmark> tree) {
        AdapterTree adapterTree = new AdapterTree(this, new ArrayList<>());
        adapterTree.setListData(tree);
        recyclerView.setAdapter(adapterTree);
        adapterTree.setOnItemClickListener((v, obj, position) -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            showInterstitialAd();
            new Handler().postDelayed(() -> pdfView.jumpTo((int) obj.getPageIdx(), true), 200);
        });
    }

    private void setAdapterChapters(List<Chapter> chapters) {
        AdapterChapter adapterChapter = new AdapterChapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapterChapter);
        adapterChapter.setListData(chapters);
        adapterChapter.setOnItemClickListener((view, obj, position) -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            showInterstitialAd();
            new Handler().postDelayed(() -> pdfView.jumpTo((int) obj.page_number - 1, true), 200);
        });
    }

    @Override
    public void onPageError(int page, Throwable t) {
        Log.e(TAG, "Cannot load page " + page);
    }

    public void showPageDialog(int totalPages) {
        final MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.dialog_jump_page, null);

        TextView txtInputPageNumber = view.findViewById(R.id.txtInputPageNumber);
        txtInputPageNumber.setText(String.format("%s %s - %s", getString(R.string.input_page_number), "1", totalPages));

        EditText edtPageNumber = view.findViewById(R.id.edtPageNumber);
        edtPageNumber.setHint(String.format("%s - %s", "1", totalPages));

        edtPageNumber.requestFocus();
        Tools.showKeyboard(this, true);

        InputFilterIntRange rangeFilter = new InputFilterIntRange(1, totalPages);
        edtPageNumber.setFilters(new InputFilter[]{rangeFilter});
        edtPageNumber.setOnFocusChangeListener(rangeFilter);

        dialog.setView(view);
        dialog.setCancelable(false);

        AlertDialog alertDialog = dialog.create();

        TextView btnPositive = view.findViewById(R.id.btnPositive);
        btnPositive.setOnClickListener(v -> new Handler().postDelayed(() -> {
            if (!edtPageNumber.getText().toString().equals("")) {
                int pageNumber = (Integer.parseInt(edtPageNumber.getText().toString()) - 1);
                new Handler().postDelayed(() -> pdfView.jumpTo(pageNumber, true), 200);
                Tools.showKeyboard(this, false);
                alertDialog.dismiss();
            } else {
                Snackbar.make(parentView, getString(R.string.msg_input_page), Snackbar.LENGTH_SHORT).show();
            }
        }, 300));

        TextView btnNegative = view.findViewById(R.id.btnNegative);
        btnNegative.setOnClickListener(v -> new Handler().postDelayed(() -> {
            Tools.showKeyboard(this, false);
            alertDialog.dismiss();
        }, 300));

        alertDialog.show();
    }

    @SuppressLint("NonConstantResourceId")
    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.menu_settings) {
                startActivity(new Intent(getApplicationContext(), ActivitySettings.class));
                showInterstitialAd();
            } else if (itemId == R.id.menu_book_info) {
                startActivity(new Intent(getApplicationContext(), ActivityDetail.class));
                showInterstitialAd();
            } else if (itemId == R.id.menu_share) {
                Tools.shareApp(this);
            } else if (itemId == R.id.menu_rate) {
                Tools.rateApp(this);
            } else if (itemId == R.id.menu_more) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sharedPref.getMoreAppsUrl())));
            }
            return true;
        });
        popupMenu.inflate(R.menu.menu_popup);
        popupMenu.show();
    }

    private void swipeProgress(final boolean show) {
        if (show) {
            lytShimmer.setVisibility(View.VISIBLE);
            lytShimmer.startShimmer();
        } else {
            lytShimmer.setVisibility(View.GONE);
            txtPercentage.setVisibility(View.GONE);
            txtLoading.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            lytShimmer.stopShimmer();
        }
    }

    private void showFailedView() {
        swipeProgress(false);
        pdfView.setVisibility(View.GONE);
        lytFailed.setVisibility(View.VISIBLE);
        btnRetry.setOnClickListener(v -> {
            swipeProgress(true);
            lytFailed.setVisibility(View.GONE);
            pdfView.setVisibility(View.GONE);
            loadFile(true);
        });
    }

    private void loadPdf() {
        if (folderPath.exists()) {
            list = Arrays.asList(Objects.requireNonNull(folderPath.listFiles((dir, name) -> name.contains(Tools.reformatFileName(bookTitle)))));
            if (list.size() > 0) {
                int totalFiles = list.size();
                fileName = list.get(totalFiles - 1).toString();
                pdfPath = new File(fileName);
                loadPdfFromFile(pdfPath);
                Log.d(TAG, "pdf file found and try to load it");
            } else {
                loadFile(true);
                Log.d(TAG, "no pdf file found, load from server first");
            }
            Log.d(TAG, "folder exist");
        } else {
            loadFile(true);
            Log.d(TAG, "folder not exist");
        }
    }

    public void loadFile(boolean saveToStorage) {
        String url = sharedPref.getPdfUrl();
        if (url.contains("drive.google.com")) {
            pdfUrl = url.replace("https://", "").replace("http://", "");
            List<String> urls = Arrays.asList(pdfUrl.split("/"));
            downloadTask("https://drive.google.com/uc?export=download&id=" + urls.get(3), saveToStorage);
        } else {
            pdfUrl = url;
            downloadTask(pdfUrl, saveToStorage);
        }
    }

    private void downloadTask(String pdfUrl, boolean saveToStorage) {
        DownloadFileFromURL downloadFileFromURL = new DownloadFileFromURL(saveToStorage);
        downloadFileFromURL.execute(pdfUrl);
    }

    @SuppressLint("StaticFieldLeak")
    public class DownloadFileFromURL extends AsyncTask<String, String, Void> {

        public boolean saveToStorage;

        public DownloadFileFromURL(boolean saveToStorage) {
            this.saveToStorage = saveToStorage;
        }

        public void onPreExecute() {
            txtPercentage.setText("0%");
            txtLoading.setText(getString(R.string.txt_preparing_book));
            super.onPreExecute();
        }

        public Void doInBackground(String... f_url) {
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                int lengthOfFile = connection.getContentLength();
                inputStream = new BufferedInputStream(url.openStream(), 1024);
                if (saveToStorage) {
                    saveToStorage(lengthOfFile, inputStream, fileExtension);
                } else {
                    loadPdfFromInputStream(inputStream);
                }
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }
            return null;
        }

        public void onProgressUpdate(String... progress) {

        }

        public void onPostExecute(Void file_url) {
            if (saveToStorage) {
                loadPdf();
            }
        }
    }

    public void saveToStorage(int lengthOfFile, InputStream inputStream, String extension) {
        try {
            File dir;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/." + getString(R.string.app_name));
            } else {
                dir = new File(Environment.getExternalStorageDirectory() + "/." + getString(R.string.app_name));
            }
            boolean success = true;
            if (!dir.exists()) {
                success = dir.mkdirs();
            }
            if (success) {
                try {
                    Log.d(TAG, "File Size = " + lengthOfFile);
                    String fileName;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        fileName = bookTitle + "_" + System.currentTimeMillis();
                    } else {
                        fileName = bookTitle;
                    }
                    outputStream = new FileOutputStream(dir + "/" + Tools.reformatFileName(fileName) + "." + extension);

                    byte[] data = new byte[4096];
                    int count;
                    int progress = 0;
                    while ((count = inputStream.read(data)) != -1) {
                        outputStream.write(data, 0, count);
                        progress += count;
                        int finalProgress = progress;
                        handler.post(() -> {
                            int percentage = ((finalProgress * 100) / lengthOfFile);
                            txtPercentage.setText(percentage + "%");
                            txtLoading.setText(getString(R.string.txt_loading_book));
                        });
                        Log.d(TAG, "Progress: " + progress + "/" + lengthOfFile + " >>>> " + (float) progress / lengthOfFile);
                    }
                    outputStream.flush();
                    loadPdf();
                    Log.d(TAG, "File saved successfully!");
                } catch (IOException e) {
                    e.printStackTrace();
                    loadFile(false);
                    Log.d(TAG, "Failed to save the file! " + e.getMessage());
                } finally {
                    if (inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                }
            } else {
                Log.d(TAG, "not success");
                loadFile(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            loadFile(true);
            Log.d(TAG, "Error to save the file! " + e.getMessage());
        }
    }

    public void loadPdfFromFile(File pdfPath) {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            ScrollHandle scrollHandle;
            if (sharedPref.getScrollHandle().equals("true")) {
                scrollHandle = new DefaultScrollHandle(MainActivity.this);
            } else {
                scrollHandle = null;
            }

            pdfView.fromFile(pdfPath)
                    .linkHandler(new DefaultLinkHandler(pdfView))
                    .defaultPage(savedReadingPages)
                    .onPageChange(MainActivity.this)
                    .enableAnnotationRendering(true)
                    .onLoad(MainActivity.this)
                    .scrollHandle(scrollHandle)
                    .spacing(0) // in dp
                    .onPageError(MainActivity.this)
                    .swipeHorizontal(sharedPref.getSwipeHorizontal())
                    .pageSnap(true)
                    .autoSpacing(true)
                    .pageFling(true)
                    .pageFitPolicy(FitPolicy.WIDTH)
                    .onError(t -> {
                        loadFile(true);
                        Log.d(TAG, "failed load pdf and try reload from url " + t.getMessage());
                    })
                    .nightMode(false)
                    .load();
            pdfView.setVisibility(View.VISIBLE);
        }, Constant.DELAY_REFRESH);
    }

    public void loadPdfFromInputStream(InputStream inputStream) {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            ScrollHandle scrollHandle;
            if (sharedPref.getScrollHandle().equals("true")) {
                scrollHandle = new DefaultScrollHandle(MainActivity.this);
            } else {
                scrollHandle = null;
            }

            pdfView.fromStream(inputStream)
                    .linkHandler(new DefaultLinkHandler(pdfView))
                    .defaultPage(savedReadingPages)
                    .onPageChange(MainActivity.this)
                    .enableAnnotationRendering(true)
                    .onLoad(MainActivity.this)
                    .scrollHandle(scrollHandle)
                    .spacing(0) // in dp
                    .onPageError(MainActivity.this)
                    .swipeHorizontal(sharedPref.getSwipeHorizontal())
                    .pageSnap(true)
                    .autoSpacing(true)
                    .pageFling(true)
                    .pageFitPolicy(FitPolicy.WIDTH)
                    .onError(t -> {
                        loadFile(false);
                        Log.d(TAG, "failed load pdf and try reload from url");
                    })
                    .nightMode(false)
                    .load();
            pdfView.setVisibility(View.VISIBLE);
        }, Constant.DELAY_REFRESH);
    }

    @Override
    public void onResume() {
        super.onResume();
        adsManager.resumeBannerAd(1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lytShimmer.stopShimmer();
        adsManager.destroyBannerAd();
        Constant.IS_APP_OPEN = false;
    }

    private void setupToolbar() {
        toolbarTitle.setText(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        setupNavigationDrawer(toolbar);
    }

    public void setupNavigationDrawer(Toolbar toolbar) {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0) {
        };
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                if (slideOffset > 0) {
                    findViewById(R.id.bannerAdView).setVisibility(View.INVISIBLE);
                } else {
                    findViewById(R.id.bannerAdView).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void updateLastPageRead() {
        if (sharedPref.getIsReadingPage()) {
            sharedPref.setLastReadingPage(lastReadPage);
            Log.d(TAG, "update last page bookmarked");
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            exitApp();
        }
    }

    public void exitApp() {
        if (Config.ENABLE_EXIT_DIALOG) {
            if (lytExitDialog.getVisibility() != View.VISIBLE) {
                showDialog(true);
            }
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showSnackBar(getString(R.string.press_again_to_exit));
                exitTime = System.currentTimeMillis();
            } else {
                updateLastPageRead();
                finish();
                Constant.IS_APP_OPEN = false;
            }
        }
    }

    @Override
    public AssetManager getAssets() {
        return getResources().getAssets();
    }

    private void inAppReview() {
        if (sharedPref.getInAppReviewToken() <= 3) {
            sharedPref.updateInAppReviewToken(sharedPref.getInAppReviewToken() + 1);
        } else {
            ReviewManager manager = ReviewManagerFactory.create(this);
            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    manager.launchReviewFlow(MainActivity.this, reviewInfo).addOnFailureListener(e -> {
                    }).addOnCompleteListener(complete -> {
                                Log.d(TAG, "In-App Review Success");
                            }
                    ).addOnFailureListener(failure -> {
                        Log.d(TAG, "In-App Review Rating Failed");
                    });
                }
            }).addOnFailureListener(failure -> Log.d("In-App Review", "In-App Request Failed " + failure));
        }
        Log.d(TAG, "in app review token : " + sharedPref.getInAppReviewToken());
    }

    private void inAppUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdateFlow(appUpdateInfo);
            }
        });
    }

    private void startUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, Constant.IMMEDIATE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                showSnackBar(getString(R.string.msg_cancel_update));
            } else if (resultCode == RESULT_OK) {
                showSnackBar(getString(R.string.msg_success_update));
            } else {
                showSnackBar(getString(R.string.msg_failed_update));
                inAppUpdate();
            }
        }
    }

    public void initExitDialog() {

        lytExitDialog = findViewById(R.id.lyt_dialog_exit);
        lytPanelView = findViewById(R.id.lyt_panel_view);
        lytPanelDialog = findViewById(R.id.lyt_panel_dialog);
        lytPanelView.setBackgroundColor(getResources().getColor(R.color.color_dialog_background_light));
        lytPanelDialog.setBackgroundResource(R.drawable.bg_rounded);

        lytPanelView.setOnClickListener(view -> {
            //empty state
        });

        LinearLayout nativeAdView = findViewById(R.id.native_ad_view);
        Tools.setNativeAdStyle(this, nativeAdView, Constant.NATIVE_AD_STYLE_EXIT_DIALOG);
        adsManager.loadNativeAd(1, Constant.NATIVE_AD_STYLE_EXIT_DIALOG);

        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnExit = findViewById(R.id.btn_exit);

        FloatingActionButton btnRate = findViewById(R.id.btn_rate);
        FloatingActionButton btnShare = findViewById(R.id.btn_share);

        btnCancel.setOnClickListener(view -> showDialog(false));

        btnExit.setOnClickListener(view -> {
            showDialog(false);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                finish();
                adsManager.destroyBannerAd();
                Constant.IS_APP_OPEN = false;
            }, 300);
        });

        btnRate.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
            showDialog(false);
        });

        btnShare.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + "\n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            intent.setType("text/plain");
            startActivity(intent);
            showDialog(false);
        });
    }

    private void showDialog(boolean show) {
        if (show) {
            lytExitDialog.setVisibility(View.VISIBLE);
            slideUp(findViewById(R.id.dialog_card_view));
            ObjectAnimator.ofFloat(lytExitDialog, View.ALPHA, 0.1f, 1.0f).setDuration(300).start();
            Tools.fullScreenMode(this, true);
        } else {
            slideDown(findViewById(R.id.dialog_card_view));
            ObjectAnimator.ofFloat(lytExitDialog, View.ALPHA, 1.0f, 0.1f).setDuration(300).start();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                lytExitDialog.setVisibility(View.GONE);
                Tools.fullScreenMode(this, false);
                Tools.setNavigation(this);
            }, 300);
        }
    }

    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(0, 0, findViewById(R.id.main_content).getHeight(), 0);
        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, findViewById(R.id.main_content).getHeight());
        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

}