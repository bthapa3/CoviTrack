package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Launcher activity launches one of login or main activity based on whether the user has been saved or not.
//if there is a instance of user saved on firebase the user is directed towards main activity
//If the user instance is null or there is an exception while getting user, it gets redirected to login activity.

public class LauncherActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       try{
           FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
           if(user!=null){
               startActivity(new Intent(getApplicationContext(),MainActivity.class));
           }
           else{
               startActivity(new Intent(getApplicationContext(), LoginActivity.class));
           }
       }
       catch (Exception e){

           startActivity(new Intent(getApplicationContext(),LoginActivity.class));
           finish();
       }

    }
}
