package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class ResourceswebpageActivity extends AppCompatActivity {
TextView myview;
WebView m_resourcepage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resourceswebpage);


        Bundle b = getIntent().getExtras();
        String value = ""; // or other values
        if(b != null){

            value = b.getString("website");
            m_resourcepage = findViewById(R.id.webView);
            WebSettings webSettings=m_resourcepage.getSettings();
            webSettings.setJavaScriptEnabled(true);
            m_resourcepage.setWebViewClient(new AssesmentActivity.callback());
            m_resourcepage.loadUrl(value);


            System.out.println("value is"+ value);
        }
        else{
           myview.setText("null");
        }




    }

    @Override
    public void onBackPressed()

    {
        Intent homeIntent = new Intent(ResourceswebpageActivity.this, ResourcesActivity.class);
        startActivity(homeIntent);
        finish();
    }

    public void GotoAsses(View view){
        startActivity(new Intent(getApplicationContext(), AssesmentActivity.class));
        finish();
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

}
