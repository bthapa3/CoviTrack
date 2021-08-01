package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**/
/*
 *  CLASS DESCRIPTION:
 *      Allows users to Signup for using the application.
 *
 *  PURPOSE:
 *     Register activity allows the users to signup for the application. If they are already signed up they
 *      can go to login page or go to reset password page.
 *
 *  AUTHOR:
 *      Bishal Thapa
 *
 *  DATE
 *       4/27/2021
 */
/**/


public class RegisterActivity extends AppCompatActivity {
    private EditText m_FullName, m_Email,m_Password,m_PasswordCheck, m_Phone;
    private Button m_RegisterBtn;
    private TextView m_LoginBtn;
    private FirebaseAuth m_fAuth;
    private ProgressBar m_progressBar;
    private String m_userID;

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
 *      On-create method stores the reference for the toolbar,Text_views, Image_views
 *      Image_buttons and buttons. Inside the on-create method the authorization state
 *      of the current user is checked and if the user is valid, user is forwarded to main activity.
 *      Otherwise on-click listeners are setup so that users can register for a new
 *      account after filling the necessary fields.
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
    protected void onCreate(Bundle a_savedInstanceState){
        super.onCreate(a_savedInstanceState);
        setContentView(R.layout.activity_register);

        m_FullName=findViewById(R.id.name);
        m_Email=findViewById(R.id.email);
        m_Password = findViewById(R.id.password);
        m_PasswordCheck=findViewById(R.id.passwordcheck);
        m_Phone=findViewById(R.id.phone);
        m_RegisterBtn=findViewById(R.id.register);
        m_LoginBtn=findViewById(R.id.login);
        m_progressBar=findViewById(R.id.progressBar);
        m_fAuth=FirebaseAuth.getInstance();

        //checking if the instance of the user is saved before creating new account
        if(m_fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        //taking to login page if the user clicks to go there.
        m_LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        //registering the user to the firebase user authorization.
        m_RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=m_Email.getText().toString().trim();
                String password=m_Password.getText().toString().trim();
                String passwordCheck=m_PasswordCheck.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    m_Email.setError("Email Value not Found");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    m_Email.setError("Password Value not Found");
                    return;
                }

                if (!password.equals(passwordCheck)){
                    m_PasswordCheck.setError("Passwords value do not match with each other");

                    Toast.makeText(RegisterActivity.this, m_Password.getText() , Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 8){
                    m_Password.setError("Password must be at least 8 characters long");
                    return;
                }
                m_progressBar.setVisibility(View.VISIBLE);
                m_fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "user created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            StoreUsersData();
                        }

                        else{
                            Toast.makeText(RegisterActivity.this, "failed to create user", Toast.LENGTH_SHORT).show();
                            m_progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });
    }


/**/
/*
 *   NAME
 *       private void StoreUsersData
 *
 *   SYNOPSIS
 *       private void StoreUsersData()
 *      no parameters.
 *
 *   DESCRIPTION
 *      StoreUsersData function saves the data of the user to the database in the form of User class.
 *      After setting up an user authorization with Email and Password, Users email password, Name and
 *      contact name are saved on the database in the form of the user class.
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

    private void StoreUsersData() {

        String name=m_FullName.getText().toString().trim();
        String phone=m_Phone.getText().toString().trim();
        String email=m_Email.getText().toString().trim();
        m_userID=m_fAuth.getCurrentUser().getUid();
        FirebaseDatabase rootNode=FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Users");
        Users Userdata=new Users(m_userID, name,email,phone,"null","null","null","null",0,"member",false,false);
        reference.child(m_userID).setValue(Userdata);

    }
}

