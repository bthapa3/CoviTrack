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

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Vector;

public class Register extends AppCompatActivity {
    EditText mFullName, mEmail,mPassword,mPasswordCheck, mPhone;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFullName=findViewById(R.id.name);
        mEmail=findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPasswordCheck=findViewById(R.id.passwordcheck);
        mPhone=findViewById(R.id.phone);
        mRegisterBtn=findViewById(R.id.register);
        mLoginBtn=findViewById(R.id.login);
        progressBar=findViewById(R.id.progressBar);
        fAuth=FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), login.class));
                finish();
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();
                String passwordCheck=mPasswordCheck.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email Value not Found");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mEmail.setError("Password Value not Found");
                    return;
                }

                if (!password.equals(passwordCheck)){
                    mPasswordCheck.setError("Passwords value do not match with each other");

                    Toast.makeText(Register.this, mPassword.getText() , Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 8){
                    mPassword.setError("Password must be at least 8 characters long");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "user created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            StoreUsersData();
                        }
                        else{
                            Toast.makeText(Register.this, "failed to create user", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });

    }

    private void StoreUsersData() {

        String name=mFullName.getText().toString().trim();
        String phone=mPhone.getText().toString().trim();
        String email=mEmail.getText().toString().trim();
        String password=mPassword.getText().toString().trim();
        userID=fAuth.getCurrentUser().getUid();
        FirebaseDatabase rootNode=FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Users");
        Users Userdata=new Users(userID, name,email,phone,"null","null","null","null",0);
        reference.child(userID).setValue(Userdata);

        //reference.child(userID).setValue("groups");

    }

}
