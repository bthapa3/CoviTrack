package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    TextView name,email,phone,userID;
    EditText groupname;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name=findViewById(R.id.name_display);
        email=findViewById(R.id.email_display);
        phone=findViewById(R.id.phone_display);
        userID=findViewById(R.id.userID_display);
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Datasnapshot) {
                // This method is called once with the initial value and again
                UserHelperClass student = Datasnapshot.child(id).getValue(UserHelperClass.class);
                name.setText("full name:   " +student.full_name);
                email.setText("email:   " +student.email);
                phone.setText("phone:   " +student.phone_number);
                userID.setText("userID:   " + student.userID);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void Logout(View view){
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(),login.class));
        finish();
    }


    public void Create_Group(View view){

        groupname=findViewById(R.id.groupname);
        String group_name=groupname.getText().toString();
        FirebaseDatabase rootNode=FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Groups");
        Date date=new Date(2001-01-01);
        UserGroups create_user=new UserGroups(group_name,date,0);
        reference.child(group_name).setValue(create_user);
        groupname.setText("");
    }




}