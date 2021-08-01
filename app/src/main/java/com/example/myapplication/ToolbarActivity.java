package com.example.myapplication;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
/**/
/*
 *  CLASS DESCRIPTION:
 *    Toolbar to be used by all the activities
 *
 *  PURPOSE:
 *     The main purpose of this activity is to create a toolbar that will help
 *      to display the title of the activity page as needed and that provides
 *      easy access to profile, logout and forgot password pages.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 *
 *  Help taken from: https://www.youtube.com/watch?v=oh4YOj9VkVE for syntax
 */
/**/
public class ToolbarActivity extends AppCompatActivity {


/**/
/*
 *   NAME
 *      public void setContentView
 *
 *   SYNOPSIS
 *      public void setContentView(int a_layoutResID)
 *      int a_layoutResID ----->Resource ID to be inflated.
 *
 *   DESCRIPTION
 *     setContentView helps the resource to be inflated, adding all top-level views to the activity
 *      It also helps set the default title of the toolbar.
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
    public void setContentView(int a_layoutResID) {
        super.setContentView(a_layoutResID);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //default title for the toolbar
        toolbar.setTitle("COVID-19 Management App");
    }

/**/
/*
 *   NAME
 *      public boolean onCreateOptionsMenu
 *
 *   SYNOPSIS
 *       public boolean onCreateOptionsMenu(Menu a_menu)
 *       Menu a_menu ---> menu which contains the options for the user to select.
 *
 *   DESCRIPTION
 *      This function inflates the menu resource (defined in XML) into the Menu provided in the callback.
 *
 *   RETURNS
 *       boolean true.
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
    public boolean onCreateOptionsMenu(Menu a_menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menubar,a_menu);
        return true;
    }

/**/
/*
 *   NAME
 *      public boolean onOptionsItemSelected
 *
 *   SYNOPSIS
 *      public boolean onOptionsItemSelected(@NonNull MenuItem item)
 *      @NonNull MenuItem item ---> the list of options inside the menu(logout, forgot_password and profile)
 *
 *   DESCRIPTION
 *      OnoptionsItemSelected function helps to find the options selected from the menu
 *      and to performa actions based on that. User can logout, change password or
 *      view their profile.
 *
 *   RETURNS
 *       returns boolean value based on if the menu item is successfully handled.
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
    public boolean onOptionsItemSelected(@NonNull MenuItem a_item) {

        switch (a_item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
                break;

            case R.id.changepassword:
                FirebaseAuth.getInstance().signOut();//logout
                startActivity(new Intent(getApplicationContext(), ForgotpasswordActivity.class));
                finish();
                break;

            case R.id.profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
        }
        return super.onOptionsItemSelected(a_item);
    }

}
