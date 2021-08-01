package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

/**/
/*
 *  CLASS DESCRIPTION:
 *     Displays the resources from different websites
 *
 *  PURPOSE:
 *     The main purpose of this page is to display all the web pages that are listed
 *      resources activity. ResourceswebpageActivity loads the url passed from
 *      resources activity and allows users to visit different websites.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 */
/**/

public class ResourceswebpageActivity extends ToolbarActivity implements View.OnClickListener {
private TextView m_myview;
private WebView m_resourcepage;

/**/
/*
 *   NAME
 *      protected void onCreate
 *
 *   SYNOPSIS
 *      protected void onCreate(Bundle a_savedInstanceState)
 *      Bundle a_savedInstanceState---->reference to a Bundle object
 *
 *   DESCRIPTION
 *      On-create method stores the reference for the toolbar and loads
 *      the website by checking the url value found in the bundle of the activity.
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
 *   Help taken from: https://stackoverflow.com/questions/14876273/simple-example-for-intent-and-bundle for syntax.
 *
 */
/**/
    @Override
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_resourceswebpage);

        //setting up toolbar for the page.
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Resources");
        //Bundle is sent from the previous activity.
        //Help taken from: https://stackoverflow.com/questions/14876273/simple-example-for-intent-and-bundle
        Bundle b = getIntent().getExtras();
        //value will store the url
        String value = "";
        //if the url was sent from the previous activity
        if(b != null){

            value = b.getString("website");
            m_resourcepage = findViewById(R.id.webView);
            WebSettings webSettings=m_resourcepage.getSettings();
            webSettings.setJavaScriptEnabled(true);
            m_resourcepage.setWebViewClient(new AssesmentActivity.callback());
            m_resourcepage.loadUrl(value);


            System.out.println("value is"+ value);
        }
        //If the url was not sent with the bundle
        else{
           m_myview.setText("Unable to load url: error 404");
        }
    }

/**/
/*
 *   NAME
 *      public void onBackPressed
 *
 *   SYNOPSIS
 *      public void onBackPressed()
 *      no parameters.
 *
 *   DESCRIPTION
 *     This function takes the user to homepage after immediate back button press.
 *      It helps the application to prevent the user from exiting the app with
 *      single back-button press.
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
        Intent homeIntent = new Intent(ResourceswebpageActivity.this, ResourcesActivity.class);
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
    public void onClick(View a_view) {

        switch(a_view.getId()){

            case R.id.resourcesButton:
                startActivity(new Intent(getApplicationContext(), ResourcesActivity.class));
                finish();
                break;

            case R.id.assessButton:
                startActivity(new Intent(getApplicationContext(), AssesmentActivity.class));
                finish();
                break;

            case R.id.groupButton:
                startActivity(new Intent(getApplicationContext(), GroupsActivity.class));
                finish();
                break;

            case R.id.uploadButton:
                startActivity(new Intent(getApplicationContext(), UploadActivity.class));
                finish();
                break;

            case R.id.homeButton:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;

            default:
                break;

        }
    }
}
