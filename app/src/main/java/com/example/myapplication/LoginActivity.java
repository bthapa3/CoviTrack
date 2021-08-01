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

/**/
/*
 *  CLASS DESCRIPTION:
 *      Allows users to login to access the application features or recover their password if they forgot it.
 *
 *  PURPOSE:
 *      Login activity allows the users to enter their credentials to verify their identity. In case they
 *      lost their credentials they can go to recover password page using the link in the login page.
 *      They can also goto sign in page incase they want to register for a new account again.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 *
 *
 */
/**/


public class LoginActivity extends AppCompatActivity {
     private  EditText m_Email,m_Password;
     private  Button m_LoginBtn;
     private TextView m_Register, m_ForgotPassword;
     private ProgressBar m_progressBar;
     private FirebaseAuth m_fAuth;
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
     *      On-create method stores the reference to Button,EditText,TextView fields and than
     *      allows the operations to be performs using on-click listener.Users can login and also
     *      also goto Register and Forgot_password Activity with onclick listener deployed
     *      from On-create methods.
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
        setContentView(R.layout.activity_login);

        //store the views ID inorder to work with events and  to modify them.
        m_Email=findViewById(R.id.email);
        m_Password=findViewById(R.id.password);
        m_progressBar=findViewById(R.id.progressBar2);
        m_LoginBtn=findViewById(R.id.login);
        m_Register=findViewById(R.id.register);
        m_ForgotPassword=findViewById(R.id.resetpassword);
        m_fAuth=FirebaseAuth.getInstance();
        m_progressBar.setVisibility(View.INVISIBLE);

        m_LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=m_Email.getText().toString().trim();
                String password=m_Password.getText().toString().trim();

                //checking the validity of email and password fields.
                if (TextUtils.isEmpty(email)){
                    m_Email.setError("Email Value not Found");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    m_Email.setError("Password Value not Found");
                    return;
                }
                if (password.length() < 8){
                    m_Password.setError("Password must be at least 8 characters long");
                    return;
                }

                //This helps the user know that app is running after the button is presses.
                //Otherwise login page might look frozen if response from the firebase auth. takes time.
                m_progressBar.setVisibility(View.VISIBLE);

                m_fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "logged in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Incorrect credentials", Toast.LENGTH_SHORT).show();
                            m_progressBar.setVisibility(View.INVISIBLE);

                        }
                    }
                });
            }
        });

        //taking the user to register activity if create user text view is clicked.
        m_Register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }

        });

        //if the user forgot the password and clicks the forgot password button
        m_ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ForgotpasswordActivity.class));
                finish();
            }
        });


    }
}