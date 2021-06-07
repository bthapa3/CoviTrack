package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;


/**/
/*
 *  CLASS DESCRIPTION:
 *     Assessment of the user's COVID-19 condition and impact on health based on user inputs.
 *
 *  PURPOSE:
 *      This activity allows the user to connect to the resource that helps
 *      them to identify potential risks and safety measures.
 *      The main resource this activity relies is Infermedica's assessment feature.
 *      Infermedica's web embed link is attached to a html  file saved as android_asset.
 *      This activity loads the html file in web view to allow users to interact
 *      and get response based on their input.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 */
/**/
public class AssesmentActivity extends ToolbarActivity implements  View.OnClickListener{


    private WebView m_assespage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assesment);

        //Image buttons for navigating through the 5 main activities.
        ImageButton a_groupbutton=findViewById(R.id.groupButton);
        ImageButton a_homebutton=findViewById(R.id.homeButton);
        ImageButton a_resourcebutton=findViewById(R.id.resourcesButton);
        ImageButton a_uploadbutton=findViewById(R.id.uploadButton);

        //Toolbar setup by extending toolbar activity
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Assessment");

        //populating webview with html page saved as asset at "file:///android_asset"
        m_assespage = findViewById(R.id.webview);
        WebSettings webSettings=m_assespage.getSettings();
        webSettings.setJavaScriptEnabled(true);
        m_assespage.setWebViewClient(new callback());
        m_assespage.loadUrl("file:///android_asset/asses.html");

        //on-click listener for each button to navigate to other pages.
        a_groupbutton.setOnClickListener(this);
        a_homebutton.setOnClickListener(this);
        a_resourcebutton.setOnClickListener(this);
        a_uploadbutton.setOnClickListener(this);
    }

/**/
/*
 *  CLASS DESCRIPTION:
 *     callback is called in order to setup a new Webview client.
 *
 *  PURPOSE:
 *      This class extends a webviewclient and allows the webview to load url.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *      4/27/2021
 */
/**/
    static class callback extends WebViewClient {
        @Override
        //Gives the host application a chance to handle the key event synchronously.
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return true;
        }
    }

/**/
/*
*   NAME
*       public void onBackPressed
*
*   SYNOPSIS
*       public void onBackPressed()
*       no parameters
*
*   DESCRIPTION
*       This function takes the user back to main activity instead of exiting an app when back button is
*       pressed.
*
*   RETURNS
*       Nothing
*
*   AUTHOR
*       Bishal Thapa
*
*   DATE
*       4/27/2021
*
*/
/**/


    @Override
    public void onBackPressed()
    {
        Intent homeIntent = new Intent(AssesmentActivity.this, MainActivity.class);
        startActivity(homeIntent);
        finish();
    }

/**/
/*
 *   NAME
 *      public void onClick
 *
 *   SYNOPSIS
 *      public void onClick(View v)
 *      view   --> view object passes the reference to the Image button which triggered the
 *                  on-click method.
 *
 *   DESCRIPTION
 *     This function allows the user to navigate through four different activities of the application.
 *      It takes View v as an input parameter and captures the ID of the button pressed to
 *      start the new activity.
 *
 *   RETURNS
 *       Nothing
 *
 *   AUTHOR
 *       Bishal Thapa
 *
 *   DATE
 *       4/27/2021
 *
 */
/**/

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.groupButton: /** Start a new Activity MyCards.java */
                startActivity(new Intent(getApplicationContext(), GroupsActivity.class));
                finish();
                break;

            case R.id.homeButton: /**erDialog when click on Exit */
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;

            case R.id.uploadButton:
                startActivity(new Intent(getApplicationContext(), UploadActivity.class));
                finish();
                break;

            case R.id.resourcesButton:
                startActivity(new Intent(getApplicationContext(), ResourcesActivity.class));
                finish();
                break;
        }
    }

}