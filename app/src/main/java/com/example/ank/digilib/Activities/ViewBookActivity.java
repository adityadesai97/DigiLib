package com.example.ank.digilib.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.ank.digilib.R;

import java.io.File;
import java.io.IOException;

public class ViewBookActivity extends AppCompatActivity {

    private File file;

    private String genreName;
    private String bookName;
    private String download_url;

    private WebView webView;
    Activity activity ; // instead of context we can use activity
    private ProgressDialog progDailog;  // loader
    String GoogleDocs="http://docs.google.com/gview?embedded=true&url=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_view_book);

        super.onCreate(savedInstanceState);
        WebView webView=new WebView(ViewBookActivity.this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        //---you need this to prevent the webview from
        // launching another browser when a url
        // redirection occurs---
        webView.setWebViewClient(new Callback());

        Intent i = getIntent();
        genreName = i.getStringExtra("genreName");
        bookName = i.getStringExtra("bookName");
        download_url = i.getStringExtra("download_url");

//        Log.v("tag1", download_url);

        String pdfURL = download_url;
        webView.loadUrl(
                "http://docs.google.com/gview?embedded=true&url=" + pdfURL);

        setContentView(webView);

        ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_actionbar, null);

        actionBarLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(actionBarLayout);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        setTitle(bookName);

    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return(false);
        }
    }
}


