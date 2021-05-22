package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
     EditText mEmail,mPassword;
     Button mLoginBtn;
     TextView mRegister;
     ProgressBar progressBar;
     FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //store the views ID inorder to work with events and  to modify them.
        mEmail=findViewById(R.id.email);
        mPassword=findViewById(R.id.password);
        progressBar=findViewById(R.id.progressBar2);
        mLoginBtn=findViewById(R.id.login);
        mRegister=findViewById(R.id.register);

        fAuth=FirebaseAuth.getInstance();
        progressBar.setVisibility(View.INVISIBLE);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();

                //checking the validity of email and password fields.
                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email Value not Found");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mEmail.setError("Password Value not Found");
                    return;
                }
                if (password.length() < 8){
                    mPassword.setError("Password must be at least 8 characters long");
                    return;
                }

                //This helps the user know that app is running after the button is presses.
                //Otherwise login page might look frozen if response from the firebase auth. takes time.
                progressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "logged in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Incorrect credentials", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    }
                });
            }
        });

        //taking the user to register activity if create user text view is clicked.
        mRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }

        });


    }
}