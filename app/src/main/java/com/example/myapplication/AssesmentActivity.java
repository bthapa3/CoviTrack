package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AssesmentActivity extends AppCompatActivity {
    WebView Assespage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assesment);
        Assespage = findViewById(R.id.webview);
        WebSettings webSettings=Assespage.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Assespage.setWebViewClient(new callback());
        Assespage.loadUrl("file:///android_asset/asses.html");
    }

    public void GotoGroup(View view){
        startActivity(new Intent(getApplicationContext(), GroupsActivity.class));
        finish();
    }

    public void GotoHome(View view){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void GotoUpload(View view){
        startActivity(new Intent(getApplicationContext(), UploadActivity.class));
        finish();
    }
    public void GotoResources(View view){
        startActivity(new Intent(getApplicationContext(), ResourcesActivity.class));
        finish();
    }
    static class callback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }
}