package com.example.admin.webapp;


import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

/*  BODY STARTS HERE                */

public class MainActivity extends AppCompatActivity {
    /*-------Int------------*/
    public Intent toDataSaver, in;
    protected Toast T, O;
    protected ProgressBar PB;
    protected EditText ET;
    protected DownloadManager DM;
    protected Switch S_W;
    protected MimeTypeMap MTM;
    protected SwipeRefreshLayout swipe_ges;
    WebView webView;
    String str;
    ImageView imageView;
    private DownloadManager.Request DMR;
    private CharSequence s = ".";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu);

        ET = (EditText) findViewById(R.id.editText);
        webView = (WebView) findViewById(R.id.webView);
        PB = (ProgressBar) findViewById(R.id.PB);
        imageView = (ImageView) findViewById(R.id.imageView);
      /*ICON SETTINGS*/


        /* ///WEBVIEW settings */
        webView.getSettings().setMediaPlaybackRequiresUserGesture(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DM = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                Uri u = Uri.parse(url);
                DMR = new DownloadManager.Request(u);
                DMR.setVisibleInDownloadsUi(true);
                DM.enqueue(DMR);
                T = Toast.makeText(getApplicationContext(), "Downloading Started", Toast.LENGTH_SHORT);
                T.show();

            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                PB.setProgress(progress);
                super.onProgressChanged(webView, progress);
            }
        });


        webView.setWebViewClient(new WebViewClient());


        webView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ET.setText(webView.getTitle());
                    ET.setWidth(20);
                }
            }
        });
        /*ACTION BAR SETTINGS
        * */
        ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus || hasWindowFocus()) {
                    ET.selectAll();
                    ET.setText(webView.getOriginalUrl());

                }

            }
        });
        /*   SWIPE ACTION */
        swipe_ges = (SwipeRefreshLayout) findViewById(R.id.swipe_ges);
        swipe_ges.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                Log.i("REFRESH", "REFRESH BY SWIPE");

            }
        });
        ET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    str = ET.getText().toString();

                    if (str.endsWith(".com") || str.contains(s)) {

                        webView.loadUrl("http://" + str);
                        return true;
                    } else {
                        webView.loadUrl("http://www.google.co.in/search?q=" + str);
                    }
                    return true;
                } else {
                }
                return true;
            }

        });
    }

    /*-------------REFRESH METHOD--------*/
    public void refresh() {

        webView.reload();
        swipe_ges.setRefreshing(false);
    }

    /*----------------------------------------Menu HANDLING-------------------------------------------*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.go_forward:
                if (webView.canGoForward()) {
                    item.setEnabled(true);
                    webView.goForward();
                    return true;
                } else if (!webView.canGoForward())
                    item.setEnabled(false);
                return true;
            case R.id.var: {
                refresh();
                return true;
            }

            case R.id.desktop_site:

                if (!item.isChecked()) {
                    item.setChecked(true);
                    webView.getSettings().setUserAgentString("Desktop");
                    webView.getSettings().setDisplayZoomControls(true);
                    webView.getSettings().setBuiltInZoomControls(true);


                    return true;
                } else if (webView.getSettings().getUserAgentString() != "Desktop") {
                    item.setChecked(false);
                    return true;
                }
            case R.id.android:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    webView.getSettings().setUserAgentString("Android");
                    return true;
                } else {
                    item.setChecked(false);
                    webView.getSettings().setUserAgentString(null);
                    return true;
                }
            case R.id.data_saver:
                toDataSaver = new Intent(this, Data_saver.class);

                startActivity(toDataSaver);
                webView.getSettings().setUserAgentString("Opera/9.80 (Android; Opera Mini/18.0.1290/34.926; U; en) Presto/2.8.119 Version/11.10");
                return true;

            case R.id.new_tab:
                in = new Intent(this, MainActivity.class);
                startActivity(in);
                O = Toast.makeText(this, "Tab opened", Toast.LENGTH_SHORT);
                O.show();
                return true;

            case R.id.exit:
                Process.killProcess(Process.myPid());
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    /*---------------------DOWNLOAD MANAGER------------------------*/


    /*--------------------------------------------------KEY HANDLING-------------------------------------------------------------------*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;

        } else if (keyCode == KeyEvent.KEYCODE_BACK && !webView.canGoBack()) {
            Process.killProcess(Process.myPid());

        }
        return super.onKeyDown(keyCode, event);
    }


}


