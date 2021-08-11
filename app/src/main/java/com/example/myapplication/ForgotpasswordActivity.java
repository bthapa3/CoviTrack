package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**/
/*
 *  CLASS DESCRIPTION:
 *    Helps user to change or recover the password which is used for login purpose.
 *
 *  PURPOSE:
 *      This class allows the user to change the password or recover the password using the
 *      valid email address setup during the sign-up process. This activity will than send a
 *      confirmation email on the user email to reset the password.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE:
 *       4/27/2021
 */
/**/
public class ForgotpasswordActivity extends AppCompatActivity {

    private TextView m_login;
    private Button m_reset;
    private EditText m_email;
    private ProgressBar m_progressbar;
    private FirebaseAuth m_fauth;

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
     *     The onCreate function stores the reference to UI buttons,progressbar and
     *      sets up the on click listener's as required.It also sets up the firebase
     *      authorization instance to allow users to reset password.
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
        setContentView(R.layout.activity_forgotpassword);

        //Setting up the id's of the xml buttons with respective variables.
        m_login= findViewById(R.id.loginpress);
        m_reset=findViewById(R.id.reset);
        m_email=findViewById(R.id.email);
        m_progressbar=findViewById(R.id.progressBar);

        //progressbar is invisible until the user presses the reset button.
        m_progressbar.setVisibility(View.INVISIBLE);

        m_fauth=FirebaseAuth.getInstance();

        //setting up on click listener for the login button that will take the user back to
        //the login page.
        m_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        //setting up on-click listener for reset button
        m_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

    }

    /**/
    /*
     *   NAME
     *      private void resetPassword
     *
     *   SYNOPSIS
     *      private void resetPassword()
     *      no parameters.
     *
     *
     *   DESCRIPTION
     *    resetPassword function allows the users to enter their email and get the password reset link in their
     *    email. This is managed using the Firebase authorization.It also ensures the email entered
     *    is valid by checking the null value and email pattern check.
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
     *   Help taken from: https://www.youtube.com/watch?v=UMNeeMSUZl0
     *
     */
    /**/
    private void resetPassword(){

        String email=m_email.getText().toString().trim();
        if(email.isEmpty()){
            m_email.setError("Email value invalid!! Enter valid Email");
            //cursor moves to the input text field
            m_email.requestFocus();
            return;

        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            m_email.setError("Please provide valid email");
            //cursor moves to the input text field
            m_email.requestFocus();
            return;
        }
        //in order to show the loading screen
        m_progressbar.setVisibility(View.VISIBLE);
        m_fauth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //User has been sent the email so email value is set to null again and Toast message is shown.
                    Toast.makeText(ForgotpasswordActivity.this,"Reset link sent to your email",Toast.LENGTH_LONG).show();
                    m_email.setText("");
                    m_progressbar.setVisibility(View.INVISIBLE);
                }
                else{
                    //Wrong email value entered or something went wrong from the database side.
                    Toast.makeText(ForgotpasswordActivity.this,"Something wrong happened!",Toast.LENGTH_LONG).show();

                }
            }
        });

    }
}

