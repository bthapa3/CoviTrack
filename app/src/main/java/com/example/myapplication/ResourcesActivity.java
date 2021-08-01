package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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


/**/
/*
 *  CLASS DESCRIPTION:
 *      Displays the organization's resources to the user
 *
 *  PURPOSE:
 *     The main purpose of this page is to display all the resources including,
 *      videos and important websites so that users or employees of the organization
 *      are aware about all the resources for making informed decisions.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 */
/**/

public class ResourcesActivity extends ToolbarActivity implements  View.OnClickListener {
    private YouTubePlayerView m_youTubePlayerView;
    private ImageButton m_cdc, m_nih, m_who;

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
     *      On-create method stores the reference for the toolbar, Image_views
     *      and video player view. Inside the on-create method the on-click listeners
     *      are setup so that user a redirected to appropriate page or the website
     *      as they want.
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
    protected void onCreate(Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_resources);
        m_youTubePlayerView=findViewById(R.id.videoview);

        // Lifecycle method helps to observe when your app goes into the background or to the foreground:
        getLifecycle().addObserver(m_youTubePlayerView);
        m_cdc=findViewById(R.id.cdcbutton);
        m_nih=findViewById(R.id.nihbutton);
        m_who=findViewById(R.id.whobutton);

        //setting up toolbar for the page.
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Resources");

        //Image buttons for navigating through the 5 main activities.
        ImageButton assessbutton=findViewById(R.id.assessButton);
        ImageButton groupbutton=findViewById(R.id.groupButton);
        ImageButton homebutton=findViewById(R.id.homeButton);
        ImageButton uploadbutton=findViewById(R.id.uploadButton);

        //on click listener that helps to determine the next activity that the user wants
        // to navigate to.
        assessbutton.setOnClickListener(this);
        groupbutton.setOnClickListener(this);
        homebutton.setOnClickListener(this);
        uploadbutton.setOnClickListener(this);

        //User wants to go to cdc website
        m_cdc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ResourcesActivity.this, ResourceswebpageActivity.class);
                Bundle bundle = new Bundle();
                //url gets sent to new activity
                bundle.putString("website", "https://www.cdc.gov/coronavirus/2019-ncov/vaccines/How-Do-I-Get-a-COVID-19-Vaccine.html");
                //Putting bundle content for next Intent
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

            }
        });
        // //User wants to go to nih website which has resources regarding mental health.
        m_nih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ResourcesActivity.this, ResourceswebpageActivity.class);
                Bundle bundle = new Bundle();
                //url gets sent to new activity
                bundle.putString("website", "https://www.nimh.nih.gov/health/topics/caring-for-your-mental-health/?utm_source=NIMHwebsite&utm_medium=Portal&utm_campaign=shareNIMH"); //Your id
                //Putting bundle content for next Intent
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

            }
        });
        //User wants to goto WHO website to get COVID-19 related stats or more information.
        m_who.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ResourcesActivity.this, ResourceswebpageActivity.class);
                Bundle bundle = new Bundle();
                //url gets sent to new activity
                bundle.putString("website", "https://covid19.who.int");
                //Putting bundle content for next Intent
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

            }
        });



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
        Intent homeIntent = new Intent(ResourcesActivity.this, MainActivity.class);
        startActivity(homeIntent);
        finish();
    }

/**/
/*
 *   NAME
 *      public void onClick
 *
 *   SYNOPSIS
 *      public void onClick(View a_view)
 *      a_view   --> view object passes the reference to the Image button which triggered the
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