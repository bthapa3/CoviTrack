package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class ResourcesActivity extends AppCompatActivity {
    private YouTubePlayerView youTubePlayerView;
    ImageButton m_cdc, m_nih, m_who;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);
        youTubePlayerView=findViewById(R.id.videoview);
        getLifecycle().addObserver(youTubePlayerView);
        m_cdc=findViewById(R.id.cdcbutton);
        m_nih=findViewById(R.id.nihbutton);
        m_who=findViewById(R.id.whobutton);

        m_cdc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ResourcesActivity.this, ResourceswebpageActivity.class);
                Bundle b = new Bundle();
                b.putString("website", "https://www.cdc.gov/coronavirus/2019-ncov/vaccines/How-Do-I-Get-a-COVID-19-Vaccine.html"); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();

            }
        });
        m_nih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ResourcesActivity.this, ResourceswebpageActivity.class);
                Bundle b = new Bundle();
                b.putString("website", "https://www.nimh.nih.gov/health/topics/caring-for-your-mental-health/?utm_source=NIMHwebsite&utm_medium=Portal&utm_campaign=shareNIMH"); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();

            }
        });
        m_who.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ResourcesActivity.this, ResourceswebpageActivity.class);
                Bundle b = new Bundle();
                b.putString("website", "https://covid19.who.int"); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();

            }
        });



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


    static class callback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }
}