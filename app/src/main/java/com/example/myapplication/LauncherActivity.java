package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



/**/
/*
 *  CLASS DESCRIPTION:
 *     This helps to decide the launcher page of the app based on the past login records.
 *
 *  PURPOSE:
 *
 *      Launcher activity launches one of login or main activity based on whether the user has been saved or not.
 *      if there is a instance of user saved on firebase the user is directed towards main activity
 *      If the user instance is null or there is an exception while getting user, it gets redirected to login activity.
 *
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 */
/**/

public class LauncherActivity extends AppCompatActivity {


    /**/
    /*
     *   NAME
     *      protected void onCreate
     *
     *   SYNOPSIS
     *      protected void onCreate(Bundle savedInstanceState)
     *      Bundle savedInstanceState---->reference to a Bundle object
     *
     *   DESCRIPTION
     *     Checks the Firebase user instance and forwards the user to new activity based on the
     *      user instance value.
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
    protected void onCreate(@Nullable Bundle a_savedInstanceState) {
        super.onCreate(a_savedInstanceState);

       try{
           FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
           if(user!=null){
               //If the user_id is present than he doesnot have to login again.
               startActivity(new Intent(getApplicationContext(),MainActivity.class));
           }
           else{
               startActivity(new Intent(getApplicationContext(), LoginActivity.class));
           }
       }
       catch (Exception e){
            //If there is exception user will still be forwarded to login page.
           startActivity(new Intent(getApplicationContext(),LoginActivity.class));
           finish();
       }

    }
}
